package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.FuelEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FuelEntryDao {

    @Query("SELECT * FROM fuel_entries WHERE vehicleId = :vehicleId ORDER BY odometerKm DESC, date DESC")
    fun getFuelEntriesByVehicle(vehicleId: Long): Flow<List<FuelEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFuelEntry(entry: FuelEntryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFuelEntries(entries: List<FuelEntryEntity>)

    @Update
    suspend fun updateFuelEntry(entry: FuelEntryEntity)

    @Delete
    suspend fun deleteFuelEntry(entry: FuelEntryEntity)

    @Query("DELETE FROM fuel_entries WHERE vehicleId = :vehicleId")
    suspend fun deleteByVehicle(vehicleId: Long)
}
