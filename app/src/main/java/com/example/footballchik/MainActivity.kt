package com.example.footballchik

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.footballchik.screens.NotificationsFragment
import com.example.footballchik.screens.ProfileFragment
import com.example.footballchik.screens.ScheduleFragment
import com.example.footballchik.screens.TableFragment
import com.example.footballchik.screens.TeamsFragment
import com.example.footballchik.utils.AuthManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        authManager = AuthManager(this)
        bottomNav = findViewById(R.id.bottom_navigation)

        // Устанавливаем цвет панели навигации
        bottomNav.setBackgroundColor(ContextCompat.getColor(this, R.color.nav_bg))

        // Загрузить TableFragment по умолчанию
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, TableFragment())
                .commit()
        }

        // Обработчик нажатий на элементы меню
        bottomNav.setOnItemSelectedListener { menuItem ->
            val selectedFragment: Fragment = when (menuItem.itemId) {
                R.id.tableFragment -> TableFragment()
                R.id.scheduleFragment -> ScheduleFragment()
                R.id.teamFragment -> TeamsFragment()
                R.id.notificationsFragment -> NotificationsFragment()
                R.id.profileFragment -> ProfileFragment()  // ✅ Добавили
                else -> TableFragment()
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, selectedFragment)
                .addToBackStack(null)
                .commit()
            true
        }
    }
}
