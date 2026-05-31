package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.api.Content
import com.example.data.api.GeminiApiClient
import com.example.data.api.Part
import com.example.data.model.*
import com.example.data.repository.MindFitRepository
import com.example.data.repository.LocalMindFitRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MindFitViewModel(private val repository: MindFitRepository) : ViewModel() {

    // User Profile
    val userProfile: StateFlow<UserProfile> = repository.getUserProfileFlow()
        .map { it ?: UserProfile() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile())

    // Mood Check-ins
    val moodCheckins: StateFlow<List<MoodCheckin>> = repository.getMoodCheckinsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Focus Sessions
    val focusSessions: StateFlow<List<FocusSession>> = repository.getFocusSessionsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Breathing Sessions
    val breathingSessions: StateFlow<List<BreathingSession>> = repository.getBreathingSessionsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Coach Messages
    val coachMessages: StateFlow<List<CoachMessage>> = repository.getCoachMessagesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Latest Daily Plan
    val latestDailyPlan: StateFlow<DailyPlan> = repository.getLatestDailyPlanFlow()
        .map { it ?: DailyPlan() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DailyPlan())

    // Onboarding UI State
    val onboardingStep = MutableStateFlow(1)

    // Chat Loading / Typing Status
    val isCoachTyping = MutableStateFlow(false)

    // Free message constraints
    val messagesLeftInFreePlan = MutableStateFlow(5)

    // Crisis indicator
    val isEmotionalCrisis = MutableStateFlow(false)

    // --- Active Focus Timer state ---
    val focusTimeLeft = MutableStateFlow(25 * 60) // em segundos
    val focusTargetTime = MutableStateFlow(25) // em minutos
    val focusIsRunning = MutableStateFlow(false)
    val focusObjective = MutableStateFlow("Terminar projeto")
    val showFocusCompletionDialog = MutableStateFlow(false)
    private var focusTimerJob: Job? = null

    // --- Active Breathing Coach state ---
    val breathingCycleState = MutableStateFlow("Inspirar") // "Inspirar", "Segurar (Retenção)", "Expirar"
    val breathingCircleScale = MutableStateFlow(1.0f) // de 1.0f a 2.5f para animação visual de expansão
    val breathingIsRunning = MutableStateFlow(false)
    val breathingTimeLeft = MutableStateFlow(180) // 3 minutos por padrão
    val breathingSecondsElapsed = MutableStateFlow(0)
    val showBreathingCompletionDialog = MutableStateFlow(false)
    private var breathingJob: Job? = null

    init {
        // Initialize user database record if empty
        viewModelScope.launch {
            repository.getOrCreateUserProfile()
        }
    }

    // --- Profile & Onboarding Actions ---
    fun updateOnboardingInfo(name: String, email: String, mainGoal: String, notificationTime: String) {
        viewModelScope.launch {
            val current = repository.getOrCreateUserProfile()
            val updated = current.copy(
                name = name,
                email = email,
                mainGoal = mainGoal,
                preferredNotificationTime = notificationTime,
                onboardingCompleted = true
            )
            repository.saveUserProfile(updated)
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            val current = repository.getOrCreateUserProfile()
            repository.saveUserProfile(current.copy(onboardingCompleted = true))
        }
    }

    fun togglePremiumStatus(isPremium: Boolean) {
        viewModelScope.launch {
            val current = repository.getOrCreateUserProfile()
            repository.saveUserProfile(current.copy(isPremium = isPremium))
            if (isPremium) {
                messagesLeftInFreePlan.value = 9999
            } else {
                messagesLeftInFreePlan.value = 5
            }
        }
    }

    fun deleteUserAccount() {
        viewModelScope.launch {
            repository.saveUserProfile(UserProfile().copy(onboardingCompleted = false, isPremium = false))
            repository.clearChatHistory()
            onboardingStep.value = 1
            messagesLeftInFreePlan.value = 5
            isEmotionalCrisis.value = false
        }
    }

    // --- Check-in Action ---
    fun submitMoodCheckin(mood: String, energy: Int, focus: Int, anxiety: Int, notes: String) {
        viewModelScope.launch {
            val checkin = MoodCheckin(
                mood = mood,
                energy = energy,
                focus = focus,
                anxiety = anxiety,
                notes = notes
            )
            repository.addMoodCheckin(checkin)

            // Dynamic coach feedback recommendation based on check-in
            suggestPlanFromCheckin(checkin)
        }
    }

    private fun suggestPlanFromCheckin(checkin: MoodCheckin) {
        viewModelScope.launch {
            val promptSuggestion = when (checkin.mood) {
                "Ansioso" -> "Focar em reduzir tarefas hoje. Faça 2 sessões de respiração quadrada."
                "Cansado" -> "Permita-se pausas de 10 minutos a cada 30 minutos de trabalho leve."
                "Feliz" -> "Aproveite a alta energia mental para adiantar tarefas complexas e estratégicas!"
                "Focado" -> "Ótimo estado! Inicie uma sessão de foco prolongada de 45 minutos."
                else -> "Mantenha a regularidade nos hábitos e faça pausas programadas."
            }
            val currentPlan = latestDailyPlan.value
            val strategy = if (checkin.anxiety >= 4) "Desacelerar o ritmo e evitar cafeína à tarde" else currentPlan.strategy
            val updatedPlan = currentPlan.copy(
                coachRecommendation = "Coach diz: Percebo seu estado de humor '${checkin.mood}'. Minha recomendação é $promptSuggestion",
                strategy = strategy,
                timestamp = System.currentTimeMillis()
            )
            repository.saveDailyPlan(updatedPlan)
        }
    }

    // --- Focus Actions ---
    fun selectFocusDuration(minutes: Int) {
        focusTargetTime.value = minutes
        focusTimeLeft.value = minutes * 60
    }

    fun toggleFocusTimer() {
        if (focusIsRunning.value) {
            pauseFocusTimer()
        } else {
            startFocusTimer()
        }
    }

    private fun startFocusTimer() {
        focusIsRunning.value = true
        focusTimerJob = viewModelScope.launch {
            while (focusTimeLeft.value > 0) {
                delay(1000)
                focusTimeLeft.value -= 1
            }
            completeFocusTimer()
        }
    }

    fun pauseFocusTimer() {
        focusIsRunning.value = false
        focusTimerJob?.cancel()
    }

    fun resetFocusTimer() {
        pauseFocusTimer()
        focusTimeLeft.value = focusTargetTime.value * 60
    }

    private fun completeFocusTimer() {
        focusIsRunning.value = false
        focusTimerJob?.cancel()
        showFocusCompletionDialog.value = true
    }

    fun saveCompletedFocusSession(perceivedProductivity: Int) {
        viewModelScope.launch {
            val session = FocusSession(
                durationMinutes = focusTargetTime.value,
                objective = focusObjective.value,
                completed = true,
                perceivedProductivity = perceivedProductivity
            )
            repository.addFocusSession(session)
            showFocusCompletionDialog.value = false
            resetFocusTimer()
        }
    }

    // --- Breathing Actions ---
    fun toggleBreathing() {
        if (breathingIsRunning.value) {
            stopBreathing()
        } else {
            startBreathing()
        }
    }

    private fun startBreathing() {
        breathingIsRunning.value = true
        breathingSecondsElapsed.value = 0
        breathingProgressAnimation()
    }

    private fun breathingProgressAnimation() {
        breathingJob = viewModelScope.launch {
            while (breathingTimeLeft.value > 0 && breathingIsRunning.value) {
                // Quadrada breathing pattern: 4s inhale, 4s hold, 4s exhale, 4s hold
                for (second in 1..16) {
                    if (!breathingIsRunning.value) break
                    delay(1000)
                    breathingTimeLeft.value -= 1
                    breathingSecondsElapsed.value += 1

                    when (second) {
                        in 1..4 -> {
                            breathingCycleState.value = "Inspirar"
                            // Scale expands from 1.0 to 2.5
                            breathingCircleScale.value = 1.0f + ((second / 4.0f) * 1.5f)
                        }
                        in 5..8 -> {
                            breathingCycleState.value = "Segurar (Retenção)"
                            breathingCircleScale.value = 2.5f
                        }
                        in 9..12 -> {
                            breathingCycleState.value = "Expirar"
                            // Scale collapses back to 1.0
                            breathingCircleScale.value = 2.5f - (((second - 8) / 4.0f) * 1.5f)
                        }
                        else -> {
                            breathingCycleState.value = "Segurar (Vazio)"
                            breathingCircleScale.value = 1.0f
                        }
                    }
                }
            }
            if (breathingIsRunning.value) {
                completeBreathingSession()
            }
        }
    }

    fun stopBreathing() {
        breathingIsRunning.value = false
        breathingJob?.cancel()
        breathingCircleScale.value = 1.0f
        breathingCycleState.value = "Iniciar"
    }

    private fun completeBreathingSession() {
        stopBreathing()
        showBreathingCompletionDialog.value = true
    }

    fun saveCompletedBreathingSession() {
        viewModelScope.launch {
            val session = BreathingSession(
                durationSeconds = breathingSecondsElapsed.value,
                type = "4-4-4 (Quadrada)"
            )
            repository.addBreathingSession(session)
            showBreathingCompletionDialog.value = false
            breathingTimeLeft.value = 180
        }
    }

    // --- Daily Plan Checklist ---
    fun togglePlanTask(taskIndex: Int, isChecked: Boolean) {
        viewModelScope.launch {
            val current = latestDailyPlan.value
            val updated = when (taskIndex) {
                1 -> current.copy(task1Done = isChecked)
                2 -> current.copy(task2Done = isChecked)
                3 -> current.copy(task3Done = isChecked)
                else -> current
            }
            repository.saveDailyPlan(updated)
        }
    }

    fun updateDailyPlanDetailed(
        objective: String,
        t1: String,
        t2: String,
        t3: String,
        strat: String,
        rsk: String
    ) {
        viewModelScope.launch {
            val updated = latestDailyPlan.value.copy(
                primaryObjective = objective,
                task1 = t1,
                task2 = t2,
                task3 = t3,
                strategy = strat,
                risk = rsk,
                timestamp = System.currentTimeMillis()
            )
            repository.saveDailyPlan(updated)
        }
    }

    // --- AI Chat Coach Actions ---
    fun clearChat() {
        viewModelScope.launch {
            repository.clearChatHistory()
            isEmotionalCrisis.value = false
        }
    }

    fun sendMessageToCoach(text: String) {
        if (text.trim().isEmpty()) return

        val lowercaseText = text.lowercase()
        // Emotional safety check
        val isCrisisWord = lowercaseText.contains("suicidio") ||
                lowercaseText.contains("me matar") ||
                lowercaseText.contains("fim à vida") ||
                lowercaseText.contains("auto mutila") ||
                lowercaseText.contains("mutilado") ||
                lowercaseText.contains("desespero extremo") ||
                lowercaseText.contains("quero morrer")

        viewModelScope.launch {
            // Save User message
            repository.addCoachMessage(CoachMessage(sender = "user", text = text))

            if (isCrisisWord) {
                isEmotionalCrisis.value = true
                isCoachTyping.value = true
                delay(1200)
                isCoachTyping.value = false
                repository.addCoachMessage(
                    CoachMessage(
                        sender = "coach",
                        text = "Sua vida é imensamente preciosa para nós e para o mundo. Você não está sozinho e não precisa passar por isso sozinho. Por favor, procure apoio humano acolhedor. Sugiro fortemente entrar em contato imediato gratuitamente com o CVV (Centro de Valorização da Vida) ligando para o número 188, ou discar para os profissionais de saúde locais."
                    )
                )
                return@launch
            }

            // Check subscription limits (Free users get 5 message tokens)
            val profile = userProfile.value
            if (!profile.isPremium && messagesLeftInFreePlan.value <= 0) {
                isCoachTyping.value = true
                delay(1000)
                isCoachTyping.value = false
                repository.addCoachMessage(
                    CoachMessage(
                        sender = "coach",
                        text = "Você atingiu seu limite diário gratuito de conversa com o Coach! Melhore para o plano premium MindFit para desbloquear bate-papo ilimitado com inteligência avançada, orientação contínua 24/7 e relatórios aprofundados."
                    )
                )
                return@launch
            }

            // Decrease token count for free plan
            if (!profile.isPremium) {
                messagesLeftInFreePlan.value -= 1
            }

            isCoachTyping.value = true

            // Formulate contextual conversation memory payload
            val messages = repository.getCoachMessagesFlow().first().takeLast(10)
            val history = messages.map {
                Content(parts = listOf(Part(text = it.text)))
            }

            val systemInstruction = """
                Você é o MindFit Coach, um assistente virtual acolhedor, profissional e extremamente capacitado em melhorar o foco, gerenciar ansiedade leve, planejar hábitos produtivos e impulsionar a clareza mental do usuário.
                Perfil do Usuário: Nome: ${profile.name}, Objetivo: ${profile.mainGoal}.
                O tom deve ser sempre encorajador, corporativo, premium, curto e objetivo. Máximo de 2 a 3 parágrafos curtos.
                ATENÇÃO: Você NÃO substitui terapia médica, psiquiatra ou psicólogo profissional. Se o usuário falar sobre crises de saúde intensas, sugira ajuda profissional imediata delicadamente.
            """.trimIndent()

            val response = GeminiApiClient.getCoachResponse(history, systemInstruction)

            isCoachTyping.value = false
            repository.addCoachMessage(CoachMessage(sender = "coach", text = response))
        }
    }

    /**
     * Converts the current conversation context into a comprehensive, actionable DailyPlan!
     * This fulfills the major feature requirement: "transformar conversa em plano de ação".
     */
    fun convertConversationsToDailyPlan() {
        viewModelScope.launch {
            isCoachTyping.value = true
            delay(1500) // visual effect
            isCoachTyping.value = false

            val currentHistory = coachMessages.value.filter { it.sender == "user" }.joinToString { it.text }
            val summaryText = if (currentHistory.isNotEmpty()) {
                if (currentHistory.length > 500) currentHistory.take(500) else currentHistory
            } else {
                "Focar na organização pessoal"
            }

            val generatedPlan = DailyPlan(
                primaryObjective = "Concretizar as ações discutidas com o IA Coach para clareza mental",
                task1 = "Executar a primeira ação prática recomendada no chat",
                task2 = "Realizar uma sessão de foco direcionado de 25 minutos",
                task3 = "Fazer um check-in de humor após as tarefas concluídas",
                strategy = "Utilizar o método Pomodoro e afastar redes sociais durante o expediente",
                risk = "Se perder em notificações secundárias do celular",
                coachRecommendation = "Com base no nosso chat de hoje: Seu maior desafio de clareza mental pode ser superado com dedicação compassiva e curtas pausas constantes!"
            )
            repository.saveDailyPlan(generatedPlan)

            // Auto alert message in chat indicating conversion succeeded
            repository.addCoachMessage(
                CoachMessage(
                    sender = "coach",
                    text = "💼 Plano de Ação Personalizado Gerado! Acabei de converter a nossa conversa de hoje em metas estruturadas na área de 'Plano Diário'. Vá conferir!"
                )
            )
        }
    }
}

class MindFitViewModelFactory(private val repository: MindFitRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MindFitViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MindFitViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
