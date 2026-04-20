package com.payroll.android.ui.admin.more

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.payroll.android.ui.theme.*

data class MoreItem(val icon: ImageVector, val label: String, val route: String)

val adminMoreItems = listOf(
    MoreItem(Icons.Default.Receipt, "Giao dịch", "admin_transactions"),
    MoreItem(Icons.Default.Book, "Sổ cái", "admin_ledger"),
    MoreItem(Icons.Default.AccountBalance, "Khoản vay", "admin_loans"),
    MoreItem(Icons.Default.Payments, "Ứng lương", "admin_advance_payments"),
    MoreItem(Icons.Default.People, "Người dùng", "admin_users"),
    MoreItem(Icons.Default.Settings, "Cài đặt", "admin_settings"),
    MoreItem(Icons.Default.MonitorHeart, "Sức khỏe hệ thống", "admin_system_health")
)

val partnerMoreItems = listOf(
    MoreItem(Icons.Default.People, "Nhân viên", "partner_employees"),
    MoreItem(Icons.Default.Settings, "Cài đặt", "admin_settings")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMoreScreen(
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Thêm", fontWeight = FontWeight.Bold) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = White)) },
        containerColor = Gray50
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            items(adminMoreItems) { item ->
                Surface(
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    color = White,
                    modifier = Modifier.clickable { onNavigate(item.route) }
                ) {
                    Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(item.icon, null, tint = Sky500)
                        Spacer(Modifier.width(16.dp))
                        Text(item.label, fontWeight = FontWeight.Medium, color = Gray800)
                        Spacer(Modifier.weight(1f))
                        Icon(Icons.Default.ChevronRight, null, tint = Gray300)
                    }
                }
            }
            item { Spacer(Modifier.height(16.dp)) }
            item {
                OutlinedButton(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Red500)
                ) {
                    Icon(Icons.Default.Logout, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Đăng xuất", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartnerMoreScreen(
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Thêm", fontWeight = FontWeight.Bold) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = White)) },
        containerColor = Gray50
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            items(partnerMoreItems) { item ->
                Surface(
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    color = White,
                    modifier = Modifier.clickable { onNavigate(item.route) }
                ) {
                    Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(item.icon, null, tint = Sky500)
                        Spacer(Modifier.width(16.dp))
                        Text(item.label, fontWeight = FontWeight.Medium, color = Gray800)
                        Spacer(Modifier.weight(1f))
                        Icon(Icons.Default.ChevronRight, null, tint = Gray300)
                    }
                }
            }
            item { Spacer(Modifier.height(16.dp)) }
            item {
                OutlinedButton(onClick = onLogout, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.outlinedButtonColors(contentColor = Red500)) {
                    Icon(Icons.Default.Logout, null); Spacer(Modifier.width(8.dp)); Text("Đăng xuất", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
