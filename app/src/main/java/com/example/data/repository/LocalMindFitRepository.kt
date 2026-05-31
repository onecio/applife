package com.example.data.repository

import com.example.data.dao.MindFitDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

class LocalMindFitRepository(private val mindFitDao: MindFitDao) : MindFitRepository {

    override fun getUserProfileFlow(): Flow<UserProfile?> {
        return mindFitDao.getUserProfileFlow()
    }

    override suspend fun getOrCreateUserProfile(): UserProfile {
        val existing = mindFitDao.getUserProfileDirect()
        return if (existing != null) {
            existing
        } else {
            val default = UserProfile()
            mindFitDao.insertUserProfile(default)
            default
        }
    }

    override suspend fun saveUserProfile(userProfile: UserProfile) {
        mindFitDao.insertUserProfile(userProfile)
    }

    override fun getMoodCheckinsFlow(): Flow<List<MoodCheckin>> {
        return mindFitDao.getMoodCheckinsFlow()
    }

    override suspend fun addMoodCheckin(moodCheckin: MoodCheckin) {
        mindFitDao.insertMoodCheckin(moodCheckin)
    }

    override fun getFocusSessionsFlow(): Flow<List<FocusSession>> {
        return mindFitDao.getFocusSessionsFlow()
    }

    override suspend fun addFocusSession(focusSession: FocusSession) {
        mindFitDao.insertFocusSession(focusSession)
    }

    override fun getBreathingSessionsFlow(): Flow<List<BreathingSession>> {
        return mindFitDao.getBreathingSessionsFlow()
    }

    override suspend fun addBreathingSession(breathingSession: BreathingSession) {
        mindFitDao.insertBreathingSession(breathingSession)
    }

    override fun getCoachMessagesFlow(): Flow<List<CoachMessage>> {
        return mindFitDao.getCoachMessagesFlow()
    }

    override suspend fun addCoachMessage(coachMessage: CoachMessage) {
        mindFitDao.insertCoachMessage(coachMessage)
    }

    override suspend fun clearChatHistory() {
        mindFitDao.clearCoachMessages()
    }

    override fun getLatestDailyPlanFlow(): Flow<DailyPlan?> {
        return mindFitDao.getLatestDailyPlanFlow()
    }

    override suspend fun saveDailyPlan(dailyPlan: DailyPlan) {
        mindFitDao.insertDailyPlan(dailyPlan)
    }
}
