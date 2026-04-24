package com.financeia.data.db.dao

import androidx.room.*
import com.financeia.data.db.entities.Transaction
import com.financeia.data.db.entities.TransactionType
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY data DESC")
    fun getAllFlow(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE data LIKE :mesAno || '%' ORDER BY data DESC")
    fun getByMesFlow(mesAno: String): Flow<List<Transaction>>
    // mesAno ex: "2025-04"

    @Query("SELECT * FROM transactions WHERE categoria = :cat ORDER BY data DESC")
    fun getByCategoria(cat: String): Flow<List<Transaction>>

    @Query("SELECT COALESCE(SUM(valor),0) FROM transactions WHERE tipo = 'RECEITA' AND data LIKE :mesAno || '%'")
    suspend fun totalReceitas(mesAno: String): Double

    @Query("SELECT COALESCE(SUM(valor),0) FROM transactions WHERE tipo = 'DESPESA' AND data LIKE :mesAno || '%'")
    suspend fun totalDespesas(mesAno: String): Double

    @Query("SELECT categoria, SUM(valor) as total FROM transactions WHERE tipo = 'DESPESA' AND data LIKE :mesAno || '%' GROUP BY categoria ORDER BY total DESC")
    suspend fun despesasPorCategoria(mesAno: String): List<CategoriaTotal>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: Int): Transaction?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: Transaction): Long

    @Update
    suspend fun update(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Query("DELETE FROM transactions")
    suspend fun deleteAll()
}

data class CategoriaTotal(val categoria: String, val total: Double)
