package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.model.MaintenanceItemEntity
import com.example.data.model.PeriodicMaintenanceEntity
import kotlinx.coroutines.flow.Flow

data class MaintenanceWithItems(
    @androidx.room.Embedded val maintenance: PeriodicMaintenanceEntity,
    @androidx.room.Relation(
        parentColumn = "id",
        entityColumn = "maintenanceId"
    )
    val items: List<MaintenanceItemEntity>
)

@Dao
interface PeriodicMaintenanceDao {
    @Query("SELECT * FROM periodic_maintenances WHERE vehicleId = :vehicleId ORDER BY targetKm ASC, targetDate ASC")
    fun getMaintenancesByVehicle(vehicleId: Long): Flow<List<PeriodicMaintenanceEntity>>

    @Transaction
    @Query("SELECT * FROM periodic_maintenances WHERE vehicleId = :vehicleId ORDER BY targetKm ASC, targetDate ASC")
    fun getMaintenancesWithItemsByVehicle(vehicleId: Long): Flow<List<MaintenanceWithItems>>

    @Transaction
    @Query("SELECT * FROM periodic_maintenances WHERE id = :id LIMIT 1")
    fun getMaintenanceWithItemsById(id: Long): Flow<MaintenanceWithItems?>

    @Query("SELECT * FROM periodic_maintenances WHERE id = :id LIMIT 1")
    suspend fun getMaintenanceById(id: Long): PeriodicMaintenanceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaintenance(maintenance: PeriodicMaintenanceEntity): Long

    @Update
    suspend fun updateMaintenance(maintenance: PeriodicMaintenanceEntity)

    @Delete
    suspend fun deleteMaintenance(maintenance: PeriodicMaintenanceEntity)
}
