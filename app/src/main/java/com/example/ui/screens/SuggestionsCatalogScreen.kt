package com.example.ui.screens

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ElectricBike
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
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
import com.example.data.local.PresetSuggestions
import com.example.data.local.RevisionTemplate
import com.example.data.local.SuggestionItem
import com.example.data.model.CustomPartEntity
import com.example.data.model.InterventionType
import com.example.data.model.VehicleEntity
import com.example.data.model.VehicleType
import com.example.ui.components.formatCurrency
import com.example.ui.components.formatKm
import com.example.ui.theme.StatusAttention
import com.example.ui.theme.StatusOk
import com.example.ui.theme.TurboAmber
import com.example.ui.theme.TurboCyan

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SuggestionsCatalogScreen(
    activeVehicle: VehicleEntity?,
    customParts: List<CustomPartEntity> = emptyList(),
    onApplyTemplate: (RevisionTemplate) -> Unit,
    onApplySingleItem: (SuggestionItem) -> Unit,
    onSaveCustomPart: (CustomPartEntity) -> Unit = {},
    onDeleteCustomPart: (CustomPartEntity) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedVehicleFilter by remember {
        mutableStateOf(
            when (activeVehicle?.type) {
                VehicleType.MOTORCYCLE.name -> "MOTORCYCLE"
                VehicleType.BICYCLE.name -> "BICYCLE"
                VehicleType.E_BIKE.name -> "E_BIKE"
                VehicleType.TRUCK.name -> "TRUCK"
                else -> "CAR"
            }
        )
    }
    var selectedCategory by remember { mutableStateOf("Todas") }
    var selectedModeFilter by remember { mutableStateOf("TODOS") }

    // Dialog to add or customize a part
    var showCustomPartDialog by remember { mutableStateOf(false) }
    var prefilledCustomName by remember { mutableStateOf("") }

    val categories = listOf(
        "Todas",
        "Transmissão",
        "Freios",
        "Rodas & Pneus",
        "Suspensão",
        "Motor & Óleo",
        "Quadro & Cockpit",
        "Elétrica & Motor E-Bike",
        "Elétrica",
        "Freios Pneumáticos",
        "Suspensão & Rodagem",
        "Transmissão & Chassi",
        "Arrefecimento",
        "Climatização",
        "Acessórios",
        "Peças Customizadas"
    )

    // Merge presets with custom items
    val allCombinedItems = remember(customParts) {
        val convertedCustoms = customParts.map { cp ->
            SuggestionItem(
                name = cp.name,
                category = cp.category,
                isPart = cp.isPart,
                estimatedPrice = cp.estimatedPrice,
                forVehicleType = cp.vehicleType,
                defaultIntervalKm = cp.defaultIntervalKm,
                defaultIntervalMonths = cp.defaultIntervalMonths,
                description = cp.notes.ifBlank { "Peça customizada criada pelo usuário." },
                defaultMode = cp.defaultMode,
                isCustom = true
            )
        }
        convertedCustoms + PresetSuggestions.allSuggestions
    }

    // Filtered templates
    val relevantTemplates = remember(selectedVehicleFilter) {
        PresetSuggestions.templates.filter { t ->
            when (selectedVehicleFilter) {
                "CAR" -> t.vehicleType == VehicleType.CAR
                "MOTORCYCLE" -> t.vehicleType == VehicleType.MOTORCYCLE
                "BICYCLE" -> t.vehicleType == VehicleType.BICYCLE
                "E_BIKE" -> t.vehicleType == VehicleType.E_BIKE
                "TRUCK" -> t.vehicleType == VehicleType.TRUCK
                else -> true
            }
        }
    }

    // Filtered suggestions
    val filteredSuggestions = remember(selectedVehicleFilter, selectedCategory, selectedModeFilter, searchQuery, allCombinedItems) {
        allCombinedItems.filter { item ->
            val matchVehicle = selectedVehicleFilter == "ALL" ||
                    item.forVehicleType == "BOTH" ||
                    item.forVehicleType == "ALL" ||
                    item.forVehicleType == selectedVehicleFilter ||
                    (selectedVehicleFilter == "BICYCLE" && item.forVehicleType == "E_BIKE") ||
                    (selectedVehicleFilter == "E_BIKE" && item.forVehicleType == "BICYCLE")

            val matchCategory = selectedCategory == "Todas" ||
                    item.category.equals(selectedCategory, ignoreCase = true) ||
                    (selectedCategory == "Peças Customizadas" && item.isCustom)

            val matchMode = selectedModeFilter == "TODOS" ||
                    item.defaultMode.equals(selectedModeFilter, ignoreCase = true)

            val matchSearch = searchQuery.isBlank() ||
                    item.name.contains(searchQuery, ignoreCase = true) ||
                    item.category.contains(searchQuery, ignoreCase = true) ||
                    item.description.contains(searchQuery, ignoreCase = true)

            matchVehicle && matchCategory && matchMode && matchSearch
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("suggestions_catalog_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Catálogo de Peças & Revisões",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Bicicletas, motos, carros e caminhões com modo de intervenção",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Button(
                        onClick = {
                            prefilledCustomName = searchQuery
                            showCustomPartDialog = true
                        },
                        modifier = Modifier.testTag("btn_create_custom_part"),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Criar Peça", fontSize = 12.sp)
                    }
                }
            }
        }

        // Vehicle Category Horizontal Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedVehicleFilter == "BICYCLE",
                    onClick = { selectedVehicleFilter = "BICYCLE" },
                    leadingIcon = {
                        Icon(Icons.Default.DirectionsBike, contentDescription = null, modifier = Modifier.size(16.dp))
                    },
                    label = { Text("Bicicletas", fontSize = 12.sp) }
                )
                FilterChip(
                    selected = selectedVehicleFilter == "E_BIKE",
                    onClick = { selectedVehicleFilter = "E_BIKE" },
                    leadingIcon = {
                        Icon(Icons.Default.ElectricBike, contentDescription = null, modifier = Modifier.size(16.dp))
                    },
                    label = { Text("E-Bikes", fontSize = 12.sp) }
                )
                FilterChip(
                    selected = selectedVehicleFilter == "MOTORCYCLE",
                    onClick = { selectedVehicleFilter = "MOTORCYCLE" },
                    leadingIcon = {
                        Icon(Icons.Default.TwoWheeler, contentDescription = null, modifier = Modifier.size(16.dp))
                    },
                    label = { Text("Motos", fontSize = 12.sp) }
                )
                FilterChip(
                    selected = selectedVehicleFilter == "CAR",
                    onClick = { selectedVehicleFilter = "CAR" },
                    leadingIcon = {
                        Icon(Icons.Default.DirectionsCar, contentDescription = null, modifier = Modifier.size(16.dp))
                    },
                    label = { Text("Carros", fontSize = 12.sp) }
                )
                FilterChip(
                    selected = selectedVehicleFilter == "TRUCK",
                    onClick = { selectedVehicleFilter = "TRUCK" },
                    leadingIcon = {
                        Icon(Icons.Default.DirectionsBus, contentDescription = null, modifier = Modifier.size(16.dp))
                    },
                    label = { Text("Caminhões", fontSize = 12.sp) }
                )
                FilterChip(
                    selected = selectedVehicleFilter == "ALL",
                    onClick = { selectedVehicleFilter = "ALL" },
                    label = { Text("Todos", fontSize = 12.sp) }
                )
            }
        }

        // Intervention Mode Selector ("Modo: Limpeza, Troca, Ajuste, Melhoria...")
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Tipo de Ação / Intervenção:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = selectedModeFilter == "TODOS",
                        onClick = { selectedModeFilter = "TODOS" },
                        label = { Text("Todos", fontSize = 11.sp) }
                    )
                    InterventionType.values().forEach { mode ->
                        FilterChip(
                            selected = selectedModeFilter == mode.id,
                            onClick = { selectedModeFilter = mode.id },
                            label = {
                                Text("${mode.badgeEmoji} ${mode.shortLabelPt}", fontSize = 11.sp)
                            }
                        )
                    }
                }
            }
        }

        // Search Bar with Instant "Salvar como Customizada" Button
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Buscar peça, acessório ou serviço...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Limpar")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("catalog_search_input"),
                    singleLine = true
                )

                // Proactive inline action: Save typed term as custom part!
                if (searchQuery.isNotBlank()) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                prefilledCustomName = searchQuery.trim()
                                showCustomPartDialog = true
                            }
                            .testTag("btn_quick_save_custom_part")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("✨", fontSize = 16.sp)
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Salvar \"${searchQuery.trim()}\" como peça customizada",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Ficará salva no catálogo permanente para você usar sempre que quiser.",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // Subcategory Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 11.sp) }
                    )
                }
            }
        }

        // Recommended Revision Packages (Templates)
        if (relevantTemplates.isNotEmpty() && searchQuery.isBlank() && selectedCategory == "Todas") {
            item {
                Text(
                    text = "Pacotes Prontos de Revisão",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(relevantTemplates) { template ->
                TemplateCard(
                    template = template,
                    onApply = { onApplyTemplate(template) }
                )
            }

            item {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Catálogo de Itens Individuais (${filteredSuggestions.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Empty state
        if (filteredSuggestions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🔍", fontSize = 36.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Nenhuma peça encontrada",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Deseja salvar \"${searchQuery}\" como uma peça customizada no aplicativo?",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(14.dp))
                        Button(
                            onClick = {
                                prefilledCustomName = searchQuery
                                showCustomPartDialog = true
                            }
                        ) {
                            Text("Salvar como Peça Customizada")
                        }
                    }
                }
            }
        } else {
            items(filteredSuggestions, key = { "${it.name}_${it.category}_${it.isCustom}" }) { item ->
                CatalogItemCard(
                    item = item,
                    onApply = { onApplySingleItem(item) },
                    onDelete = if (item.isCustom) {
                        val matchingPart = customParts.firstOrNull { it.name.equals(item.name, ignoreCase = true) }
                        if (matchingPart != null) {
                            { onDeleteCustomPart(matchingPart) }
                        } else null
                    } else null
                )
            }
        }
    }

    // Modal to create/save a custom part
    if (showCustomPartDialog) {
        AddCustomPartDialog(
            initialName = prefilledCustomName,
            initialVehicleType = selectedVehicleFilter,
            onDismiss = { showCustomPartDialog = false },
            onSave = { newPart ->
                onSaveCustomPart(newPart)
                showCustomPartDialog = false
                searchQuery = ""
            }
        )
    }
}

