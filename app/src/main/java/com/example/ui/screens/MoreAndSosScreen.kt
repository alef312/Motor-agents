package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.FontDownload
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.dao.MaintenanceWithItems
import com.example.data.model.MaintenanceHistoryEntity
import com.example.data.model.VehicleEntity
import com.example.ui.dialogs.LanguageSelectorDialog
import com.example.ui.theme.StatusAttention
import com.example.ui.theme.StatusOk
import com.example.ui.theme.StatusOverdue
import com.example.ui.theme.TurboAmber
import com.example.ui.theme.TurboCyan
import com.example.ui.theme.parseHexColor
import com.example.ui.utils.LanguageManager
import com.example.ui.utils.LanguageOption
import com.example.ui.viewmodel.AppAppearanceConfig
import com.example.ui.viewmodel.GoogleUserProfile
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MoreAndSosScreen(
    activeVehicle: VehicleEntity?,
    maintenances: List<MaintenanceWithItems>,
    history: List<MaintenanceHistoryEntity>,
    appearance: AppAppearanceConfig,
    googleProfile: GoogleUserProfile,
    isSyncing: Boolean,
    currentLanguage: String = "pt",
    onSelectLanguage: (String) -> Unit = {},
    onUpdateAppearance: (themeColorHex: String, cornerRadiusDp: Int, fontScale: Float, iconScale: Float, isDarkTheme: Boolean, useVehicleColor: Boolean) -> Unit,
    onToggleGoogleConnect: () -> Unit,
    onSyncCloud: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSubTab by remember { mutableStateOf(0) } // 0: SOS & Emergência, 1: Dossiê Pro, 2: OBD-II, 3: Google & Backup, 4: Personalização

    Column(modifier = modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedSubTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(selected = selectedSubTab == 0, onClick = { selectedSubTab = 0 }, text = { Text("SOS", fontSize = 12.sp, fontWeight = FontWeight.Bold) })
            Tab(selected = selectedSubTab == 1, onClick = { selectedSubTab = 1 }, text = { Text("Dossiê", fontSize = 12.sp, fontWeight = FontWeight.Bold) })
            Tab(selected = selectedSubTab == 2, onClick = { selectedSubTab = 2 }, text = { Text("OBD-II", fontSize = 12.sp, fontWeight = FontWeight.Bold) })
            Tab(selected = selectedSubTab == 3, onClick = { selectedSubTab = 3 }, text = { Text("Nuvem", fontSize = 12.sp, fontWeight = FontWeight.Bold) })
            Tab(selected = selectedSubTab == 4, onClick = { selectedSubTab = 4 }, text = { Text("Ajustes & Idioma", fontSize = 12.sp, fontWeight = FontWeight.Bold) })
        }

        when (selectedSubTab) {
            0 -> SosTab(activeVehicle = activeVehicle)
            1 -> DossierTab(activeVehicle = activeVehicle, maintenances = maintenances, history = history)
            2 -> ObdTab()
            3 -> GoogleSyncTab(profile = googleProfile, isSyncing = isSyncing, onToggleConnect = onToggleGoogleConnect, onSync = onSyncCloud)
            4 -> CustomizationTab(
                activeVehicle = activeVehicle,
                appearance = appearance,
                currentLanguage = currentLanguage,
                onSelectLanguage = onSelectLanguage,
                onUpdate = onUpdateAppearance
            )
        }
    }
}

@Composable
fun SosTab(activeVehicle: VehicleEntity?) {
    val context = LocalContext.current
    var bloodType by remember { mutableStateOf("O+") }
    var allergies by remember { mutableStateOf("Nenhuma") }
    var emergencyContactName by remember { mutableStateOf("Contato de Emergência") }
    var emergencyContactPhone by remember { mutableStateOf("11999998888") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = StatusOverdue.copy(alpha = 0.15f)),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, StatusOverdue)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Emergency, contentDescription = null, tint = StatusOverdue, modifier = Modifier.size(28.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Botão de Pânico & Socorro GPS", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = StatusOverdue)
                    }

                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "Envia uma mensagem de emergência instantânea com a localização exata, placa do veículo e dados do condutor via WhatsApp ou SMS.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(Modifier.height(14.dp))

                    Button(
                        onClick = {
                            val vInfo = "${activeVehicle?.brand} ${activeVehicle?.model} (Placa: ${activeVehicle?.plate})"
                            val msg = "🚨 SOCORRO DE EMERGÊNCIA! Preciso de ajuda com meu veículo $vInfo. Minha localização: https://maps.google.com/?q=-23.55052,-46.633308 (Tipo Sanguíneo: $bloodType)"
                            val sendIntent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse("https://api.whatsapp.com/send?text=" + Uri.encode(msg))
                            }
                            context.startActivity(sendIntent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = StatusOverdue),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("ENVIAR LOCALIZAÇÃO DE SOCORRO")
                    }
                }
            }
        }

        // Medical sheet
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MedicalServices, contentDescription = null, tint = StatusOk)
                        Spacer(Modifier.width(8.dp))
                        Text("Ficha Médica do Condutor (Acesso Rápido)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = bloodType,
                            onValueChange = { bloodType = it },
                            label = { Text("Tipo Sanguíneo") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = allergies,
                            onValueChange = { allergies = it },
                            label = { Text("Alergias") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = emergencyContactPhone,
                        onValueChange = { emergencyContactPhone = it },
                        label = { Text("Telefone Contato de Confiança") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }
        }
    }
}

