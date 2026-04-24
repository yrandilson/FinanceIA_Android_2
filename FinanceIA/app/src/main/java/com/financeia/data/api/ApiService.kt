package com.financeia.data.api

import com.financeia.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

// ─── Modelos de request/response ───────────────────────────────────────────

data class ChatMessage(val role: String, val content: String)

data class ChatRequest(
    val messages: List<ChatMessage>,
    val contexto_financeiro: ContextoFinanceiro? = null
)

data class ContextoFinanceiro(
    val saldo_atual: Double,
    val receitas_mes: Double,
    val despesas_mes: Double,
    val top_categorias: List<String>
)

data class ChatResponse(
    val resposta: String,
    val tokens_usados: Int = 0
)

data class AnaliseRequest(
    val extrato_texto: String,
    val mes_referencia: String
)

data class AnaliseResponse(
    val transacoes: List<TransacaoSugerida>,
    val resumo: String
)

data class TransacaoSugerida(
    val titulo: String,
    val valor: Double,
    val tipo: String,       // "RECEITA" ou "DESPESA"
    val categoria: String,
    val data: String
)

// ─── Interface Retrofit ─────────────────────────────────────────────────────

interface FinanceIAApi {

    @POST("api/chat")
    suspend fun chat(@Body body: ChatRequest): ChatResponse

    @POST("api/analisar-extrato")
    suspend fun analisarExtrato(@Body body: AnaliseRequest): AnaliseResponse

    @POST("api/insight-mensal")
    suspend fun insightMensal(@Body body: Map<String, Any>): ChatResponse
}

// ─── Singleton Retrofit ─────────────────────────────────────────────────────

object RetrofitClient {

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)   // LLM pode demorar
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val api: FinanceIAApi by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FinanceIAApi::class.java)
    }
}
