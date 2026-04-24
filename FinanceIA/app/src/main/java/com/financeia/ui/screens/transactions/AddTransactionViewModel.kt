package com.financeia.ui.screens.transactions

import android.app.Application
import androidx.lifecycle.*
import com.financeia.FinanceApp
import com.financeia.data.db.entities.Transaction
import com.financeia.data.db.entities.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class AddTransactionState(
    val titulo: String    = "",
    val valor: String     = "",
    val tipo: TransactionType = TransactionType.DESPESA,
    val categoria: String = "Outros",
    val data: String      = LocalDate.now().toString(),
    val descricao: String = "",
    val recorrente: Boolean = false,
    val salvando: Boolean = false,
    val erro: String?     = null,
    val isEdicao: Boolean = false
)

class AddTransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = (application as FinanceApp).transactionRepo
    private val _state = MutableStateFlow(AddTransactionState())
    val state = _state.asStateFlow()

    fun carregar(id: Int) {
        if (id <= 0) return
        viewModelScope.launch {
            // carrega do banco para edição
            // (acesso direto via DAO)
            val db = (getApplication() as FinanceApp).database
            val t = db.transactionDao().getById(id) ?: return@launch
            _state.update { _ ->
                AddTransactionState(
                    titulo = t.titulo, valor = t.valor.toString(),
                    tipo = t.tipo, categoria = t.categoria,
                    data = t.data, descricao = t.descricao,
                    recorrente = t.recorrente, isEdicao = true
                )
            }
        }
    }

    fun setTitulo(v: String)     { _state.update { it.copy(titulo = v) } }
    fun setValor(v: String)      { _state.update { it.copy(valor = v) } }
    fun setTipo(v: TransactionType) { _state.update { it.copy(tipo = v) } }
    fun setCategoria(v: String)  { _state.update { it.copy(categoria = v) } }
    fun setData(v: String)       { _state.update { it.copy(data = v) } }
    fun setDescricao(v: String)  { _state.update { it.copy(descricao = v) } }
    fun setRecorrente(v: Boolean){ _state.update { it.copy(recorrente = v) } }

    fun salvar(onDone: () -> Unit) {
        val s = _state.value
        if (s.titulo.isBlank()) { _state.update { it.copy(erro = "Informe o título") }; return }
        val valorDouble = s.valor.replace(",", ".").toDoubleOrNull()
        if (valorDouble == null || valorDouble <= 0) {
            _state.update { it.copy(erro = "Valor inválido") }; return
        }
        viewModelScope.launch {
            _state.update { it.copy(salvando = true, erro = null) }
            repo.inserir(Transaction(
                titulo     = s.titulo,
                valor      = valorDouble,
                tipo       = s.tipo,
                categoria  = s.categoria,
                data       = s.data,
                descricao  = s.descricao,
                recorrente = s.recorrente
            ))
            onDone()
        }
    }
}
