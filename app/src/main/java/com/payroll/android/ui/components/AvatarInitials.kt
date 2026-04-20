package com.payroll.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.payroll.android.ui.theme.*

@Composable
fun AvatarInitials(
    name: String?,
    modifier: Modifier = Modifier
) {
    val initials = (name?.trim()?.split(" ")?.mapNotNull { it.firstOrNull()?.uppercase() }
        ?.takeLast(2)?.joinToString("") ?: "?")

    Box(
        modifier = modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(Sky400, Sky600))),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            style = MaterialTheme.typography.titleMedium,
            color = White
        )
    }
}
