package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "installed_components",
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
data class InstalledComponentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val vehicleId: Long,
    val name: String,
    val category: String, // Pneus, Freios, Motor, Transmissão, Elétrica/Bateria, Suspensão, Acessórios
    val brand: String = "",
    val model: String = "",
    val partNumber: String = "",
    val installedKm: Int = 0,
    val installedDate: Long = System.currentTimeMillis(),
    val estimatedLifespanKm: Int = 20000,
    val estimatedLifespanMonths: Int = 24,
    val cost: Double = 0.0,
    val photoUri: String = "",
    val notes: String = ""
)

enum class ComponentWearLevel {
    NEW,       // 0% - 30% wear
    NORMAL,    // 30% - 70% wear
    HIGH,      // 70% - 90% wear
    CRITICAL   // > 90% wear
}

data class ComponentWearStatus(
    val wearPercentage: Float, // 0.0f to 1.0f (or higher if overdue)
    val remainingKm: Int,
    val remainingDays: Int,
    val level: ComponentWearLevel
)

fun calculateComponentWear(
    currentKm: Int,
    installedKm: Int,
    installedDate: Long,
    lifespanKm: Int,
    lifespanMonths: Int
): ComponentWearStatus {
    val kmTraveled = (currentKm - installedKm).coerceAtLeast(0)
    val kmWear = if (lifespanKm > 0) kmTraveled.toFloat() / lifespanKm.toFloat() else 0f

    val now = System.currentTimeMillis()
    val daysElapsed = ((now - installedDate) / (1000L * 60 * 60 * 24)).coerceAtLeast(0L).toInt()
    val totalLifespanDays = (lifespanMonths * 30).coerceAtLeast(1)
    val timeWear = daysElapsed.toFloat() / totalLifespanDays.toFloat()

    val maxWear = maxOf(kmWear, timeWear)
    val remainingKm = if (lifespanKm > 0) (lifespanKm - kmTraveled) else 0
    val remainingDays = totalLifespanDays - daysElapsed

    val level = when {
        maxWear >= 0.90f -> ComponentWearLevel.CRITICAL
        maxWear >= 0.70f -> ComponentWearLevel.HIGH
        maxWear >= 0.30f -> ComponentWearLevel.NORMAL
        else -> ComponentWearLevel.NEW
    }

    return ComponentWearStatus(
        wearPercentage = maxWear.coerceAtLeast(0f),
        remainingKm = remainingKm,
        remainingDays = remainingDays,
        level = level
    )
}
