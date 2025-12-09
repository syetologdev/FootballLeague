package com.example.footballchik.screens

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.footballchik.LoginActivity
import com.example.footballchik.R
import com.example.footballchik.utils.AuthManager

class ProfileFragment : Fragment() {

    private lateinit var authManager: AuthManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authManager = AuthManager(requireContext())

        val emailTextView = view.findViewById<TextView>(R.id.email_text)
        val userIdTextView = view.findViewById<TextView>(R.id.user_id_text)
        val roleTextView = view.findViewById<TextView>(R.id.role_text)
        val logoutButton = view.findViewById<Button>(R.id.logout_button)

        // Показываем информацию пользователя
        val email = authManager.getEmail()
        val userId = authManager.getUserId()
        val role = authManager.getRole()

        emailTextView.text = "Email: $email"
        userIdTextView.text = "ID: $userId"
        roleTextView.text = "Роль: $role"

        // Обработчик кнопки выхода
        logoutButton.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Выход из аккаунта")
            .setMessage("Вы уверены, что хотите выйти?")
            .setPositiveButton("Да") { _, _ ->
                logout()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun logout() {
        authManager.logout()
        Toast.makeText(requireContext(), "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show()

        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
