package com.financeia.ui.screens.transactions

import android.app.Application
import androidx.lifecycle.*
import com.financeia.FinanceApp
import com.financeia.data.db.entities.Transaction
import com.financeia.data.db.entities.TransactionType
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class TransactionsUiState(
    val lista: List<Transaction>     = emptyList(),
    val filtro: String               = "Todos",
    val busca: String                = "",
    val mesAno: String               = "",
    val carregando: Boolean          = true
)

class TransactionsViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = (application as FinanceApp).transactionRepo

    private val _filtro = MutableStateFlow("Todos")
    private val _busca  = MutableStateFlow("")
    private val _mesAno = MutableStateFlow(
        LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
    )

    val state: StateFlow<TransactionsUiState> = combine(
        repo.getAll(), _filtro, _busca, _mesAno
    ) { lista, filtro, busca, mes ->
        var result = lista
        if (filtro == "Receitas") result = result.filter { it.tipo == TransactionType.RECEITA }
        if (filtro == "Despesas") result = result.filter { it.tipo == TransactionType.DESPESA }
        if (busca.isNotBlank()) result = result.filter {
            it.titulo.contains(busca, ignoreCase = true) ||
            it.categoria.contains(busca, ignoreCase = true)
        }
        TransactionsUiState(lista = result, filtro = filtro, busca = busca,
            mesAno = mes, carregando = false)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000),
        TransactionsUiState())

    fun setFiltro(f: String) { _filtro.value = f }
    fun setBusca(b: String)  { _busca.value  = b }

    fun deletar(t: Transaction) {
        viewModelScope.launch { repo.deletar(t) }
    }
}
