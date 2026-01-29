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
import retrofit2.http.GET
import retrofit2.http.POST

// Modelos de Resposta (Vindo do seu amigo)
data class FlashcardResponse(
    val id: String,
    val pergunta: String,
    val resposta: String,
    val dificuldade: String
)

data class DeckResponse(
    val id: String,
    val titulo: String,
    val flashcards: List<FlashcardResponse>
)

data class ListarDecksResponse(
    val decks: List<DeckResponse>
)

interface DeckApi {
    @GET("deck/listar")
    suspend fun listarDecks(): Response<ListarDecksResponse>

    @POST("deck/cadastrar")
    suspend fun createDeck(@Body request: DeckRequest): Response<Unit>
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
        answer = this.resposta,
        difficulty = this.dificuldade
    )
}
