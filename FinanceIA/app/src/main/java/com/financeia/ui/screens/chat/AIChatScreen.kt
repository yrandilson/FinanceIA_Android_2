package com.financeia.ui.screens.chat

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

val SUGESTOES = listOf(
    "Como economizar mais esse mês?",
    "Onde estou gastando mais?",
    "Como montar uma reserva de emergência?",
    "Explique CDB vs Tesouro Direto"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIChatScreen(vm: AIChatViewModel = viewModel()) {
    val state     by vm.state.collectAsStateWithLifecycle()
    val listState  = rememberLazyListState()
    val scope      = rememberCoroutineScope()

    // Auto-scroll para a última mensagem
    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {

        // ── Header ───────────────────────────────────────────────────────
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier.size(32.dp).clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.AutoAwesome, null,
                            tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    }
                    Column {
                        Text("FinanceIA", style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold)
                        Text("Assistente financeiro IA",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            },
            actions = {
                IconButton(onClick = vm::limparConversa) {
                    Icon(Icons.Filled.RestartAlt, "Limpar conversa")
                }
            }
        )

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

        // ── Mensagens ────────────────────────────────────────────────────
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.messages) { msg ->
                ChatBubble(msg)
            }

            // Sugestões iniciais
            if (state.messages.size == 1) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Sugestões:", style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        SUGESTOES.forEach { sugestao ->
                            SugestaoChip(sugestao) {
                                vm.setInput(sugestao)
                                vm.enviar()
                            }
                        }
                    }
                }
            }

            // Indicador "digitando"
            if (state.digitando) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(28.dp).clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.AutoAwesome, null,
                                tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                        }
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        Text("Analisando...", style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // ── Erro ─────────────────────────────────────────────────────────
        state.erro?.let {
            Surface(color = MaterialTheme.colorScheme.errorContainer) {
                Row(modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Warning, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                    Text("Backend offline. Verifique a conexão.",
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer)
                }
            }
        }

        // ── Input ────────────────────────────────────────────────────────
        Surface(
            tonalElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp, 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = state.inputTexto,
                    onValueChange = vm::setInput,
                    placeholder = { Text("Pergunte sobre suas finanças...") },
                    modifier = Modifier.weight(1f),
                    maxLines = 3,
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    )
                )
                FloatingActionButton(
                    onClick = vm::enviar,
                    modifier = Modifier.size(46.dp),
                    containerColor = if (state.inputTexto.isBlank() || state.digitando)
                        MaterialTheme.colorScheme.surfaceVariant
                    else MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Filled.Send, "Enviar",
                        tint = if (state.inputTexto.isBlank() || state.digitando)
                            MaterialTheme.colorScheme.onSurfaceVariant
                        else MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun ChatBubble(msg: ChatUiMessage) {
    val isUser = msg.role == "user"
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier.size(28.dp).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.AutoAwesome, null,
                    tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
            }
            Spacer(Modifier.width(6.dp))
        }

        Card(
            shape = RoundedCornerShape(
                topStart = 16.dp, topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) MaterialTheme.colorScheme.primary
                                 else MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = msg.content,
                modifier = Modifier.padding(12.dp, 10.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = if (isUser) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (isUser) {
            Spacer(Modifier.width(6.dp))
            Box(
                modifier = Modifier.size(28.dp).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Person, null,
                    tint = MaterialTheme.colorScheme.onSecondary, modifier = Modifier.size(14.dp))
            }
        }
    }
}

@Composable
fun SugestaoChip(texto: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(12.dp, 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Filled.Lightbulb, null,
                tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
            Text(texto, style = MaterialTheme.typography.bodySmall)
        }
    }
}
