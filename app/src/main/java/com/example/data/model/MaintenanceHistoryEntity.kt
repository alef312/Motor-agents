package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "maintenance_history",
    foreignKeys = [
        ForeignKey(
            entity = VehicleEntity::class,
            parentColumns = ["id"],
            childColumns = ["vehicleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("vehicleId")]
)
data class MaintenanceHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val vehicleId: Long,
    val maintenanceId: Long? = null,
    val title: String,
    val performedKm: Int,
    val performedDate: Long = System.currentTimeMillis(),
    val workshop: String = "Oficina Especializada",
    val totalCost: Double = 0.0,
    val notes: String = "",
    val itemsSummary: String = ""
)
