package com.fxj.simplecalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fxj.simplecalculator.ui.theme.SimpleCalculatorTheme
import com.fxj.simplecalculator.utils.ButtonUtils


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimpleCalculatorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Keyboard(onClick = { button ->
                        {

                        }
                    })
                }
            }
        }
    }
}

@Composable
fun Keyboard(
    modifier: Modifier = Modifier,
    onClick: (button: Pair<Int, String>) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 25.dp),
    ) {
        ButtonUtils.BUTTONS.forEach { it0 ->
            Row(
                horizontalArrangement =Arrangement.SpaceEvenly
            ) {
                it0.forEach {
                        Button(
                            modifier = Modifier
                                .padding(10.dp)
                                .weight(if (it.first == ButtonUtils.BUTTON_ID_EQUAL) 2f else 1f)
                                .aspectRatio(if (it.first == ButtonUtils.BUTTON_ID_EQUAL) 2.8f else
                                    1.4f)
                                .clip(CircleShape),
                            onClick = {
                                // TODO
                            }
                        ){
                            Text(
                                text = it.second,
                                style = TextStyle(fontSize = 30.sp)
                            )
                        }
                }
            }
        }
    }
}