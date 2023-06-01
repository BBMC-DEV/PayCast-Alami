package kr.co.bbmc.paycastdid.presentation.main

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview(widthDp = 1080, heightDp = 1920)
@Composable
fun PaymentActivity() {
    // 최종 결제 금액 viewModel 에서 가져올 것
    val paymentAmount = 10000

    Column(
        modifier = Modifier
            .background(Color.Black)
            .padding(50.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "TEST",
            fontSize = 40.sp,
            color = Color.White
        )
        Text(
            text = "GGG",
            fontSize = 40.sp,
            color = Color.Red,
            modifier = Modifier
                .padding(bottom = 40.dp)
        )
        Box(
            modifier = Modifier
                .padding(top = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "2222",
                fontSize = 40.sp,
                color = Color.White
            )
        }
        Column(
            modifier = Modifier
                .padding(top = 140.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "남은 시간",
                fontSize = 50.sp,
                color = Color.White
            )
            Text(
                text = "30",
                fontSize = 70.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "stringResource(id = R.string.msg_back_page)",
                fontSize = 30.sp,
                color = Color.White
            )
        }
    }
}