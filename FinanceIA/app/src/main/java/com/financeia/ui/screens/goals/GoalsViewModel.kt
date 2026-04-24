package com.financeia.ui.screens.goals

import android.app.Application
import androidx.lifecycle.*
import com.financeia.FinanceApp
import com.financeia.data.db.entities.Goal
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class GoalsViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = (application as FinanceApp).goalRepo
    val goals: StateFlow<List<Goal>> = repo.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun inserir(g: Goal)          { viewModelScope.launch { repo.inserir(g) } }
    fun adicionarValor(id: Int, v: Double) { viewModelScope.launch { repo.adicionarValor(id, v) } }
    fun deletar(g: Goal)          { viewModelScope.launch { repo.deletar(g) } }
}
