package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "custom_parts")
data class CustomPartEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String = "Peças Customizadas",
    val vehicleType: String = "ALL", // "BICYCLE", "E_BIKE", "MOTORCYCLE", "CAR", "TRUCK", "ALL"
    val isPart: Boolean = true,
    val defaultMode: String = "TROCA", // TROCA, LIMPEZA, AJUSTE, MELHORIA, INSPECAO, LUBRIFICACAO
    val estimatedPrice: Double = 0.0,
    val defaultIntervalKm: Int = 10000,
    val defaultIntervalMonths: Int = 6,
    val brand: String = "",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
