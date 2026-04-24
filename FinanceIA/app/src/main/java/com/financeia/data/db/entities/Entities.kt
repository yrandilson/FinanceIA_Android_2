package com.financeia.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

enum class TransactionType { RECEITA, DESPESA }

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titulo: String,
    val valor: Double,
    val tipo: TransactionType,
    val categoria: String,
    val data: String,           // ISO-8601: "2025-04-19"
    val descricao: String = "",
    val recorrente: Boolean = false
)

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titulo: String,
    val valorAlvo: Double,
    val valorAtual: Double = 0.0,
    val emoji: String = "🎯",
    val prazo: String,          // ISO-8601
    val concluida: Boolean = false
)

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nome: String,
    val emoji: String,
    val cor: String             // hex: "#FF5252"
)
