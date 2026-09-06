package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.MaintenanceItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MaintenanceItemDao {
    @Query("SELECT * FROM maintenance_items WHERE maintenanceId = :maintenanceId ORDER BY id ASC")
    fun getItemsForMaintenance(maintenanceId: Long): Flow<List<MaintenanceItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: MaintenanceItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<MaintenanceItemEntity>)

    @Update
    suspend fun updateItem(item: MaintenanceItemEntity)

    @Delete
    suspend fun deleteItem(item: MaintenanceItemEntity)

    @Query("DELETE FROM maintenance_items WHERE maintenanceId = :maintenanceId")
    suspend fun deleteItemsForMaintenance(maintenanceId: Long)
}
