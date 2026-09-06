package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class VehicleType(val label: String, val iconName: String) {
    CAR("Carro", "directions_car"),
    MOTORCYCLE("Moto", "two_wheeler"),
    TRUCK("Caminhão", "local_shipping"),
    E_BIKE("Bicicleta Elétrica", "electric_bike"),
    BICYCLE("Bicicleta", "pedal_bike"),
    OTHER("Outros", "commute")
}

@Entity(tableName = "vehicles")
data class VehicleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: String = VehicleType.CAR.name,
    val brand: String = "",
    val model: String = "",
    val year: Int = 2022,
    val plate: String = "",
    val currentKm: Int = 0,
    val fuelType: String = "Flex",
    val color: String = "Grafite",
    val nickname: String = "",
    val notes: String = "",
    val themeColorHex: String = "#E65100",
    val tankCapacityLiters: Double = 50.0,
    val currentFuelLiters: Double = 35.0,
    val fipeValue: Double = 0.0,
    val insuranceCompany: String = "",
    val insurancePolicyNumber: String = "",
    val insuranceEmergencyPhone: String = "",
    val ipvaPaid: Boolean = true,
    val licensingPaid: Boolean = true,
    val emergencyBloodType: String = "O+",
    val emergencyAllergies: String = "",
    val emergencyContactName: String = "",
    val emergencyContactPhone: String = "",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
