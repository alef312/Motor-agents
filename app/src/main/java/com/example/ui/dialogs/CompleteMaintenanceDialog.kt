package com.example.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.dao.MaintenanceWithItems
import com.example.data.model.PeriodicMaintenanceEntity
import com.example.ui.components.formatCurrency
import com.example.ui.components.formatKm

@Composable
fun CompleteMaintenanceDialog(
    maintenanceWithItems: MaintenanceWithItems,
    currentVehicleKm: Int,
    onDismiss: () -> Unit,
    onConfirm: (
        performedKm: Int,
        performedDate: Long,
        workshop: String,
        cost: Double,
        notes: String,
        itemsSummary: String
    ) -> Unit
) {
    val m = maintenanceWithItems.maintenance
    val estimatedTotal = maintenanceWithItems.items.sumOf { it.estimatedPrice }

    var performedKmStr by remember {
        mutableStateOf(maxOf(currentVehicleKm, m.targetKm).toString())
    }
    var workshop by remember { mutableStateOf("Oficina Especializada") }
    var costStr by remember {
        mutableStateOf(if (estimatedTotal > 0) String.format("%.2f", estimatedTotal).replace(".", ",") else "")
    }
    var notes by remember { mutableStateOf("") }
    var itemsSummary by remember {
        mutableStateOf(maintenanceWithItems.items.joinToString(", ") { it.name })
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Registrar Revisão Realizada",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = m.title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    )
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
                        Text(
                            text = "ℹ️ Ao registrar, o ciclo avançará automaticamente para a próxima revisão:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        if (m.intervalKm > 0) {
                            Text(
                                text = "• Próxima meta: +${formatKm(m.intervalKm)}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        if (m.intervalMonths > 0) {
                            Text(
                                text = "• Próximo prazo: +${m.intervalMonths} meses",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = performedKmStr,
                    onValueChange = { performedKmStr = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Quilometragem Realizada (KM)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("performed_km_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = workshop,
                    onValueChange = { workshop = it },
                    label = { Text("Local / Oficina (ex: Garagem Própria, Bosch)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = costStr,
                    onValueChange = { costStr = it },
                    label = { Text("Custo Total Pago (R$)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("performed_cost_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = itemsSummary,
                    onValueChange = { itemsSummary = it },
                    label = { Text("Peças e Serviços Executados") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Observações adicionais / Garantia") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val pKm = performedKmStr.toIntOrNull() ?: currentVehicleKm
                    val cost = costStr.replace(",", ".").toDoubleOrNull() ?: 0.0
                    onConfirm(
                        pKm,
                        System.currentTimeMillis(),
                        workshop,
                        cost,
                        notes,
                        itemsSummary
                    )
                },
                modifier = Modifier.testTag("confirm_complete_maintenance_button")
            ) {
                Text("Confirmar Conclusão")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
