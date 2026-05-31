package com.example.data.repository

import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

interface MindFitRepository {
    fun getUserProfileFlow(): Flow<UserProfile?>
    suspend fun getOrCreateUserProfile(): UserProfile
    suspend fun saveUserProfile(userProfile: UserProfile)

    fun getMoodCheckinsFlow(): Flow<List<MoodCheckin>>
    suspend fun addMoodCheckin(moodCheckin: MoodCheckin)

    fun getFocusSessionsFlow(): Flow<List<FocusSession>>
    suspend fun addFocusSession(focusSession: FocusSession)

    fun getBreathingSessionsFlow(): Flow<List<BreathingSession>>
    suspend fun addBreathingSession(breathingSession: BreathingSession)

    fun getCoachMessagesFlow(): Flow<List<CoachMessage>>
    suspend fun addCoachMessage(coachMessage: CoachMessage)
    suspend fun clearChatHistory()

    fun getLatestDailyPlanFlow(): Flow<DailyPlan?>
    suspend fun saveDailyPlan(dailyPlan: DailyPlan)
}
