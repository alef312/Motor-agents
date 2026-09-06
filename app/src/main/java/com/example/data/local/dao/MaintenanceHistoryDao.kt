package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.MaintenanceHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MaintenanceHistoryDao {
    @Query("SELECT * FROM maintenance_history WHERE vehicleId = :vehicleId ORDER BY performedDate DESC, performedKm DESC")
    fun getHistoryByVehicle(vehicleId: Long): Flow<List<MaintenanceHistoryEntity>>

    @Query("SELECT * FROM maintenance_history ORDER BY performedDate DESC LIMIT 50")
    fun getAllHistory(): Flow<List<MaintenanceHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: MaintenanceHistoryEntity): Long

    @Delete
    suspend fun deleteHistory(history: MaintenanceHistoryEntity)
}
