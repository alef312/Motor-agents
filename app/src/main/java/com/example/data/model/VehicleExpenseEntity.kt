package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class ExpenseCategory(val label: String, val isFine: Boolean = false) {
    IPVA("IPVA / Imposto"),
    LICENSING("Licenciamento"),
    INSURANCE("Seguro Automotivo"),
    FINE("Multa de Trânsito", isFine = true),
    TOLL("Pedágio"),
    PARKING("Estacionamento"),
    OTHER("Outros Custos")
}

@Entity(
    tableName = "vehicle_expenses",
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
data class VehicleExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val vehicleId: Long,
    val title: String,
    val category: String = ExpenseCategory.OTHER.name,
    val amount: Double,
    val date: Long = System.currentTimeMillis(),
    val dueDate: Long = 0L,
    val isPaid: Boolean = true,
    val cnhPoints: Int = 0, // Pontos na carteira para multas
    val notes: String = ""
)

data class TcoCalculation(
    val vehicleFipe: Double,
    val estimatedAnnualDepreciation: Double,
    val totalMaintenanceCost: Double,
    val totalFuelCost: Double,
    val totalExpensesCost: Double,
    val totalTco: Double,
    val monthlyEstimatedCost: Double,
    val costPerKm: Double
)
