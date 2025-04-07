package com.abenzaggagh.jettip

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abenzaggagh.jettip.components.InputField
import com.abenzaggagh.jettip.ui.theme.JetTipTheme
import com.abenzaggagh.jettip.util.calculateTotalPerPerson
import com.abenzaggagh.jettip.util.calculateTotalTip
import com.abenzaggagh.jettip.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JetTipApp {
                Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                    MainContent()
                }
            }
        }
    }
}

@Composable
fun JetTipApp(content: @Composable () -> Unit) {
    JetTipTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Surface(modifier = Modifier.padding(innerPadding)) {
                content()
            }
        }
    }
}

@Composable
fun TotalPerPerson(totalPerPerson: Double = 134.0) {

    val total = "%.2f".format(totalPerPerson)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .height(150.dp),
        color = MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(corner = CornerSize(12.dp))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Total Per Person",
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = "$$total",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Preview
@Composable
fun MainContent() {
    BillForm(modifier = Modifier.padding(4.dp)) { billTotal ->
        Log.d("MainContent", "MainContent: $billTotal")
    }
}

@Composable
fun BillForm(
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {
    val totalBillState = remember {
        mutableStateOf("")
    }

    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    var splitByState = remember {
        mutableIntStateOf(1)
    }

    var sliderPositionState = remember {
        mutableFloatStateOf(0f)
    }

    val tipPercentage = (sliderPositionState.floatValue * 100).toInt()

    val range = IntRange(start = 1, endInclusive = 100)

    val tipAmountState = remember {
        mutableDoubleStateOf(0.0)
    }

    val totalPerPersonState = remember {
        mutableDoubleStateOf(0.0)
    }

    Spacer(Modifier.height(14.dp))

    TotalPerPerson(totalPerPerson = totalPerPersonState.doubleValue)

    Spacer(Modifier.height(8.dp))

    Surface(
        modifier = Modifier
            .padding(2.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(width = 1.dp, color = Color.LightGray)
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            InputField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                valueState = totalBillState,
                label = "Enter Bill",
                enabled = true,
                isSingleLine = true,
                keyboardType = KeyboardType.Number,
                onAction = KeyboardActions({
                    if (!validState) return@KeyboardActions

                    onValueChange(totalBillState.value.trim())

                    tipAmountState.doubleValue = calculateTotalTip(
                        totalBill = totalBillState.value.toDouble(),
                        tipPercentage = tipPercentage
                    )

                    totalPerPersonState.doubleValue = calculateTotalPerPerson(
                        totalBill = totalBillState.value.toDouble(),
                        splitBy = splitByState.intValue,
                        tipPercentage = tipPercentage,
                    )

                    keyboardController?.hide()
                })
            )

            if (validState) {
                Row(
                    modifier = Modifier.padding(6.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text("Split",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(120.dp))
                    Row(
                        modifier = modifier.padding(horizontal = 3.dp),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        RoundIconButton(
                            imageVector = Icons.Default.Remove,
                            onClick = {
                                if (splitByState.intValue > range.start) {
                                    splitByState.intValue = splitByState.intValue - 1
                                }

                                totalPerPersonState.doubleValue = calculateTotalPerPerson(
                                    totalBill = totalBillState.value.toDouble(),
                                    splitBy = splitByState.intValue,
                                    tipPercentage = tipPercentage,
                                )
                            },
                        )
                        Text(text = "${splitByState.intValue}",
                            modifier = Modifier
                                .align(alignment = Alignment.CenterVertically)
                                .padding(start = 9.dp, end = 9.dp)

                        )
                        RoundIconButton(
                            imageVector = Icons.Default.Add,
                            onClick = {
                                if (splitByState.intValue < range.last) {
                                    splitByState.intValue += 1
                                }

                                totalPerPersonState.doubleValue = calculateTotalPerPerson(
                                    totalBill = totalBillState.value.toDouble(),
                                    splitBy = splitByState.intValue,
                                    tipPercentage = tipPercentage,
                                )
                            }
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("Tip",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically)
                    )
                    Spacer(modifier = modifier.width(140.dp))
                    Text("${tipAmountState.doubleValue} $",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically)
                    )
                }
                Spacer(modifier = modifier.height(14.dp))
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("$tipPercentage %")
                    Spacer(modifier = modifier.height(14.dp))
                    Slider(
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                        value = sliderPositionState.floatValue,
                        // valueRange = 0f..1f,
                        // steps = 5,
                        onValueChange = { newValue ->
                            sliderPositionState.floatValue = newValue

                            tipAmountState.doubleValue = calculateTotalTip(
                                totalBill = totalBillState.value.toDouble(),
                                tipPercentage = tipPercentage
                            )

                            totalPerPersonState.doubleValue = calculateTotalPerPerson(
                                totalBill = totalBillState.value.toDouble(),
                                splitBy = splitByState.intValue,
                                tipPercentage = tipPercentage,
                            )
                        }
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
             } else {
                Box() {}
            }

        }


    }
}

