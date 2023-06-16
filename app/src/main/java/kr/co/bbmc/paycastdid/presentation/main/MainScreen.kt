package kr.co.bbmc.paycastdid.presentation.main

import android.media.MediaPlayer
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.orhanobut.logger.Logger
import kotlinx.coroutines.FlowPreview
import kr.co.bbmc.paycastdid.R
import kr.co.bbmc.selforderutil.FileUtils
import java.io.File


@OptIn(ExperimentalFoundationApi::class)
@FlowPreview
@Composable
fun MainScreen(vm: MainViewModel) {
    val didItems = vm.didInfo.collectAsState().value?.groupBy { it.orderNumber }

    val imgFile = File(FileUtils.BBMC_PAYCAST_BG_DIRECTORY + "background.jpg")
//    Logger.e("imgFile : $imgFile")
    val resource = if (!imgFile.exists()) R.drawable.bg_default else imgFile
    val orderCount = didItems?.keys?.size ?: 0

    val newDidOrder = vm.newDidOrder.observeAsState()
    Logger.e("새로 들어온 주문 유무 : ${newDidOrder.value}")

    val idxNum = remember { mutableStateOf(-1) }
    val bgColor = remember { Animatable(Color.Transparent) }

    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current).data(resource).build(),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        contentScale = ContentScale.Crop
    )
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f),
        )
        Row(
            modifier = Modifier
                .padding(top = 24.dp)
                .background(Color.Transparent)
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.65f)
                    .padding(10.dp)
                //.align(alignment = Alignment.Bottom)
            ) {
                itemsIndexed(didItems?.toList() ?: emptyList()) { i, it ->
                    if (i == 0) idxNum.value = it.first
                    if (vm.newDidOrder.value == true) {
                        LaunchedEffect(Unit) {
                            repeat(5) {
                                bgColor.animateTo(Color.Red, tween(500))
                                bgColor.animateTo(Color.LightGray, tween(500
                                ))
                            }
                            if (didItems?.get(idxNum.value)?.any { it.cookingState == "O" }!!) {
                                bgColor.animateTo(Color.Red)
                            } else {
                                bgColor.animateTo(Color.Transparent)
                            }
                            vm.newDidOrder(false)
                        }
                    }
                    Card(
                        modifier = Modifier
                            .padding(24.dp)
                            .height(320.dp)
                            .width(360.dp)
                            .border(
                                width = 8.dp,
                                color = if (i == 0) bgColor.value else if (it.second.any { it.cookingState == "O" }) Color.Red else Color.Transparent,
                                shape = RoundedCornerShape(20.dp)
                            ),
                        backgroundColor = Color.LightGray,
                        elevation = 8.dp,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(0.5f)
                                        .wrapContentWidth(Alignment.CenterHorizontally)
                                ) {
                                    Text(
                                        text = it.first.toString(),
                                        fontSize = 80.sp,
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(end = 16.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(0.5f)
                                        .wrapContentWidth(Alignment.End)
                                ) {
                                    Text(
                                        text = "총 ${it.second.size}건",
                                        fontSize = 40.sp,
                                        color = Color.DarkGray,
                                        textAlign = TextAlign.End,
                                        modifier = Modifier
                                            .padding(end = 16.dp)
                                            .basicMarquee()
                                    )
                                    Spacer(Modifier.height(8.dp))
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(16.dp)
                            ) {
                                val splitList = if (it.second.size > 1) {
                                    it.second.subList(0, 2)
                                } else {
                                    it.second
                                }
                                items(splitList) { data ->
                                    Row (
                                        modifier = Modifier
                                            .padding(horizontal = 8.dp)
                                    ) {
                                        Text(
                                            text = data.menuName.toString(),
                                            fontSize = 45.sp,
                                            color = if (data.cookingState == "N") Color.Black else Color.Red,
                                            modifier = Modifier
                                                .width(280.dp)
                                                .basicMarquee()
                                        )
                                        Spacer(Modifier.width(16.dp))
                                        Text(
                                            text = data.count.toString(),
                                            fontSize = 45.sp,
                                            color = Color.DarkGray,
                                            modifier = Modifier
                                                .width(60.dp)
                                        )
                                    }
                                    Spacer(Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent)
                    .padding(bottom = 24.dp, end = 24.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Text(
                    text = "대기 : $orderCount",
                    fontSize = 60.sp,
                    modifier = Modifier
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
            }
        }
    }
}