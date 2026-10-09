package com.example.detectordequeda.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.detectordequeda.domain.model.FallClassification
import com.example.detectordequeda.domain.model.FallResult

@Composable
fun ResultSection(result: FallResult) {
    val color = when (result.classification) {
        FallClassification.POSSIBLE_FALL -> Color.Red
        FallClassification.ATTENTION -> Color(0xFFFFA500)
        FallClassification.NORMAL -> Color(0xFF008000)
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Ângulo do tronco: ${"%.1f".format(result.angleDegrees)}°", fontSize = 18.sp)
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Resultado:\n${result.classification.label}",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            textAlign = TextAlign.Center
        )
    }
}