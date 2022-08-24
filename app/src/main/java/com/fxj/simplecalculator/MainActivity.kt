package com.fxj.simplecalculator

import android.content.res.Configuration
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fxj.simplecalculator.engine.Engine
import com.fxj.simplecalculator.ui.theme.*
import com.fxj.simplecalculator.utils.ButtonUtils
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CalculatorResult(val expression: String = "", val result: String = "") : Parcelable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimpleCalculatorTheme {
                rememberSystemUiController().run {
                    setSystemBarsColor(MaterialTheme.colors.background, !isSystemInDarkTheme())
                }
                var calculatorState by rememberSaveable {
                    mutableStateOf(CalculatorResult())
                }
                val configuration = LocalConfiguration.current
                when (configuration.orientation) {
                    Configuration.ORIENTATION_PORTRAIT -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colors.background)
                                .padding(15.dp),
                            verticalArrangement = Arrangement.Bottom,
                            horizontalAlignment = Alignment.End,
                        ) {
                            CalculatorPanel(modifier = Modifier.weight(1f), result = calculatorState)
                            Box(
                                modifier = Modifier
                                    .height(31.dp)
                                    .fillMaxWidth()
                                    .padding(vertical = 15.dp)
                                    .background(if (isSystemInDarkTheme()) Color(0xAAFFFFFF) else Color(0x33000000)),
                            ) {
                            }
                            Keyboard(
                                configuration = configuration,
                                onClick = { button ->
                                    calculatorState = updateCalculatorResult(calculatorState, button)
                                })
                        }
                    }
                    else -> {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colors.background)
                                .padding(15.dp),
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.End,
                        ) {
                            CalculatorPanel(modifier = Modifier.weight(1f), result = calculatorState)
                            Box(
                                modifier = Modifier
                                    .width(31.dp)
                                    .fillMaxHeight()
                                    .padding(horizontal = 15.dp)
                                    .background(if (isSystemInDarkTheme()) Color(0xAAFFFFFF) else Color(0x33000000)),
                            ) {
                            }
                            Keyboard(
                                configuration = configuration,
                                onClick = { button ->
                                    calculatorState = updateCalculatorResult(calculatorState, button)
                                })
                        }
                    }
                }

            }
        }
    }
}

fun updateCalculatorResult(currentResult: CalculatorResult, button: Pair<Int, String>): CalculatorResult {
    if (button.first == ButtonUtils.BUTTON_ID_CLEAR) {
        return currentResult.copy(expression = "", result = "")
    }

    var expression = currentResult.expression
    var result = currentResult.result
    if (button.first == ButtonUtils.BUTTON_ID_EQUAL) {
        if (Engine.isDividedByZero(expression)) {
            result = Engine.ERROR
        } else if (result != Engine.ERROR) {
            result = Engine.evaluate(expression, true)
        }
        if (Engine.NAN.equals(result, ignoreCase = true) || Engine.ERROR.equals(result)) {
            result = Engine.ERROR
        } else {
            expression = result
            result = ""
        }
    } else {
        var handledTakeOff = false
        if (button.first == ButtonUtils.BUTTON_ID_PERCENT) {
            var baseOpt: Char
            var optIndex = expression.length - 1
            do {
                baseOpt = expression[optIndex]
                if (!Engine.isBaseOperator(baseOpt)) {
                    optIndex--
                }
            } while (optIndex >= 0 && !Engine.isBaseOperator(baseOpt))
            if (baseOpt == ButtonUtils.BUTTON_TEXT_PLUS || baseOpt == ButtonUtils.BUTTON_TEXT_MINUS) {
                var left: Char
                var leftPartenIndex = optIndex
                do {
                    left = expression[leftPartenIndex]
                    leftPartenIndex--
                } while (leftPartenIndex >= 0)
                val prefixExpress = expression.substring(0, optIndex)
                val suffix = expression.substring(optIndex)
                expression = Engine.getSuggested(expression, suffix, button, true)
                expression = prefixExpress + expression
                handledTakeOff = true
            }
        }

        if (!handledTakeOff) {
            expression = Engine.getSuggested(expression, expression, button, true)
        }

        if (Engine.isDividedByZero(expression)) {
            result = Engine.ERROR
        } else {
            result = Engine.evaluate(expression, true, result)
            if (Engine.NAN.equals(result) ||
                Engine.ERROR.equals(result)
            ) {
                result = Engine.ERROR
            }
        }
    }

    return CalculatorResult(expression, result)
}

