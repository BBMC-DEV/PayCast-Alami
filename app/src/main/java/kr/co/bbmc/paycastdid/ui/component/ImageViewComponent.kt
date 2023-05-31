package kr.co.bbmc.paycast.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.airbnb.lottie.compose.*


@Composable
fun ImageWithBackground(
    target: Painter,
    @DrawableRes backgroundDrawableResId: Int,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null
) {
    Box(
        modifier = modifier
    ) {
        Image(
            painter = painterResource(backgroundDrawableResId),
            contentDescription = null,
            modifier = Modifier
                .matchParentSize()
        )
        Image(
            painter = target,
            contentDescription = contentDescription,
            alignment = alignment,
            contentScale = contentScale,
            alpha = alpha,
            colorFilter = colorFilter,
            modifier = Modifier
                .matchParentSize()
        )
    }
}

@Composable
fun ImageWholeBackground(
    @DrawableRes backgroundDrawableResId: Int,
    modifier: Modifier = Modifier,
    @Suppress("UNUSED_PARAMETER") contentDescription: String? = null,
    @Suppress("UNUSED_PARAMETER") alignment: Alignment = Alignment.Center,
    @Suppress("UNUSED_PARAMETER") contentScale: ContentScale = ContentScale.Fit,
    @Suppress("UNUSED_PARAMETER") alpha: Float = DefaultAlpha,
    @Suppress("UNUSED_PARAMETER") colorFilter: ColorFilter? = null
) {
    Box(
        modifier = modifier
    ) {
        Image(
            painter = painterResource(backgroundDrawableResId),
            contentDescription = null,
            modifier = Modifier
                .matchParentSize()
        )
    }
}

@Composable
fun BaseLottieView(resId: Int, modifier: Modifier = Modifier, repeatCount: Int = 0) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(resId))
    val repeat = if(repeatCount == 0) LottieConstants.IterateForever else repeatCount
    val progress by animateLottieCompositionAsState(composition = composition, iterations = repeat)
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier
    )
}