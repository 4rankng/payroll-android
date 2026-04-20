package com.payroll.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.payroll.android.ui.theme.Sky100

@Composable
fun LoadingSkeleton(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Sky100)
    )
}

@Composable
fun LoadingCard() {
    LoadingSkeleton(modifier = Modifier.fillMaxWidth().height(120.dp))
}

@Composable
fun LoadingList(count: Int = 5) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        repeat(count) { LoadingCard() }
    }
}
