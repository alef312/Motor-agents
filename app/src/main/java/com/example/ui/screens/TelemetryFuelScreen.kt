package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import com.example.data.model.FuelConsumptionStats
import com.example.data.model.FuelEntryEntity
import com.example.data.model.VehicleEntity
import com.example.ui.theme.StatusAttention
import com.example.ui.theme.StatusOk
import com.example.ui.theme.StatusOverdue
import com.example.ui.theme.TurboAmber
import com.example.ui.theme.TurboCyan
import com.example.ui.viewmodel.TripMetrics
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TelemetryFuelScreen(
    activeVehicle: VehicleEntity?,
    isGpsTrackingActive: Boolean,
    currentSpeedKmh: Double,
    trip1: TripMetrics,
    trip2: TripMetrics,
    bluetoothDevice: String,
    fuelEntries: List<FuelEntryEntity>,
    fuelStats: FuelConsumptionStats,
    onToggleGpsTracking: () -> Unit,
    onResetTrip1: () -> Unit,
    onResetTrip2: () -> Unit,
    onAddFuelEntry: (odometerKm: Int, liters: Double, pricePerLiter: Double, fuelType: String, isFullTank: Boolean, gasStation: String, notes: String) -> Unit,
    onDeleteFuelEntry: (FuelEntryEntity) -> Unit,
    onUpdateKm: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSubTab by remember { mutableStateOf(0) } // 0: Telemetria & GPS, 1: Abastecimento & Consumo, 2: Calculadoras
    var showAddFuelDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        // Tab selector
        TabRow(
            selectedTabIndex = selectedSubTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(
                selected = selectedSubTab == 0,
                onClick = { selectedSubTab = 0 },
                text = { Text("Trip & GPS", fontSize = 13.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedSubTab == 1,
                onClick = { selectedSubTab = 1 },
                text = { Text("Abastecer & Tanque", fontSize = 13.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedSubTab == 2,
                onClick = { selectedSubTab = 2 },
                text = { Text("Calculadoras", fontSize = 13.sp, fontWeight = FontWeight.Bold) }
            )
        }

        when (selectedSubTab) {
            0 -> TelemetryGpsTab(
                activeVehicle = activeVehicle,
                isGpsTrackingActive = isGpsTrackingActive,
                currentSpeedKmh = currentSpeedKmh,
                trip1 = trip1,
                trip2 = trip2,
                bluetoothDevice = bluetoothDevice,
                onToggleGpsTracking = onToggleGpsTracking,
                onResetTrip1 = onResetTrip1,
                onResetTrip2 = onResetTrip2
            )
            1 -> FuelManagementTab(
                activeVehicle = activeVehicle,
                fuelEntries = fuelEntries,
                fuelStats = fuelStats,
                onOpenAddFuel = { showAddFuelDialog = true },
                onDeleteFuel = onDeleteFuelEntry
            )
            2 -> CalculatorsTab(activeVehicle = activeVehicle)
        }
    }

    if (showAddFuelDialog && activeVehicle != null) {
        AddFuelEntryDialog(
            currentKm = activeVehicle.currentKm,
            defaultFuel = activeVehicle.fuelType,
            onDismiss = { showAddFuelDialog = false },
            onSave = { km, liters, price, fuelType, isFull, station, notes ->
                onAddFuelEntry(km, liters, price, fuelType, isFull, station, notes)
                showAddFuelDialog = false
            }
        )
    }
}

@Composable
fun TelemetryGpsTab(
    activeVehicle: VehicleEntity?,
    isGpsTrackingActive: Boolean,
    currentSpeedKmh: Double,
    trip1: TripMetrics,
    trip2: TripMetrics,
    bluetoothDevice: String,
    onToggleGpsTracking: () -> Unit,
    onResetTrip1: () -> Unit,
    onResetTrip2: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // GPS Tracking Control Card
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isGpsTrackingActive)
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                    else
                        MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isGpsTrackingActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(if (isGpsTrackingActive) StatusOk else StatusAttention)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = if (isGpsTrackingActive) "Rastreamento GPS ATIVO" else "Odômetro GPS Parado",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isGpsTrackingActive) StatusOk else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Button(
                            onClick = onToggleGpsTracking,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isGpsTrackingActive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.testTag("btn_toggle_gps")
                        ) {
                            Icon(
                                if (isGpsTrackingActive) Icons.Default.Stop else Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(if (isGpsTrackingActive) "Pausar" else "Iniciar")
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Instant speed HUD
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = String.format(Locale.US, "%.0f", currentSpeedKmh),
                            fontSize = 64.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "km/h",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    Text(
                        text = "Odômetro do veículo: ${activeVehicle?.currentKm ?: 0} km (atualiza automaticamente conforme você roda)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Trip 1 Card
        item {
            TripCard(
                title = "Trip 1 (Parcial Viagem)",
                metrics = trip1,
                onReset = onResetTrip1,
                badgeColor = TurboAmber
            )
        }

        // Trip 2 Card
        item {
            TripCard(
                title = "Trip 2 (Tanque / Mensal)",
                metrics = trip2,
                onReset = onResetTrip2,
                badgeColor = TurboCyan
            )
        }

        // Connectivity Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Bluetooth, contentDescription = null, tint = TurboCyan)
                        Spacer(Modifier.width(8.dp))
                        Text("Conexão com o Veículo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }

                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "Dispositivo emparelhado: $bluetoothDevice",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Quando conectado ao Bluetooth do som ou intercomunicador, o app pode iniciar o odômetro automaticamente.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Iniciar odômetro ao conectar Bluetooth", style = MaterialTheme.typography.bodySmall)
                        Switch(checked = true, onCheckedChange = {})
                    }
                }
            }
        }
    }
}

