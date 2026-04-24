package com.financeia.data.db.dao

import androidx.room.*
import com.financeia.data.db.entities.Goal
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {

    @Query("SELECT * FROM goals ORDER BY concluida ASC, prazo ASC")
    fun getAllFlow(): Flow<List<Goal>>

    @Query("SELECT * FROM goals WHERE concluida = 0 ORDER BY prazo ASC")
    fun getActivasFlow(): Flow<List<Goal>>

    @Query("SELECT * FROM goals WHERE id = :id")
    suspend fun getById(id: Int): Goal?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: Goal): Long

    @Update
    suspend fun update(goal: Goal)

    @Delete
    suspend fun delete(goal: Goal)

    @Query("UPDATE goals SET valorAtual = valorAtual + :valor WHERE id = :id")
    suspend fun adicionarValor(id: Int, valor: Double)

    @Query("UPDATE goals SET concluida = 1 WHERE id = :id")
    suspend fun concluir(id: Int)
}