@Composable
private fun TemplateCard(
    template: RevisionTemplate,
    onApply: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = template.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Pacote",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = template.description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Items list inside template
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                template.items.forEach { item ->
                    val mode = InterventionType.fromId(item.defaultMode)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${mode.badgeEmoji} ${item.name}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = formatCurrency(item.estimatedPrice),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val totalEst = template.items.sumOf { it.estimatedPrice }
                Column {
                    Text(
                        text = "Total Estimado: ${formatCurrency(totalEst)}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Ciclo: ${formatKm(template.intervalKm)} ou ${template.intervalMonths} meses",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = onApply,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Usar Pacote", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun CatalogItemCard(
    item: SuggestionItem,
    onApply: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
    val mode = InterventionType.fromId(item.defaultMode)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isCustom) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
            }
        ),
        border = if (item.isCustom) {
            BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
        } else null
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = item.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        if (item.isCustom) {
                            Spacer(Modifier.width(6.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "Personalizada",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Intervention Mode Badge
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "${mode.badgeEmoji} ${mode.shortLabelPt}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Text(
                            text = "${item.category} • ${if (item.isPart) "Peça" else "Mão de Obra"}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formatCurrency(item.estimatedPrice),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (onDelete != null) {
                        IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Excluir peça customizada",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            if (item.description.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = item.description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Intervalo sugerido: ${formatKm(item.defaultIntervalKm)} ou ${item.defaultIntervalMonths}m",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedButton(
                    onClick = onApply,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Adicionar", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun AddCustomPartDialog(
    initialName: String = "",
    initialVehicleType: String = "ALL",
    onDismiss: () -> Unit,
    onSave: (CustomPartEntity) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var category by remember { mutableStateOf("Peças Customizadas") }
    var vehicleType by remember { mutableStateOf(initialVehicleType) }
    var isPart by remember { mutableStateOf(true) }
    var selectedMode by remember { mutableStateOf("TROCA") }
    var priceStr by remember { mutableStateOf("") }
    var intervalKmStr by remember { mutableStateOf("10000") }
    var intervalMonthsStr by remember { mutableStateOf("6") }
    var notes by remember { mutableStateOf("") }

    val vehicleOptions = listOf(
        "ALL" to "Geral / Todos",
        "BICYCLE" to "Bicicleta",
        "E_BIKE" to "Bicicleta Elétrica",
        "MOTORCYCLE" to "Moto",
        "CAR" to "Carro",
        "TRUCK" to "Caminhão"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Salvar Peça Customizada", fontWeight = FontWeight.Bold)
                Text(
                    text = "Esta peça ficará disponível no seu catálogo para sempre.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome da Peça ou Serviço *") },
                    placeholder = { Text("ex: Vela de Iridium, Pastilha Dianteira...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_custom_part_name"),
                    singleLine = true
                )

                // Modo de Intervenção
                Text("Tipo de Intervenção:", style = MaterialTheme.typography.labelSmall)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    InterventionType.values().forEach { mode ->
                        FilterChip(
                            selected = selectedMode == mode.id,
                            onClick = { selectedMode = mode.id },
                            label = { Text("${mode.badgeEmoji} ${mode.shortLabelPt}", fontSize = 11.sp) }
                        )
                    }
                }

                // Tipo de Veículo
                Text("Aplicável a:", style = MaterialTheme.typography.labelSmall)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    vehicleOptions.forEach { (code, label) ->
                        FilterChip(
                            selected = vehicleType == code,
                            onClick = { vehicleType = code },
                            label = { Text(label, fontSize = 11.sp) }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Categoria") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = priceStr,
                        onValueChange = { priceStr = it },
                        label = { Text("Preço Estimado (R$)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isPart) "Tipo: Peça de Reposição" else "Tipo: Serviço / Mão de Obra",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Switch(checked = isPart, onCheckedChange = { isPart = it })
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val price = priceStr.replace(",", ".").toDoubleOrNull() ?: 0.0
                        val intKm = intervalKmStr.toIntOrNull() ?: 10000
                        val intMonths = intervalMonthsStr.toIntOrNull() ?: 6
                        onSave(
                            CustomPartEntity(
                                name = name.trim(),
                                category = category.trim().ifBlank { "Peças Customizadas" },
                                vehicleType = vehicleType,
                                isPart = isPart,
                                defaultMode = selectedMode,
                                estimatedPrice = price,
                                defaultIntervalKm = intKm,
                                defaultIntervalMonths = intMonths,
                                notes = notes.trim()
                            )
                        )
                    }
                },
                enabled = name.isNotBlank(),
                modifier = Modifier.testTag("btn_confirm_custom_part")
            ) {
                Text("Salvar Peça")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
