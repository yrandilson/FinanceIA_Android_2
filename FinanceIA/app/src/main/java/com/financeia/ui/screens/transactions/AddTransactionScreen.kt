package com.financeia.ui.screens.transactions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.financeia.data.db.entities.TransactionType

val CATEGORIAS = listOf(
    "Alimentação" to "🍔",
    "Transporte"  to "🚗",
    "Moradia"     to "🏠",
    "Saúde"       to "💊",
    "Lazer"       to "🎮",
    "Educação"    to "📚",
    "Salário"     to "💰",
    "Freelance"   to "💻",
    "Investimentos" to "📈",
    "Outros"      to "📦"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    transacaoId: Int,
    onDone: () -> Unit,
    vm: AddTransactionViewModel = viewModel()
) {
    LaunchedEffect(transacaoId) { vm.carregar(transacaoId) }
    val state by vm.state.collectAsStateWithLifecycle()
    var showCatDropdown by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (state.isEdicao) "Editar transação" else "Nova transação")
                },
                navigationIcon = {
                    IconButton(onClick = onDone) {
                        Icon(Icons.Filled.ArrowBack, "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            // ── Tipo (Receita / Despesa) ──────────────────────────────
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TipoButton(
                    label = "Despesa", emoji = "📤",
                    selected = state.tipo == TransactionType.DESPESA,
                    selectedColor = Color(0xFFEF4444),
                    modifier = Modifier.weight(1f),
                    onClick = { vm.setTipo(TransactionType.DESPESA) }
                )
                TipoButton(
                    label = "Receita", emoji = "📥",
                    selected = state.tipo == TransactionType.RECEITA,
                    selectedColor = Color(0xFF22C55E),
                    modifier = Modifier.weight(1f),
                    onClick = { vm.setTipo(TransactionType.RECEITA) }
                )
            }

            // ── Título ───────────────────────────────────────────────
            OutlinedTextField(
                value = state.titulo,
                onValueChange = vm::setTitulo,
                label = { Text("Título *") },
                placeholder = { Text("Ex: Supermercado, Salário...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // ── Valor ────────────────────────────────────────────────
            OutlinedTextField(
                value = state.valor,
                onValueChange = vm::setValor,
                label = { Text("Valor (R$) *") },
                placeholder = { Text("0,00") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                leadingIcon = { Text("R$", modifier = Modifier.padding(start = 4.dp)) },
                shape = RoundedCornerShape(12.dp)
            )

            // ── Categoria ────────────────────────────────────────────
            ExposedDropdownMenuBox(
                expanded = showCatDropdown,
                onExpandedChange = { showCatDropdown = it }
            ) {
                OutlinedTextField(
                    value = CATEGORIAS.find { it.first == state.categoria }
                        ?.let { "${it.second} ${it.first}" } ?: state.categoria,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoria") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(showCatDropdown) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = showCatDropdown,
                    onDismissRequest = { showCatDropdown = false }
                ) {
                    CATEGORIAS.forEach { (nome, emoji) ->
                        DropdownMenuItem(
                            text = { Text("$emoji  $nome") },
                            onClick = { vm.setCategoria(nome); showCatDropdown = false }
                        )
                    }
                }
            }

            // ── Data ─────────────────────────────────────────────────
            OutlinedTextField(
                value = state.data,
                onValueChange = vm::setData,
                label = { Text("Data (AAAA-MM-DD)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Filled.CalendarToday, null) },
                shape = RoundedCornerShape(12.dp)
            )

            // ── Descrição ────────────────────────────────────────────
            OutlinedTextField(
                value = state.descricao,
                onValueChange = vm::setDescricao,
                label = { Text("Descrição (opcional)") },
                modifier = Modifier.fillMaxWidth().height(80.dp),
                shape = RoundedCornerShape(12.dp)
            )

            // ── Recorrente ───────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Transação recorrente", style = MaterialTheme.typography.bodyMedium)
                    Text("Repete todo mês", style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(checked = state.recorrente, onCheckedChange = vm::setRecorrente)
            }

            // ── Erro ─────────────────────────────────────────────────
            state.erro?.let { erro ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF3A1A1A)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Error, null, tint = Color(0xFFF87171))
                        Text(erro, color = Color(0xFFF87171),
                            style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            // ── Botão salvar ─────────────────────────────────────────
            Button(
                onClick = { vm.salvar(onDone) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !state.salvando
            ) {
                if (state.salvando) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Filled.Check, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Salvar transação", fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun TipoButton(
    label: String, emoji: String, selected: Boolean,
    selectedColor: Color, modifier: Modifier, onClick: () -> Unit
) {
    val bg = if (selected) selectedColor.copy(alpha = 0.15f)
             else MaterialTheme.colorScheme.surfaceVariant
    val border = if (selected) selectedColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)

    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bg),
        border = androidx.compose.foundation.BorderStroke(
            if (selected) 1.5.dp else 0.5.dp, border
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(emoji)
            Spacer(Modifier.width(6.dp))
            Text(label, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (selected) selectedColor else MaterialTheme.colorScheme.onSurface)
        }
    }
}
