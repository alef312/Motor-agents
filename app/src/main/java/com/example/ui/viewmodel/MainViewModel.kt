package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.dao.MaintenanceWithItems
import com.example.data.model.ComponentWearStatus
import com.example.data.model.CustomPartEntity
import com.example.data.model.FuelConsumptionStats
import com.example.data.model.FuelEntryEntity
import com.example.data.model.InstalledComponentEntity
import com.example.data.model.MaintenanceHistoryEntity
import com.example.data.model.MaintenanceItemEntity
import com.example.data.model.MaintenanceStatus
import com.example.data.model.PeriodicMaintenanceEntity
import com.example.data.model.ReminderTaskEntity
import com.example.data.model.TcoCalculation
import com.example.data.model.VehicleEntity
import com.example.data.model.VehicleExpenseEntity
import com.example.data.model.VehicleType
import com.example.data.model.calculateComponentWear
import com.example.data.model.calculateFuelStats
import com.example.data.model.calculateMaintenanceStatus
import com.example.data.local.PresetSuggestions
import com.example.data.local.SuggestionItem
import com.example.data.repository.VehicleMaintenanceRepository
import com.example.ui.utils.LanguageManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class AppNavTab(val title: String) {
    GARAGE("Garagem"),
    MAINTENANCE("Revisões & Peças"),
    TELEMETRY("Telemetria"),
    COSTS("Custos & FIPE"),
    MORE("Mais & SOS"),
    // Legacy shortcuts
    SUGGESTIONS("Sugestões"),
    HISTORY("Histórico"),
    TASKS("Lembretes")
}

data class DashboardSummary(
    val totalRevisions: Int = 0,
    val okRevisions: Int = 0,
    val attentionRevisions: Int = 0,
    val overdueRevisions: Int = 0,
    val pendingTasksCount: Int = 0,
    val totalInvested: Double = 0.0,
    val componentsCount: Int = 0,
    val wornComponentsCount: Int = 0
)

data class TripMetrics(
    val distanceKm: Double = 0.0,
    val movingTimeSeconds: Long = 0L,
    val avgSpeedKmh: Double = 0.0,
    val maxSpeedKmh: Double = 0.0
)

data class AppAppearanceConfig(
    val themeColorHex: String = "#F59E0B",
    val cornerRadiusDp: Int = 16,
    val fontScale: Float = 1.0f,
    val iconScale: Float = 1.0f,
    val isDarkTheme: Boolean = true,
    val useVehicleColor: Boolean = true
)

