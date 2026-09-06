package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.dao.MaintenanceWithItems
import com.example.data.model.MaintenanceItemEntity
import com.example.data.model.PeriodicMaintenanceEntity
import com.example.data.model.VehicleEntity
import com.example.data.model.VehicleType
import com.example.ui.dialogs.AddEditVehicleDialog
import com.example.ui.dialogs.AddManualHistoryDialog
import com.example.ui.dialogs.AddTaskDialog
import com.example.ui.dialogs.CompleteMaintenanceDialog
import com.example.ui.dialogs.CreateEditMaintenanceDialog
import com.example.ui.dialogs.UpdateKmDialog
import com.example.ui.screens.CostsFipeScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.MaintenanceHubScreen
import com.example.ui.screens.MoreAndSosScreen
import com.example.ui.screens.TelemetryFuelScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppNavTab
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val appearance by viewModel.appearanceConfig.collectAsStateWithLifecycle()
            val activeVehicle by viewModel.activeVehicle.collectAsStateWithLifecycle()

            val effectiveColorHex = if (appearance.useVehicleColor && !activeVehicle?.themeColorHex.isNullOrBlank()) {
                activeVehicle!!.themeColorHex
            } else {
                appearance.themeColorHex
            }

            MyApplicationTheme(
                darkTheme = appearance.isDarkTheme,
                customPrimaryHex = effectiveColorHex,
                cornerRadiusDp = appearance.cornerRadiusDp
            ) {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

data class NavItem(
    val tab: AppNavTab,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(viewModel: MainViewModel) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val activeVehicle by viewModel.activeVehicle.collectAsStateWithLifecycle()
    val allVehicles by viewModel.vehicles.collectAsStateWithLifecycle()
    val maintenances by viewModel.maintenancesWithItems.collectAsStateWithLifecycle()
    val tasks by viewModel.reminderTasks.collectAsStateWithLifecycle()
    val historyList by viewModel.historyList.collectAsStateWithLifecycle()
    val summary by viewModel.dashboardSummary.collectAsStateWithLifecycle()

    val installedComponents by viewModel.installedComponents.collectAsStateWithLifecycle()
    val fuelEntries by viewModel.fuelEntries.collectAsStateWithLifecycle()
    val fuelStats by viewModel.fuelStats.collectAsStateWithLifecycle()
    val expenses by viewModel.vehicleExpenses.collectAsStateWithLifecycle()
    val tco by viewModel.tcoCalculation.collectAsStateWithLifecycle()
    val customParts by viewModel.customParts.collectAsStateWithLifecycle()
    val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()

    val appearance by viewModel.appearanceConfig.collectAsStateWithLifecycle()
    val googleProfile by viewModel.googleProfile.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val isGpsActive by viewModel.isGpsTrackingActive.collectAsStateWithLifecycle()
    val currentSpeed by viewModel.currentSpeedKmh.collectAsStateWithLifecycle()
    val trip1 by viewModel.trip1.collectAsStateWithLifecycle()
    val trip2 by viewModel.trip2.collectAsStateWithLifecycle()
    val bluetoothDeviceName by viewModel.bluetoothDeviceName.collectAsStateWithLifecycle()
    val isAutoBt by viewModel.isAutoBluetoothStart.collectAsStateWithLifecycle()
    val webhookUrl by viewModel.trackerWebhookUrl.collectAsStateWithLifecycle()

    // Dialog States
    var showAddVehicleDialog by remember { mutableStateOf(false) }
    var vehicleToEdit by remember { mutableStateOf<VehicleEntity?>(null) }
    var showUpdateKmDialog by remember { mutableStateOf(false) }

    var showCreateEditMaintenanceDialog by remember { mutableStateOf(false) }
    var editingMaintenanceWithItems by remember { mutableStateOf<MaintenanceWithItems?>(null) }
    var preloadedItemsForNewMaintenance by remember { mutableStateOf<List<MaintenanceItemEntity>>(emptyList()) }
    var preloadedCategoryForNewMaintenance by remember { mutableStateOf("Motor & Óleo") }
    var preloadedTitleForNewMaintenance by remember { mutableStateOf("") }
    var preloadedIntervalKm by remember { mutableStateOf(10000) }
    var preloadedIntervalMonths by remember { mutableStateOf(6) }

    var maintenanceToComplete by remember { mutableStateOf<MaintenanceWithItems?>(null) }
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var showAddManualHistoryDialog by remember { mutableStateOf(false) }

    val navItems = listOf(
        NavItem(
            tab = AppNavTab.GARAGE,
            label = "Garagem",
            selectedIcon = Icons.Filled.DirectionsCar,
            unselectedIcon = Icons.Outlined.DirectionsCar,
            testTag = "nav_tab_garage"
        ),
        NavItem(
            tab = AppNavTab.MAINTENANCE,
            label = "Revisões",
            selectedIcon = Icons.Filled.Build,
            unselectedIcon = Icons.Outlined.Build,
            testTag = "nav_tab_maintenance"
        ),
        NavItem(
            tab = AppNavTab.TELEMETRY,
            label = "Telemetria",
            selectedIcon = Icons.Filled.Speed,
            unselectedIcon = Icons.Outlined.Speed,
            testTag = "nav_tab_telemetry"
        ),
        NavItem(
            tab = AppNavTab.COSTS,
            label = "Custos",
            selectedIcon = Icons.Filled.AttachMoney,
            unselectedIcon = Icons.Outlined.AttachMoney,
            testTag = "nav_tab_costs"
        ),
        NavItem(
            tab = AppNavTab.MORE,
            label = "Mais & SOS",
            selectedIcon = Icons.Filled.Menu,
            unselectedIcon = Icons.Outlined.Menu,
            testTag = "nav_tab_more"
        )
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (currentTab) {
                            AppNavTab.GARAGE -> "Garagem Inteligente"
                            AppNavTab.MAINTENANCE -> "Revisões & Componentes"
                            AppNavTab.TELEMETRY -> "Telemetria & Odômetro GPS"
                            AppNavTab.COSTS -> "Custos, IPVA & FIPE"
                            AppNavTab.MORE -> "Modo SOS, Laudo & Ajustes"
                            else -> "AutoMoto Assistente"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                windowInsets = WindowInsets.navigationBars,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                navItems.forEach { item ->
                    val selected = currentTab == item.tab
                    NavigationBarItem(
                        selected = selected,
                        onClick = { viewModel.selectTab(item.tab) },
                        icon = {
                            Icon(
                                imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                fontSize = 11.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.testTag(item.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                AppNavTab.GARAGE -> {
                    DashboardScreen(
                        activeVehicle = activeVehicle,
                        allVehicles = allVehicles,
                        maintenances = maintenances,
                        tasks = tasks,
                        summary = summary,
                        onSelectVehicle = { viewModel.selectVehicle(it) },
                        onAddVehicle = {
                            vehicleToEdit = null
                            showAddVehicleDialog = true
                        },
                        onEditVehicle = {
                            vehicleToEdit = it
                            showAddVehicleDialog = true
                        },
                        onUpdateKm = { showUpdateKmDialog = true },
                        onCreateRevision = {
                            editingMaintenanceWithItems = null
                            preloadedItemsForNewMaintenance = emptyList()
                            preloadedTitleForNewMaintenance = ""
                            preloadedCategoryForNewMaintenance = "Motor & Óleo"
                            preloadedIntervalKm = 10000
                            preloadedIntervalMonths = 6
                            showCreateEditMaintenanceDialog = true
                        },
                        onNavigateTab = { viewModel.selectTab(it) },
                        onCompleteMaintenance = { maintenanceToComplete = it },
                        onToggleTask = { id, done -> viewModel.toggleTaskDone(id, done) },
                        onAddTask = { showAddTaskDialog = true }
                    )
                }

                AppNavTab.MAINTENANCE, AppNavTab.SUGGESTIONS, AppNavTab.HISTORY, AppNavTab.TASKS -> {
                    MaintenanceHubScreen(
                        activeVehicle = activeVehicle,
                        maintenances = maintenances,
                        installedComponents = installedComponents,
                        historyList = historyList,
                        tasks = tasks,
                        customParts = customParts,
                        onCreateRevision = {
                            editingMaintenanceWithItems = null
                            preloadedItemsForNewMaintenance = emptyList()
                            preloadedTitleForNewMaintenance = ""
                            preloadedCategoryForNewMaintenance = "Motor & Óleo"
                            preloadedIntervalKm = 10000
                            preloadedIntervalMonths = 6
                            showCreateEditMaintenanceDialog = true
                        },
                        onEditRevision = { item ->
                            editingMaintenanceWithItems = item
                            showCreateEditMaintenanceDialog = true
                        },
                        onDeleteRevision = { viewModel.deleteMaintenance(it) },
                        onCompleteRevision = { maintenanceToComplete = it },
                        onSaveCustomPart = { cp ->
                            viewModel.saveCustomPart(
                                name = cp.name,
                                category = cp.category,
                                vehicleType = cp.vehicleType,
                                isPart = cp.isPart,
                                mode = cp.defaultMode,
                                price = cp.estimatedPrice,
                                intervalKm = cp.defaultIntervalKm,
                                intervalMonths = cp.defaultIntervalMonths,
                                brand = cp.brand,
                                notes = cp.notes
                            )
                        },
                        onDeleteCustomPart = { viewModel.deleteCustomPart(it) },
                        onApplyTemplate = { template ->
                            editingMaintenanceWithItems = null
                            preloadedTitleForNewMaintenance = template.title
                            preloadedCategoryForNewMaintenance = template.category
                            preloadedIntervalKm = template.intervalKm
                            preloadedIntervalMonths = template.intervalMonths
                            preloadedItemsForNewMaintenance = template.items.map { s ->
                                MaintenanceItemEntity(
                                    maintenanceId = 0,
                                    name = s.name,
                                    category = s.category,
                                    isPart = s.isPart,
                                    estimatedPrice = s.estimatedPrice
                                )
                            }
                            showCreateEditMaintenanceDialog = true
                        },
                        onApplySingleItem = { item ->
                            editingMaintenanceWithItems = null
                            preloadedTitleForNewMaintenance = "Revisão: ${item.name}"
                            preloadedCategoryForNewMaintenance = item.category
                            preloadedIntervalKm = item.defaultIntervalKm
                            preloadedIntervalMonths = item.defaultIntervalMonths
                            preloadedItemsForNewMaintenance = listOf(
                                MaintenanceItemEntity(
                                    maintenanceId = 0,
                                    name = item.name,
                                    category = item.category,
                                    isPart = item.isPart,
                                    estimatedPrice = item.estimatedPrice
                                )
                            )
                            showCreateEditMaintenanceDialog = true
                        },
                        onAddComponent = { viewModel.saveInstalledComponent(it) },
                        onUpdateComponent = { viewModel.saveInstalledComponent(it) },
                        onDeleteComponent = { viewModel.deleteInstalledComponent(it) },
                        onAddHistory = { showAddManualHistoryDialog = true },
                        onDeleteHistory = { viewModel.deleteHistory(it) },
                        onAddTask = { showAddTaskDialog = true },
                        onToggleTask = { id, done -> viewModel.toggleTaskDone(id, done) },
                        onDeleteTask = { viewModel.deleteTask(it) }
                    )
                }

                AppNavTab.TELEMETRY -> {
                    TelemetryFuelScreen(
                        activeVehicle = activeVehicle,
                        isGpsTrackingActive = isGpsActive,
                        currentSpeedKmh = currentSpeed,
                        trip1 = trip1,
                        trip2 = trip2,
                        bluetoothDevice = bluetoothDeviceName,
                        fuelEntries = fuelEntries,
                        fuelStats = fuelStats,
                        onToggleGpsTracking = { viewModel.toggleGpsTracking() },
                        onResetTrip1 = { viewModel.resetTrip1() },
                        onResetTrip2 = { viewModel.resetTrip2() },
                        onAddFuelEntry = { odoKm, liters, pricePerLiter, fuelType, isFull, stName, notes ->
                            viewModel.addFuelEntry(odoKm, liters, pricePerLiter, fuelType, isFull, stName, notes)
                        },
                        onDeleteFuelEntry = { viewModel.deleteFuelEntry(it) },
                        onUpdateKm = { viewModel.updateCurrentKm(it) }
                    )
                }

                AppNavTab.COSTS -> {
                    CostsFipeScreen(
                        activeVehicle = activeVehicle,
                        expenses = expenses,
                        tco = tco,
                        onAddExpense = { title, cat, amount, due, isPaid, pts, notes ->
                            viewModel.addExpense(title, cat, amount, due, isPaid, pts, notes)
                        },
                        onDeleteExpense = { viewModel.deleteExpense(it) }
                    )
                }

                AppNavTab.MORE -> {
                    MoreAndSosScreen(
                        activeVehicle = activeVehicle,
                        maintenances = maintenances,
                        history = historyList,
                        appearance = appearance,
                        googleProfile = googleProfile,
                        isSyncing = isSyncing,
                        currentLanguage = currentLanguage,
                        onSelectLanguage = { viewModel.setLanguage(it) },
                        onUpdateAppearance = { color, corner, font, icon, dark, useVehicle ->
                            viewModel.updateAppearanceConfig(color, corner, font, icon, dark, useVehicle)
                        },
                        onToggleGoogleConnect = { viewModel.toggleGoogleConnection() },
                        onSyncCloud = { viewModel.syncCloudData() }
                    )
                }
            }
        }
    }

    // Dialog: Add or Edit Vehicle
    if (showAddVehicleDialog) {
        AddEditVehicleDialog(
            vehicleToEdit = vehicleToEdit,
            onDismiss = {
                showAddVehicleDialog = false
                vehicleToEdit = null
            },
            onSave = { name, type, brand, model, year, plate, currentKm, fuelType, color, nickname, notes, themeColorHex, tankCapacityLiters, fipeValue, insuranceCompany, insuranceEmergencyPhone ->
                if (vehicleToEdit == null) {
                    viewModel.addVehicle(
                        name = name,
                        type = type,
                        brand = brand,
                        model = model,
                        year = year,
                        plate = plate,
                        currentKm = currentKm,
                        fuelType = fuelType,
                        color = color,
                        nickname = nickname,
                        notes = notes,
                        themeColorHex = themeColorHex,
                        tankCapacityLiters = tankCapacityLiters,
                        fipeValue = fipeValue,
                        insuranceCompany = insuranceCompany,
                        insuranceEmergencyPhone = insuranceEmergencyPhone
                    )
                } else {
                    viewModel.updateVehicle(
                        vehicleToEdit!!.copy(
                            name = name,
                            type = type.name,
                            brand = brand,
                            model = model,
                            year = year,
                            plate = plate,
                            currentKm = currentKm,
                            fuelType = fuelType,
                            color = color,
                            nickname = nickname,
                            notes = notes,
                            themeColorHex = themeColorHex,
                            tankCapacityLiters = tankCapacityLiters,
                            fipeValue = fipeValue,
                            insuranceCompany = insuranceCompany,
                            insuranceEmergencyPhone = insuranceEmergencyPhone
                        )
                    )
                }
                showAddVehicleDialog = false
                vehicleToEdit = null
            }
        )
    }

    // Dialog: Update KM
    if (showUpdateKmDialog && activeVehicle != null) {
        UpdateKmDialog(
            currentKm = activeVehicle!!.currentKm,
            vehicleName = activeVehicle!!.nickname.ifBlank { activeVehicle!!.name },
            onDismiss = { showUpdateKmDialog = false },
            onConfirm = { newKm ->
                viewModel.updateCurrentKm(newKm)
                showUpdateKmDialog = false
            }
        )
    }

    // Dialog: Create or Edit Periodic Maintenance
    if (showCreateEditMaintenanceDialog) {
        val activeType = try {
            VehicleType.valueOf(activeVehicle?.type ?: "CAR")
        } catch (e: Exception) {
            VehicleType.CAR
        }
        val currentKm = activeVehicle?.currentKm ?: 0

        val initialMaintenance = editingMaintenanceWithItems?.maintenance ?: PeriodicMaintenanceEntity(
            vehicleId = activeVehicle?.id ?: 0,
            title = preloadedTitleForNewMaintenance,
            category = preloadedCategoryForNewMaintenance,
            intervalKm = preloadedIntervalKm,
            intervalMonths = preloadedIntervalMonths,
            lastPerformedKm = currentKm
        )

        val initialItems = editingMaintenanceWithItems?.items ?: preloadedItemsForNewMaintenance

        CreateEditMaintenanceDialog(
            initialMaintenance = if (editingMaintenanceWithItems != null) initialMaintenance else initialMaintenance.copy(id = 0),
            initialItems = initialItems,
            vehicleType = activeType,
            currentVehicleKm = currentKm,
            customParts = customParts,
            onSaveCustomPart = { cp ->
                viewModel.saveCustomPart(
                    name = cp.name,
                    category = cp.category,
                    vehicleType = cp.vehicleType,
                    isPart = cp.isPart,
                    mode = cp.defaultMode,
                    price = cp.estimatedPrice,
                    intervalKm = cp.defaultIntervalKm,
                    intervalMonths = cp.defaultIntervalMonths,
                    brand = cp.brand,
                    notes = cp.notes
                )
            },
            onDismiss = {
                showCreateEditMaintenanceDialog = false
                editingMaintenanceWithItems = null
            },
            onSave = { title, desc, cat, intKm, intMo, lastKm, lastDate, items ->
                viewModel.savePeriodicMaintenance(
                    maintenanceId = editingMaintenanceWithItems?.maintenance?.id ?: 0L,
                    title = title,
                    description = desc,
                    category = cat,
                    intervalKm = intKm,
                    intervalMonths = intMo,
                    lastPerformedKm = lastKm,
                    lastPerformedDate = lastDate,
                    items = items
                )
                showCreateEditMaintenanceDialog = false
                editingMaintenanceWithItems = null
            }
        )
    }

    // Dialog: Complete Maintenance
    if (maintenanceToComplete != null) {
        CompleteMaintenanceDialog(
            maintenanceWithItems = maintenanceToComplete!!,
            currentVehicleKm = activeVehicle?.currentKm ?: 0,
            onDismiss = { maintenanceToComplete = null },
            onConfirm = { pKm, pDate, workshop, cost, notes, summaryItems ->
                viewModel.completeMaintenance(
                    maintenance = maintenanceToComplete!!.maintenance,
                    performedKm = pKm,
                    performedDate = pDate,
                    workshop = workshop,
                    cost = cost,
                    notes = notes,
                    itemsSummary = summaryItems
                )
                maintenanceToComplete = null
            }
        )
    }

    // Dialog: Add Task
    if (showAddTaskDialog) {
        AddTaskDialog(
            onDismiss = { showAddTaskDialog = false },
            onSave = { title, dueDate, category, notes ->
                viewModel.addTask(title, dueDate, category, notes)
                showAddTaskDialog = false
            }
        )
    }

    // Dialog: Add Manual History
    if (showAddManualHistoryDialog) {
        AddManualHistoryDialog(
            currentVehicleKm = activeVehicle?.currentKm ?: 0,
            onDismiss = { showAddManualHistoryDialog = false },
            onSave = { title, km, date, workshop, cost, notes, itemsSummary ->
                viewModel.addHistoryManual(
                    title = title,
                    performedKm = km,
                    performedDate = date,
                    workshop = workshop,
                    cost = cost,
                    notes = notes,
                    itemsSummary = itemsSummary
                )
                showAddManualHistoryDialog = false
            }
        )
    }
}
