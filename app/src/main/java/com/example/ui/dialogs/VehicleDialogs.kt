package com.example.ui.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Commute
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ElectricScooter
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.PedalBike
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VehicleEntity
import com.example.data.model.VehicleType
import com.example.ui.theme.parseHexColor

fun getVehicleCategoryIcon(type: String): ImageVector {
    return when (type) {
        VehicleType.MOTORCYCLE.name -> Icons.Default.TwoWheeler
        VehicleType.TRUCK.name -> Icons.Default.LocalShipping
        VehicleType.E_BIKE.name -> Icons.Default.ElectricScooter
        VehicleType.BICYCLE.name -> Icons.Default.PedalBike
        VehicleType.OTHER.name -> Icons.Default.Commute
        else -> Icons.Default.DirectionsCar
    }
}

fun getVehicleCategoryLabel(type: VehicleType): String {
    return when (type) {
        VehicleType.CAR -> "Carro"
        VehicleType.MOTORCYCLE -> "Moto"
        VehicleType.TRUCK -> "Caminhão"
        VehicleType.E_BIKE -> "Bike Elétrica"
        VehicleType.BICYCLE -> "Bicicleta"
        VehicleType.OTHER -> "Outro"
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditVehicleDialog(
    vehicleToEdit: VehicleEntity? = null,
    onDismiss: () -> Unit,
    onSave: (
        name: String,
        type: VehicleType,
        brand: String,
        model: String,
        year: Int,
        plate: String,
        currentKm: Int,
        fuelType: String,
        color: String,
        nickname: String,
        notes: String,
        themeColorHex: String,
        tankCapacityLiters: Double,
        fipeValue: Double,
        insuranceCompany: String,
        insuranceEmergencyPhone: String
    ) -> Unit
) {
    var selectedType by remember {
        mutableStateOf(
            if (vehicleToEdit != null) {
                try {
                    VehicleType.valueOf(vehicleToEdit.type)
                } catch (_: Exception) {
                    VehicleType.CAR
                }
            } else {
                VehicleType.CAR
            }
        )
    }
    var nickname by remember { mutableStateOf(vehicleToEdit?.nickname ?: "") }
    var name by remember { mutableStateOf(vehicleToEdit?.name ?: "") }
    var brand by remember { mutableStateOf(vehicleToEdit?.brand ?: "") }
    var model by remember { mutableStateOf(vehicleToEdit?.model ?: "") }
    var yearStr by remember { mutableStateOf(vehicleToEdit?.year?.toString() ?: "2023") }
    var plate by remember { mutableStateOf(vehicleToEdit?.plate ?: "") }
    var currentKmStr by remember { mutableStateOf(vehicleToEdit?.currentKm?.toString() ?: "0") }
    var fuelType by remember { mutableStateOf(vehicleToEdit?.fuelType ?: "Flex") }
    var color by remember { mutableStateOf(vehicleToEdit?.color ?: "Preto") }
    var themeColorHex by remember { mutableStateOf(vehicleToEdit?.themeColorHex ?: "#F59E0B") }
    var tankCapacityStr by remember { mutableStateOf((vehicleToEdit?.tankCapacityLiters ?: 50.0).toInt().toString()) }
    var fipeStr by remember { mutableStateOf(if (vehicleToEdit != null && vehicleToEdit.fipeValue > 0) vehicleToEdit.fipeValue.toInt().toString() else "85000") }
    var insuranceCompany by remember { mutableStateOf(vehicleToEdit?.insuranceCompany ?: "Porto Seguro Auto") }
    var insurancePhone by remember { mutableStateOf(vehicleToEdit?.insuranceEmergencyPhone ?: "0800-727-2766") }
    var notes by remember { mutableStateOf(vehicleToEdit?.notes ?: "") }

    val vehicleCategories = listOf(
        Pair(VehicleType.CAR, Icons.Default.DirectionsCar),
        Pair(VehicleType.MOTORCYCLE, Icons.Default.TwoWheeler),
        Pair(VehicleType.TRUCK, Icons.Default.LocalShipping),
        Pair(VehicleType.E_BIKE, Icons.Default.ElectricScooter),
        Pair(VehicleType.BICYCLE, Icons.Default.PedalBike),
        Pair(VehicleType.OTHER, Icons.Default.Commute)
    )

    val colorOptions = listOf(
        Pair("Laranja Turbo", "#F59E0B"),
        Pair("Azul Veloz", "#0284C7"),
        Pair("Vermelho Sport", "#DC2626"),
        Pair("Verde Elétrico", "#16A34A"),
        Pair("Roxo Neon", "#9333EA"),
        Pair("Dourado", "#D97706"),
        Pair("Prata / Chumbo", "#64748B")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (vehicleToEdit == null) "Novo Veículo na Garagem" else "Editar Veículo",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Categoria do Veículo:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )

                // 6 Vehicle Category options with icons
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    vehicleCategories.forEach { (type, icon) ->
                        val isSelected = selectedType == type
                        Surface(
                            onClick = { selectedType = type },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                            modifier = Modifier.testTag("select_category_${type.name}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = getVehicleCategoryLabel(type),
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = nickname,
                        onValueChange = { nickname = it },
                        label = { Text("Apelido (ex: Trovão)") },
                        placeholder = { Text("ex: FZ25 ou Branquinho") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = plate,
                        onValueChange = { plate = it.uppercase() },
                        label = { Text("Placa") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = brand,
                        onValueChange = { brand = it },
                        label = { Text("Marca (ex: Yamaha)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = model,
                        onValueChange = { model = it },
                        label = { Text("Modelo (ex: Fazer 250)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = yearStr,
                        onValueChange = { yearStr = it },
                        label = { Text("Ano") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = currentKmStr,
                        onValueChange = { currentKmStr = it.filter { ch -> ch.isDigit() } },
                        label = { Text("KM Atual") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = fuelType,
                        onValueChange = { fuelType = it },
                        label = { Text("Combustível") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = tankCapacityStr,
                        onValueChange = { tankCapacityStr = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Tanque (L)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                // Dynamic Material You vehicle theme color selection
                Text(
                    text = "Cor do Veículo & Dynamic Theme:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    colorOptions.take(5).forEach { (cName, hex) ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(parseHexColor(hex))
                                .border(
                                    width = if (themeColorHex == hex) 3.dp else 1.dp,
                                    color = if (themeColorHex == hex) Color.White else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { themeColorHex = hex }
                        )
                    }
                }

                OutlinedTextField(
                    value = fipeStr,
                    onValueChange = { fipeStr = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Valor Estimado FIPE (R$)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Anotações do Veículo") },
                    placeholder = { Text("ex: Manual do proprietário no porta-luvas, chave reserva...") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val yr = yearStr.toIntOrNull() ?: 2023
                    val km = currentKmStr.toIntOrNull() ?: 0
                    val tank = tankCapacityStr.toDoubleOrNull() ?: 50.0
                    val fipe = fipeStr.toDoubleOrNull() ?: 85000.0
                    val displayName = nickname.ifBlank { name.ifBlank { "$brand $model".trim() } }
                    onSave(
                        displayName,
                        selectedType,
                        brand,
                        model,
                        yr,
                        plate,
                        km,
                        fuelType,
                        color,
                        nickname,
                        notes,
                        themeColorHex,
                        tank,
                        fipe,
                        insuranceCompany,
                        insurancePhone
                    )
                },
                modifier = Modifier.testTag("save_vehicle_button")
            ) {
                Text("Salvar Veículo")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun UpdateKmDialog(
    currentKm: Int,
    vehicleName: String,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var kmInput by remember { mutableStateOf(currentKm.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Atualizar Odômetro") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Informe a quilometragem atual de $vehicleName para recalcular os prazos das revisões e desgaste de peças:",
                    style = MaterialTheme.typography.bodyMedium
                )
                OutlinedTextField(
                    value = kmInput,
                    onValueChange = { kmInput = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Novo Odômetro (KM)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("update_km_input"),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val km = kmInput.toIntOrNull() ?: currentKm
                    onConfirm(km)
                },
                modifier = Modifier.testTag("confirm_km_button")
            ) {
                Text("Atualizar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

