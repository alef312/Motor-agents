package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MaintenanceStatus
import com.example.ui.theme.StatusAttention
import com.example.ui.theme.StatusAttentionContainer
import com.example.ui.theme.StatusOk
import com.example.ui.theme.StatusOkContainer
import com.example.ui.theme.StatusOverdue
import com.example.ui.theme.StatusOverdueContainer
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatCurrency(value: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    return format.format(value)
}

fun formatDate(timestamp: Long): String {
    if (timestamp <= 0) return "--/--/----"
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    return sdf.format(Date(timestamp))
}

fun formatKm(km: Int): String {
    val format = NumberFormat.getIntegerInstance(Locale("pt", "BR"))
    return "${format.format(km)} km"
}

@Composable
fun StatusBadge(
    status: MaintenanceStatus,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, icon, label) = when (status) {
        MaintenanceStatus.OK -> Quadruple(
            StatusOkContainer,
            StatusOk,
            Icons.Default.CheckCircle,
            "Em dia"
        )
        MaintenanceStatus.ATTENTION -> Quadruple(
            StatusAttentionContainer,
            StatusAttention,
            Icons.Default.Warning,
            "Atenção"
        )
        MaintenanceStatus.OVERDUE -> Quadruple(
            StatusOverdueContainer,
            StatusOverdue,
            Icons.Default.Error,
            "Vencida"
        )
    }

    Surface(
        color = bgColor.copy(alpha = 0.35f),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                color = textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
