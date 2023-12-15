package ui.reusable.form

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PlainTooltipBox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import dev.icerock.moko.resources.compose.stringResource
import filamagenta.MR

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
fun FormField(
    value: String?,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    nextFocusRequester: FocusRequester? = null,
    error: String? = null
) {
    val softwareKeyboardController = LocalSoftwareKeyboardController.current

    var showingPassword by remember { mutableStateOf(!isPassword) }

    OutlinedTextField(
        value = value ?: "",
        onValueChange = onValueChange,
        modifier = modifier,
        label = { Text(label) },
        visualTransformation = if (showingPassword) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Text,
            imeAction = if (nextFocusRequester != null) ImeAction.Next else ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onNext = { nextFocusRequester?.requestFocus() },
            onDone = { softwareKeyboardController?.hide() }
        ),
        trailingIcon = (@Composable {
            PlainTooltipBox(
                tooltip = {
                    Text(
                        text = if (showingPassword) {
                            stringResource(MR.strings.hide_password)
                        } else {
                            stringResource(MR.strings.show_password)
                        }
                    )
                }
            ) {
                IconButton(
                    onClick = { showingPassword = !showingPassword }
                ) {
                    Icon(
                        imageVector = if (showingPassword) {
                            Icons.Rounded.VisibilityOff
                        } else {
                            Icons.Rounded.Visibility
                        },
                        contentDescription = if (showingPassword) {
                            stringResource(MR.strings.hide_password)
                        } else {
                            stringResource(MR.strings.show_password)
                        }
                    )
                }
            }
        }).takeIf { isPassword },
        isError = error != null,
        supportingText = (@Composable {
            Text(error ?: "")
        }).takeIf { error != null }
    )
}
