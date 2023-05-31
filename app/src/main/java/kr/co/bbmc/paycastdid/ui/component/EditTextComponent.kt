package kr.co.bbmc.paycast.ui.component

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue

@Composable
fun AdNetEditText(
    value: TextFieldValue,
    afterTextChanged: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    useOutLine: Boolean = true,
    enabled: Boolean = true,
    textStyle: TextStyle = LocalTextStyle.current,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = false,
    shape: Shape = TextFieldDefaults.OutlinedTextFieldShape,
    colors: TextFieldColors = TextFieldDefaults.outlinedTextFieldColors(),  // 외곽선
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
) {

    when(useOutLine) {
        true -> {
            OutlinedTextField(
                modifier = modifier,
                value = value,
                onValueChange = afterTextChanged,
                enabled = enabled,
                textStyle = textStyle,
                isError = isError,
                keyboardOptions = keyboardOptions,
                singleLine = singleLine,
                shape = shape,
                colors = colors,
                label = label,
                placeholder = placeholder,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon
            )
        }
        else -> {
            TextField(
                modifier = modifier,
                value = value,
                onValueChange = afterTextChanged,
                enabled = enabled,
                textStyle = textStyle,
                isError = isError,
                keyboardOptions = keyboardOptions,
                singleLine = singleLine,
                shape = shape,
                colors = colors,
                label = label,
                placeholder = placeholder,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon
            )
        }
    }

}