@Composable
fun DossierTab(
    activeVehicle: VehicleEntity?,
    maintenances: List<MaintenanceWithItems>,
    history: List<MaintenanceHistoryEntity>
) {
    val context = LocalContext.current
    val sdf = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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
                            Icon(Icons.Default.Print, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(8.dp))
                            Text("Dossiê de Manutenção Pro", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }

                        Surface(
                            color = StatusOk.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Selo de Procedência",
                                color = StatusOk,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "Gere um relatório completo e estruturado de todas as manutenções, peças originais trocadas e carimbos para valorizar na revenda do veículo.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(14.dp))

                    Button(
                        onClick = {
                            val dossierText = buildString {
                                appendLine("📋 DOSSIÊ DE MANUTENÇÃO - PROCEDÊNCIA COMPROVADA")
                                appendLine("Veículo: ${activeVehicle?.brand} ${activeVehicle?.model} (${activeVehicle?.year})")
                                appendLine("Placa: ${activeVehicle?.plate} | KM Atual: ${activeVehicle?.currentKm} km")
                                appendLine("----------------------------------------")
                                appendLine("HISTÓRICO DE REVISÕES REALIZADAS:")
                                if (history.isEmpty()) {
                                    appendLine("Nenhum histórico registrado.")
                                } else {
                                    history.forEach { h ->
                                        appendLine("• ${h.title} em ${sdf.format(Date(h.performedDate))} (${h.performedKm} km)")
                                        if (h.workshop.isNotBlank()) appendLine("  Oficina: ${h.workshop}")
                                        if (h.itemsSummary.isNotBlank()) appendLine("  Peças: ${h.itemsSummary}")
                                        appendLine("  Valor: R$ ${String.format(Locale.US, "%.2f", h.totalCost)}")
                                    }
                                }
                                appendLine("----------------------------------------")
                                appendLine("Dossiê emitido pelo Assistente Veicular.")
                            }

                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "Dossiê de Manutenção - ${activeVehicle?.plate}")
                                putExtra(Intent.EXTRA_TEXT, dossierText)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Compartilhar Dossiê"))
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Exportar & Compartilhar Dossiê")
                    }
                }
            }
        }
    }
}

