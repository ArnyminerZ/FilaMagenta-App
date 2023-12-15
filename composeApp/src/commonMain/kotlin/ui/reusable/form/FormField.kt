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
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import dev.icerock.moko.resources.compose.stringResource
import filamagenta.MR

/**
 * Displays an outlined text field intended to be used in forms.
 * It forced to have just one line, and some utilities are provided.
 *
 * @param value The current value of the field, if null, the field will be empty.
 * @param onValueChange Will be called whenever the user types something in the field.
 * @param label The text to display on top of the field.
 * @param modifier If any, modifiers to apply to the field.
 * @param enabled If `true` the field is intractable, `false` disables the field. Default: `true`
 * @param error If not `null`, this text will be displayed in red under the field.
 * @param isPassword If true, the field will be considered a password input.
 * A show/hide password button will be displayed at the end of the field, and the characters will be obfuscated.
 * @param nextFocusRequester If any, what should be selected when tapping the "next" button in the keyboard, or when
 * pressing TAB.
 * @param onSubmit If any, will be called when the user presses the submit button in the software keyboard, or enter
 * in a hardware keyboard.
 * @param capitalization Can be provided to specify the capitalization options for the keyboard. Defaults to none.
 * @param supportingText If any, the text that will be displayed under the field for giving more information about the
 * field to the user.
 * Won't be displayed if [error] is not null.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
fun FormField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    error: String? = null,
    isPassword: Boolean = false,
    nextFocusRequester: FocusRequester? = null,
    onSubmit: (() -> Unit)? = null,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None,
    supportingText: String? = null
) {
    val softwareKeyboardController = LocalSoftwareKeyboardController.current

    var showingPassword by remember { mutableStateOf(!isPassword) }

    var selection: TextRange? = null

    OutlinedTextField(
        value = value,
        onValueChange = {
            selection = it.selection
            onValueChange(it)
        },
        modifier = Modifier
            .onPreviewKeyEvent { ev ->
                when {
                    (ev.key == Key.Enter || ev.key == Key.NumPadEnter) && ev.type == KeyEventType.KeyUp -> {
                        onSubmit?.invoke()
                        true
                    }

                    ev.key == Key.Tab && ev.type == KeyEventType.KeyUp -> {
                        nextFocusRequester?.requestFocus()
                        true
                    }

                    ev.isCtrlPressed && ev.key == Key.Backspace && ev.type == KeyEventType.KeyUp -> {
                        selection
                            ?.takeIf { it.length <= 0 }
                            ?.takeIf { it.start > 0 }
                            ?.let { range ->
                                val textToRemove = value.text.substring(0, range.start)
                                onValueChange(
                                    value.copy(
                                        text = value.text.replace(textToRemove, "")
                                    )
                                )
                                true
                            } ?: false
                    }

                    ev.isCtrlPressed && ev.key == Key.Delete && ev.type == KeyEventType.KeyUp -> {
                        selection
                            ?.takeIf { it.length <= 0 }
                            ?.let { range ->
                                val textToRemove = value.text.substring(range.start)
                                onValueChange(
                                    value.copy(
                                        text = value.text.replace(textToRemove, "")
                                    )
                                )
                                true
                            } ?: false
                    }

                    else -> false
                }
            }
            .then(modifier),
        label = { Text(label) },
        enabled = enabled,
        singleLine = true,
        maxLines = 1,
        visualTransformation = if (showingPassword) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Text,
            imeAction = when {
                nextFocusRequester != null -> ImeAction.Next
                onSubmit != null -> ImeAction.Go
                else -> ImeAction.Done
            },
            capitalization = capitalization
        ),
        keyboardActions = KeyboardActions(
            onNext = { nextFocusRequester?.requestFocus() },
            onDone = { softwareKeyboardController?.hide() },
            onGo = { onSubmit?.invoke() }
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
        }).takeIf { error != null } ?: supportingText?.let {
            { Text(it) }
        }
    )
}
