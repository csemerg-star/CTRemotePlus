package com.ctremoteplus.app.net

import okhttp3.*
import org.json.JSONObject

class LgTvController(
    private val ip: String,
    private val savedClientKey: String?,
    private val onClientKey: (String) -> Unit
) {
    private var ws: WebSocket? = null
    private var pointerSocket: WebSocket? = null
    private val client = OkHttpClient()
    private var msgId = 0

    fun connect(onReady: () -> Unit = {}) {
        ws = client.newWebSocket(Request.Builder().url("ws://$ip:3000").build(), object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) { register() }
            override fun onMessage(webSocket: WebSocket, text: String) {
                val json = JSONObject(text)
                val id = json.optString("id")
                if (json.optString("type") == "registered") {
                    json.optJSONObject("payload")?.optString("client-key")?.let { if (it.isNotEmpty()) onClientKey(it) }
                    openPointerSocket(); onReady()
                } else if (id.startsWith("ptr_")) {
                    json.optJSONObject("payload")?.optString("socketPath")?.let { if (it.isNotEmpty()) openPointerSocketDirect(it) }
                }
            }
        })
    }

    private fun register() {
        val manifest = JSONObject().apply {
            put("manifestVersion", 1)
            put("permissions", listOf(
                "LAUNCH","CONTROL_AUDIO","CONTROL_INPUT_MEDIA_PLAYBACK","CONTROL_INPUT_TV",
                "READ_INSTALLED_APPS","CONTROL_DISPLAY","CONTROL_INPUT_JOYSTICK",
                "CONTROL_INPUT_TEXT","CONTROL_MOUSE_AND_KEYBOARD"
            ))
        }
        val payload = JSONObject().apply {
            put("forcePairing", false); put("pairingType", "PROMPT"); put("manifest", manifest)
            if (!savedClientKey.isNullOrEmpty()) put("client-key", savedClientKey)
        }
        ws?.send(JSONObject().apply { put("type", "register"); put("id", "register_0"); put("payload", payload) }.toString())
    }

    private fun openPointerSocket() {
        ws?.send(JSONObject().apply {
            put("type", "request"); put("id", "ptr_${msgId++}")
            put("uri", "ssap://com.webos.service.networkinput/getPointerInputSocket")
        }.toString())
    }

    private fun openPointerSocketDirect(socketPath: String) {
        pointerSocket = client.newWebSocket(Request.Builder().url(socketPath).build(), object : WebSocketListener() {})
    }

    fun sendButton(name: String) { pointerSocket?.send("type:button\nname:$name\n\n") }

    fun switchInput(inputId: String) {
        ws?.send(JSONObject().apply {
            put("type", "request"); put("id", "input_${msgId++}")
            put("uri", "ssap://tv/switchInput"); put("payload", JSONObject().put("inputId", inputId))
        }.toString())
    }

    fun disconnect() { pointerSocket?.close(1000, null); ws?.close(1000, null) }
}
