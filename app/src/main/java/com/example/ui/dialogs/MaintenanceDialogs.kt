package com.example.ui.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PresetSuggestions
import com.example.data.local.SuggestionItem
import com.example.data.model.CustomPartEntity
import com.example.data.model.InterventionType
import com.example.data.model.MaintenanceItemEntity
import com.example.data.model.PeriodicMaintenanceEntity
import com.example.data.model.VehicleType
import com.example.ui.components.formatCurrency

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateEditMaintenanceDialog(
    initialMaintenance: PeriodicMaintenanceEntity? = null,
    initialItems: List<MaintenanceItemEntity> = emptyList(),
    vehicleType: VehicleType,
    currentVehicleKm: Int,
    customParts: List<CustomPartEntity> = emptyList(),
    onSaveCustomPart: ((CustomPartEntity) -> Unit)? = null,
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        description: String,
        category: String,
        intervalKm: Int,
        intervalMonths: Int,
        lastKm: Int,
        lastDate: Long,
        items: List<MaintenanceItemEntity>
    ) -> Unit
) {
    var title by remember { mutableStateOf(initialMaintenance?.title ?: "") }
    var description by remember { mutableStateOf(initialMaintenance?.description ?: "") }
    var category by remember {
        mutableStateOf(
            initialMaintenance?.category ?: when (vehicleType) {
                VehicleType.BICYCLE, VehicleType.E_BIKE -> "Transmissão"
                VehicleType.TRUCK -> "Motor & Óleo"
                else -> "Motor & Óleo"
            }
        )
    }
    var intervalKmStr by remember {
        mutableStateOf(
            initialMaintenance?.intervalKm?.toString() ?: when (vehicleType) {
                VehicleType.BICYCLE -> "1500"
                VehicleType.E_BIKE -> "2500"
                VehicleType.MOTORCYCLE -> "3000"
                VehicleType.TRUCK -> "20000"
                else -> "10000"
            }
        )
    }
    var intervalMonthsStr by remember {
        mutableStateOf(initialMaintenance?.intervalMonths?.toString() ?: "6")
    }
    var lastKmStr by remember {
        mutableStateOf(initialMaintenance?.lastPerformedKm?.toString() ?: currentVehicleKm.toString())
    }

    val itemsList = remember {
        mutableStateListOf<MaintenanceItemEntity>().apply {
            addAll(initialItems)
        }
    }

    // Accordion / Picker for suggestions
    var showSuggestionsPicker by remember { mutableStateOf(false) }
    var suggestionSearchQuery by remember { mutableStateOf("") }
    var selectedSuggestionCategory by remember { mutableStateOf("Todas") }

    // Manual item dialog
    var showAddManualItemDialog by remember { mutableStateOf(false) }
    var manualItemName by remember { mutableStateOf("") }
    var manualItemIsPart by remember { mutableStateOf(true) }
    var manualItemPriceStr by remember { mutableStateOf("") }
    var manualItemMode by remember { mutableStateOf("TROCA") }
    var manualSaveAsCustom by remember { mutableStateOf(true) }

    // Adaptive categories per vehicle type
    val categories = remember(vehicleType) {
        when (vehicleType) {
            VehicleType.BICYCLE -> listOf(
                "Transmissão", "Freios", "Rodas & Pneus", "Suspensão", "Quadro & Cockpit", "Acessórios", "Geral"
            )
            VehicleType.E_BIKE -> listOf(
                "Elétrica & Motor E-Bike", "Transmissão", "Freios", "Rodas & Pneus", "Suspensão", "Quadro & Cockpit", "Acessórios", "Geral"
            )
            VehicleType.MOTORCYCLE -> listOf(
                "Motor & Óleo", "Transmissão", "Freios", "Suspensão", "Rodas & Pneus", "Elétrica", "Acessórios", "Geral"
            )
            VehicleType.TRUCK -> listOf(
                "Motor & Óleo", "Freios Pneumáticos", "Suspensão & Rodagem", "Transmissão & Chassi", "Elétrica", "Acessórios", "Geral"
            )
            else -> listOf(
                "Motor & Óleo", "Transmissão", "Freios", "Suspensão", "Arrefecimento", "Elétrica", "Climatização", "Acessórios", "Geral"
            )
        }
    }

    // Combined suggestions (Presets + Custom parts)
    val combinedSuggestions = remember(vehicleType, customParts) {
        val convertedCustoms = customParts.map { cp ->
            SuggestionItem(
                name = cp.name,
                category = cp.category,
                isPart = cp.isPart,
                estimatedPrice = cp.estimatedPrice,
                forVehicleType = cp.vehicleType,
                defaultIntervalKm = cp.defaultIntervalKm,
                defaultIntervalMonths = cp.defaultIntervalMonths,
                description = cp.notes.ifBlank { "Peça salva pelo usuário." },
                defaultMode = cp.defaultMode,
                isCustom = true
            )
        }

        val presets = PresetSuggestions.allSuggestions.filter { item ->
            when (vehicleType) {
                VehicleType.BICYCLE -> item.forVehicleType in listOf("BICYCLE", "ALL")
                VehicleType.E_BIKE -> item.forVehicleType in listOf("E_BIKE", "BICYCLE", "ALL")
                VehicleType.MOTORCYCLE -> item.forVehicleType in listOf("MOTORCYCLE", "BOTH", "ALL")
                VehicleType.CAR -> item.forVehicleType in listOf("CAR", "BOTH", "ALL")
                VehicleType.TRUCK -> item.forVehicleType in listOf("TRUCK", "ALL")
                else -> true
            }
        }

        convertedCustoms + presets
    }

    // Filtered by subcategory and search query
    val relevantSuggestions = remember(combinedSuggestions, selectedSuggestionCategory, suggestionSearchQuery) {
        combinedSuggestions.filter { item ->
            val matchCategory = selectedSuggestionCategory == "Todas" ||
                    item.category.equals(selectedSuggestionCategory, ignoreCase = true) ||
                    (selectedSuggestionCategory == "Peças Customizadas" && item.isCustom)

            val matchSearch = suggestionSearchQuery.isBlank() ||
                    item.name.contains(suggestionSearchQuery, ignoreCase = true) ||
                    item.category.contains(suggestionSearchQuery, ignoreCase = true) ||
                    item.description.contains(suggestionSearchQuery, ignoreCase = true)

            matchCategory && matchSearch
        }
    }

    val totalEstimated = itemsList.sumOf { it.estimatedPrice }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("create_edit_maintenance_dialog"),
        title = {
            Column {
                Text(
                    text = if (initialMaintenance == null) "Nova Revisão / Plano" else "Editar Revisão",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = "${vehicleType.name} • Defina intervalo e peças associadas",
                    fontSize = 12.sp,
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
                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título do Plano *") },
                    placeholder = {
                        Text(
                            when (vehicleType) {
                                VehicleType.BICYCLE -> "ex: Revisão Geral da Relação & Freios"
                                VehicleType.E_BIKE -> "ex: Manutenção Motor & Bateria E-Bike"
                                VehicleType.MOTORCYCLE -> "ex: Troca de Óleo & Kit Relação"
                                VehicleType.TRUCK -> "ex: Revisão Cárter & Freio Pneumático"
                                else -> "ex: Revisão Periódica dos 10.000 km"
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("maintenance_title_input"),
                    singleLine = true
                )

                // Category Chips
                Column {
                    Text(
                        text = "Categoria Principal:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        categories.forEach { cat ->
                            FilterChip(
                                selected = category == cat,
                                onClick = { category = cat },
                                label = { Text(cat, fontSize = 11.sp) }
                            )
                        }
                    }
                }

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Instruções e Observações") },
                    placeholder = { Text("ex: Usar óleo sintético original, checar folgas...") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )

                // Intervals Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = intervalKmStr,
                        onValueChange = { intervalKmStr = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Intervalo (KM)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = intervalMonthsStr,
                        onValueChange = { intervalMonthsStr = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Meses") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(0.8f),
                        singleLine = true
                    )
                }

                // Last execution KM
                OutlinedTextField(
                    value = lastKmStr,
                    onValueChange = { lastKmStr = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Odômetro da Última Execução (KM)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Divider()

                // Parts & Services Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Peças e Serviços (${itemsList.size})",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Orçamento estimado: ${formatCurrency(totalEstimated)}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Action buttons to add items
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { showSuggestionsPicker = !showSuggestionsPicker },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("add_suggestions_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            if (showSuggestionsPicker) "Fechar Catálogo" else "Ver Catálogo",
                            fontSize = 12.sp
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            manualItemName = ""
                            manualItemPriceStr = ""
                            manualItemMode = "TROCA"
                            manualSaveAsCustom = true
                            showAddManualItemDialog = true
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("add_manual_item_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Item Avulso", fontSize = 12.sp)
                    }
                }

                // Inline Suggestions & Parts Catalog Accordion
                AnimatedVisibility(visible = showSuggestionsPicker) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "💡 Toque nas peças para adicionar. Se não achar, digite e salve como customizada:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // Search inside catalog
                            OutlinedTextField(
                                value = suggestionSearchQuery,
                                onValueChange = { suggestionSearchQuery = it },
                                placeholder = { Text("Pesquisar peça (ex: vela, corrente...)", fontSize = 12.sp) },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                trailingIcon = {
                                    if (suggestionSearchQuery.isNotEmpty()) {
                                        IconButton(onClick = { suggestionSearchQuery = "" }) {
                                            Icon(Icons.Default.Clear, contentDescription = "Limpar", modifier = Modifier.size(16.dp))
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("dialog_suggestion_search"),
                                singleLine = true
                            )

                            // Quick action: Save typed term as custom part!
                            if (suggestionSearchQuery.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            manualItemName = suggestionSearchQuery.trim()
                                            manualItemPriceStr = ""
                                            manualItemMode = "TROCA"
                                            manualSaveAsCustom = true
                                            showAddManualItemDialog = true
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "✨ Salvar \"${suggestionSearchQuery.trim()}\" como customizada",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Sub-categories filter
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                (listOf("Todas") + categories + listOf("Peças Customizadas")).forEach { cat ->
                                    FilterChip(
                                        selected = selectedSuggestionCategory == cat,
                                        onClick = { selectedSuggestionCategory = cat },
                                        label = { Text(cat, fontSize = 10.sp) },
                                        modifier = Modifier.height(28.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Suggestions Grid
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                relevantSuggestions.take(30).forEach { item ->
                                    val alreadyAdded = itemsList.any { it.name.equals(item.name, ignoreCase = true) }
                                    val mode = InterventionType.fromId(item.defaultMode)

                                    Surface(
                                        color = if (alreadyAdded) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(
                                            1.dp,
                                            if (alreadyAdded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                                        ),
                                        modifier = Modifier.clickable {
                                            if (alreadyAdded) {
                                                itemsList.removeAll { it.name.equals(item.name, ignoreCase = true) }
                                            } else {
                                                itemsList.add(
                                                    MaintenanceItemEntity(
                                                        maintenanceId = 0,
                                                        name = "${mode.badgeEmoji} ${item.name}",
                                                        category = item.category,
                                                        isPart = item.isPart,
                                                        estimatedPrice = item.estimatedPrice
                                                    )
                                                )
                                            }
                                        }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "${mode.badgeEmoji} ${item.name}",
                                                fontSize = 11.sp,
                                                fontWeight = if (alreadyAdded) FontWeight.Bold else FontWeight.Normal,
                                                color = if (alreadyAdded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = formatCurrency(item.estimatedPrice),
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Added Items List
                if (itemsList.isEmpty()) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Nenhuma peça associada ainda. Toque em 'Ver Catálogo' ou adicione um 'Item Avulso'.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        itemsList.forEachIndexed { index, item ->
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Surface(
                                            color = if (item.isPart) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = if (item.isPart) "PEÇA" else "SERVIÇO",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (item.isPart) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = item.name,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Text(
                                                text = "${item.category} • ${formatCurrency(item.estimatedPrice)}",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    IconButton(
                                        onClick = { itemsList.removeAt(index) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remover",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val intKm = intervalKmStr.toIntOrNull() ?: 10000
                    val intMo = intervalMonthsStr.toIntOrNull() ?: 6
                    val lKm = lastKmStr.toIntOrNull() ?: currentVehicleKm
                    val lDate = initialMaintenance?.lastPerformedDate ?: System.currentTimeMillis()

                    onSave(
                        title.ifEmpty { "Revisão Periódica" },
                        description,
                        category,
                        intKm,
                        intMo,
                        lKm,
                        lDate,
                        itemsList.toList()
                    )
                },
                modifier = Modifier.testTag("save_maintenance_button")
            ) {
                Text("Salvar Revisão")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )

    // Sub-dialog to add custom item manually with mode & persistent custom save
    if (showAddManualItemDialog) {
        AlertDialog(
            onDismissRequest = { showAddManualItemDialog = false },
            title = {
                Column {
                    Text("Adicionar Peça ou Serviço", fontWeight = FontWeight.Bold)
                    Text(
                        text = "Informe os dados e salve como peça customizada se desejar.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = manualItemName,
                        onValueChange = { manualItemName = it },
                        label = { Text("Nome da Peça ou Serviço *") },
                        placeholder = { Text("ex: Vela de Iridium, Pastilha Dianteira...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("manual_item_name_input"),
                        singleLine = true
                    )

                    // Modo de Intervenção
                    Text("Modo da Ação:", style = MaterialTheme.typography.labelSmall)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        InterventionType.values().forEach { mode ->
                            FilterChip(
                                selected = manualItemMode == mode.id,
                                onClick = { manualItemMode = mode.id },
                                label = { Text("${mode.badgeEmoji} ${mode.shortLabelPt}", fontSize = 11.sp) }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (manualItemIsPart) "Tipo: Peça de Reposição" else "Tipo: Serviço / Mão de Obra",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Switch(
                            checked = manualItemIsPart,
                            onCheckedChange = { manualItemIsPart = it }
                        )
                    }

                    OutlinedTextField(
                        value = manualItemPriceStr,
                        onValueChange = { manualItemPriceStr = it },
                        label = { Text("Valor Estimado (R$)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("manual_item_price_input"),
                        singleLine = true
                    )

                    // Persistent custom part checkbox
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Salvar como peça customizada",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Ficará salva no app para usar sempre que precisar.",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = manualSaveAsCustom,
                                onCheckedChange = { manualSaveAsCustom = it }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val price = manualItemPriceStr.replace(",", ".").toDoubleOrNull() ?: 0.0
                        if (manualItemName.isNotBlank()) {
                            val mode = InterventionType.fromId(manualItemMode)
                            val cleanName = manualItemName.trim()

                            // Add to current maintenance revision
                            itemsList.add(
                                MaintenanceItemEntity(
                                    maintenanceId = 0,
                                    name = "${mode.badgeEmoji} $cleanName",
                                    category = category,
                                    isPart = manualItemIsPart,
                                    estimatedPrice = price
                                )
                            )

                            // Save as permanent custom part if option is checked!
                            if (manualSaveAsCustom) {
                                onSaveCustomPart?.invoke(
                                    CustomPartEntity(
                                        name = cleanName,
                                        category = category,
                                        vehicleType = vehicleType.name,
                                        isPart = manualItemIsPart,
                                        defaultMode = manualItemMode,
                                        estimatedPrice = price
                                    )
                                )
                            }

                            manualItemName = ""
                            manualItemPriceStr = ""
                            showAddManualItemDialog = false
                        }
                    },
                    modifier = Modifier.testTag("confirm_manual_item_button"),
                    enabled = manualItemName.isNotBlank()
                ) {
                    Text("Adicionar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddManualItemDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
