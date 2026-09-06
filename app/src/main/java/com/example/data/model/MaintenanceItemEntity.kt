package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "maintenance_items",
    foreignKeys = [
        ForeignKey(
            entity = PeriodicMaintenanceEntity::class,
            parentColumns = ["id"],
            childColumns = ["maintenanceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("maintenanceId")]
)
data class MaintenanceItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val maintenanceId: Long,
    val name: String,
    val category: String = "Peças",
    val isPart: Boolean = true, // true = Peça, false = Serviço / Mão de Obra
    val estimatedPrice: Double = 0.0,
    val isCompleted: Boolean = false
)