data class GoogleUserProfile(
    val isConnected: Boolean = true,
    val name: String = "Alef Oliveira",
    val email: String = "alefdeoliver2002@gmail.com",
    val isAutoSyncEnabled: Boolean = true,
    val lastSyncTimestamp: Long = System.currentTimeMillis()
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: VehicleMaintenanceRepository

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = VehicleMaintenanceRepository(
            vehicleDao = database.vehicleDao(),
            periodicMaintenanceDao = database.periodicMaintenanceDao(),
            maintenanceItemDao = database.maintenanceItemDao(),
            maintenanceHistoryDao = database.maintenanceHistoryDao(),
            reminderTaskDao = database.reminderTaskDao(),
            installedComponentDao = database.installedComponentDao(),
            fuelEntryDao = database.fuelEntryDao(),
            vehicleExpenseDao = database.vehicleExpenseDao(),
            customPartDao = database.customPartDao()
        )

        // GPS Telemetry background simulation loop
        viewModelScope.launch {
            while (isActive) {
                delay(1000)
                if (_isGpsTrackingActive.value) {
                    processTelemetryTick()
                }
            }
        }
    }

    // Active Navigation Tab
    private val _currentTab = MutableStateFlow(AppNavTab.GARAGE)
    val currentTab: StateFlow<AppNavTab> = _currentTab.asStateFlow()

    fun selectTab(tab: AppNavTab) {
        _currentTab.value = tab
    }

    // Appearance & Customization Settings
    private val _appearanceConfig = MutableStateFlow(AppAppearanceConfig())
    val appearanceConfig: StateFlow<AppAppearanceConfig> = _appearanceConfig.asStateFlow()

    fun updateAppearanceConfig(
        themeColorHex: String = _appearanceConfig.value.themeColorHex,
        cornerRadiusDp: Int = _appearanceConfig.value.cornerRadiusDp,
        fontScale: Float = _appearanceConfig.value.fontScale,
        iconScale: Float = _appearanceConfig.value.iconScale,
        isDarkTheme: Boolean = _appearanceConfig.value.isDarkTheme,
        useVehicleColor: Boolean = _appearanceConfig.value.useVehicleColor
    ) {
        _appearanceConfig.value = AppAppearanceConfig(
            themeColorHex = themeColorHex,
            cornerRadiusDp = cornerRadiusDp,
            fontScale = fontScale,
            iconScale = iconScale,
            isDarkTheme = isDarkTheme,
            useVehicleColor = useVehicleColor
        )
    }

    // Language Selection (Ordered by global speakers)
    private val _currentLanguage = MutableStateFlow(LanguageManager.getSavedLanguage(application))
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    fun setLanguage(code: String) {
        _currentLanguage.value = code
        LanguageManager.saveLanguage(getApplication(), code)
    }

    // Google User Profile & Sync
    private val _googleProfile = MutableStateFlow(GoogleUserProfile())
    val googleProfile: StateFlow<GoogleUserProfile> = _googleProfile.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    fun toggleGoogleConnection() {
        _googleProfile.value = _googleProfile.value.copy(
            isConnected = !_googleProfile.value.isConnected
        )
    }

    fun syncCloudData() {
        viewModelScope.launch {
            _isSyncing.value = true
            delay(1200) // Simulated cloud sync
            _googleProfile.value = _googleProfile.value.copy(
                lastSyncTimestamp = System.currentTimeMillis()
            )
            _isSyncing.value = false
        }
    }

    // Telemetry & GPS Tracking
    private val _isGpsTrackingActive = MutableStateFlow(false)
    val isGpsTrackingActive: StateFlow<Boolean> = _isGpsTrackingActive.asStateFlow()

    private val _currentSpeedKmh = MutableStateFlow(0.0)
    val currentSpeedKmh: StateFlow<Double> = _currentSpeedKmh.asStateFlow()

    private val _trip1 = MutableStateFlow(TripMetrics())
    val trip1: StateFlow<TripMetrics> = _trip1.asStateFlow()

    private val _trip2 = MutableStateFlow(TripMetrics())
    val trip2: StateFlow<TripMetrics> = _trip2.asStateFlow()

    private val _bluetoothDeviceName = MutableStateFlow("Multimídia Bluetooth Pareado")
    val bluetoothDeviceName: StateFlow<String> = _bluetoothDeviceName.asStateFlow()

    private val _isAutoBluetoothStart = MutableStateFlow(true)
    val isAutoBluetoothStart: StateFlow<Boolean> = _isAutoBluetoothStart.asStateFlow()

    private val _trackerWebhookUrl = MutableStateFlow("https://api.traccar.org/webhook/v1")
    val trackerWebhookUrl: StateFlow<String> = _trackerWebhookUrl.asStateFlow()

    fun toggleGpsTracking() {
        _isGpsTrackingActive.value = !_isGpsTrackingActive.value
        if (!_isGpsTrackingActive.value) {
            _currentSpeedKmh.value = 0.0
        }
    }

    fun resetTrip1() {
        _trip1.value = TripMetrics()
    }

    fun resetTrip2() {
        _trip2.value = TripMetrics()
    }

    fun setBluetoothAutoStart(enabled: Boolean) {
        _isAutoBluetoothStart.value = enabled
    }

    fun setTrackerWebhook(url: String) {
        _trackerWebhookUrl.value = url
    }

    private var accumulatedMetersToKm = 0.0

    private fun processTelemetryTick() {
        // Realistic speed variation between 45 and 78 km/h
        val instantSpeed = 50.0 + Random.nextDouble(-12.0, 18.0)
        _currentSpeedKmh.value = instantSpeed

        val distanceIncrementKm = (instantSpeed / 3600.0)

        // Update Trip 1
        val t1Time = _trip1.value.movingTimeSeconds + 1
        val t1Dist = _trip1.value.distanceKm + distanceIncrementKm
        val t1Max = maxOf(_trip1.value.maxSpeedKmh, instantSpeed)
        val t1Avg = if (t1Time > 0) (t1Dist / (t1Time / 3600.0)) else 0.0
        _trip1.value = TripMetrics(t1Dist, t1Time, t1Avg, t1Max)

        // Update Trip 2
        val t2Time = _trip2.value.movingTimeSeconds + 1
        val t2Dist = _trip2.value.distanceKm + distanceIncrementKm
        val t2Max = maxOf(_trip2.value.maxSpeedKmh, instantSpeed)
        val t2Avg = if (t2Time > 0) (t2Dist / (t2Time / 3600.0)) else 0.0
        _trip2.value = TripMetrics(t2Dist, t2Time, t2Avg, t2Max)

        // Increment vehicle odometer every 1 km
        accumulatedMetersToKm += (distanceIncrementKm * 1000.0)
        if (accumulatedMetersToKm >= 1000.0) {
            val kmToAdd = (accumulatedMetersToKm / 1000.0).toInt()
            accumulatedMetersToKm %= 1000.0
            val vehicle = activeVehicle.value
            if (vehicle != null) {
                updateCurrentKm(vehicle.currentKm + kmToAdd)
            }
        }
    }

    // Vehicles
    val vehicles: StateFlow<List<VehicleEntity>> = repository.allVehicles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedVehicleId = MutableStateFlow<Long?>(null)
    val selectedVehicleId: StateFlow<Long?> = _selectedVehicleId.asStateFlow()

    // Active vehicle
    val activeVehicle: StateFlow<VehicleEntity?> = combine(vehicles, selectedVehicleId) { list, selId ->
        if (selId != null) {
            list.find { it.id == selId } ?: list.firstOrNull()
        } else {
            list.firstOrNull()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun selectVehicle(vehicleId: Long) {
        _selectedVehicleId.value = vehicleId
    }

    // Active vehicle's maintenances
    @OptIn(ExperimentalCoroutinesApi::class)
    val maintenancesWithItems: StateFlow<List<MaintenanceWithItems>> = activeVehicle
        .flatMapLatest { vehicle ->
            if (vehicle != null) {
                repository.getMaintenancesWithItems(vehicle.id)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active vehicle's tasks
    @OptIn(ExperimentalCoroutinesApi::class)
    val reminderTasks: StateFlow<List<ReminderTaskEntity>> = activeVehicle
        .flatMapLatest { vehicle ->
            if (vehicle != null) {
                repository.getTasksByVehicle(vehicle.id)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active vehicle's history
    @OptIn(ExperimentalCoroutinesApi::class)
    val historyList: StateFlow<List<MaintenanceHistoryEntity>> = activeVehicle
        .flatMapLatest { vehicle ->
            if (vehicle != null) {
                repository.getHistoryByVehicle(vehicle.id)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active vehicle's Installed Components
    @OptIn(ExperimentalCoroutinesApi::class)
    val installedComponents: StateFlow<List<InstalledComponentEntity>> = activeVehicle
        .flatMapLatest { vehicle ->
            if (vehicle != null) {
                repository.getComponentsByVehicle(vehicle.id)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active vehicle's Fuel Entries
    @OptIn(ExperimentalCoroutinesApi::class)
    val fuelEntries: StateFlow<List<FuelEntryEntity>> = activeVehicle
        .flatMapLatest { vehicle ->
            if (vehicle != null) {
                repository.getFuelEntriesByVehicle(vehicle.id)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val fuelStats: StateFlow<FuelConsumptionStats> = fuelEntries.map { list ->
        calculateFuelStats(list)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FuelConsumptionStats())

    // Active vehicle's Expenses
    @OptIn(ExperimentalCoroutinesApi::class)
    val vehicleExpenses: StateFlow<List<VehicleExpenseEntity>> = activeVehicle
        .flatMapLatest { vehicle ->
            if (vehicle != null) {
                repository.getExpensesByVehicle(vehicle.id)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // TCO Calculation
    val tcoCalculation: StateFlow<TcoCalculation> = combine(
        activeVehicle,
        historyList,
        fuelEntries,
        vehicleExpenses
    ) { vehicle, history, fuel, expenses ->
        val fipe = vehicle?.fipeValue ?: 85000.0
        val annualDepreciation = fipe * 0.08 // ~8% annual depreciation
        val totalMaint = history.sumOf { it.totalCost }
        val totalFuel = fuel.sumOf { it.totalPrice }
        val totalExp = expenses.sumOf { it.amount }
        val totalTco = annualDepreciation + totalMaint + totalFuel + totalExp
        val monthlyCost = totalTco / 12.0
        val km = (vehicle?.currentKm ?: 1).coerceAtLeast(1)
        val costPerKm = totalTco / km.toDouble()

        TcoCalculation(
            vehicleFipe = fipe,
            estimatedAnnualDepreciation = annualDepreciation,
            totalMaintenanceCost = totalMaint,
            totalFuelCost = totalFuel,
            totalExpensesCost = totalExp,
            totalTco = totalTco,
            monthlyEstimatedCost = monthlyCost,
            costPerKm = costPerKm
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TcoCalculation(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0))

    // Dashboard summary
    val dashboardSummary: StateFlow<DashboardSummary> = combine(
        activeVehicle,
        maintenancesWithItems,
        reminderTasks,
        historyList,
        installedComponents
    ) { vehicle, maintenances, tasks, history, components ->
        val currentKm = vehicle?.currentKm ?: 0
        var ok = 0
        var attention = 0
        var overdue = 0

        for (m in maintenances) {
            when (calculateMaintenanceStatus(
                currentKm = currentKm,
                targetKm = m.maintenance.targetKm,
                targetDate = m.maintenance.targetDate,
                intervalKm = m.maintenance.intervalKm,
                intervalMonths = m.maintenance.intervalMonths
            )) {
                MaintenanceStatus.OK -> ok++
                MaintenanceStatus.ATTENTION -> attention++
                MaintenanceStatus.OVERDUE -> overdue++
            }
        }

        var wornComponents = 0
        for (comp in components) {
            val wear = calculateComponentWear(
                currentKm = currentKm,
                installedKm = comp.installedKm,
                installedDate = comp.installedDate,
                lifespanKm = comp.estimatedLifespanKm,
                lifespanMonths = comp.estimatedLifespanMonths
            )
            if (wear.wearPercentage >= 0.70f) {
                wornComponents++
            }
        }

        DashboardSummary(
            totalRevisions = maintenances.size,
            okRevisions = ok,
            attentionRevisions = attention,
            overdueRevisions = overdue,
            pendingTasksCount = tasks.count { !it.isDone },
            totalInvested = history.sumOf { it.totalCost },
            componentsCount = components.size,
            wornComponentsCount = wornComponents
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardSummary())

    // Actions: Vehicles
    fun addVehicle(
        name: String,
        type: VehicleType,
        brand: String,
        model: String,
        year: Int,
        plate: String,
        currentKm: Int,
        fuelType: String,
        color: String,
        nickname: String = "",
        notes: String = "",
        themeColorHex: String = "#F59E0B",
        tankCapacityLiters: Double = 50.0,
        fipeValue: Double = 0.0,
        insuranceCompany: String = "",
        insuranceEmergencyPhone: String = ""
    ) {
        viewModelScope.launch {
            val newId = repository.insertVehicle(
                VehicleEntity(
                    name = name.trim().ifEmpty { "${brand.trim()} ${model.trim()}" },
                    type = type.name,
                    brand = brand.trim(),
                    model = model.trim(),
                    year = year,
                    plate = plate.trim().uppercase(),
                    currentKm = currentKm,
                    fuelType = fuelType,
                    color = color.trim(),
                    nickname = nickname.trim(),
                    notes = notes.trim(),
                    themeColorHex = themeColorHex,
                    tankCapacityLiters = tankCapacityLiters,
                    fipeValue = fipeValue,
                    insuranceCompany = insuranceCompany.trim(),
                    insuranceEmergencyPhone = insuranceEmergencyPhone.trim()
                )
            )
            _selectedVehicleId.value = newId
        }
    }

    fun updateVehicle(vehicle: VehicleEntity) {
        viewModelScope.launch {
            repository.updateVehicle(vehicle)
        }
    }

    fun updateCurrentKm(newKm: Int) {
        val vehicle = activeVehicle.value ?: return
        viewModelScope.launch {
            repository.updateVehicleKm(vehicle.id, newKm)
        }
    }

    fun deleteVehicle(vehicle: VehicleEntity) {
        viewModelScope.launch {
            repository.deleteVehicle(vehicle)
            if (_selectedVehicleId.value == vehicle.id) {
                _selectedVehicleId.value = null
            }
        }
    }

    // Actions: Periodic Maintenances
    fun savePeriodicMaintenance(
        maintenanceId: Long = 0,
        title: String,
        description: String,
        category: String,
        intervalKm: Int,
        intervalMonths: Int,
        lastPerformedKm: Int,
        lastPerformedDate: Long,
        items: List<MaintenanceItemEntity>
    ) {
        val vehicle = activeVehicle.value ?: return
        viewModelScope.launch {
            val targetKm = if (intervalKm > 0) lastPerformedKm + intervalKm else 0
            val targetDate = if (intervalMonths > 0) {
                lastPerformedDate + (intervalMonths.toLong() * 30L * 24 * 60 * 60 * 1000)
            } else {
                0L
            }

            val entity = PeriodicMaintenanceEntity(
                id = maintenanceId,
                vehicleId = vehicle.id,
                title = title.trim(),
                description = description.trim(),
                intervalKm = intervalKm,
                intervalMonths = intervalMonths,
                lastPerformedKm = lastPerformedKm,
                lastPerformedDate = lastPerformedDate,
                targetKm = targetKm,
                targetDate = targetDate,
                category = category,
                isCustom = true
            )
            repository.savePeriodicMaintenance(entity, items)
        }
    }

    fun completeMaintenance(
        maintenance: PeriodicMaintenanceEntity,
        performedKm: Int,
        performedDate: Long,
        workshop: String,
        cost: Double,
        notes: String,
        itemsSummary: String
    ) {
        viewModelScope.launch {
            repository.completeMaintenance(
                maintenance = maintenance,
                performedKm = performedKm,
                performedDate = performedDate,
                workshop = workshop.trim(),
                cost = cost,
                notes = notes.trim(),
                itemsSummary = itemsSummary.trim()
            )
        }
    }

    fun deleteMaintenance(maintenance: PeriodicMaintenanceEntity) {
        viewModelScope.launch {
            repository.deleteMaintenance(maintenance)
        }
    }

    // Actions: Installed Components (Ficha Técnica Customizada)
    fun saveInstalledComponent(component: InstalledComponentEntity) {
        val vehicle = activeVehicle.value ?: return
        viewModelScope.launch {
            val adjusted = if (component.vehicleId == 0L) component.copy(vehicleId = vehicle.id) else component
            if (adjusted.id == 0L) {
                repository.insertComponent(adjusted)
            } else {
                repository.updateComponent(adjusted)
            }
        }
    }

    fun saveInstalledComponent(
        id: Long = 0,
        name: String,
        category: String,
        brand: String,
        model: String,
        partNumber: String,
        installedKm: Int,
        installedDate: Long,
        estimatedLifespanKm: Int,
        estimatedLifespanMonths: Int,
        cost: Double,
        notes: String = ""
    ) {
        val vehicle = activeVehicle.value ?: return
        viewModelScope.launch {
            val entity = InstalledComponentEntity(
                id = id,
                vehicleId = vehicle.id,
                name = name.trim(),
                category = category.trim(),
                brand = brand.trim(),
                model = model.trim(),
                partNumber = partNumber.trim(),
                installedKm = installedKm,
                installedDate = installedDate,
                estimatedLifespanKm = estimatedLifespanKm,
                estimatedLifespanMonths = estimatedLifespanMonths,
                cost = cost,
                notes = notes.trim()
            )
            if (id == 0L) {
                repository.insertComponent(entity)
            } else {
                repository.updateComponent(entity)
            }
        }
    }

    fun deleteInstalledComponent(component: InstalledComponentEntity) {
        viewModelScope.launch {
            repository.deleteComponent(component)
        }
    }

    // Actions: Fuel Entries
    fun addFuelEntry(
        odometerKm: Int,
        liters: Double,
        pricePerLiter: Double,
        fuelType: String,
        isFullTank: Boolean,
        gasStation: String,
        notes: String = ""
    ) {
        val vehicle = activeVehicle.value ?: return
        viewModelScope.launch {
            val entry = FuelEntryEntity(
                vehicleId = vehicle.id,
                odometerKm = odometerKm,
                liters = liters,
                pricePerLiter = pricePerLiter,
                totalPrice = liters * pricePerLiter,
                fuelType = fuelType.trim(),
                isFullTank = isFullTank,
                gasStation = gasStation.trim(),
                notes = notes.trim()
            )
            repository.insertFuelEntry(entry)
            // Update fuel tank estimated
            val currentLiters = (vehicle.currentFuelLiters + liters).coerceAtMost(vehicle.tankCapacityLiters)
            repository.updateVehicle(vehicle.copy(currentFuelLiters = currentLiters))
        }
    }

    fun deleteFuelEntry(entry: FuelEntryEntity) {
        viewModelScope.launch {
            repository.deleteFuelEntry(entry)
        }
    }

    // Actions: Expenses, Taxes & Fines
    fun addExpense(
        title: String,
        category: String,
        amount: Double,
        dueDate: Long = 0L,
        isPaid: Boolean = true,
        cnhPoints: Int = 0,
        notes: String = ""
    ) {
        val vehicle = activeVehicle.value ?: return
        viewModelScope.launch {
            val expense = VehicleExpenseEntity(
                vehicleId = vehicle.id,
                title = title.trim(),
                category = category,
                amount = amount,
                dueDate = dueDate,
                isPaid = isPaid,
                cnhPoints = cnhPoints,
                notes = notes.trim()
            )
            repository.insertExpense(expense)
        }
    }

    fun deleteExpense(expense: VehicleExpenseEntity) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }

    // Actions: Tasks / Reminders
    fun addTask(title: String, dueDate: Long, category: String, notes: String = "") {
        val vehicle = activeVehicle.value ?: return
        viewModelScope.launch {
            repository.insertTask(
                ReminderTaskEntity(
                    vehicleId = vehicle.id,
                    title = title.trim(),
                    dueDate = dueDate,
                    isDone = false,
                    category = category.trim(),
                    notes = notes.trim()
                )
            )
        }
    }

    fun toggleTaskDone(taskId: Long, isDone: Boolean) {
        viewModelScope.launch {
            repository.setTaskDone(taskId, isDone)
        }
    }

    fun deleteTask(task: ReminderTaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    // Actions: History
    fun addHistoryManual(
        title: String,
        performedKm: Int,
        performedDate: Long,
        workshop: String,
        cost: Double,
        notes: String,
        itemsSummary: String
    ) {
        val vehicle = activeVehicle.value ?: return
        viewModelScope.launch {
            repository.insertHistory(
                MaintenanceHistoryEntity(
                    vehicleId = vehicle.id,
                    title = title.trim(),
                    performedKm = performedKm,
                    performedDate = performedDate,
                    workshop = workshop.trim(),
                    totalCost = cost,
                    notes = notes.trim(),
                    itemsSummary = itemsSummary.trim()
                )
            )
            if (performedKm > vehicle.currentKm) {
                repository.updateVehicleKm(vehicle.id, performedKm)
            }
        }
    }

    fun deleteHistory(history: MaintenanceHistoryEntity) {
        viewModelScope.launch {
            repository.deleteHistory(history)
        }
    }

    // ==========================================
    // Custom Parts / Peças e Serviços Customizados
    // ==========================================
    val customParts: StateFlow<List<CustomPartEntity>> = repository.allCustomParts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun saveCustomPart(
        name: String,
        category: String = "Peças Customizadas",
        vehicleType: String = "ALL",
        isPart: Boolean = true,
        mode: String = "TROCA",
        price: Double = 0.0,
        intervalKm: Int = 10000,
        intervalMonths: Int = 6,
        brand: String = "",
        notes: String = ""
    ) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.insertCustomPart(
                CustomPartEntity(
                    name = name.trim(),
                    category = category.trim().ifBlank { "Peças Customizadas" },
                    vehicleType = vehicleType,
                    isPart = isPart,
                    defaultMode = mode,
                    estimatedPrice = price,
                    defaultIntervalKm = intervalKm,
                    defaultIntervalMonths = intervalMonths,
                    brand = brand.trim(),
                    notes = notes.trim()
                )
            )
        }
    }

    fun deleteCustomPart(part: CustomPartEntity) {
        viewModelScope.launch {
            repository.deleteCustomPart(part)
        }
    }

    // Combina o catálogo padrão do veículo com as peças customizadas do usuário
    fun getCombinedSuggestions(vehicleType: VehicleType?): List<SuggestionItem> {
        val typeStr = when (vehicleType) {
            VehicleType.CAR -> "CAR"
            VehicleType.MOTORCYCLE -> "MOTORCYCLE"
            VehicleType.BICYCLE -> "BICYCLE"
            VehicleType.E_BIKE -> "E_BIKE"
            VehicleType.TRUCK -> "TRUCK"
            else -> "ALL"
        }

        // Itens predefinidos para o tipo de veículo
        val presets = PresetSuggestions.allSuggestions.filter { item ->
            when (vehicleType) {
                VehicleType.CAR -> item.forVehicleType in listOf("CAR", "BOTH", "ALL")
                VehicleType.MOTORCYCLE -> item.forVehicleType in listOf("MOTORCYCLE", "BOTH", "ALL")
                VehicleType.BICYCLE -> item.forVehicleType in listOf("BICYCLE", "ALL")
                VehicleType.E_BIKE -> item.forVehicleType in listOf("E_BIKE", "BICYCLE", "ALL")
                VehicleType.TRUCK -> item.forVehicleType in listOf("TRUCK", "ALL")
                else -> true
            }
        }

        // Itens customizados salvos pelo usuário
        val customs = customParts.value.filter { part ->
            part.vehicleType == "ALL" || part.vehicleType == typeStr
        }.map { part ->
            SuggestionItem(
                name = part.name,
                category = part.category,
                isPart = part.isPart,
                estimatedPrice = part.estimatedPrice,
                forVehicleType = part.vehicleType,
                defaultIntervalKm = part.defaultIntervalKm,
                defaultIntervalMonths = part.defaultIntervalMonths,
                description = part.notes.ifBlank { "Peça customizada salva pelo usuário." },
                defaultMode = part.defaultMode,
                isCustom = true
            )
        }

        // Evita duplicatas de nomes entre presets e customizados
        val customNames = customs.map { it.name.lowercase().trim() }.toSet()
        val filteredPresets = presets.filterNot { it.name.lowercase().trim() in customNames }

        return customs + filteredPresets
    }
}

