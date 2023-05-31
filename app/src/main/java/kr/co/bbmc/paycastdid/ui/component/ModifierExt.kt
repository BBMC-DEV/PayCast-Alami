package kr.bbmc.signcast.adnet.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.imgModifier(
    size: Dp,
    borderStroke: BorderStroke = BorderStroke(1.dp, Color.Transparent),
    bgColor: Color = Color.Transparent,
    roundShape: Dp? = null
): Modifier = this
    .size(size)
    .border(borderStroke)
    .background(bgColor)
    .clip(RoundedCornerShape(roundShape ?: 0.dp))

fun Modifier.startPadding(paddingValues: Dp): Modifier =
    this.padding(paddingValues, 0.dp, 0.dp, 0.dp)