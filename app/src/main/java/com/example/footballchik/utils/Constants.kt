package com.example.footballchik.utils

object Constants {
    // Supabase
    const val SUPABASE_URL = "https://htlhaeixdhrdznnbgbsb.supabase.co"  // Вставь свой URL
    const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imh0bGhhZWl4ZGhyZHpubmJnYnNiIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjUyOTMzOTQsImV4cCI6MjA4MDg2OTM5NH0.CdxOg-PkZoWioqWJtq3zqZ8KE1xSVYUIQccY_ZcdVGk"  // Вставь свой ключ

    // Database Tables
    const val TEAMS_TABLE = "teams"
    const val MATCHES_TABLE = "matches"
    const val PLAYERS_TABLE = "players"
    const val USERS_TABLE = "users"

    // Shared Preferences
    const val PREF_NAME = "football_league_prefs"
    const val PREF_USER_ROLE = "user_role"
    const val PREF_TEAM_ID = "team_id"
    const val PREF_USER_ID = "user_id"

    // User Roles
    const val ROLE_ADMIN = "admin"
    const val ROLE_MANAGER = "manager"
    const val ROLE_VIEWER = "viewer"

    // API Timeouts
    const val CONNECT_TIMEOUT = 30L
    const val READ_TIMEOUT = 30L
    const val WRITE_TIMEOUT = 30L
}
