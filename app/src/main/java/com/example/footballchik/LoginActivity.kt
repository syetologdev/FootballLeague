package com.example.footballchik

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.footballchik.repository.SupabaseRepository
import com.example.footballchik.utils.AuthManager
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var submitButton: Button
    private lateinit var toggleModeButton: TextView
    private lateinit var authTitle: TextView
    private lateinit var authManager: AuthManager

    private val repository = SupabaseRepository()
    private var isLoginMode = true // true = Вход, false = Регистрация

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        authManager = AuthManager(this)

        // Проверяем, если уже залогинен - сразу в MainActivity
        if (authManager.isLoggedIn()) {
            goToMainActivity()
            return
        }

        emailInput = findViewById(R.id.email_input)
        passwordInput = findViewById(R.id.password_input)
        submitButton = findViewById(R.id.submit_button)
        toggleModeButton = findViewById(R.id.toggle_mode_button)
        authTitle = findViewById(R.id.auth_title)

        updateUI()

        // Кнопка "Войти" / "Зарегистрироваться"
        submitButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                if (isLoginMode) {
                    loginUser(email, password)
                } else {
                    registerUser(email, password)
                }
            } else {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }

        // Переключение режима (Вход <-> Регистрация)
        toggleModeButton.setOnClickListener {
            isLoginMode = !isLoginMode
            updateUI()
            emailInput.text.clear()
            passwordInput.text.clear()
        }
    }

    private fun updateUI() {
        if (isLoginMode) {
            submitButton.text = "Войти"
            authTitle.text = "Войди в свой аккаунт"
            toggleModeButton.text = "Нет аккаунта? Создать аккаунт"
        } else {
            submitButton.text = "Зарегистрироваться"
            authTitle.text = "Создай новый аккаунт"
            toggleModeButton.text = "Уже есть аккаунт? Войти"
        }
    }

    private fun loginUser(email: String, pass: String) {
        lifecycleScope.launch {
            submitButton.isEnabled = false
            try {
                repository.loginUser(email, pass).collect { (success, user) ->
                    submitButton.isEnabled = true
                    if (success && user != null) {
                        // Сохраняем сессию
                        authManager.login(user.id, user.email, user.role)
                        Toast.makeText(this@LoginActivity, "Добро пожаловать!", Toast.LENGTH_SHORT).show()
                        goToMainActivity()
                    } else {
                        Toast.makeText(this@LoginActivity, "❌ Неверный email или пароль", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                submitButton.isEnabled = true
                Toast.makeText(this@LoginActivity, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerUser(email: String, pass: String) {
        // Валидация email
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Введите корректный email", Toast.LENGTH_SHORT).show()
            return
        }

        // Валидация пароля (минимум 6 символов)
        if (pass.length < 6) {
            Toast.makeText(this, "Пароль должен быть минимум 6 символов", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            submitButton.isEnabled = false
            try {
                repository.registerUser(email, pass).collect { (success, message) ->
                    if (success) {
                        Toast.makeText(this@LoginActivity, "✅ Регистрация успешна! Входим...", Toast.LENGTH_SHORT).show()
                        // Сразу логиним после успешной регистрации
                        loginUser(email, pass)
                    } else {
                        submitButton.isEnabled = true
                        Toast.makeText(this@LoginActivity, "❌ $message", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                submitButton.isEnabled = true
                Toast.makeText(this@LoginActivity, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        // Очищаем стек activity, чтобы нельзя было вернуться назад на логин
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
