package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val name: String = "Usuário",
    val email: String = "user@example.com",
    val mainGoal: String = "Foco & Clareza Mental",
    val preferredNotificationTime: String = "09:00",
    val isPremium: Boolean = false,
    val onboardingCompleted: Boolean = false
)

@Entity(tableName = "mood_checkins")
data class MoodCheckin(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val mood: String, // "Feliz", "Calmo", "Ansioso", "Cansado", "Focado"
    val energy: Int = 3, // 1 a 5
    val focus: Int = 3, // 1 a 5
    val anxiety: Int = 3, // 1 a 5
    val notes: String = ""
)

@Entity(tableName = "focus_sessions")
data class FocusSession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val durationMinutes: Int,
    val objective: String,
    val completed: Boolean = true,
    val perceivedProductivity: Int = 3 // 1 a 5
)

@Entity(tableName = "breathing_sessions")
data class BreathingSession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val durationSeconds: Int,
    val type: String = "4-4-4 (Quadrada)"
)

@Entity(tableName = "coach_messages")
data class CoachMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val sender: String, // "user" ou "coach"
    val text: String
)

@Entity(tableName = "daily_plans")
data class DailyPlan(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val primaryObjective: String = "Manter o foco absoluto nas prioridades",
    val task1: String = "Organizar a lista de tarefas prioritárias",
    val task1Done: Boolean = false,
    val task2: String = "Fazer 1 sessão de foco ininterrupta",
    val task2Done: Boolean = false,
    val task3: String = "Praticar 5 minutos de respiração consciente",
    val task3Done: Boolean = false,
    val strategy: String = "Evitar notificações de redes sociais nas primeiras horas",
    val risk: String = "Distração digital constante",
    val coachRecommendation: String = "Respire conscientemente antes de iniciar tarefas complexas."
)
