package com.solo4.aggry.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun GradientText(
    text: String,
    style: TextStyle,
    colors: List<Color> = listOf(
        Color(0xFF5D3FD3),
        Color(0xFF7C5CFF),
        Color(0xFF00BFA6)
    ),
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Text(
            text = text,
            style = style.copy(
                brush = Brush.linearGradient(colors = colors)
            )
        )
    }
}

@Composable
fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    GradientText(
        text = text,
        style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
        modifier = modifier.padding(vertical = 8.dp)
    )
}