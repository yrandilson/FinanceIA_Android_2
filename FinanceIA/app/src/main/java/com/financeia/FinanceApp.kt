package com.financeia

import android.app.Application
import com.financeia.data.db.AppDatabase
import com.financeia.data.repository.AIRepository
import com.financeia.data.repository.GoalRepository
import com.financeia.data.repository.TransactionRepository

class FinanceApp : Application() {

    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }

    val transactionRepo: TransactionRepository by lazy {
        TransactionRepository(database.transactionDao())
    }

    val goalRepo: GoalRepository by lazy {
        GoalRepository(database.goalDao())
    }

    val aiRepo: AIRepository by lazy {
        AIRepository()
    }
}
