package com.financeia.ui.screens.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.financeia.data.db.entities.Transaction
import com.financeia.data.db.entities.TransactionType
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onAddTransaction: () -> Unit,
    onVerTodas: () -> Unit,
    vm: DashboardViewModel = viewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val fmt = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // ── Header com gradiente ──────────────────────────────────────────
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                Color.Transparent
                            )
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Olá 👋", style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("FinanceIA", style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold)
                        }
                        FloatingActionButton(
                            onClick = onAddTransaction,
                            containerColor = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(Icons.Filled.Add, "Adicionar", modifier = Modifier.size(20.dp))
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    // Navegação de mês
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { vm.mudarMes(-1) }) {
                            Icon(Icons.Filled.ChevronLeft, "Mês anterior")
                        }
                        Text(
                            text = formatarMesAno(state.mesAno),
                            style = MaterialTheme.typography.titleMedium
                        )
                        IconButton(onClick = { vm.mudarMes(1) }) {
                            Icon(Icons.Filled.ChevronRight, "Próximo mês")
                        }
                    }
                }
            }
        }

        // ── Cards de saldo ───────────────────────────────────────────────
        item {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Saldo principal
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Saldo", style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                        Spacer(Modifier.height(4.dp))
                        Text(
                            fmt.format(state.saldo),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Receitas
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1A3A2A)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Filled.TrendingUp, null,
                                tint = Color(0xFF4ADE80), modifier = Modifier.size(16.dp))
                            Column {
                                Text("Receitas", style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF4ADE80))
                                Text(fmt.format(state.receitas),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold, color = Color.White)
                            }
                        }
                    }
                    // Despesas
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF3A1A1A)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Filled.TrendingDown, null,
                                tint = Color(0xFFF87171), modifier = Modifier.size(16.dp))
                            Column {
                                Text("Despesas", style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFFF87171))
                                Text(fmt.format(state.despesas),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // ── Gráfico de categorias ────────────────────────────────────────
        if (state.gastoPorCategoria.isNotEmpty()) {
            item {
                Spacer(Modifier.height(20.dp))
                Card(
                    modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Despesas por categoria",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(12.dp))
                        val total = state.gastoPorCategoria.values.sum()
                        val cores = listOf(
                            Color(0xFFEF4444), Color(0xFFF59E0B), Color(0xFF3B82F6),
                            Color(0xFF10B981), Color(0xFF8B5CF6)
                        )
                        state.gastoPorCategoria.entries.forEachIndexed { i, (cat, val_) ->
                            val pct = if (total > 0) (val_ / total).toFloat() else 0f
                            val cor = cores.getOrElse(i) { Color.Gray }
                            CategoriaProgressRow(
                                nome = cat, valor = fmt.format(val_),
                                percentual = pct, cor = cor
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        }

        // ── Insight IA ───────────────────────────────────────────────────
        item {
            Spacer(Modifier.height(20.dp))
            Card(
                modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Filled.AutoAwesome, null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(18.dp))
                            Text("Insight IA", style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold)
                        }
                        if (!state.carregandoInsight) {
                            TextButton(onClick = vm::gerarInsightIA) {
                                Text(if (state.insightIA.isEmpty()) "Analisar" else "Atualizar",
                                    style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                    if (state.carregandoInsight) {
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            Text("Analisando seus dados...",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    AnimatedVisibility(
                        visible = state.insightIA.isNotEmpty(),
                        enter = fadeIn(tween(400))
                    ) {
                        Column {
                            Spacer(Modifier.height(8.dp))
                            Text(state.insightIA,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f))
                        }
                    }
                }
            }
        }

        // ── Transações recentes ──────────────────────────────────────────
        item {
            Spacer(Modifier.height(20.dp))
            Row(
                modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Recentes", style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold)
                TextButton(onClick = onVerTodas) { Text("Ver todas") }
            }
        }

        if (state.transacoesRecentes.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.padding(20.dp).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📭", fontSize = 32.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("Nenhuma transação ainda",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        TextButton(onClick = onAddTransaction) { Text("Adicionar primeira") }
                    }
                }
            }
        } else {
            items(state.transacoesRecentes) { t ->
                TransacaoItem(t, fmt)
            }
        }
    }
}

// ─── Componentes auxiliares ─────────────────────────────────────────────────

@Composable
fun CategoriaProgressRow(nome: String, valor: String, percentual: Float, cor: Color) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text(nome, style = MaterialTheme.typography.bodySmall)
            Text(valor, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
        }
        Spacer(Modifier.height(3.dp))
        LinearProgressIndicator(
            progress = { percentual },
            modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
            color = cor,
            trackColor = cor.copy(alpha = 0.15f)
        )
    }
}

@Composable
fun TransacaoItem(t: Transaction, fmt: NumberFormat) {
    val isReceita = t.tipo == TransactionType.RECEITA
    val cor = if (isReceita) Color(0xFF4ADE80) else Color(0xFFF87171)

    ListItem(
        headlineContent = { Text(t.titulo, style = MaterialTheme.typography.bodyMedium) },
        supportingContent = {
            Text("${t.categoria} · ${t.data.take(10)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        },
        trailingContent = {
            Text(
                "${if (isReceita) "+" else "-"} ${fmt.format(t.valor)}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = cor
            )
        },
        leadingContent = {
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape)
                    .background(cor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (isReceita) Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
                    null, tint = cor, modifier = Modifier.size(16.dp)
                )
            }
        }
    )
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 20.dp),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
    )
}

fun formatarMesAno(mesAno: String): String {
    if (mesAno.length < 7) return mesAno
    val meses = listOf("Jan","Fev","Mar","Abr","Mai","Jun",
        "Jul","Ago","Set","Out","Nov","Dez")
    return try {
        val mes = mesAno.substring(5, 7).toInt()
        val ano = mesAno.substring(0, 4)
        "${meses[mes - 1]} $ano"
    } catch (e: Exception) { mesAno }
}
