package kr.co.bbmc.paycastdid.presentation.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.asLiveData
import com.orhanobut.logger.Logger
import kotlinx.coroutines.FlowPreview
import kr.co.bbmc.paycastdid.R
import kr.co.bbmc.paycastdid.network.model.CookingData


@OptIn(ExperimentalFoundationApi::class)
@FlowPreview
@Composable
fun MainScreen(vm: MainViewModel) {
    val didItems = vm.didInfo.collectAsState().value?.groupBy { it.orderNumber }
    val dpInfo = vm.dpInfo.observeAsState().value
//    val didItemList: List<CookingData> = didItems?.flatMap { it.value } ?: emptyList()

    didItems?.keys?.forEach { key ->
        val values = didItems[key]
        Logger.e("Group by Test == Key : $key, Values: $values")
    }

    Column {
        Text(
            text = vm.dpInfo.value.toString()
        )
        Row(
            modifier = Modifier
                .background(Color.White)
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.7f)
                    .padding(10.dp)
            ) {
                didItems?.forEach { (key, values) ->
                    item {
                        Box(
                            modifier = Modifier
                                .padding(24.dp)
                                .height(380.dp)
                                .width(380.dp)
                                .background(Color.LightGray, shape = RoundedCornerShape(48.dp))
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(24.dp)
                                    .fillMaxSize()
                            ) {
                                Text(
                                    text = "$key",
                                    fontSize = 70.sp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                values.forEach {
                                    Row {
                                        Text(
                                            text = "${it.menuName}",
                                            fontSize = 20.sp,
                                            color = if(it.cookingState == "N") Color.Black else Color.Red,
                                            modifier = Modifier.basicMarquee()
                                        )
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Text(
                                            text = "${it.count}",
                                            fontSize = 20.sp,
                                            color = Color.Gray,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

//    Column {
////        Image(
////            painter = painterResource(id = R.drawable.sample_cocatu),
////            contentDescription = null,
////            modifier = Modifier
////                .fillMaxWidth()
////                .fillMaxHeight(0.6f),
////            contentScale = ContentScale.Crop
////        )
//        Row(
//            modifier = Modifier
//                .background(Color.White)
//        ) {
//            LazyRow(
//                modifier = Modifier
//                    .fillMaxHeight()
//                    .fillMaxWidth(0.7f)
//                    .padding(10.dp)
//            ) {
//                items(didItemList) {
//                    Box(
//                        modifier = Modifier
//                            .padding(24.dp)
//                            .height(380.dp)
//                            .width(380.dp)
//                            .background(Color.LightGray, shape = RoundedCornerShape(48.dp))
//                    ) {
//                        Column(
//                            modifier = Modifier
//                                .padding(24.dp)
//                                .fillMaxSize()
//                        ) {
//                            Text(
//                                text = it.orderNumber.toString(),
////                                fontSize = 120.sp,
//                                fontSize = 70.sp,
//                                color = Color.Black,
//                                fontWeight = FontWeight.Bold,
//                                textAlign = TextAlign.Center,
//                                modifier = Modifier.fillMaxWidth()
//                            )
////                            Row(
////                                modifier = Modifier.basicMarquee()
////                            ) {
////                                repeat(didItemList.size) {
////                                    LazyRow(
////                                        modifier = Modifier
////                                    ) {
////                                        items(didItemList) {
////                                            Text(
////                                                text = it.menuName.toString(),
////                                                fontSize = 50.sp,
////                                                color = if(it.cookingState == "N") Color.Black else Color.Red,
////                                                maxLines = 1,
////                                                modifier = Modifier.wrapContentSize(unbounded = true)
////                                            )
////                                            Spacer(modifier = Modifier.width(8.dp))
////                                            Text(
////                                                text = it.count.toString(),
////                                                fontSize = 50.sp,
////                                                color = if(it.cookingState == "N") Color.Black else Color.Red,
////                                                maxLines = 1,
////                                                modifier = Modifier.wrapContentSize(unbounded = true)
////                                            )
////                                        }
////                                    }
////                                }
////                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
}