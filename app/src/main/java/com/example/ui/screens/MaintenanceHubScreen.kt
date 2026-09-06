package com.example.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.RevisionTemplate
import com.example.data.local.SuggestionItem
import com.example.data.local.dao.MaintenanceWithItems
import com.example.data.model.CustomPartEntity
import com.example.data.model.InstalledComponentEntity
import com.example.data.model.MaintenanceHistoryEntity
import com.example.data.model.PeriodicMaintenanceEntity
import com.example.data.model.ReminderTaskEntity
import com.example.data.model.VehicleEntity

@Composable
fun MaintenanceHubScreen(
    activeVehicle: VehicleEntity?,
    maintenances: List<MaintenanceWithItems>,
    installedComponents: List<InstalledComponentEntity>,
    historyList: List<MaintenanceHistoryEntity>,
    tasks: List<ReminderTaskEntity>,
    customParts: List<CustomPartEntity> = emptyList(),
    onCreateRevision: () -> Unit,
    onEditRevision: (MaintenanceWithItems) -> Unit,
    onDeleteRevision: (PeriodicMaintenanceEntity) -> Unit,
    onCompleteRevision: (MaintenanceWithItems) -> Unit,
    onApplyTemplate: (RevisionTemplate) -> Unit,
    onApplySingleItem: (SuggestionItem) -> Unit,
    onSaveCustomPart: (CustomPartEntity) -> Unit = {},
    onDeleteCustomPart: (CustomPartEntity) -> Unit = {},
    onAddComponent: (InstalledComponentEntity) -> Unit,
    onUpdateComponent: (InstalledComponentEntity) -> Unit,
    onDeleteComponent: (InstalledComponentEntity) -> Unit,
    onAddHistory: () -> Unit,
    onDeleteHistory: (MaintenanceHistoryEntity) -> Unit,
    onAddTask: () -> Unit,
    onToggleTask: (Long, Boolean) -> Unit,
    onDeleteTask: (ReminderTaskEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }

    Column(modifier = modifier.fillMaxSize()) {
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            edgePadding = 12.dp,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Revisões", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Peças Ativas (${installedComponents.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Sugestões & Peças", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
            )
            Tab(
                selected = selectedTab == 3,
                onClick = { selectedTab = 3 },
                text = { Text("Histórico (${historyList.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
            )
            Tab(
                selected = selectedTab == 4,
                onClick = { selectedTab = 4 },
                text = { Text("Lembretes (${tasks.count { !it.isDone }})", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
            )
        }

        when (selectedTab) {
            0 -> PeriodicMaintenanceScreen(
                activeVehicle = activeVehicle,
                maintenances = maintenances,
                onCreateRevision = onCreateRevision,
                onEditRevision = onEditRevision,
                onDeleteRevision = onDeleteRevision,
                onCompleteRevision = onCompleteRevision,
                onOpenSuggestions = { selectedTab = 2 }
            )
            1 -> InstalledComponentsScreen(
                activeVehicle = activeVehicle,
                components = installedComponents,
                onAddComponent = onAddComponent,
                onUpdateComponent = onUpdateComponent,
                onDeleteComponent = onDeleteComponent
            )
            2 -> SuggestionsCatalogScreen(
                activeVehicle = activeVehicle,
                customParts = customParts,
                onApplyTemplate = onApplyTemplate,
                onApplySingleItem = onApplySingleItem,
                onSaveCustomPart = onSaveCustomPart,
                onDeleteCustomPart = onDeleteCustomPart
            )
            3 -> HistoryScreen(
                activeVehicle = activeVehicle,
                historyList = historyList,
                onAddHistory = onAddHistory,
                onDeleteHistory = onDeleteHistory
            )
            4 -> TasksScreen(
                activeVehicle = activeVehicle,
                tasks = tasks,
                onAddTask = onAddTask,
                onToggleTask = onToggleTask,
                onDeleteTask = onDeleteTask
            )
        }
    }
}
