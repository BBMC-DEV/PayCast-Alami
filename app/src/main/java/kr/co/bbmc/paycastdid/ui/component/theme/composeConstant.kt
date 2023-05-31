package kr.co.bbmc.paycast.ui.component.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(4.dp),
    large = RoundedCornerShape(0.dp)
)

enum class ButtonType(val type: Int) { Single(0), Cancel(1), Notice(2) }

val imageExt = listOf("jpg", "png", "jpeg")