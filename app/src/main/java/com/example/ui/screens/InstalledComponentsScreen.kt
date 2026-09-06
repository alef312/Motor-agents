package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ComponentWearLevel
import com.example.data.model.ComponentWearStatus
import com.example.data.model.InstalledComponentEntity
import com.example.data.model.VehicleEntity
import com.example.data.model.calculateComponentWear
import com.example.ui.theme.StatusAttention
import com.example.ui.theme.StatusOk
import com.example.ui.theme.StatusOverdue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun InstalledComponentsScreen(
    activeVehicle: VehicleEntity?,
    components: List<InstalledComponentEntity>,
    onAddComponent: (InstalledComponentEntity) -> Unit,
    onUpdateComponent: (InstalledComponentEntity) -> Unit,
    onDeleteComponent: (InstalledComponentEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    InstalledComponentsSection(
        activeVehicle = activeVehicle,
        components = components,
        onAddComponent = onAddComponent,
        onUpdateComponent = onUpdateComponent,
        onDeleteComponent = onDeleteComponent,
        modifier = modifier
    )
}

@Composable
fun InstalledComponentsSection(
    activeVehicle: VehicleEntity?,
    components: List<InstalledComponentEntity>,
    onAddComponent: (InstalledComponentEntity) -> Unit,
    onUpdateComponent: (InstalledComponentEntity) -> Unit,
    onDeleteComponent: (InstalledComponentEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategoryFilter by remember { mutableStateOf("Todas") }
    var showAddEditDialog by remember { mutableStateOf(false) }
    var editingComponent by remember { mutableStateOf<InstalledComponentEntity?>(null) }
    var componentToDelete by remember { mutableStateOf<InstalledComponentEntity?>(null) }

    val categories = listOf("Todas", "Pneus", "Freios", "Transmissão", "Motor", "Bateria & Elétrica", "Suspensão")
    val filteredList = if (selectedCategoryFilter == "Todas") {
        components
    } else {
        components.filter { it.category.contains(selectedCategoryFilter, ignoreCase = true) }
    }

    val currentKm = activeVehicle?.currentKm ?: 0

    Column(modifier = modifier.fillMaxSize()) {
        // Top action row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Componentes Ativos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${components.size} peças monitoradas individualmente",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = {
                    editingComponent = null
                    showAddEditDialog = true
                },
                modifier = Modifier.testTag("btn_add_component"),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Nova Peça", fontSize = 13.sp)
            }
        }

        // Category Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.take(4).forEach { cat ->
                FilterChip(
                    selected = selectedCategoryFilter == cat,
                    onClick = { selectedCategoryFilter = cat },
                    label = { Text(cat, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Build,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "Nenhum componente cadastrado",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Cadastre pneus, pastilhas, corrente ou bateria para acompanhar o desgaste exato.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredList, key = { it.id }) { comp ->
                    val wear = calculateComponentWear(
                        currentKm = currentKm,
                        installedKm = comp.installedKm,
                        installedDate = comp.installedDate,
                        lifespanKm = comp.estimatedLifespanKm,
                        lifespanMonths = comp.estimatedLifespanMonths
                    )

                    ComponentCard(
                        component = comp,
                        currentKm = currentKm,
                        wear = wear,
                        onEdit = {
                            editingComponent = comp
                            showAddEditDialog = true
                        },
                        onDelete = {
                            componentToDelete = comp
                        }
                    )
                }
            }
        }
    }

    if (showAddEditDialog && activeVehicle != null) {
        AddEditComponentDialog(
            currentVehicleKm = currentKm,
            existing = editingComponent,
            onDismiss = {
                showAddEditDialog = false
                editingComponent = null
            },
            onSave = { saved ->
                if (editingComponent == null) {
                    onAddComponent(saved.copy(vehicleId = activeVehicle.id))
                } else {
                    onUpdateComponent(saved.copy(vehicleId = activeVehicle.id))
                }
                showAddEditDialog = false
                editingComponent = null
            }
        )
    }

    if (componentToDelete != null) {
        AlertDialog(
            onDismissRequest = { componentToDelete = null },
            title = { Text("Excluir Componente") },
            text = { Text("Deseja realmente remover '${componentToDelete?.name}' da ficha técnica do veículo?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteComponent(componentToDelete!!)
                        componentToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                TextButton(onClick = { componentToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun ComponentCard(
    component: InstalledComponentEntity,
    currentKm: Int,
    wear: ComponentWearStatus,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val wearColor = when (wear.level) {
        ComponentWearLevel.NEW -> StatusOk
        ComponentWearLevel.NORMAL -> StatusOk
        ComponentWearLevel.HIGH -> StatusAttention
        ComponentWearLevel.CRITICAL -> StatusOverdue
    }

    val wearLabel = when (wear.level) {
        ComponentWearLevel.NEW -> "Novo"
        ComponentWearLevel.NORMAL -> "Meia-Vida"
        ComponentWearLevel.HIGH -> "Desgaste Alto"
        ComponentWearLevel.CRITICAL -> "Troca Recomendada"
    }

    val kmUsed = (currentKm - component.installedKm).coerceAtLeast(0)
    val percentageFormatted = (wear.wearPercentage * 100).toInt().coerceAtMost(100)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_component_${component.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = component.category,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        if (component.partNumber.isNotBlank()) {
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Cód: ${component.partNumber}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = component.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    if (component.brand.isNotBlank() || component.model.isNotBlank()) {
                        Text(
                            text = "${component.brand} ${component.model}".trim(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    color = wearColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, wearColor.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "$percentageFormatted% uso",
                        color = wearColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { wear.wearPercentage.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = wearColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(Modifier.height(8.dp))

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Rodado: $kmUsed km",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (wear.remainingKm > 0) "Resta: ~${wear.remainingKm} km" else "Vida útil atingida",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = if (wear.remainingKm <= 0) StatusOverdue else MaterialTheme.colorScheme.onSurface
                )
            }

            if (component.cost > 0.0) {
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Valor investido: R$ ${String.format(Locale.US, "%.2f", component.cost)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row {
                        IconButton(onClick = onEdit, modifier = Modifier.size(30.dp)) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar", modifier = Modifier.size(16.dp))
                        }
                        IconButton(onClick = onDelete, modifier = Modifier.size(30.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddEditComponentDialog(
    currentVehicleKm: Int,
    existing: InstalledComponentEntity?,
    onDismiss: () -> Unit,
    onSave: (InstalledComponentEntity) -> Unit
) {
    var name by remember { mutableStateOf(existing?.name ?: "") }
    var category by remember { mutableStateOf(existing?.category ?: "Pneus") }
    var brand by remember { mutableStateOf(existing?.brand ?: "") }
    var model by remember { mutableStateOf(existing?.model ?: "") }
    var partNumber by remember { mutableStateOf(existing?.partNumber ?: "") }
    var installedKm by remember { mutableStateOf((existing?.installedKm ?: currentVehicleKm).toString()) }
    var lifespanKm by remember { mutableStateOf((existing?.estimatedLifespanKm ?: 25000).toString()) }
    var lifespanMonths by remember { mutableStateOf((existing?.estimatedLifespanMonths ?: 24).toString()) }
    var cost by remember { mutableStateOf(if (existing != null && existing.cost > 0) existing.cost.toString() else "") }
    var notes by remember { mutableStateOf(existing?.notes ?: "") }

    val categories = listOf("Pneus", "Freios", "Transmissão", "Motor", "Bateria & Elétrica", "Suspensão", "Acessórios")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing == null) "Novo Componente" else "Editar Componente") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome da Peça *") },
                    placeholder = { Text("ex: Pneu Traseiro Michelin") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Category chips
                Text("Categoria:", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.take(3).forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 11.sp) }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = brand,
                        onValueChange = { brand = it },
                        label = { Text("Marca") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = model,
                        onValueChange = { model = it },
                        label = { Text("Modelo / Medida") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = partNumber,
                        onValueChange = { partNumber = it },
                        label = { Text("Código / Part Number") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = cost,
                        onValueChange = { cost = it },
                        label = { Text("Valor R$") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = installedKm,
                        onValueChange = { installedKm = it },
                        label = { Text("KM Instalação") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = lifespanKm,
                        onValueChange = { lifespanKm = it },
                        label = { Text("Vida útil (KM)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val comp = (existing ?: InstalledComponentEntity(
                            vehicleId = 0,
                            name = name,
                            category = category
                        )).copy(
                            name = name.trim(),
                            category = category.trim(),
                            brand = brand.trim(),
                            model = model.trim(),
                            partNumber = partNumber.trim(),
                            installedKm = installedKm.toIntOrNull() ?: currentVehicleKm,
                            estimatedLifespanKm = lifespanKm.toIntOrNull() ?: 20000,
                            estimatedLifespanMonths = lifespanMonths.toIntOrNull() ?: 24,
                            cost = cost.toDoubleOrNull() ?: 0.0,
                            notes = notes.trim()
                        )
                        onSave(comp)
                    }
                },
                enabled = name.isNotBlank()
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
