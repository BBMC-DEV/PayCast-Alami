package kr.co.bbmc.paycastdid.presentation.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Bottom
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.asFlow
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
    val isVisible = vm.isVisible.asFlow().collectAsState(false).value

    val imgFile = File(FileUtils.BBMC_PAYCAST_BG_DIRECTORY + "background.jpg")
    Logger.e("imgFile : $imgFile")
    val resource = if(!imgFile.exists()) R.drawable.bg_default else imgFile
    val orderCount = didItems?.keys?.size ?: 0

    didItems?.keys?.forEach { key ->
        val values = didItems[key]
    }
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
                .background(Color.Transparent)
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.7f)
                    .padding(10.dp)
                    //.align(alignment = Alignment.Bottom)
            ) {
                didItems?.forEach { (key, values) ->
                    item {
                        Card(
                            modifier = Modifier
                                .padding(24.dp)
                                .height(360.dp)
                                .width(360.dp),
                            backgroundColor = Color.LightGray,
                            elevation = 8.dp,
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(24.dp)
                                    .fillMaxSize()
                            ) {
                                Text(
                                    text = "$key",
                                    fontSize = 120.sp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                values.forEach {
                                    Row {
                                        Text(
                                            text = "${it.menuName}",
                                            fontSize = 45.sp,
                                            color = if (it.cookingState == "N") Color.Black else Color.Red,
                                            modifier = Modifier.basicMarquee()
                                        )
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Text(
                                            text = "${it.count}",
                                            fontSize = 45.sp,
                                            color = Color.Gray,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent)
            ) {
                Text(
                    text = "대기: $orderCount",
                    fontSize = 50.sp,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
            }
        }
    }

//    if (false) {
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(Color.Transparent)
//        ) {
//            Box(
//                modifier = Modifier
//                    .size(width = 800.dp, height = 600.dp)
//                    .background(Color.White)
//                    .align(Alignment.Center)
//            ) {
//                Column(
//                    modifier = Modifier
//                        .fillMaxSize()
//                ) {
//                    Text(
//                        text = "주문이 완료되었습니다",
//                        fontSize = 100.sp,
//                        fontWeight = FontWeight.Bold,
//                        color = Color.Black
//                    )
//                    Spacer(Modifier.height(24.dp))
//                    Image(
//                        painter = painterResource(id = R.drawable.icon_no_order),
//                        contentDescription = null,
//                    )
//                }
//            }
//        }
//    }
}