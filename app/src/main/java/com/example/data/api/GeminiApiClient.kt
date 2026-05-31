package com.example.data.api

import com.example.BuildConfig
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class Content(
    val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class Part(
    val text: String? = null
)

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    val contents: List<Content>,
    val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    val candidates: List<Candidate>? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
    val content: Content? = null
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object GeminiApiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val service: GeminiApiService by lazy {
        retrofit.create(GeminiApiService::class.java)
    }

    /**
     * Executes a chat prompt against Gemini API.
     * Integrates emotional safety filters and falls back gracefully to a robust local
     * coach response if the API Key is empty, invalid, or during offline times.
     */
    suspend fun getCoachResponse(
        history: List<Content>,
        systemPrompt: String
    ): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY" || apiKey == "GEMINI_API_KEY") {
            // Graceful smart local emotional fallback
            return getLocalCoachFallback(history.lastOrNull()?.parts?.firstOrNull()?.text ?: "")
        }

        val request = GenerateContentRequest(
            contents = history,
            systemInstruction = Content(parts = listOf(Part(text = systemPrompt)))
        )

        return try {
            val response = service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                ?: "Desculpe, não consegui processar essa informação agora."
        } catch (e: Exception) {
            e.printStackTrace()
            getLocalCoachFallback(history.lastOrNull()?.parts?.firstOrNull()?.text ?: "")
        }
    }

    private fun getLocalCoachFallback(userMessage: String): String {
        val lowercase = userMessage.lowercase()
        return when {
            lowercase.contains("suicidio") || lowercase.contains("me matar") || lowercase.contains("fim à vida") || lowercase.contains("auto mutila") || lowercase.contains("mutilado") -> {
                "Sua vida é imensamente preciosa e você não precisa carregar esse peso sozinho. Por favor, procure apoio imediato. Ligue gratuitamente para o CVV (Centro de Valorização da Vida) no número 188 para conversar com um profissional acolhedor que está pronto para te apoiar agora."
            }
            lowercase.contains("ansia") || lowercase.contains("ansioso") || lowercase.contains("panico") || lowercase.contains("desespero") -> {
                "Entendo perfeitamente que a ansiedade parece sufocante agora. Sugiro iniciarmos uma pausa imediata. Vamos respirar devagar: inspire em 4 segundos, segure por 4, e expire em 4 segundos. Pode fazer isso 3 vezes? Escreva aqui como se sentiu após o exercício."
            }
            lowercase.contains("foco") || lowercase.contains("distra") || lowercase.contains("concentra") -> {
                "Manter a atenção hoje em dia é um grande desafio! Para resgatar seu foco agora: experimente definir apenas uma única tarefa principal para as próximas duas horas, desligue notificações e inicie nosso timer de 25 minutos. Vamos começar?"
            }
            lowercase.contains("desanim") || lowercase.contains("triste") || lowercase.contains("cansad") -> {
                "É perfeitamente normal se sentir esgotado ou desanimado. Permita-se descansar um pouco. No seu plano diário, reduza a pressão: faça uma pausa de 10 minutos agora para tomar uma água ou olhar pela janela. Estou aqui para criarmos um roteiro leve."
            }
            else -> {
                "Olá! Sou seu IA Coach da MindFit. Percebo seu empenho em buscar mais clareza mental e produtividade. O que está mais desafiador para você hoje: manter o foco em suas tarefas, controlar a inquietação mental ou organizar sua rotina diária? Me conte, e vamos estruturar um plano de ação curto e prático juntos!"
            }
        }
    }
}
