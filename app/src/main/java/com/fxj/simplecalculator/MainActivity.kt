package com.fxj.simplecalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.fxj.simplecalculator.ui.theme.SimpleCalculatorTheme




class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimpleCalculatorTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Keyboard(
    modifier: Modifier = Modifier,
    onClick: (button: ButtonInfo) -> Unit
) {
    val buttonModifier =
        if (modeState.isScienceMode) scienceButtonModifier else simpleButtonModifier
    Column(
        modifier = modifier,
    ) {
        buttons.forEach { it0 ->
            Row(
            ) {
                it0.forEach {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(keybordContainerRatio),
                        contentAlignment = Alignment.Center
                    ) {
                        CalculatorButton(
                            modifier = if (it.first == KeyboardUtils.KEY_ID_EQUAL) equalButtonModifier else buttonModifier,
                            it.second,
                            modeState,
                            onClick
                        )
                    }
                }
            }
        }
    }
}

}