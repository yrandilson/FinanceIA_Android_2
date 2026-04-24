package com.financeia.ui.screens.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.financeia.data.db.entities.Transaction
import com.financeia.data.db.entities.TransactionType
import com.financeia.ui.screens.dashboard.TransacaoItem
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    onAddClick: () -> Unit,
    onEditClick: (Int) -> Unit,
    vm: TransactionsViewModel = viewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val fmt = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    val filtros = listOf("Todos", "Receitas", "Despesas")

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {

        // ── Top bar ──────────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Transações", style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold)
            FloatingActionButton(
                onClick = onAddClick,
                modifier = Modifier.size(40.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Filled.Add, null, modifier = Modifier.size(18.dp))
            }
        }

        // ── Busca ────────────────────────────────────────────────────────
        OutlinedTextField(
            value = state.busca,
            onValueChange = vm::setBusca,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            placeholder = { Text("Buscar transações...") },
            leadingIcon = { Icon(Icons.Filled.Search, null) },
            trailingIcon = {
                if (state.busca.isNotEmpty())
                    IconButton(onClick = { vm.setBusca("") }) {
                        Icon(Icons.Filled.Clear, null)
                    }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(Modifier.height(12.dp))

        // ── Filtros ──────────────────────────────────────────────────────
        Row(
            modifier = Modifier.padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filtros.forEach { f ->
                FilterChip(
                    selected = state.filtro == f,
                    onClick  = { vm.setFiltro(f) },
                    label    = { Text(f) }
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // ── Lista ────────────────────────────────────────────────────────
        if (state.carregando) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.lista.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🔍", fontSize = 40.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("Nenhuma transação encontrada",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn {
                items(
                    items = state.lista,
                    key   = { it.id }
                ) { transacao ->
                    SwipeToDeleteContainer(
                        item = transacao,
                        onDelete = { vm.deletar(transacao) }
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .clickable { onEditClick(transacao.id) }) {
                            TransacaoItem(transacao, fmt)
                        }
                    }
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SwipeToDeleteContainer(
    item: T,
    onDelete: () -> Unit,
    content: @Composable () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) { onDelete(); true }
            else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier.fillMaxSize()
                    .background(Color(0xFFEF4444))
                    .padding(end = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(Icons.Filled.Delete, null, tint = Color.White)
            }
        }
    ) { content() }
}
