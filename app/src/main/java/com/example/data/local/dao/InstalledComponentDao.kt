package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.InstalledComponentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InstalledComponentDao {

    @Query("SELECT * FROM installed_components WHERE vehicleId = :vehicleId ORDER BY installedDate DESC")
    fun getComponentsByVehicle(vehicleId: Long): Flow<List<InstalledComponentEntity>>

    @Query("SELECT * FROM installed_components WHERE id = :id")
    suspend fun getComponentById(id: Long): InstalledComponentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComponent(component: InstalledComponentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComponents(components: List<InstalledComponentEntity>)

    @Update
    suspend fun updateComponent(component: InstalledComponentEntity)

    @Delete
    suspend fun deleteComponent(component: InstalledComponentEntity)

    @Query("DELETE FROM installed_components WHERE vehicleId = :vehicleId")
    suspend fun deleteByVehicle(vehicleId: Long)
}
