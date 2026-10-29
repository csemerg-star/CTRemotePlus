package com.ctremoteplus.app.net

import android.util.Base64
import okhttp3.*

class SamsungTvController(private val ip: String, private val appName: String = "CTRemotePlus") {
    private var ws: WebSocket? = null
    private val client = OkHttpClient()
    private fun tokenName(): String = Base64.encodeToString(appName.toByteArray(), Base64.NO_WRAP)

    fun connect(onReady: () -> Unit = {}) {
        val url = "ws://$ip:8001/api/v2/channels/samsung.remote.control?name=${tokenName()}"
        ws = client.newWebSocket(Request.Builder().url(url).build(), object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) { onReady() }
        })
    }

    fun sendKey(key: String) {
        ws?.send("""{"method":"ms.remote.control","params":{"Cmd":"Click","DataOfCmd":"$key","Option":"false","TypeOfRemote":"SendRemoteKey"}}""")
    }

    fun switchSource(inputId: String) { sendKey("KEY_$inputId") }
    fun disconnect() { ws?.close(1000, "bye") }
}
