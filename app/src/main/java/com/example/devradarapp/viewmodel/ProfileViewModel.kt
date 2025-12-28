package com.example.devradarapp.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.devradarapp.data.AppDatabase
import com.example.devradarapp.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext
    private val userDao = AppDatabase.getDatabase(context).userDao()
    private val apiService = RetrofitClient.instance

    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState: StateFlow<UploadState> = _uploadState.asStateFlow()

    // 資料來源偏好設定
    private val prefs = application.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    
    // 如果未設定預設為 true
    private val _isIThomeEnabled = MutableStateFlow(prefs.getBoolean("source_ithome", true))
    val isIThomeEnabled: StateFlow<Boolean> = _isIThomeEnabled.asStateFlow()

    private val _isThreadsEnabled = MutableStateFlow(prefs.getBoolean("source_threads", true))
    val isThreadsEnabled: StateFlow<Boolean> = _isThreadsEnabled.asStateFlow()

    fun toggleIThome(enabled: Boolean) {
        _isIThomeEnabled.value = enabled
        prefs.edit().putBoolean("source_ithome", enabled).apply()
    }

    fun toggleThreads(enabled: Boolean) {
        _isThreadsEnabled.value = enabled
        prefs.edit().putBoolean("source_threads", enabled).apply()
    }

    fun uploadAvatar(uri: Uri, userId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uploadState.value = UploadState.Loading
            try {
                // 1. 壓縮圖片
                val file = compressImage(uri)
                if (file == null) {
                    _uploadState.value = UploadState.Error("Failed to process image")
                    return@launch
                }

                // 2. 準備 Multipart
                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

                // 3. 上傳
                val response = apiService.uploadAvatar(userId, body)

                // 4. 更新本地資料庫
                userDao.updateAvatar(userId, response.url)
                
                // 5. 成功
                _uploadState.value = UploadState.Success
                onSuccess()
                
            } catch (e: Exception) {
                _uploadState.value = UploadState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private suspend fun compressImage(uri: Uri): File? = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (originalBitmap == null) return@withContext null

            // 如果太大則調整大小 (例如 > 1024x1024)
            val maxDimension = 1024
            var width = originalBitmap.width
            var height = originalBitmap.height
            val scale = if (width > maxDimension || height > maxDimension) {
                if (width > height) maxDimension.toFloat() / width else maxDimension.toFloat() / height
            } else {
                1f
            }

            val resizedBitmap = if (scale < 1f) {
                Bitmap.createScaledBitmap(originalBitmap, (width * scale).toInt(), (height * scale).toInt(), true)
            } else {
                originalBitmap
            }

            // 壓縮至檔案
            val cacheDir = context.cacheDir
            val file = File(cacheDir, "avatar_temp.jpg")
            val outputStream = FileOutputStream(file)
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            outputStream.flush()
            outputStream.close()
            
            return@withContext file
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }

    fun resetState() {
        _uploadState.value = UploadState.Idle
    }
}

sealed class UploadState {
    object Idle : UploadState()
    object Loading : UploadState()
    object Success : UploadState()
    data class Error(val message: String) : UploadState()
}
