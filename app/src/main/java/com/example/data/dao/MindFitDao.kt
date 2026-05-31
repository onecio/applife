package com.example.data.dao

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MindFitDao {

    // User Profile
    @Query("SELECT * FROM user_profiles WHERE id = 1 LIMIT 1")
    fun getUserProfileFlow(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profiles WHERE id = 1 LIMIT 1")
    suspend fun getUserProfileDirect(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(userProfile: UserProfile)

    // Mood Check-in
    @Query("SELECT * FROM mood_checkins ORDER BY timestamp DESC")
    fun getMoodCheckinsFlow(): Flow<List<MoodCheckin>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoodCheckin(checkin: MoodCheckin)

    // Focus Sessions
    @Query("SELECT * FROM focus_sessions ORDER BY timestamp DESC")
    fun getFocusSessionsFlow(): Flow<List<FocusSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFocusSession(session: FocusSession)

    // Breathing Sessions
    @Query("SELECT * FROM breathing_sessions ORDER BY timestamp DESC")
    fun getBreathingSessionsFlow(): Flow<List<BreathingSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBreathingSession(session: BreathingSession)

    // Coach Messages
    @Query("SELECT * FROM coach_messages ORDER BY timestamp ASC")
    fun getCoachMessagesFlow(): Flow<List<CoachMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoachMessage(message: CoachMessage)

    @Query("DELETE FROM coach_messages")
    suspend fun clearCoachMessages()

    // Daily Plans
    @Query("SELECT * FROM daily_plans ORDER BY timestamp DESC LIMIT 1")
    fun getLatestDailyPlanFlow(): Flow<DailyPlan?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyPlan(plan: DailyPlan)
}
