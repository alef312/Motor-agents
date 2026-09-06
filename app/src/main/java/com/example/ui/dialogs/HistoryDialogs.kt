package com.example.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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

@Composable
fun AddManualHistoryDialog(
    currentVehicleKm: Int,
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        performedKm: Int,
        performedDate: Long,
        workshop: String,
        cost: Double,
        notes: String,
        itemsSummary: String
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var kmStr by remember { mutableStateOf(currentVehicleKm.toString()) }
    var workshop by remember { mutableStateOf("Oficina Mecânica") }
    var costStr by remember { mutableStateOf("") }
    var itemsSummary by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Adicionar Histórico de Manutenção", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título (ex: Troca de Bateria, Pneus)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("history_title_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = kmStr,
                    onValueChange = { kmStr = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Quilometragem (KM)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = workshop,
                    onValueChange = { workshop = it },
                    label = { Text("Oficina / Estabelecimento") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = costStr,
                    onValueChange = { costStr = it },
                    label = { Text("Valor Gasto (R$)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = itemsSummary,
                    onValueChange = { itemsSummary = it },
                    label = { Text("Peças e Serviços Utilizados") },
                    placeholder = { Text("ex: Bateria Heliar 60Ah, Mão de obra") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Observações") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val km = kmStr.toIntOrNull() ?: currentVehicleKm
                        val cost = costStr.replace(",", ".").toDoubleOrNull() ?: 0.0
                        onSave(
                            title,
                            km,
                            System.currentTimeMillis(),
                            workshop,
                            cost,
                            notes,
                            itemsSummary
                        )
                    }
                },
                modifier = Modifier.testTag("save_history_button")
            ) {
                Text("Salvar Registro")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
