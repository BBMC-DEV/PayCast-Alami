package kr.co.bbmc.paycast.ui.component.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import kr.co.bbmc.paycast.ui.component.*


val baseColors = lightColors(
    primary = base_purple_800,
    secondary = base_red,
    surface = base_purple_900,
    onSurface = base_white,
    primaryVariant = base_purple_700
)

val BottomSheetShape = RoundedCornerShape(
    topStart = 20.dp,
    topEnd = 20.dp,
    bottomStart = 0.dp,
    bottomEnd = 0.dp
)

private val DarkColorPalette = darkColors(
    primary = adNetPurple200,
    primaryVariant = adNetPurple700,
    secondary = adNetTeal200
)

private val LightColorPalette = lightColors(
    primary = adNetPurple500,
    primaryVariant = adNetPurple700,
    secondary = adNetTeal200
)

@Composable
fun BaseTheme(content: @Composable () -> Unit) {
    MaterialTheme(colors = baseColors, typography = adNetTypography) {
        content()
    }
}

@Composable
fun AdNetTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = adNetTypography,
        shapes = Shapes,
        content = content
    )
}