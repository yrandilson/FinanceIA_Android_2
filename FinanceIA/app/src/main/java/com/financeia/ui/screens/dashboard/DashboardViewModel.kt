package com.financeia.ui.screens.dashboard

import android.app.Application
import androidx.lifecycle.*
import com.financeia.FinanceApp
import com.financeia.data.db.entities.Transaction
import com.financeia.data.repository.AIRepository
import com.financeia.data.repository.TransactionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class DashboardUiState(
    val mesAno: String            = "",
    val saldo: Double             = 0.0,
    val receitas: Double          = 0.0,
    val despesas: Double          = 0.0,
    val transacoesRecentes: List<Transaction> = emptyList(),
    val gastoPorCategoria: Map<String, Double> = emptyMap(),
    val insightIA: String         = "",
    val carregandoInsight: Boolean = false,
    val erro: String?             = null
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repo  = (application as FinanceApp).transactionRepo
    private val aiRepo = (application as FinanceApp).aiRepo

    private val _mesAno = MutableStateFlow(
        LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
    )

    private val _state = MutableStateFlow(DashboardUiState())
    val state: StateFlow<DashboardUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _mesAno.collectLatest { mes ->
                repo.getByMes(mes).collectLatest { lista ->
                    val receitas  = lista.filter { it.tipo.name == "RECEITA" }.sumOf { it.valor }
                    val despesas  = lista.filter { it.tipo.name == "DESPESA" }.sumOf { it.valor }
                    val porCat    = lista
                        .filter { it.tipo.name == "DESPESA" }
                        .groupBy { it.categoria }
                        .mapValues { (_, v) -> v.sumOf { it.valor } }
                        .entries.sortedByDescending { it.value }
                        .take(5)
                        .associate { it.key to it.value }

                    _state.update { s -> s.copy(
                        mesAno               = mes,
                        receitas             = receitas,
                        despesas             = despesas,
                        saldo                = receitas - despesas,
                        transacoesRecentes   = lista.take(5),
                        gastoPorCategoria    = porCat
                    )}
                }
            }
        }
    }

    fun mudarMes(meses: Int) {
        val atual = LocalDate.parse(_mesAno.value + "-01")
        _mesAno.value = atual.plusMonths(meses.toLong())
            .format(DateTimeFormatter.ofPattern("yyyy-MM"))
    }

    fun gerarInsightIA() {
        viewModelScope.launch {
            _state.update { it.copy(carregandoInsight = true, insightIA = "") }
            val s = _state.value
            val result = aiRepo.insightMensal(
                s.saldo, s.receitas, s.despesas,
                s.gastoPorCategoria.keys.toList()
            )
            when (result) {
                is AIRepository.AIResult.Success ->
                    _state.update { it.copy(insightIA = result.data.resposta, carregandoInsight = false) }
                is AIRepository.AIResult.Error ->
                    _state.update { it.copy(erro = result.message, carregandoInsight = false) }
            }
        }
    }
}
