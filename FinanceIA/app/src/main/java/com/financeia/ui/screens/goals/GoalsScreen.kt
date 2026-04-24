package com.financeia.ui.screens.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.financeia.data.db.entities.Goal
import java.text.NumberFormat
import java.time.LocalDate
import java.util.Locale
import kotlin.math.min

@Composable
fun GoalsScreen(vm: GoalsViewModel = viewModel()) {
    val goals by vm.goals.collectAsStateWithLifecycle()
    val fmt = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    var showAddDialog by remember { mutableStateOf(false) }
    var goalToDeposit by remember { mutableStateOf<Goal?>(null) }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        LazyColumn(contentPadding = PaddingValues(20.dp, 16.dp, 20.dp, 80.dp)) {
            item {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Text("Metas", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    FloatingActionButton(onClick = { showAddDialog = true },
                        modifier = Modifier.size(40.dp),
                        containerColor = MaterialTheme.colorScheme.primary) {
                        Icon(Icons.Filled.Add, null, modifier = Modifier.size(18.dp))
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            if (goals.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(top = 60.dp), Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🎯", fontSize = 48.sp)
                            Spacer(Modifier.height(12.dp))
                            Text("Nenhuma meta criada", style = MaterialTheme.typography.titleMedium)
                            Text("Defina um objetivo financeiro",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(16.dp))
                            Button(onClick = { showAddDialog = true }) { Text("Criar meta") }
                        }
                    }
                }
            } else {
                items(goals, key = { it.id }) { goal ->
                    GoalCard(goal, fmt,
                        onDeposit = { goalToDeposit = goal },
                        onDelete  = { vm.deletar(goal) }
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }

    if (showAddDialog) {
        AddGoalDialog(
            onDismiss  = { showAddDialog = false },
            onConfirm  = { vm.inserir(it); showAddDialog = false }
        )
    }

    goalToDeposit?.let { g ->
        DepositDialog(
            goal      = g,
            fmt       = fmt,
            onDismiss = { goalToDeposit = null },
            onConfirm = { v -> vm.adicionarValor(g.id, v); goalToDeposit = null }
        )
    }
}

@Composable
fun GoalCard(goal: Goal, fmt: NumberFormat, onDeposit: () -> Unit, onDelete: () -> Unit) {
    val pct = min(1f, if (goal.valorAlvo > 0) (goal.valorAtual / goal.valorAlvo).toFloat() else 0f)
    val corBarra = when {
        goal.concluida -> Color(0xFF22C55E)
        pct >= 0.75f   -> Color(0xFF22C55E)
        pct >= 0.40f   -> Color(0xFFF59E0B)
        else           -> Color(0xFF3B82F6)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.Top) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(goal.emoji, fontSize = 24.sp)
                    Column {
                        Text(goal.titulo, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        Text("Prazo: ${goal.prazo}", style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                if (goal.concluida) {
                    Surface(color = Color(0xFF1A3A2A), shape = RoundedCornerShape(20.dp)) {
                        Text("✓ Concluída", modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall, color = Color(0xFF4ADE80))
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text(fmt.format(goal.valorAtual), style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold, color = corBarra)
                Text("de ${fmt.format(goal.valorAlvo)}", style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { pct },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = corBarra, trackColor = corBarra.copy(alpha = 0.15f)
            )

            Text("${(pct * 100).toInt()}% concluído", style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp))

            if (!goal.concluida) {
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDeposit, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Filled.Add, null, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Depositar")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Filled.Delete, null,
                            tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalDialog(onDismiss: () -> Unit, onConfirm: (Goal) -> Unit) {
    var titulo by remember { mutableStateOf("") }
    var valorAlvo by remember { mutableStateOf("") }
    var prazo by remember { mutableStateOf(LocalDate.now().plusMonths(6).toString()) }
    var emoji by remember { mutableStateOf("🎯") }
    val emojis = listOf("🎯","🏠","🚗","✈️","📱","💍","🎓","🏖️","💻","🏋️")

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(20.dp)) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Nova meta", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                // Emoji picker
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    emojis.forEach { e ->
                        Surface(
                            onClick = { emoji = e },
                            shape = RoundedCornerShape(8.dp),
                            color = if (emoji == e) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) { Text(e, fontSize = 16.sp) }
                        }
                    }
                }
                OutlinedTextField(value = titulo, onValueChange = { titulo = it },
                    label = { Text("Título da meta") }, modifier = Modifier.fillMaxWidth(),
                    singleLine = true)
                OutlinedTextField(value = valorAlvo, onValueChange = { valorAlvo = it },
                    label = { Text("Valor alvo (R$)") }, modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true)
                OutlinedTextField(value = prazo, onValueChange = { prazo = it },
                    label = { Text("Prazo (AAAA-MM-DD)") }, modifier = Modifier.fillMaxWidth(),
                    singleLine = true)
                Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancelar") }
                    Button(
                        onClick = {
                            val v = valorAlvo.replace(",", ".").toDoubleOrNull() ?: return@Button
                            onConfirm(Goal(titulo = titulo, valorAlvo = v, prazo = prazo, emoji = emoji))
                        },
                        modifier = Modifier.weight(1f),
                        enabled = titulo.isNotBlank() && valorAlvo.isNotBlank()
                    ) { Text("Criar") }
                }
            }
        }
    }
}

@Composable
fun DepositDialog(goal: Goal, fmt: NumberFormat, onDismiss: () -> Unit, onConfirm: (Double) -> Unit) {
    var valor by remember { mutableStateOf("") }
    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(20.dp)) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Depositar em \"${goal.titulo}\"", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                OutlinedTextField(value = valor, onValueChange = { valor = it },
                    label = { Text("Valor (R$)") }, modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true)
                Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancelar") }
                    Button(onClick = {
                        val v = valor.replace(",",".").toDoubleOrNull() ?: return@Button
                        onConfirm(v)
                    }, modifier = Modifier.weight(1f), enabled = valor.isNotBlank()) { Text("Depositar") }
                }
            }
        }
    }
}
