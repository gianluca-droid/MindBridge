package com.mindbridge.app.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.mindbridge.app.data.model.User
import com.mindbridge.app.data.model.UserRole
import com.mindbridge.app.data.repository.MockRepository

data class AuthState(
    val isLoggedIn: Boolean = false,
    val currentUser: User? = null,
    val errorMessage: String? = null,
    val isLoading: Boolean = false
)

class AuthViewModel : ViewModel() {
    var state by mutableStateOf(AuthState())
        private set

    fun login(email: String, password: String) {
        state = state.copy(isLoading = true, errorMessage = null)
        // Mock login: cerca per email
        val user = MockRepository.getUserByEmail(email)
        if (user != null && password.isNotBlank()) {
            state = state.copy(isLoggedIn = true, currentUser = user, isLoading = false)
        } else {
            state = state.copy(
                errorMessage = "Credenziali non valide. Prova: sofia.conti@email.it o elena.rossi@mindbridge.it",
                isLoading = false
            )
        }
    }

    fun register(nome: String, cognome: String, email: String, password: String, ruolo: UserRole) {
        state = state.copy(isLoading = true, errorMessage = null)
        if (nome.isBlank() || cognome.isBlank() || email.isBlank() || password.length < 6) {
            state = state.copy(
                errorMessage = "Compila tutti i campi. La password deve avere almeno 6 caratteri.",
                isLoading = false
            )
            return
        }
        val newUser = User(
            id = "u${System.currentTimeMillis()}",
            nome = nome, cognome = cognome, email = email, ruolo = ruolo
        )
        state = state.copy(isLoggedIn = true, currentUser = newUser, isLoading = false)
    }

    fun logout() {
        state = AuthState()
    }

    fun clearError() {
        state = state.copy(errorMessage = null)
    }
}
