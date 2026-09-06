package com.example.data.repository

import com.example.data.local.dao.CustomPartDao
import com.example.data.local.dao.FuelEntryDao
import com.example.data.local.dao.InstalledComponentDao
import com.example.data.local.dao.MaintenanceHistoryDao
import com.example.data.local.dao.MaintenanceItemDao
import com.example.data.local.dao.MaintenanceWithItems
import com.example.data.local.dao.PeriodicMaintenanceDao
import com.example.data.local.dao.ReminderTaskDao
import com.example.data.local.dao.VehicleDao
import com.example.data.local.dao.VehicleExpenseDao
import com.example.data.model.CustomPartEntity
import com.example.data.model.FuelEntryEntity
import com.example.data.model.InstalledComponentEntity
import com.example.data.model.MaintenanceHistoryEntity
import com.example.data.model.MaintenanceItemEntity
import com.example.data.model.PeriodicMaintenanceEntity
import com.example.data.model.ReminderTaskEntity
import com.example.data.model.VehicleEntity
import com.example.data.model.VehicleExpenseEntity
import kotlinx.coroutines.flow.Flow

class VehicleMaintenanceRepository(
    private val vehicleDao: VehicleDao,
    private val periodicMaintenanceDao: PeriodicMaintenanceDao,
    private val maintenanceItemDao: MaintenanceItemDao,
    private val maintenanceHistoryDao: MaintenanceHistoryDao,
    private val reminderTaskDao: ReminderTaskDao,
    private val installedComponentDao: InstalledComponentDao,
    private val fuelEntryDao: FuelEntryDao,
    private val vehicleExpenseDao: VehicleExpenseDao,
    private val customPartDao: CustomPartDao
) {
    // Vehicles
    val allVehicles: Flow<List<VehicleEntity>> = vehicleDao.getAllVehicles()

    fun getVehicleById(id: Long): Flow<VehicleEntity?> = vehicleDao.getVehicleById(id)

    suspend fun insertVehicle(vehicle: VehicleEntity): Long = vehicleDao.insertVehicle(vehicle)

    suspend fun updateVehicle(vehicle: VehicleEntity) = vehicleDao.updateVehicle(vehicle)

    suspend fun updateVehicleKm(vehicleId: Long, newKm: Int) = vehicleDao.updateVehicleKm(vehicleId, newKm)

    suspend fun deleteVehicle(vehicle: VehicleEntity) = vehicleDao.deleteVehicle(vehicle)

    // Maintenances
    fun getMaintenancesWithItems(vehicleId: Long): Flow<List<MaintenanceWithItems>> =
        periodicMaintenanceDao.getMaintenancesWithItemsByVehicle(vehicleId)

    fun getMaintenanceWithItemsById(id: Long): Flow<MaintenanceWithItems?> =
        periodicMaintenanceDao.getMaintenanceWithItemsById(id)

    suspend fun savePeriodicMaintenance(
        maintenance: PeriodicMaintenanceEntity,
        items: List<MaintenanceItemEntity>
    ): Long {
        val maintenanceId = if (maintenance.id == 0L) {
            periodicMaintenanceDao.insertMaintenance(maintenance)
        } else {
            periodicMaintenanceDao.updateMaintenance(maintenance)
            maintenanceItemDao.deleteItemsForMaintenance(maintenance.id)
            maintenance.id
        }

        val itemsToInsert = items.map { it.copy(maintenanceId = maintenanceId, id = 0L) }
        if (itemsToInsert.isNotEmpty()) {
            maintenanceItemDao.insertItems(itemsToInsert)
        }
        return maintenanceId
    }

    suspend fun deleteMaintenance(maintenance: PeriodicMaintenanceEntity) {
        periodicMaintenanceDao.deleteMaintenance(maintenance)
    }

    // Complete / Execute Maintenance: updates periodic revision to next cycle and adds to history
    suspend fun completeMaintenance(
        maintenance: PeriodicMaintenanceEntity,
        performedKm: Int,
        performedDate: Long,
        workshop: String,
        cost: Double,
        notes: String,
        itemsSummary: String
    ) {
        // 1. Calculate new cycle target
        val newTargetKm = if (maintenance.intervalKm > 0) performedKm + maintenance.intervalKm else 0
        val newTargetDate = if (maintenance.intervalMonths > 0) {
            performedDate + (maintenance.intervalMonths.toLong() * 30L * 24 * 60 * 60 * 1000)
        } else {
            0L
        }

        val updatedMaintenance = maintenance.copy(
            lastPerformedKm = performedKm,
            lastPerformedDate = performedDate,
            targetKm = newTargetKm,
            targetDate = newTargetDate
        )
        periodicMaintenanceDao.updateMaintenance(updatedMaintenance)

        // 2. Also update current vehicle km if performedKm is higher
        val currentVehicle = vehicleDao.getVehicleByIdOnce(maintenance.vehicleId)
        if (currentVehicle != null && performedKm > currentVehicle.currentKm) {
            vehicleDao.updateVehicleKm(maintenance.vehicleId, performedKm)
        }

        // 3. Register history
        val historyEntry = MaintenanceHistoryEntity(
            vehicleId = maintenance.vehicleId,
            maintenanceId = maintenance.id,
            title = maintenance.title,
            performedKm = performedKm,
            performedDate = performedDate,
            workshop = workshop,
            totalCost = cost,
            notes = notes,
            itemsSummary = itemsSummary
        )
        maintenanceHistoryDao.insertHistory(historyEntry)
    }

    // History
    fun getHistoryByVehicle(vehicleId: Long): Flow<List<MaintenanceHistoryEntity>> =
        maintenanceHistoryDao.getHistoryByVehicle(vehicleId)

    suspend fun insertHistory(history: MaintenanceHistoryEntity): Long =
        maintenanceHistoryDao.insertHistory(history)

    suspend fun deleteHistory(history: MaintenanceHistoryEntity) =
        maintenanceHistoryDao.deleteHistory(history)

    // Tasks / Reminders
    fun getTasksByVehicle(vehicleId: Long): Flow<List<ReminderTaskEntity>> =
        reminderTaskDao.getTasksByVehicle(vehicleId)

    suspend fun insertTask(task: ReminderTaskEntity): Long =
        reminderTaskDao.insertTask(task)

    suspend fun updateTask(task: ReminderTaskEntity) =
        reminderTaskDao.updateTask(task)

    suspend fun setTaskDone(taskId: Long, isDone: Boolean) =
        reminderTaskDao.setTaskDone(taskId, isDone)

    suspend fun deleteTask(task: ReminderTaskEntity) =
        reminderTaskDao.deleteTask(task)

    // Installed Components (Ficha Técnica Customizada)
    fun getComponentsByVehicle(vehicleId: Long): Flow<List<InstalledComponentEntity>> =
        installedComponentDao.getComponentsByVehicle(vehicleId)

    suspend fun insertComponent(component: InstalledComponentEntity): Long =
        installedComponentDao.insertComponent(component)

    suspend fun updateComponent(component: InstalledComponentEntity) =
        installedComponentDao.updateComponent(component)

    suspend fun deleteComponent(component: InstalledComponentEntity) =
        installedComponentDao.deleteComponent(component)

    // Fuel Entries (Abastecimentos & Consumo)
    fun getFuelEntriesByVehicle(vehicleId: Long): Flow<List<FuelEntryEntity>> =
        fuelEntryDao.getFuelEntriesByVehicle(vehicleId)

    suspend fun insertFuelEntry(entry: FuelEntryEntity): Long {
        val id = fuelEntryDao.insertFuelEntry(entry)
        val vehicle = vehicleDao.getVehicleByIdOnce(entry.vehicleId)
        if (vehicle != null && entry.odometerKm > vehicle.currentKm) {
            vehicleDao.updateVehicleKm(entry.vehicleId, entry.odometerKm)
        }
        return id
    }

    suspend fun updateFuelEntry(entry: FuelEntryEntity) =
        fuelEntryDao.updateFuelEntry(entry)

    suspend fun deleteFuelEntry(entry: FuelEntryEntity) =
        fuelEntryDao.deleteFuelEntry(entry)

    // Vehicle Expenses, Fines & Taxes (Custos, IPVA, Seguro, Multas)
    fun getExpensesByVehicle(vehicleId: Long): Flow<List<VehicleExpenseEntity>> =
        vehicleExpenseDao.getExpensesByVehicle(vehicleId)

    suspend fun insertExpense(expense: VehicleExpenseEntity): Long =
        vehicleExpenseDao.insertExpense(expense)

    suspend fun updateExpense(expense: VehicleExpenseEntity) =
        vehicleExpenseDao.updateExpense(expense)

    suspend fun deleteExpense(expense: VehicleExpenseEntity) =
        vehicleExpenseDao.deleteExpense(expense)

    // Custom Parts / Catálogo Personalizado
    val allCustomParts: Flow<List<CustomPartEntity>> = customPartDao.getAllCustomParts()

    fun getCustomPartsByVehicleType(vehicleType: String): Flow<List<CustomPartEntity>> =
        customPartDao.getCustomPartsByVehicleType(vehicleType)

    fun searchCustomParts(query: String): Flow<List<CustomPartEntity>> =
        customPartDao.searchCustomParts(query)

    suspend fun insertCustomPart(part: CustomPartEntity): Long =
        customPartDao.insertCustomPart(part)

    suspend fun updateCustomPart(part: CustomPartEntity) =
        customPartDao.updateCustomPart(part)

    suspend fun deleteCustomPart(part: CustomPartEntity) =
        customPartDao.deleteCustomPart(part)
}
