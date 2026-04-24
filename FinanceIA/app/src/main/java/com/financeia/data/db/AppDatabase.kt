package com.financeia.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.financeia.data.db.dao.GoalDao
import com.financeia.data.db.dao.TransactionDao
import com.financeia.data.db.entities.Category
import com.financeia.data.db.entities.Goal
import com.financeia.data.db.entities.Transaction
import com.financeia.data.db.entities.TransactionType

@TypeConverters(Converters::class)
@Database(
    entities = [Transaction::class, Goal::class, Category::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun goalDao(): GoalDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "financeia.db"
                )
                    .addCallback(SeedCallback())
                    .build()
                    .also { INSTANCE = it }
            }
    }

    private class SeedCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            val cats = listOf(
                "('Alimentação','🍔','#EF4444')",
                "('Transporte','🚗','#F59E0B')",
                "('Moradia','🏠','#3B82F6')",
                "('Saúde','💊','#10B981')",
                "('Lazer','🎮','#8B5CF6')",
                "('Educação','📚','#06B6D4')",
                "('Salário','💰','#22C55E')",
                "('Freelance','💻','#84CC16')",
                "('Investimentos','📈','#6366F1')",
                "('Outros','📦','#94A3B8')"
            )
            cats.forEach { cat ->
                db.execSQL("INSERT INTO categories (nome, emoji, cor) VALUES $cat")
            }
        }
    }
}

class Converters {
    @TypeConverter
    fun fromType(v: TransactionType): String = v.name

    @TypeConverter
    fun toType(v: String): TransactionType = TransactionType.valueOf(v)
}