@Composable
fun CalculatorPanel(modifier: Modifier, result: CalculatorResult) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Bottom,
            reverseLayout = true
        ) {
            item {
                Text(
                    text = result.expression,
                    style = TextStyle(
                        fontSize = 50.sp,
                        textAlign = TextAlign.End,
                        color = if (isSystemInDarkTheme()) TextColorDark else TextColorLight,
                    ),
                )
            }
        }
        Text(
            modifier = Modifier.padding(top = 20.dp),
            style = TextStyle(
                fontSize = 30.sp,
                color = if (isSystemInDarkTheme()) TextColorDark else TextColorLight,
                textAlign = TextAlign.End,
            ),
            text = result.result
        )
    }
}

@Composable
fun Keyboard(
    modifier: Modifier = Modifier,
    configuration: Configuration,
    onClick: (button: Pair<Int, String>) -> Unit
) {
    when (configuration.orientation) {
        Configuration.ORIENTATION_PORTRAIT -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                ButtonUtils.BUTTONS.forEach { it0 ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(15.dp)
                    ) {
                        it0.forEach {
                            CalculatorButton(
                                modifier = Modifier
                                    .weight(if (it.first == ButtonUtils.BUTTON_ID_EQUAL) 2f else 1f)
                                    .aspectRatio(
                                        if (it.first == ButtonUtils.BUTTON_ID_EQUAL) 2.8f else
                                            1.4f
                                    )
                                    .clip(CircleShape)
                                    .border(
                                        shape = CircleShape,
                                        border = BorderStroke(0.1.dp, if (isSystemInDarkTheme()) Color(0x33FFFFFF) else Color.Gray)
                                    ),
                                button = it, onClick = onClick
                            )
                        }
                    }
                }
            }
        }
        else -> {
            Column(
                modifier = Modifier
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                ButtonUtils.BUTTONS.forEach { it0 ->
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(15.dp)
                    ) {
                        it0.forEach {
                            CalculatorButton(
                                modifier = Modifier
                                    /*.weight(if (it.first == ButtonUtils.BUTTON_ID_EQUAL) 2f else 1f)*/
                                    .aspectRatio(
                                        if (it.first == ButtonUtils.BUTTON_ID_EQUAL) 3f else
                                            1.4f
                                    )
                                    .clip(CircleShape)
                                    .border(
                                        shape = CircleShape,
                                        border = BorderStroke(0.1.dp, if (isSystemInDarkTheme()) Color(0x33FFFFFF) else Color.Gray)
                                    ),
                                button = it, onClick = onClick
                            )
                        }
                    }
                }
            }
        }
    }

}


@Composable
fun CalculatorButton(
    modifier: Modifier = Modifier,
    button: Pair<Int, String>,
    onClick: (button: Pair<Int, String>) -> Unit
) {
    Button(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            backgroundColor =
            if (button.first == ButtonUtils.BUTTON_ID_EQUAL) EqualButtonBgColor
            else if (!isSystemInDarkTheme())
                ButtonBgColorLight else ButtonBgColorDark
        ),
        contentPadding = PaddingValues(0.dp),
        onClick = { onClick(button) }
    ) {
        Text(
            text = button.second,
            color = when (button.first) {
                ButtonUtils.BUTTON_ID_EQUAL -> Color.White
                ButtonUtils.BUTTON_ID_CLEAR -> TextColorClear
                ButtonUtils.BUTTON_ID_DEL, ButtonUtils.BUTTON_ID_PERCENT,
                ButtonUtils.BUTTON_ID_DIV, ButtonUtils.BUTTON_ID_MUL,
                ButtonUtils.BUTTON_ID_MINUS, ButtonUtils.BUTTON_ID_PLUS -> TextColorOpt
                else -> if (isSystemInDarkTheme()) TextColorDark else TextColorLight
            },
            style = TextStyle(
                fontSize = when (button.first) {
                    ButtonUtils.BUTTON_ID_DEL, ButtonUtils.BUTTON_ID_CLEAR -> 20.sp
                    else -> 30.sp
                }
            )
        )
    }
}