@Composable
fun TripCard(
    title: String,
    metrics: TripMetrics,
    onReset: () -> Unit,
    badgeColor: Color
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(badgeColor)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }

                TextButton(
                    onClick = onReset,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Zerar", fontSize = 12.sp)
                }
            }

            Spacer(Modifier.height(12.dp))

            // Large distance metric
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = String.format(Locale.US, "%.1f", metrics.distanceKm),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "km",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            Spacer(Modifier.height(12.dp))

            // Sub metrics grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val hours = metrics.movingTimeSeconds / 3600
                val minutes = (metrics.movingTimeSeconds % 3600) / 60
                val seconds = metrics.movingTimeSeconds % 60
                val timeStr = if (hours > 0) String.format(Locale.US, "%02dh %02dm", hours, minutes) else String.format(Locale.US, "%02dm %02ds", minutes, seconds)

                Column {
                    Text("Tempo Movimento", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(timeStr, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                }

                Column {
                    Text("Velocidade Média", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${String.format(Locale.US, "%.1f", metrics.avgSpeedKmh)} km/h", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                }

                Column {
                    Text("Velocidade Máx", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${String.format(Locale.US, "%.1f", metrics.maxSpeedKmh)} km/h", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun FuelManagementTab(
    activeVehicle: VehicleEntity?,
    fuelEntries: List<FuelEntryEntity>,
    fuelStats: FuelConsumptionStats,
    onOpenAddFuel: () -> Unit,
    onDeleteFuel: (FuelEntryEntity) -> Unit
) {
    var itemToDelete by remember { mutableStateOf<FuelEntryEntity?>(null) }

    val tankCapacity = activeVehicle?.tankCapacityLiters ?: 50.0
    val currentLiters = activeVehicle?.currentFuelLiters ?: 35.0
    val tankPercentage = ((currentLiters / tankCapacity) * 100).toInt().coerceIn(0, 100)
    val estimatedRangeKm = if (fuelStats.averageKmL > 0) (currentLiters * fuelStats.averageKmL).toInt() else 0

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tank status card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalGasStation, contentDescription = null, tint = TurboAmber)
                            Spacer(Modifier.width(8.dp))
                            Text("Nível do Tanque", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }

                        Text(
                            text = "$tankPercentage% (~${String.format(Locale.US, "%.1f", currentLiters)} L)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (tankPercentage < 15) StatusOverdue else MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { (tankPercentage / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = if (tankPercentage < 15) StatusOverdue else MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Capacidade: ${tankCapacity.toInt()} Litros",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (estimatedRangeKm > 0) {
                            Text(
                                text = "Autonomia est.: ~$estimatedRangeKm km",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = StatusOk
                            )
                        }
                    }
                }
            }
        }

        // Consumption stats card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Estatísticas de Consumo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Consumo Médio", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = if (fuelStats.averageKmL > 0) "${String.format(Locale.US, "%.1f", fuelStats.averageKmL)} km/L" else "--",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Column {
                            Text("Custo por KM", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = if (fuelStats.costPerKm > 0) "R$ ${String.format(Locale.US, "%.2f", fuelStats.costPerKm)}" else "--",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = StatusOk
                            )
                        }

                        Column {
                            Text("Total Abastecido", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = "R$ ${String.format(Locale.US, "%.0f", fuelStats.totalCost)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Action header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Histórico de Abastecimentos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = onOpenAddFuel,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("btn_add_fuel_entry")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Abastecer", fontSize = 13.sp)
                }
            }
        }

        if (fuelEntries.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nenhum abastecimento registrado ainda.\nClique em 'Abastecer' para começar a monitorar consumo e gastos.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            items(fuelEntries, key = { it.id }) { entry ->
                FuelEntryCard(entry = entry, onDelete = { itemToDelete = entry })
            }
        }
    }

    if (itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Excluir Registro") },
            text = { Text("Deseja remover este abastecimento de ${itemToDelete?.liters}L?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteFuel(itemToDelete!!)
                        itemToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun FuelEntryCard(
    entry: FuelEntryEntity,
    onDelete: () -> Unit
) {
    val sdf = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val dateStr = sdf.format(Date(entry.date))

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = entry.fuelType,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    if (entry.isFullTank) {
                        Spacer(Modifier.width(6.dp))
                        Surface(
                            color = StatusOk.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "Tanque Cheio",
                                style = MaterialTheme.typography.labelSmall,
                                color = StatusOk,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${entry.odometerKm} km • ${String.format(Locale.US, "%.1f", entry.liters)} Litros",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "$dateStr ${if (entry.gasStation.isNotBlank()) "• " + entry.gasStation else ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "R$ ${String.format(Locale.US, "%.2f", entry.totalPrice)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "R$ ${String.format(Locale.US, "%.2f", entry.pricePerLiter)}/L",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun CalculatorsTab(activeVehicle: VehicleEntity?) {
    var etanolPriceStr by remember { mutableStateOf("3.89") }
    var gasolinePriceStr by remember { mutableStateOf("5.99") }

    val etanolPrice = etanolPriceStr.toDoubleOrNull() ?: 0.0
    val gasolinePrice = gasolinePriceStr.toDoubleOrNull() ?: 1.0
    val ratio = if (gasolinePrice > 0) (etanolPrice / gasolinePrice) else 0.0
    val isEtanolAdvantage = ratio <= 0.70

    // Additive dosage calculator
    var fuelLitersStr by remember { mutableStateOf("45") }
    var ratioMlPerLiterStr by remember { mutableStateOf("10") } // 10ml por litro ou 1:50
    val fuelLiters = fuelLitersStr.toDoubleOrNull() ?: 0.0
    val mlPerLiter = ratioMlPerLiterStr.toDoubleOrNull() ?: 10.0
    val totalMlRequired = fuelLiters * mlPerLiter

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Flex Calculator Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Calculate, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(8.dp))
                        Text("Calculadora Flex: Etanol x Gasolina", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }

                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "A regra prática de 70% indica qual combustível compensa mais financeiramente.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = etanolPriceStr,
                            onValueChange = { etanolPriceStr = it },
                            label = { Text("Preço Etanol (R$)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = gasolinePriceStr,
                            onValueChange = { gasolinePriceStr = it },
                            label = { Text("Preço Gasolina (R$)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    Surface(
                        color = (if (isEtanolAdvantage) StatusOk else TurboAmber).copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, (if (isEtanolAdvantage) StatusOk else TurboAmber).copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = if (isEtanolAdvantage) "✓ Compensa abastecer com ETANOL" else "✓ Compensa abastecer com GASOLINA",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isEtanolAdvantage) StatusOk else TurboAmber
                            )
                            Text(
                                text = "Proporção atual: ${String.format(Locale.US, "%.1f", ratio * 100)}% (limite recomendado: 70%)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Additive & 2T Oil Dosage Calculator Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Dosador de Aditivos & Óleo 2T", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Calcule a quantidade exata de aditivo para combustível ou proporção de óleo em motores 2 tempos.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = fuelLitersStr,
                            onValueChange = { fuelLitersStr = it },
                            label = { Text("Litros no Tanque") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = ratioMlPerLiterStr,
                            onValueChange = { ratioMlPerLiterStr = it },
                            label = { Text("mL por Litro") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Quantidade a adicionar:", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = "${totalMlRequired.toInt()} mL",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddFuelEntryDialog(
    currentKm: Int,
    defaultFuel: String,
    onDismiss: () -> Unit,
    onSave: (odometerKm: Int, liters: Double, pricePerLiter: Double, fuelType: String, isFullTank: Boolean, gasStation: String, notes: String) -> Unit
) {
    var kmStr by remember { mutableStateOf(currentKm.toString()) }
    var litersStr by remember { mutableStateOf("") }
    var pricePerLiterStr by remember { mutableStateOf("5.89") }
    var fuelType by remember { mutableStateOf(defaultFuel.ifBlank { "Gasolina Comum" }) }
    var isFullTank by remember { mutableStateOf(true) }
    var gasStation by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val fuels = listOf("Gasolina Comum", "Gasolina Aditivada", "Etanol", "Diesel S10", "GNV")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Novo Abastecimento") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = kmStr,
                    onValueChange = { kmStr = it },
                    label = { Text("Odômetro Atual (KM) *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = litersStr,
                        onValueChange = { litersStr = it },
                        label = { Text("Litros *") },
                        placeholder = { Text("ex: 42.5") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = pricePerLiterStr,
                        onValueChange = { pricePerLiterStr = it },
                        label = { Text("Preço/Litro R$") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Text("Tipo de Combustível:", style = MaterialTheme.typography.labelSmall)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    fuels.take(3).forEach { f ->
                        FilterChip(
                            selected = fuelType == f,
                            onClick = { fuelType = f },
                            label = { Text(f.split(" ").first(), fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = gasStation,
                    onValueChange = { gasStation = it },
                    label = { Text("Posto / Bandeira") },
                    placeholder = { Text("ex: Posto Ipiranga") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Tanque Cheio?", style = MaterialTheme.typography.bodyMedium)
                    Switch(checked = isFullTank, onCheckedChange = { isFullTank = it })
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val km = kmStr.toIntOrNull() ?: currentKm
                    val liters = litersStr.toDoubleOrNull() ?: 0.0
                    val price = pricePerLiterStr.toDoubleOrNull() ?: 0.0
                    if (liters > 0) {
                        onSave(km, liters, price, fuelType, isFullTank, gasStation, notes)
                    }
                },
                enabled = litersStr.toDoubleOrNull() != null && (litersStr.toDoubleOrNull() ?: 0.0) > 0.0
            ) {
                Text("Registrar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
