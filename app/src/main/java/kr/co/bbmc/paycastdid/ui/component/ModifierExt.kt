package kr.bbmc.signcast.adnet.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
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

//fun rememberInfiniteTransition.StartAnim() = {
//    animateFloat(
//        initialValue = 0.0f, targetValue = 1f, animationSpec = infiniteRepeatable(
//            animation = keyframes {
//                durationMillis = 1000
//                0.7f at 500
//            },
//            repeatMode = RepeatMode.Reverse
//        )
//    )
//}

@Composable
fun InfiniteTransition.backgroundAnim(): State<Float> {
    return this.animateFloat(
        initialValue = 0.0f, targetValue = 1f, animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 2000
                0.7f at 500
            },
            repeatMode = RepeatMode.Reverse
        )
    )
    //animateValue(initialValue, targetValue, Float.VectorConverter, animationSpec, label)
}
