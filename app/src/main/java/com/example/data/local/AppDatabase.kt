package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.CustomPartDao
import com.example.data.local.dao.FuelEntryDao
import com.example.data.local.dao.InstalledComponentDao
import com.example.data.local.dao.MaintenanceHistoryDao
import com.example.data.local.dao.MaintenanceItemDao
import com.example.data.local.dao.PeriodicMaintenanceDao
import com.example.data.local.dao.ReminderTaskDao
import com.example.data.local.dao.VehicleDao
import com.example.data.local.dao.VehicleExpenseDao
import com.example.data.model.CustomPartEntity
import com.example.data.model.ExpenseCategory
import com.example.data.model.FuelEntryEntity
import com.example.data.model.InstalledComponentEntity
import com.example.data.model.MaintenanceHistoryEntity
import com.example.data.model.MaintenanceItemEntity
import com.example.data.model.PeriodicMaintenanceEntity
import com.example.data.model.ReminderTaskEntity
import com.example.data.model.VehicleEntity
import com.example.data.model.VehicleExpenseEntity
import com.example.data.model.VehicleType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        VehicleEntity::class,
        PeriodicMaintenanceEntity::class,
        MaintenanceItemEntity::class,
        MaintenanceHistoryEntity::class,
        ReminderTaskEntity::class,
        InstalledComponentEntity::class,
        FuelEntryEntity::class,
        VehicleExpenseEntity::class,
        CustomPartEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun vehicleDao(): VehicleDao
    abstract fun periodicMaintenanceDao(): PeriodicMaintenanceDao
    abstract fun maintenanceItemDao(): MaintenanceItemDao
    abstract fun maintenanceHistoryDao(): MaintenanceHistoryDao
    abstract fun reminderTaskDao(): ReminderTaskDao
    abstract fun installedComponentDao(): InstalledComponentDao
    abstract fun fuelEntryDao(): FuelEntryDao
    abstract fun vehicleExpenseDao(): VehicleExpenseDao
    abstract fun customPartDao(): CustomPartDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "automoto_revisoes.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }
        }

        suspend fun populateInitialData(database: AppDatabase) {
            val vehicleDao = database.vehicleDao()
            val maintenanceDao = database.periodicMaintenanceDao()
            val itemDao = database.maintenanceItemDao()
            val historyDao = database.maintenanceHistoryDao()
            val taskDao = database.reminderTaskDao()

            // 1. Initial Car
            val carId = vehicleDao.insertVehicle(
                VehicleEntity(
                    name = "Civic Touring",
                    type = VehicleType.CAR.name,
                    brand = "Honda",
                    model = "Civic 1.5 Turbo Touring",
                    year = 2021,
                    plate = "BRA-2E19",
                    currentKm = 42500,
                    fuelType = "Gasolina",
                    color = "Cinza Bário"
                )
            )

            // 2. Initial Motorcycle
            val motoId = vehicleDao.insertVehicle(
                VehicleEntity(
                    name = "MT-07 Yamaha",
                    type = VehicleType.MOTORCYCLE.name,
                    brand = "Yamaha",
                    model = "MT-07 ABS",
                    year = 2022,
                    plate = "MOT-7X20",
                    currentKm = 14200,
                    fuelType = "Gasolina",
                    color = "Icon Blue"
                )
            )

            // Periodic Revisions for Car
            val carRev1Id = maintenanceDao.insertMaintenance(
                PeriodicMaintenanceEntity(
                    vehicleId = carId,
                    title = "Troca de Óleo & Filtros (10.000 km)",
                    description = "Óleo sintético 0W20 e substituição dos filtros de óleo e combustível.",
                    intervalKm = 10000,
                    intervalMonths = 12,
                    lastPerformedKm = 40000,
                    lastPerformedDate = System.currentTimeMillis() - (60L * 24 * 60 * 60 * 1000),
                    targetKm = 50000,
                    targetDate = System.currentTimeMillis() + (305L * 24 * 60 * 60 * 1000),
                    category = "Motor & Óleo"
                )
            )

            itemDao.insertItems(
                listOf(
                    MaintenanceItemEntity(
                        maintenanceId = carRev1Id,
                        name = "Óleo 0W20 100% Sintético (4L)",
                        category = "Motor & Óleo",
                        isPart = true,
                        estimatedPrice = 240.0
                    ),
                    MaintenanceItemEntity(
                        maintenanceId = carRev1Id,
                        name = "Filtro de Óleo Blindado",
                        category = "Motor & Óleo",
                        isPart = true,
                        estimatedPrice = 45.0
                    ),
                    MaintenanceItemEntity(
                        maintenanceId = carRev1Id,
                        name = "Filtro de Cabine / Ar-Condicionado",
                        category = "Climatização",
                        isPart = true,
                        estimatedPrice = 55.0
                    ),
                    MaintenanceItemEntity(
                        maintenanceId = carRev1Id,
                        name = "Mão de Obra Troca de Óleo e Inspeção",
                        category = "Motor & Óleo",
                        isPart = false,
                        estimatedPrice = 70.0
                    )
                )
            )

            val carRev2Id = maintenanceDao.insertMaintenance(
                PeriodicMaintenanceEntity(
                    vehicleId = carId,
                    title = "Alinhamento, Balanceamento & Rodízio",
                    description = "Geometria 3D, calibração e rodízio dos 4 pneus.",
                    intervalKm = 10000,
                    intervalMonths = 6,
                    lastPerformedKm = 35000,
                    lastPerformedDate = System.currentTimeMillis() - (150L * 24 * 60 * 60 * 1000),
                    targetKm = 45000,
                    targetDate = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000),
                    category = "Suspensão"
                )
            )

            itemDao.insertItems(
                listOf(
                    MaintenanceItemEntity(
                        maintenanceId = carRev2Id,
                        name = "Alinhamento e Balanceamento 4 Rodas",
                        category = "Suspensão",
                        isPart = false,
                        estimatedPrice = 110.0
                    ),
                    MaintenanceItemEntity(
                        maintenanceId = carRev2Id,
                        name = "Rodízio em X dos Pneus",
                        category = "Suspensão",
                        isPart = false,
                        estimatedPrice = 40.0
                    )
                )
            )

            // Periodic Revisions for Moto
            val motoRev1Id = maintenanceDao.insertMaintenance(
                PeriodicMaintenanceEntity(
                    vehicleId = motoId,
                    title = "Revisão dos 15.000 km & Relação",
                    description = "Óleo 10W40 Yamalube, filtro, limpeza e ajuste da corrente de transmissão.",
                    intervalKm = 5000,
                    intervalMonths = 6,
                    lastPerformedKm = 10000,
                    lastPerformedDate = System.currentTimeMillis() - (120L * 24 * 60 * 60 * 1000),
                    targetKm = 15000,
                    targetDate = System.currentTimeMillis() + (60L * 24 * 60 * 60 * 1000),
                    category = "Geral"
                )
            )

            itemDao.insertItems(
                listOf(
                    MaintenanceItemEntity(
                        maintenanceId = motoRev1Id,
                        name = "Óleo Yamalube 10W40 4T (2.6L)",
                        category = "Motor & Óleo",
                        isPart = true,
                        estimatedPrice = 160.0
                    ),
                    MaintenanceItemEntity(
                        maintenanceId = motoRev1Id,
                        name = "Filtro de Óleo HF204",
                        category = "Motor & Óleo",
                        isPart = true,
                        estimatedPrice = 50.0
                    ),
                    MaintenanceItemEntity(
                        maintenanceId = motoRev1Id,
                        name = "Lubrificação e Regulagem da Corrente",
                        category = "Transmissão",
                        isPart = false,
                        estimatedPrice = 35.0
                    )
                )
            )

            // Initial History Records
            historyDao.insertHistory(
                MaintenanceHistoryEntity(
                    vehicleId = carId,
                    title = "Troca de Óleo e Filtros (40.000 km)",
                    performedKm = 40000,
                    performedDate = System.currentTimeMillis() - (60L * 24 * 60 * 60 * 1000),
                    workshop = "Concessionária Honda",
                    totalCost = 390.00,
                    notes = "Troca completa sem pendências. Próxima em 50.000 km.",
                    itemsSummary = "Óleo 0W20 Sintético, Filtro de Óleo, Filtro de Cabine"
                )
            )

            historyDao.insertHistory(
                MaintenanceHistoryEntity(
                    vehicleId = motoId,
                    title = "Revisão dos 10.000 km Realizada",
                    performedKm = 10000,
                    performedDate = System.currentTimeMillis() - (120L * 24 * 60 * 60 * 1000),
                    workshop = "Yamaha Red Baron",
                    totalCost = 280.00,
                    notes = "Verificada folga de corrente e pastilhas com meia vida.",
                    itemsSummary = "Óleo Yamalube 10W40, Filtro de Óleo, Ajuste da relação"
                )
            )

            // Initial Tasks/Reminders
            taskDao.insertTask(
                ReminderTaskEntity(
                    vehicleId = carId,
                    title = "Calibrar pneus dianteiros (32 PSI) e traseiros (30 PSI)",
                    dueDate = System.currentTimeMillis() + (3L * 24 * 60 * 60 * 1000),
                    isDone = false,
                    category = "Segurança",
                    notes = "Calibrar a frio no posto Ipiranga"
                )
            )

            taskDao.insertTask(
                ReminderTaskEntity(
                    vehicleId = motoId,
                    title = "Limpar e passar spray C4 na corrente",
                    dueDate = System.currentTimeMillis() + (2L * 24 * 60 * 60 * 1000),
                    isDone = false,
                    category = "Transmissão",
                    notes = "Usar desengraxante suave e lubrificante específico"
                )
            )

            // 3. Initial Truck (Caminhão)
            val truckId = vehicleDao.insertVehicle(
                VehicleEntity(
                    name = "Scania R450",
                    type = VehicleType.TRUCK.name,
                    brand = "Scania",
                    model = "R 450 6x2 Highline",
                    year = 2020,
                    plate = "SCA-4R50",
                    currentKm = 285400,
                    fuelType = "Diesel S10",
                    color = "Branco Polar",
                    nickname = "Gigante da Estrada",
                    themeColorHex = "#1E88E5",
                    tankCapacityLiters = 400.0,
                    currentFuelLiters = 290.0,
                    fipeValue = 540000.0,
                    insuranceCompany = "SulAmérica Frota",
                    insurancePolicyNumber = "SCN-9941-20",
                    insuranceEmergencyPhone = "0800 701 4000"
                )
            )

            // 4. Initial E-Bike (Bicicleta Elétrica)
            val ebikeId = vehicleDao.insertVehicle(
                VehicleEntity(
                    name = "Oggi Big Wheel E-Bike",
                    type = VehicleType.E_BIKE.name,
                    brand = "Oggi",
                    model = "Big Wheel 8.2 29",
                    year = 2023,
                    plate = "",
                    currentKm = 1850,
                    fuelType = "Elétrica",
                    color = "Verde Floresta",
                    nickname = "Comutadora",
                    themeColorHex = "#2E7D32",
                    tankCapacityLiters = 100.0, // 100% bateria
                    currentFuelLiters = 85.0
                )
            )

            // Initial Installed Components
            val componentDao = database.installedComponentDao()
            componentDao.insertComponents(
                listOf(
                    InstalledComponentEntity(
                        vehicleId = carId,
                        name = "Jogo de Pneus Michelin Primacy 4",
                        category = "Pneus",
                        brand = "Michelin",
                        model = "Primacy 4 215/50 R17",
                        partNumber = "MICH-P4-17",
                        installedKm = 30000,
                        installedDate = System.currentTimeMillis() - (200L * 24 * 60 * 60 * 1000),
                        estimatedLifespanKm = 50000,
                        estimatedLifespanMonths = 48,
                        cost = 2480.0,
                        notes = "Excelente aderência no molhado. Calibrar a cada 15 dias."
                    ),
                    InstalledComponentEntity(
                        vehicleId = carId,
                        name = "Pastilhas de Freio Dianteiras Cerâmica",
                        category = "Freios",
                        brand = "Ferodo",
                        model = "Ceramic Premier",
                        partNumber = "FDB-4412",
                        installedKm = 38000,
                        installedDate = System.currentTimeMillis() - (90L * 24 * 60 * 60 * 1000),
                        estimatedLifespanKm = 25000,
                        estimatedLifespanMonths = 24,
                        cost = 320.0,
                        notes = "Baixa emissão de fuligem e sem ruído."
                    ),
                    InstalledComponentEntity(
                        vehicleId = motoId,
                        name = "Pneu Traseiro Michelin Road 5",
                        category = "Pneus",
                        brand = "Michelin",
                        model = "Road 5 180/55 ZR17",
                        partNumber = "MIC-RD5-180",
                        installedKm = 10000,
                        installedDate = System.currentTimeMillis() - (120L * 24 * 60 * 60 * 1000),
                        estimatedLifespanKm = 12000,
                        estimatedLifespanMonths = 24,
                        cost = 980.0,
                        notes = "Bicomposto esportivo para turismo."
                    ),
                    InstalledComponentEntity(
                        vehicleId = motoId,
                        name = "Kit Relação Corrente D.I.D com Retentor",
                        category = "Transmissão",
                        brand = "D.I.D / JT",
                        model = "525VX3 Gold O-Ring",
                        partNumber = "DID-525-120",
                        installedKm = 8000,
                        installedDate = System.currentTimeMillis() - (180L * 24 * 60 * 60 * 1000),
                        estimatedLifespanKm = 20000,
                        estimatedLifespanMonths = 36,
                        cost = 650.0,
                        notes = "Lubrificação a cada 500 km."
                    )
                )
            )

            // Initial Fuel Entries
            val fuelDao = database.fuelEntryDao()
            fuelDao.insertFuelEntries(
                listOf(
                    FuelEntryEntity(
                        vehicleId = carId,
                        date = System.currentTimeMillis() - (25L * 24 * 60 * 60 * 1000),
                        odometerKm = 41950,
                        liters = 42.0,
                        pricePerLiter = 5.89,
                        fuelType = "Gasolina Comum",
                        gasStation = "Posto Shell Aeroporto",
                        isFullTank = true
                    ),
                    FuelEntryEntity(
                        vehicleId = carId,
                        date = System.currentTimeMillis() - (5L * 24 * 60 * 60 * 1000),
                        odometerKm = 42500,
                        liters = 40.5,
                        pricePerLiter = 5.92,
                        fuelType = "Gasolina V-Power",
                        gasStation = "Posto Shell Morumbi",
                        isFullTank = true
                    ),
                    FuelEntryEntity(
                        vehicleId = motoId,
                        date = System.currentTimeMillis() - (8L * 24 * 60 * 60 * 1000),
                        odometerKm = 14200,
                        liters = 12.0,
                        pricePerLiter = 5.89,
                        fuelType = "Gasolina Podium",
                        gasStation = "BR Petrobras",
                        isFullTank = true
                    )
                )
            )

            // Initial Vehicle Expenses & Fines
            val expenseDao = database.vehicleExpenseDao()
            expenseDao.insertExpenses(
                listOf(
                    VehicleExpenseEntity(
                        vehicleId = carId,
                        title = "IPVA 2024 (Parcela Única c/ Desconto)",
                        category = ExpenseCategory.IPVA.name,
                        amount = 3250.0,
                        date = System.currentTimeMillis() - (180L * 24 * 60 * 60 * 1000),
                        isPaid = true
                    ),
                    VehicleExpenseEntity(
                        vehicleId = carId,
                        title = "Seguro Auto Total Tokio Marine",
                        category = ExpenseCategory.INSURANCE.name,
                        amount = 2800.0,
                        date = System.currentTimeMillis() - (150L * 24 * 60 * 60 * 1000),
                        isPaid = true
                    ),
                    VehicleExpenseEntity(
                        vehicleId = carId,
                        title = "Pedágio Rodovia dos Imigrantes",
                        category = ExpenseCategory.TOLL.name,
                        amount = 33.80,
                        date = System.currentTimeMillis() - (12L * 24 * 60 * 60 * 1000),
                        isPaid = true
                    ),
                    VehicleExpenseEntity(
                        vehicleId = motoId,
                        title = "Seguro Contra Terceiros & Roubo Suhai",
                        category = ExpenseCategory.INSURANCE.name,
                        amount = 1450.0,
                        date = System.currentTimeMillis() - (110L * 24 * 60 * 60 * 1000),
                        isPaid = true
                    )
                )
            )
        }
    }
}
