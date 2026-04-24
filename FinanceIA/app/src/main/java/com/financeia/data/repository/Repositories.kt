package com.financeia.data.repository

import com.financeia.data.api.*
import com.financeia.data.db.dao.CategoriaTotal
import com.financeia.data.db.dao.GoalDao
import com.financeia.data.db.dao.TransactionDao
import com.financeia.data.db.entities.Goal
import com.financeia.data.db.entities.Transaction
import kotlinx.coroutines.flow.Flow

// ─── TransactionRepository ──────────────────────────────────────────────────

class TransactionRepository(private val dao: TransactionDao) {

    fun getAll(): Flow<List<Transaction>>               = dao.getAllFlow()
    fun getByMes(mesAno: String): Flow<List<Transaction>> = dao.getByMesFlow(mesAno)

    suspend fun totalReceitas(mesAno: String): Double  = dao.totalReceitas(mesAno)
    suspend fun totalDespesas(mesAno: String): Double  = dao.totalDespesas(mesAno)
    suspend fun saldoMes(mesAno: String): Double       = totalReceitas(mesAno) - totalDespesas(mesAno)

    suspend fun despesasPorCategoria(mesAno: String): List<CategoriaTotal> =
        dao.despesasPorCategoria(mesAno)

    suspend fun inserir(t: Transaction)  = dao.insert(t)
    suspend fun atualizar(t: Transaction) = dao.update(t)
    suspend fun deletar(t: Transaction)  = dao.delete(t)
}

// ─── GoalRepository ─────────────────────────────────────────────────────────

class GoalRepository(private val dao: GoalDao) {

    fun getAll(): Flow<List<Goal>>    = dao.getAllFlow()
    fun getAtivas(): Flow<List<Goal>> = dao.getActivasFlow()

    suspend fun inserir(g: Goal)          = dao.insert(g)
    suspend fun atualizar(g: Goal)        = dao.update(g)
    suspend fun deletar(g: Goal)          = dao.delete(g)
    suspend fun adicionarValor(id: Int, v: Double) = dao.adicionarValor(id, v)
    suspend fun concluir(id: Int)         = dao.concluir(id)
}

// ─── AIRepository ───────────────────────────────────────────────────────────

class AIRepository(private val api: FinanceIAApi = RetrofitClient.api) {

    sealed class AIResult<out T> {
        data class Success<T>(val data: T) : AIResult<T>()
        data class Error(val message: String) : AIResult<Nothing>()
    }

    suspend fun chat(
        messages: List<ChatMessage>,
        contexto: ContextoFinanceiro? = null
    ): AIResult<ChatResponse> = runCatching {
        api.chat(ChatRequest(messages, contexto))
    }.fold(
        onSuccess = { AIResult.Success(it) },
        onFailure = { AIResult.Error(it.message ?: "Erro desconhecido") }
    )

    suspend fun analisarExtrato(texto: String, mes: String): AIResult<AnaliseResponse> =
        runCatching {
            api.analisarExtrato(AnaliseRequest(texto, mes))
        }.fold(
            onSuccess = { AIResult.Success(it) },
            onFailure = { AIResult.Error(it.message ?: "Erro ao analisar extrato") }
        )

    suspend fun insightMensal(
        saldo: Double, receitas: Double, despesas: Double, categorias: List<String>
    ): AIResult<ChatResponse> = runCatching {
        api.insightMensal(mapOf(
            "saldo" to saldo,
            "receitas" to receitas,
            "despesas" to despesas,
            "top_categorias" to categorias
        ))
    }.fold(
        onSuccess = { AIResult.Success(it) },
        onFailure = { AIResult.Error(it.message ?: "Erro ao gerar insight") }
    )
}
