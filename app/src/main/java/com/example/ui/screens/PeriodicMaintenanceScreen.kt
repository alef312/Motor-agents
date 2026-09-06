package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.dao.MaintenanceWithItems
import com.example.data.model.MaintenanceStatus
import com.example.data.model.PeriodicMaintenanceEntity
import com.example.data.model.VehicleEntity
import com.example.data.model.VehicleType
import com.example.data.model.calculateMaintenanceStatus
import com.example.ui.components.StatusBadge
import com.example.ui.components.formatCurrency
import com.example.ui.components.formatDate
import com.example.ui.components.formatKm
import com.example.ui.theme.StatusAttention
import com.example.ui.theme.StatusOk
import com.example.ui.theme.StatusOverdue

@Composable
fun PeriodicMaintenanceScreen(
    activeVehicle: VehicleEntity?,
    maintenances: List<MaintenanceWithItems>,
    onCreateRevision: () -> Unit,
    onEditRevision: (MaintenanceWithItems) -> Unit,
    onDeleteRevision: (PeriodicMaintenanceEntity) -> Unit,
    onCompleteRevision: (MaintenanceWithItems) -> Unit,
    onOpenSuggestions: () -> Unit
) {
    var selectedFilterTab by remember { mutableStateOf(0) } // 0: Todas, 1: Pendentes/Atenção, 2: Em Dia
    val currentKm = activeVehicle?.currentKm ?: 0

    val filteredList = remember(maintenances, selectedFilterTab, currentKm) {
        when (selectedFilterTab) {
            1 -> maintenances.filter {
                val s = calculateMaintenanceStatus(
                    currentKm = currentKm,
                    targetKm = it.maintenance.targetKm,
                    targetDate = it.maintenance.targetDate,
                    intervalKm = it.maintenance.intervalKm,
                    intervalMonths = it.maintenance.intervalMonths
                )
                s == MaintenanceStatus.OVERDUE || s == MaintenanceStatus.ATTENTION
            }
            2 -> maintenances.filter {
                val s = calculateMaintenanceStatus(
                    currentKm = currentKm,
                    targetKm = it.maintenance.targetKm,
                    targetDate = it.maintenance.targetDate,
                    intervalKm = it.maintenance.intervalKm,
                    intervalMonths = it.maintenance.intervalMonths
                )
                s == MaintenanceStatus.OK
            }
            else -> maintenances
        }
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCreateRevision,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Criar Revisão") },
                modifier = Modifier
                    .padding(bottom = 70.dp)
                    .testTag("create_revision_fab")
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("periodic_maintenance_screen")
        ) {
            // Vehicle Header Banner
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val isCar = activeVehicle?.type == VehicleType.CAR.name
                        Icon(
                            imageVector = if (isCar) Icons.Default.DirectionsCar else Icons.Default.TwoWheeler,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = activeVehicle?.name ?: "Veículo",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Odômetro: ${formatKm(currentKm)}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = onOpenSuggestions,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Sugestões", fontSize = 12.sp)
                    }
                }
            }

            // Tabs
            TabRow(
                selectedTabIndex = selectedFilterTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = selectedFilterTab == 0,
                    onClick = { selectedFilterTab = 0 },
                    text = { Text("Todas (${maintenances.size})", fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedFilterTab == 1,
                    onClick = { selectedFilterTab = 1 },
                    text = {
                        val urgentCount = maintenances.count {
                            val s = calculateMaintenanceStatus(
                                currentKm = currentKm,
                                targetKm = it.maintenance.targetKm,
                                targetDate = it.maintenance.targetDate,
                                intervalKm = it.maintenance.intervalKm,
                                intervalMonths = it.maintenance.intervalMonths
                            )
                            s != MaintenanceStatus.OK
                        }
                        Text("Pendentes ($urgentCount)", fontSize = 12.sp)
                    }
                )
                Tab(
                    selected = selectedFilterTab == 2,
                    onClick = { selectedFilterTab = 2 },
                    text = { Text("Em Dia", fontSize = 12.sp) }
                )
            }

            // List of Revisions
            if (filteredList.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Nenhuma revisão encontrada",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Crie sua própria revisão periódica com intervalo de km ou tempo, e inclua peças e serviços recomendados.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onCreateRevision,
                        modifier = Modifier.testTag("empty_create_revision_button")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Criar Nova Revisão")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(filteredList, key = { it.maintenance.id }) { item ->
                        RevisionCard(
                            maintenanceWithItems = item,
                            currentKm = currentKm,
                            onComplete = { onCompleteRevision(item) },
                            onEdit = { onEditRevision(item) },
                            onDelete = { onDeleteRevision(item.maintenance) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RevisionCard(
    maintenanceWithItems: MaintenanceWithItems,
    currentKm: Int,
    onComplete: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val m = maintenanceWithItems.maintenance
    val items = maintenanceWithItems.items
    val status = calculateMaintenanceStatus(
        currentKm = currentKm,
        targetKm = m.targetKm,
        targetDate = m.targetDate,
        intervalKm = m.intervalKm,
        intervalMonths = m.intervalMonths
    )

    var isExpanded by remember { mutableStateOf(false) }

    val totalBudget = items.sumOf { it.estimatedPrice }

    // Calculate progress in current cycle
    val cycleStartKm = m.lastPerformedKm
    val cycleEndKm = m.targetKm
    val progress = if (cycleEndKm > cycleStartKm && m.intervalKm > 0) {
        ((currentKm - cycleStartKm).toFloat() / (cycleEndKm - cycleStartKm).toFloat()).coerceIn(0f, 1f)
    } else 0f

    val kmRemaining = m.targetKm - currentKm

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("revision_card_${m.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            when (status) {
                MaintenanceStatus.OVERDUE -> StatusOverdue.copy(alpha = 0.5f)
                MaintenanceStatus.ATTENTION -> StatusAttention.copy(alpha = 0.5f)
                MaintenanceStatus.OK -> MaterialTheme.colorScheme.outlineVariant
            }
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Category and Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = m.category,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                StatusBadge(status = status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = m.title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (m.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = m.description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Criteria Grid (Interval & Target)
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Intervalo Configurado",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            val intervalText = buildString {
                                if (m.intervalKm > 0) append("A cada ${formatKm(m.intervalKm)}")
                                if (m.intervalKm > 0 && m.intervalMonths > 0) append(" ou ")
                                if (m.intervalMonths > 0) append("${m.intervalMonths} meses")
                            }
                            Text(
                                text = intervalText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Próxima Meta / Prazo",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (m.intervalKm > 0) "${formatKm(m.targetKm)} • ${formatDate(m.targetDate)}" else formatDate(m.targetDate),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (status == MaintenanceStatus.OVERDUE) StatusOverdue else MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    if (m.intervalKm > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp),
                            color = when (status) {
                                MaintenanceStatus.OVERDUE -> StatusOverdue
                                MaintenanceStatus.ATTENTION -> StatusAttention
                                MaintenanceStatus.OK -> StatusOk
                            },
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            strokeCap = StrokeCap.Round
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Última: ${formatKm(m.lastPerformedKm)}",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (kmRemaining <= 0) "Venceu há ${formatKm(-kmRemaining)}"
                                else "Restam ${formatKm(kmRemaining)}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (status == MaintenanceStatus.OVERDUE) StatusOverdue else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Expandable Parts & Services Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Peças e Serviços (${items.size}) • Est: ${formatCurrency(totalBudget)}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (items.isEmpty()) {
                        Text(
                            text = "Nenhuma peça/serviço associado a esta revisão.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        items.forEach { item ->
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            color = if (item.isPart) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer,
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = if (item.isPart) "Peça" else "Serviço",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = item.name,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }

                                    Text(
                                        text = formatCurrency(item.estimatedPrice),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onComplete,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("action_complete_${m.id}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (status == MaintenanceStatus.OVERDUE) StatusOverdue else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Registrar Realizada", fontSize = 12.sp)
                }

                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Excluir",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
