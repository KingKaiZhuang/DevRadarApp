package com.example.devradarapp.network

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class WebSocketManager {

    private var client: OkHttpClient? = null
    private var webSocket: WebSocket? = null
    private val TAG = "WebSocketManager"

    // Callback for received messages
    var onMessageReceived: ((String) -> Unit)? = null

    fun connect(userId: Int) {
        if (webSocket != null) return // Already connected

        client = OkHttpClient()
        // Replace with your actual IP address for emulator
        val request = Request.Builder()
            .url("ws://10.0.2.2:8000/ws/$userId") 
            .build()

        webSocket = client?.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d(TAG, "WebSocket Connected")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d(TAG, "Received message: $text")
                onMessageReceived?.invoke(text)
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                Log.d(TAG, "Received bytes: ${bytes.hex()}")
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "WebSocket Closing: $code / $reason")
                webSocket.close(1000, null)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e(TAG, "WebSocket Failure: ${t.message}")
                t.printStackTrace()
                this@WebSocketManager.webSocket = null
            }
        })
    }

    fun close() {
        webSocket?.close(1000, "User logout or app close")
        webSocket = null
        // OkHttp 3.x compatibility: use method calls
        client?.dispatcher()?.executorService()?.shutdown()
        client = null
    }
}
