package my.android.passwordmanagerapp.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import my.android.passwordmanagerapp.data.Password
import my.android.passwordmanagerapp.data.PasswordRepository

class PasswordViewModel(private val repository: PasswordRepository) : ViewModel() {

    val passwords = mutableStateListOf<Password>()

    init {
        loadPasswords()
    }

    fun loadPasswords() {
        viewModelScope.launch {
            passwords.clear()
            passwords.addAll(repository.getAll())
        }
    }

    fun addPassword(password: Password) {
        viewModelScope.launch {
            repository.insert(password)
            loadPasswords()
        }
    }

    fun updatePassword(password: Password) {
        viewModelScope.launch {
            repository.update(password)
            loadPasswords()
        }
    }

    fun deletePassword(password: Password) {
        viewModelScope.launch {
            repository.delete(password)
            loadPasswords()
        }
    }
}

class PasswordViewModelFactory(private val repository: PasswordRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PasswordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PasswordViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
