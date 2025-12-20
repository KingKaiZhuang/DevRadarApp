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

    fun uploadAvatar(uri: Uri, userId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uploadState.value = UploadState.Loading
            try {
                // 1. Compress Image
                val file = compressImage(uri)
                if (file == null) {
                    _uploadState.value = UploadState.Error("Failed to process image")
                    return@launch
                }

                // 2. Prepare Multipart
                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

                // 3. Upload
                val response = apiService.uploadAvatar(userId, body)

                // 4. Update Local DB
                userDao.updateAvatar(userId, response.url)
                
                // 5. Success
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

            // Resize if too large (e.g., > 1024x1024)
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

            // Compress to File
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
