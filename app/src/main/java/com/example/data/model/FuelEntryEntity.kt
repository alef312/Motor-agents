package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "fuel_entries",
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
data class FuelEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val vehicleId: Long,
    val date: Long = System.currentTimeMillis(),
    val odometerKm: Int,
    val liters: Double,
    val pricePerLiter: Double,
    val totalPrice: Double = liters * pricePerLiter,
    val fuelType: String = "Gasolina Comum", // Gasolina Comum, Gasolina Aditivada, Etanol, Diesel, Eletricidade
    val isFullTank: Boolean = true,
    val gasStation: String = "",
    val notes: String = ""
)

data class FuelConsumptionStats(
    val averageKmL: Double = 0.0,
    val costPerKm: Double = 0.0,
    val totalLiters: Double = 0.0,
    val totalCost: Double = 0.0,
    val lastPricePerLiter: Double = 0.0
)

fun calculateFuelStats(entries: List<FuelEntryEntity>): FuelConsumptionStats {
    if (entries.isEmpty()) return FuelConsumptionStats()
    val sorted = entries.sortedBy { it.odometerKm }
    val totalCost = sorted.sumOf { it.totalPrice }
    val totalLiters = sorted.sumOf { it.liters }
    val lastPrice = sorted.lastOrNull()?.pricePerLiter ?: 0.0

    if (sorted.size < 2) {
        return FuelConsumptionStats(
            averageKmL = 0.0,
            costPerKm = 0.0,
            totalLiters = totalLiters,
            totalCost = totalCost,
            lastPricePerLiter = lastPrice
        )
    }

    val minKm = sorted.first().odometerKm
    val maxKm = sorted.last().odometerKm
    val deltaKm = (maxKm - minKm).coerceAtLeast(0)

    // Sum liters consumed between first and last full tank
    val litersConsumed = sorted.drop(1).sumOf { it.liters }
    val costConsumed = sorted.drop(1).sumOf { it.totalPrice }

    val avgKmL = if (litersConsumed > 0 && deltaKm > 0) deltaKm.toDouble() / litersConsumed else 0.0
    val costPerKm = if (deltaKm > 0 && costConsumed > 0) costConsumed / deltaKm.toDouble() else 0.0

    return FuelConsumptionStats(
        averageKmL = avgKmL,
        costPerKm = costPerKm,
        totalLiters = totalLiters,
        totalCost = totalCost,
        lastPricePerLiter = lastPrice
    )
}
