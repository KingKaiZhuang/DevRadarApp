package com.example.devradarapp.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.devradarapp.data.AppDatabase
import com.example.devradarapp.data.UserEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val userDao = AppDatabase.getDatabase(application).userDao()
    // 使用 SharedPreferences 來儲存簡單的登入資訊
    private val prefs = application.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    // 當前登入的使用者狀態 (null 代表未登入或訪客)
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    // 登入狀態
    private val _loginState = MutableStateFlow<LoginStatus>(LoginStatus.Idle)
    val loginState: StateFlow<LoginStatus> = _loginState.asStateFlow()

    // 初始化時，檢查是否有儲存的 User ID
    init {
        checkSavedLogin()
    }

    private fun checkSavedLogin() {
        val savedUserId = prefs.getInt("logged_in_user_id", -1)
        if (savedUserId != -1) {
            viewModelScope.launch {
                val user = userDao.getUserById(savedUserId)
                if (user != null) {
                    _currentUser.value = user
                    _loginState.value = LoginStatus.Success
                }
            }
        }
    }

    fun register(email: String, pass: String, name: String) {
        viewModelScope.launch {
            _loginState.value = LoginStatus.Loading
            try {
                val initials = name.split(" ")
                    .mapNotNull { it.firstOrNull()?.toString() }
                    .take(2)
                    .joinToString("")
                    .uppercase()

                val newUser = UserEntity(email = email, password = pass, name = name, initials = initials)
                userDao.registerUser(newUser)

                // 註冊後取得完整的 User 物件 (含自動生成的 ID)
                val createdUser = userDao.login(email, pass)
                if (createdUser != null) {
                    saveLoginState(createdUser)
                }
            } catch (e: Exception) {
                _loginState.value = LoginStatus.Error("註冊失敗: ${e.message}")
            }
        }
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _loginState.value = LoginStatus.Loading
            val user = userDao.login(email, pass)
            if (user != null) {
                saveLoginState(user)
            } else {
                _loginState.value = LoginStatus.Error("帳號或密碼錯誤")
            }
        }
    }

    // 統一處理登入成功後的狀態儲存
    private fun saveLoginState(user: UserEntity) {
        _currentUser.value = user
        _loginState.value = LoginStatus.Success

        // 將 User ID 寫入 SharedPreferences
        prefs.edit().putInt("logged_in_user_id", user.id).apply()
    }

    fun logout() {
        _currentUser.value = null
        _loginState.value = LoginStatus.Idle

        // 登出時清除 SharedPreferences
        prefs.edit().remove("logged_in_user_id").apply()
    }

    fun resetState() {
        _loginState.value = LoginStatus.Idle
    }
}

sealed class LoginStatus {
    object Idle : LoginStatus()
    object Loading : LoginStatus()
    object Success : LoginStatus()
    data class Error(val message: String) : LoginStatus()
}