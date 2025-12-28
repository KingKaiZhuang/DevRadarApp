package com.example.devradarapp.network

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class WebSocketManager {

    private var currentUserId: Int? = null
    private var client: OkHttpClient? = null
    private var webSocket: WebSocket? = null
    private val TAG = "WebSocketManager"

    // 接收訊息的回調
    var onMessageReceived: ((String) -> Unit)? = null

    fun connect(userId: Int) {
        currentUserId = userId
        if (webSocket != null) return // 已連線

        client = OkHttpClient()
        // 模擬器請替換為你的實際 IP 位址
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

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "WebSocket Closed")
                this@WebSocketManager.webSocket = null

            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e(TAG, "WebSocket Failure: ${t.message}")
                t.printStackTrace()
                this@WebSocketManager.webSocket = null
                // 延遲後嘗試重連
                attemptReconnect()
            }
        })
    }
    
    private fun attemptReconnect() {
        if (currentUserId == null) return
        Log.d(TAG, "Attempting to reconnect in 3s...")
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
             if (webSocket == null && currentUserId != null) {
                 connect(currentUserId!!)
             }
        }, 3000)
    }

    fun close() {
        currentUserId = null // 停止重連
        webSocket?.close(1000, "User logout or app close")
        webSocket = null
        // OkHttp 3.x 相容性：使用方法呼叫
        client?.dispatcher?.executorService?.shutdown()
        client = null
    }
}
