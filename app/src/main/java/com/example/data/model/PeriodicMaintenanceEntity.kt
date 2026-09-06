package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "periodic_maintenances",
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
data class PeriodicMaintenanceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val vehicleId: Long,
    val title: String,
    val description: String = "",
    val intervalKm: Int = 10000,
    val intervalMonths: Int = 6,
    val lastPerformedKm: Int = 0,
    val lastPerformedDate: Long = System.currentTimeMillis(),
    val targetKm: Int = 10000,
    val targetDate: Long = System.currentTimeMillis() + (6L * 30 * 24 * 60 * 60 * 1000),
    val category: String = "Geral",
    val isCustom: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

enum class MaintenanceStatus {
    OK,       // Em dia
    ATTENTION,// Próximo do vencimento
    OVERDUE   // Vencido
}

fun calculateMaintenanceStatus(
    currentKm: Int,
    targetKm: Int,
    targetDate: Long,
    intervalKm: Int,
    intervalMonths: Int
): MaintenanceStatus {
    val now = System.currentTimeMillis()
    val kmDiff = targetKm - currentKm
    val daysDiff = (targetDate - now) / (1000 * 60 * 60 * 24)

    val kmOverdue = intervalKm > 0 && kmDiff <= 0
    val dateOverdue = intervalMonths > 0 && daysDiff <= 0

    if (kmOverdue || dateOverdue) {
        return MaintenanceStatus.OVERDUE
    }

    val kmAttentionThreshold = if (intervalKm > 0) (intervalKm * 0.15).coerceIn(300.0, 1500.0) else 0.0
    val kmAttention = intervalKm > 0 && kmDiff <= kmAttentionThreshold
    val dateAttention = intervalMonths > 0 && daysDiff <= 20

    if (kmAttention || dateAttention) {
        return MaintenanceStatus.ATTENTION
    }

    return MaintenanceStatus.OK
}