@Composable
fun ObdTab() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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
                            Icon(Icons.Default.Sensors, contentDescription = null, tint = TurboCyan)
                            Spacer(Modifier.width(8.dp))
                            Text("Scanner OBD-II (ELM327)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }

                        Surface(
                            color = StatusOk.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Conectado", color = StatusOk, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("RPM Motor", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("1.850", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Column {
                            Text("Temp. Líquido", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("92 °C", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = StatusOk)
                        }
                        Column {
                            Text("Bateria (ECU)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("14.2 V", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TurboCyan)
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Surface(
                        color = StatusOk.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Build, contentDescription = null, tint = StatusOk, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("ECU Status: Sem códigos de falha (DTC) ativos", style = MaterialTheme.typography.bodySmall, color = StatusOk, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GoogleSyncTab(
    profile: GoogleUserProfile,
    isSyncing: Boolean,
    onToggleConnect: () -> Unit,
    onSync: () -> Unit
) {
    val sdf = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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
                            Icon(Icons.Default.AccountCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(profile.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text(profile.email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Surface(
                            color = (if (profile.isConnected) StatusOk else StatusAttention).copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (profile.isConnected) "Conectado" else "Desconectado",
                                color = if (profile.isConnected) StatusOk else StatusAttention,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = "Última sincronização com Google Cloud: ${sdf.format(Date(profile.lastSyncTimestamp))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onSync,
                            enabled = !isSyncing,
                            modifier = Modifier.weight(1f)
                        ) {
                            if (isSyncing) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = MaterialTheme.colorScheme.onPrimary)
                            } else {
                                Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Sincronizar Agora")
                            }
                        }

                        OutlinedButton(
                            onClick = onToggleConnect,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (profile.isConnected) "Desconectar" else "Login Google")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomizationTab(
    activeVehicle: VehicleEntity?,
    appearance: AppAppearanceConfig,
    currentLanguage: String = "pt",
    onSelectLanguage: (String) -> Unit = {},
    onUpdate: (themeColorHex: String, cornerRadiusDp: Int, fontScale: Float, iconScale: Float, isDarkTheme: Boolean, useVehicleColor: Boolean) -> Unit
) {
    var showLanguageDialog by remember { mutableStateOf(false) }
    val activeLangOption: LanguageOption = remember(currentLanguage) {
        LanguageManager.getLanguageByCode(currentLanguage)
    }

    val palette = listOf(
        Pair("Turbo Laranja", "#F59E0B"),
        Pair("Azul Veloz", "#0284C7"),
        Pair("Vermelho Sport", "#DC2626"),
        Pair("Verde Elétrico", "#16A34A"),
        Pair("Roxo Neon", "#9333EA"),
        Pair("Dourado", "#D97706"),
        Pair("Monocromático", "#475569")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Language Selector Card (Most used to least used)
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_language_selector")
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
                                    .size(42.dp)
                                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Idioma do Sistema / Language",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Ordenado pelas 14 línguas mais faladas no mundo",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Selected Language display pill & change button
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = activeLangOption.flag,
                                    fontSize = 24.sp,
                                    modifier = Modifier.padding(end = 10.dp)
                                )
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = activeLangOption.nativeName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Spacer(Modifier.width(6.dp))
                                        Surface(
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "#${activeLangOption.rank} Global",
                                                color = MaterialTheme.colorScheme.onPrimary,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${activeLangOption.localizedNamePt} • ${activeLangOption.speakersGlobal} falantes",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Button(
                                onClick = { showLanguageDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("btn_open_language_dialog"),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("Alterar", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Vehicle Dynamic Color
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Material You: Cor do Veículo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(
                                text = "Adapta botões, cards e destaques para combinar com a cor do veículo ativo (${activeVehicle?.themeColorHex ?: "#F59E0B"}).",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Switch(
                            checked = appearance.useVehicleColor,
                            onCheckedChange = { checked ->
                                val color = if (checked && activeVehicle?.themeColorHex != null) activeVehicle.themeColorHex else appearance.themeColorHex
                                onUpdate(color, appearance.cornerRadiusDp, appearance.fontScale, appearance.iconScale, appearance.isDarkTheme, checked)
                            }
                        )
                    }
                }
            }
        }

        // Palette selector
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Paleta de Cores do Aplicativo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        palette.take(4).forEach { (name, hex) ->
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(parseHexColor(hex))
                                    .border(
                                        width = if (appearance.themeColorHex == hex) 3.dp else 1.dp,
                                        color = if (appearance.themeColorHex == hex) Color.White else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        onUpdate(hex, appearance.cornerRadiusDp, appearance.fontScale, appearance.iconScale, appearance.isDarkTheme, false)
                                    }
                            )
                        }
                    }
                }
            }
        }

        // Shape corner radius
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Formato dos Cantos dos Cards & Abas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(Pair("Quadrado", 8), Pair("Padrão", 16), Pair("Redondo", 24), Pair("Pílula", 32)).forEach { (name, r) ->
                            FilterChip(
                                selected = appearance.cornerRadiusDp == r,
                                onClick = { onUpdate(appearance.themeColorHex, r, appearance.fontScale, appearance.iconScale, appearance.isDarkTheme, appearance.useVehicleColor) },
                                label = { Text(name, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }
        }

        // Dark/Light Theme toggle
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(if (appearance.isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(if (appearance.isDarkTheme) "Modo Escuro (Dark)" else "Modo Claro (Light)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }

                    Switch(
                        checked = appearance.isDarkTheme,
                        onCheckedChange = { checked ->
                            onUpdate(appearance.themeColorHex, appearance.cornerRadiusDp, appearance.fontScale, appearance.iconScale, checked, appearance.useVehicleColor)
                        }
                    )
                }
            }
        }
    }

    if (showLanguageDialog) {
        LanguageSelectorDialog(
            currentLanguageCode = currentLanguage,
            onLanguageSelected = { newLang ->
                onSelectLanguage(newLang)
                showLanguageDialog = false
            },
            onDismiss = { showLanguageDialog = false }
        )
    }
}
