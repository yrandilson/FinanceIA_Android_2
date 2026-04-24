package com.financeia.ui.screens.chat

import android.app.Application
import androidx.lifecycle.*
import com.financeia.FinanceApp
import com.financeia.data.api.ChatMessage
import com.financeia.data.api.ContextoFinanceiro
import com.financeia.data.repository.AIRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class ChatUiMessage(
    val role: String,       // "user" ou "assistant"
    val content: String,
    val timestamp: String = ""
)

data class ChatUiState(
    val messages: List<ChatUiMessage> = listOf(
        ChatUiMessage("assistant",
            "Olá! 👋 Sou a FinanceIA, sua assistente financeira pessoal.\n\n" +
            "Posso te ajudar a:\n• Analisar seus gastos e receitas\n" +
            "• Dar dicas de economia\n• Explicar conceitos financeiros\n" +
            "• Ajudar a planejar suas metas\n\nO que você quer saber?")
    ),
    val inputTexto: String    = "",
    val digitando: Boolean    = false,
    val erro: String?         = null
)

class AIChatViewModel(application: Application) : AndroidViewModel(application) {

    private val aiRepo   = (application as FinanceApp).aiRepo
    private val txRepo   = (application as FinanceApp).transactionRepo
    private val _state   = MutableStateFlow(ChatUiState())
    val state: StateFlow<ChatUiState> = _state.asStateFlow()

    fun setInput(v: String) { _state.update { it.copy(inputTexto = v) } }

    fun enviar() {
        val texto = _state.value.inputTexto.trim()
        if (texto.isBlank() || _state.value.digitando) return

        val userMsg = ChatUiMessage("user", texto)
        _state.update { it.copy(
            messages   = it.messages + userMsg,
            inputTexto = "",
            digitando  = true,
            erro       = null
        )}

        viewModelScope.launch {
            val mesAtual = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
            val receitas = txRepo.totalReceitas(mesAtual)
            val despesas = txRepo.totalDespesas(mesAtual)
            val topCats  = txRepo.despesasPorCategoria(mesAtual)
                .take(3).map { it.categoria }

            val contexto = ContextoFinanceiro(
                saldo_atual   = receitas - despesas,
                receitas_mes  = receitas,
                despesas_mes  = despesas,
                top_categorias = topCats
            )

            // Monta histórico no formato da API
            val history = _state.value.messages.map {
                ChatMessage(it.role, it.content)
            }

            when (val result = aiRepo.chat(history, contexto)) {
                is AIRepository.AIResult.Success -> {
                    val resposta = ChatUiMessage("assistant", result.data.resposta)
                    _state.update { it.copy(messages = it.messages + resposta, digitando = false) }
                }
                is AIRepository.AIResult.Error -> {
                    _state.update { it.copy(
                        digitando = false,
                        erro = result.message
                    )}
                }
            }
        }
    }

    fun limparConversa() {
        _state.update { ChatUiState() }
    }
}
