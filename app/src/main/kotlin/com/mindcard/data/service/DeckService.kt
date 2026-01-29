package com.mindcard.data.service

import com.mindcard.BuildConfig
import com.mindcard.data.model.DeckRequest
import com.mindcard.data.model.Mindcard
import com.mindcard.data.model.MindcardItem
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PATCH
import retrofit2.http.Path

// Modelos de Resposta
data class FlashcardResponse(
    val id: String,
    val pergunta: String,
    val resposta: String
)

data class DeckResponse(
    val id: String,
    val titulo: String,
    val usuarioId: String? = null,
    val dataCriacao: String? = null,
    val flashcards: List<FlashcardResponse>
)

// Modelos de Request para atualização
data class UpdateFlashcardRequest(
    val pergunta: String,
    val resposta: String
)

data class UpdateDeckRequest(
    val titulo: String,
    val novosFlashcards: List<NewFlashcardRequest> = emptyList()
)

data class NewFlashcardRequest(
    val pergunta: String,
    val resposta: String
)

interface DeckApi {
    @GET("deck/listar")
    suspend fun listarDecks(): Response<List<DeckResponse>>

    @POST("deck/cadastrar")
    suspend fun createDeck(@Body request: DeckRequest): Response<Unit>
    
    @DELETE("deck/deletar/{id}")
    suspend fun deleteDeck(@Path("id") id: String): Response<Unit>
    
    @PATCH("deck/atualizar/{id}")
    suspend fun updateDeck(@Path("id") id: String, @Body request: UpdateDeckRequest): Response<Unit>
    
    @DELETE("flashcard/deletar/{id}")
    suspend fun deleteFlashcard(@Path("id") id: String): Response<Unit>
    
    @PATCH("flashcard/atualizar/{id}")
    suspend fun updateFlashcard(@Path("id") id: String, @Body request: UpdateFlashcardRequest): Response<Unit>
}

class DeckService(private val tokenProvider: () -> String?) {
    
    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val token = tokenProvider()
        
        val request = if (token != null) {
            original.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            original
        }
        
        chain.proceed(request)
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val api: DeckApi = retrofit.create(DeckApi::class.java)
}

// Extensões para conversão
fun DeckResponse.toMindcard(): Mindcard {
    return Mindcard(
        id = this.id,
        title = this.titulo,
        category = "Geral",
        items = this.flashcards.map { it.toMindcardItem() }
    )
}

fun FlashcardResponse.toMindcardItem(): MindcardItem {
    return MindcardItem(
        id = this.id,
        question = this.pergunta,
        answer = this.resposta
    )
}
