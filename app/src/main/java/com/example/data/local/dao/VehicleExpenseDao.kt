package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.VehicleExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleExpenseDao {

    @Query("SELECT * FROM vehicle_expenses WHERE vehicleId = :vehicleId ORDER BY date DESC")
    fun getExpensesByVehicle(vehicleId: Long): Flow<List<VehicleExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: VehicleExpenseEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpenses(expenses: List<VehicleExpenseEntity>)

    @Update
    suspend fun updateExpense(expense: VehicleExpenseEntity)

    @Delete
    suspend fun deleteExpense(expense: VehicleExpenseEntity)

    @Query("DELETE FROM vehicle_expenses WHERE vehicleId = :vehicleId")
    suspend fun deleteByVehicle(vehicleId: Long)
}
