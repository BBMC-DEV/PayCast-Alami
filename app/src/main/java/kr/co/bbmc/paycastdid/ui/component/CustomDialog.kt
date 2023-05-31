package kr.co.bbmc.paycast.ui.component

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kr.co.bbmc.paycast.ui.component.theme.ButtonType

@Composable
fun CustomDialog(
    dlgTitle: String? = "제목",
    dlgContent: String,
    dlgOnPositive: () -> Unit,
    dlgOnNegative: () -> Unit,
    buttonType: ButtonType = ButtonType.Cancel,
    modifier: Modifier,
    useIcon: Int? = null
) {
    Dialog(
        onDismissRequest = { dlgOnPositive() },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        CustomDialogUI(
            title = dlgTitle,
            content = dlgContent,
            onPositive = dlgOnPositive,
            onNegative = dlgOnNegative,
            buttonType = buttonType,
            modifier = modifier,
            useIcon = useIcon
        )
    }
}

@Composable
fun CustomDialogUI(
    title: String?,
    content: String,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    onPositive: () -> Unit,
    onNegative: () -> Unit,
    buttonType: ButtonType,
    useIcon: Int? = null
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.padding(10.dp, 5.dp, 10.dp, 10.dp),
        elevation = 8.dp
    ) {
        Column(
            modifier.background(Color.White)
        ) {

            useIcon?.let {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = null, // decorative
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(
                        color = Color.Blue
                    ),
                    modifier = Modifier
                        .padding(top = 35.dp)
                        .height(70.dp)
                        .fillMaxWidth(),

                    )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                title?.let {
                    Text(
                        text = it,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(top = 5.dp)
                            .fillMaxWidth(),
                        style = MaterialTheme.typography.h4,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = content,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 10.dp, start = 25.dp, end = 25.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.h5
                )
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .background(Color.Cyan),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                if (buttonType == ButtonType.Cancel) {
                    TextButton(onClick = { onNegative() }) {
                        Text(
                            "취소",
                            fontWeight = FontWeight.Bold,
                            color = Color.Blue,
                            modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                        )
                    }
                }

                if(buttonType.type < ButtonType.Notice.type) {
                    TextButton(onClick = { onPositive() }) {
                        Text(
                            "확인",
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.Black,
                            modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                        )
                    }
                }
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview(name = "Custom Dialog")
@Composable
fun MyDialogUIPreview() {
    CustomDialogUI(
        title = "기기인증 실패",
        content = "기기인증을 받을수 없습니다.\n관리자에게 문의 바랍니다.\n02-4444-5555",
        onNegative = {},
        onPositive = {},
        buttonType = ButtonType.Notice)
}