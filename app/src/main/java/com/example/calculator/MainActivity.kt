package com.example.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.ui.theme.CalculatorTheme
import kotlin.math.sqrt
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalculatorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CalculatorApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}



@Composable
fun CalculatorApp(modifier: Modifier = Modifier) {
    var display by remember { mutableStateOf("0") }
    var operand by remember { mutableDoubleStateOf(0.0) }
    var operator by remember { mutableStateOf<String?>(null) }
    var waitingForOperand by remember { mutableStateOf(false) }

    val maxDisplayLength = 10

    fun calculatePendingOperation() {
        val inputValue = display.toDoubleOrNull() ?: return
        if (operator != null) {
            operand = when (operator) {
                "+" -> operand + inputValue
                "-" -> operand - inputValue
                "*" -> operand * inputValue
                "/" -> {
                    if (inputValue == 0.0) {
                        display = "Error cannot divide by 0"
                        return
                    } else {
                        operand / inputValue
                    }
                }
                else -> inputValue
            }
            display = operand.toString()
        } else {
            operand = inputValue
        }
    }

    Column(
        modifier
            .fillMaxSize()
            .fillMaxHeight()
            .padding(16.dp),

        verticalArrangement = Arrangement.spacedBy(80.dp),

        ) {
        TextField(
            value = display,
            onValueChange = { newValue ->
                if (newValue.matches(Regex("[0-9]*\\.?[0-9]*"))) {
                    display = newValue
                    waitingForOperand = false
                }
            },
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.headlineMedium.copy(fontSize = 40.sp)

        )
        CalculatorButtons(
            onButtonClick = { label ->
                when (label) {
                    in "0".."9", "." -> {
                        if (waitingForOperand) {
                            display = if (label == ".") "0." else label
                            waitingForOperand = false
                        } else {
                            if (label == "." && display.contains(".")) {
                            } else {
                                if (display.length < maxDisplayLength) {
                                    display = if (display == "0" && label != ".") label else display + label
                                }
                            }
                        }
                    }
                    "+", "-", "*", "/" -> {
                        calculatePendingOperation()
                        operator = label
                        waitingForOperand = true
                    }
                    "sqrt" -> {
                        val value = display.toDoubleOrNull()
                        if (value != null) {
                            if (value >= 0) {
                                display = sqrt(value).toString().take(maxDisplayLength)
                            } else {
                                display = "Error cannot sqrt - #"
                            }
                        } else {
                            display = "Error Input Value"
                        }
                        operator = null
                        waitingForOperand = true
                    }
                    "=" -> {
                        calculatePendingOperation()
                        operator = null
                        waitingForOperand = true
                    }
                    else -> {
                        // can't think of any cases
                    }
                }
            }
        )
    }
}

@Composable
fun CalculatorButtons(onButtonClick: (String) -> Unit) {
    val buttons = listOf(
        listOf("1", "2", "3", "+", "*"),
        listOf("4", "5", "6", "-", "/"),
        listOf("7", "8", "9", "sqrt"),
        listOf("0", ".", "=")
    )

    val buttonModifier = Modifier
        .fillMaxWidth()

    for (i in 0..1) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            buttons[i].forEach { label ->
                Button(
                    onClick = { onButtonClick(label) },
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp),
                    shape = RoundedCornerShape(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        label,
                        fontSize = 40.sp
                    )
                }
            }
        }
    }

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        buttons[2].forEachIndexed { index, label ->
            val weight = if (label == "sqrt") 2f else 1f
            Button(
                onClick = { onButtonClick(label) },
                modifier = Modifier
                    .weight(weight)
                    .height(80.dp),
                shape = RoundedCornerShape(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.LightGray,
                    contentColor = Color.Black
                )

            ) {
                Text(
                    label,
                    fontSize = 40.sp
                )
            }
            if (label == "sqrt" && index == 3) {
                return@forEachIndexed
            }
        }
    }

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = { onButtonClick("0") },
            modifier = Modifier
                .weight(2f)
                .height(80.dp),
            shape = RoundedCornerShape(0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.LightGray,
                contentColor = Color.Black
            )
        ) {
            Text(
                "0",
                fontSize = 40.sp
            )
        }
        Button(
            onClick = { onButtonClick(".") },
            modifier = Modifier
                .weight(1f)
                .height(80.dp),
            shape = RoundedCornerShape(0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.LightGray,
                contentColor = Color.Black
            )
        ) {
            Text(
                ".",
                fontSize = 50.sp
            )
        }
        Button(
            onClick = { onButtonClick("=") },
            modifier = Modifier
                .weight(2f)
                .height(80.dp),
            shape = RoundedCornerShape(0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.LightGray,
                contentColor = Color.Black
            )
        ) {
            Text(
                "=",
                fontSize = 24.sp
            )
        }
    }
}
