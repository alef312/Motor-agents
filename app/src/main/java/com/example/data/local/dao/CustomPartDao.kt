package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CustomPartEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomPartDao {

    @Query("SELECT * FROM custom_parts ORDER BY createdAt DESC")
    fun getAllCustomParts(): Flow<List<CustomPartEntity>>

    @Query("SELECT * FROM custom_parts WHERE vehicleType = :vehicleType OR vehicleType = 'ALL' ORDER BY name ASC")
    fun getCustomPartsByVehicleType(vehicleType: String): Flow<List<CustomPartEntity>>

    @Query("SELECT * FROM custom_parts WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchCustomParts(query: String): Flow<List<CustomPartEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomPart(part: CustomPartEntity): Long

    @Update
    suspend fun updateCustomPart(part: CustomPartEntity)

    @Delete
    suspend fun deleteCustomPart(part: CustomPartEntity)
}
