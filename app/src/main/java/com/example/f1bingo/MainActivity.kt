@file:Suppress("DEPRECATION")

package com.example.f1bingo

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.f1bingo.ui.theme.F1BingoTheme
import kotlin.text.isBlank

@Suppress("DEPRECATION")
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            F1BingoTheme {
                var currentScreen by rememberSaveable { mutableStateOf(Screen.GridSizeInput) }
                var gridSize by rememberSaveable { mutableStateOf("") }
                var bingoTexts by rememberSaveable { mutableStateOf(listOf<String>()) }

                when (currentScreen) {
                    Screen.GridSizeInput -> {
                        GridSelectionScreen(startBingo = { newGridSize ->
                            gridSize = newGridSize
                            currentScreen = Screen.FillBingoCards
                        })
                    }

                    Screen.FillBingoCards -> {
                        FillBingoCardsScreen(numCards = gridSize.toInt().times(gridSize.toInt()),
                            generateCards = { cardTexts ->
                                bingoTexts = cardTexts
                                currentScreen = Screen.BingoGame
                            })
                    }

                    Screen.BingoGame -> {
                        BingoGameScreen(gridSize = gridSize.toInt(), cardTexts = bingoTexts)
                    }
                }
            }
        }
    }

    enum class Screen {
        GridSizeInput,
        FillBingoCards,
        BingoGame
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GridSelectionScreen(startBingo: (String) -> Unit) {
    var gridSize by remember { mutableStateOf("") }
    val context = LocalContext.current

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                modifier = Modifier
                    .padding(10.dp),
                painter = painterResource(id = R.drawable.f1_bingo_icon),
                contentDescription = "F1 Bingo App Icon",
                alignment = Alignment.TopCenter
            )

            OutlinedTextField(
                modifier = Modifier
                    .padding(10.dp),
                value = gridSize,
                onValueChange = { size ->
                    gridSize = size
                },
                label = {
                    Text(
                        "Grid Size",
                        color = Color.Red,
                        fontSize = 15.sp
                    )
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Red,
                    unfocusedBorderColor = Color(0xFFa60021),
                    cursorColor = Color.Black
                ),
                placeholder = {
                    Text(
                        "Enter size",
                        color = Color.LightGray
                    )
                },
            )

            F1Button(
                text = "Create Bingo Cards",
                onClick = {
                    if (gridSize.isBlank()) {
                        Toast.makeText(context, "Grid Size cannot be empty", Toast.LENGTH_SHORT)
                            .show()
                    } else if (gridSize.toIntOrNull() == null || gridSize.toInt() < 0) {
                        Toast.makeText(
                            context,
                            "Grid Size must be a positive Integer",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    } else {
                        startBingo(gridSize)
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FillBingoCardsScreen(numCards: Int, generateCards: (List<String>) -> Unit) {
    var cardText by remember { mutableStateOf("") }
    var cardTexts by remember { mutableStateOf(listOf<String>()) }
    val context = LocalContext.current

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray)
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier
                    .padding(top = 40.dp),
                painter = painterResource(id = R.drawable.f1_bingo_icon),
                contentDescription = "F1 Bingo App Icon",
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                text = "Entered Bingo Cards: ${cardTexts.size} / $numCards\nClick a Text to delete it",
                textAlign = TextAlign.Center
            )

            Row(
                modifier = Modifier
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .weight(1f)
                        .padding(10.dp),
                    value = cardText,
                    onValueChange = { text ->
                        cardText = text
                    },
                    label = {
                        Text(
                            "Card Texts",
                            color = Color.Red,
                            fontSize = 15.sp
                        )
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Red,
                        unfocusedBorderColor = Color(0xFFa60021),
                        cursorColor = Color.Black
                    ),
                    placeholder = {
                        Text(
                            "Enter Bingo Card Text",
                            color = Color.LightGray
                        )
                    }
                )
                F1Button(
                    text = "Add",
                    onClick = {
                        if (cardTexts.size < numCards) {
                            if (cardText.isNotBlank()) {
                                cardTexts = cardTexts + cardText
                                cardText = ""
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Maximum Number of Bingo Cards ($numCards) reached",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                )
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
            ) {
                itemsIndexed(cardTexts) { index, curCardPhrase ->
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(Color.LightGray.copy(alpha = 0.2f))
                            .clickable {
                                val newList = cardTexts.toMutableList()
                                newList.removeAt(index)
                                cardTexts = newList
                            }
                            .padding(8.dp),
                        text = "${index + 1}. $curCardPhrase"
                    )
                    Divider(
                        color = Color.Black,
                        thickness = 1.dp
                    )
                }
            }
            if (cardTexts.isNotEmpty()) {
                F1Button(
                    text = "Start Bingo",
                    onClick = {
                        if (cardTexts.size < numCards) {
                            Toast.makeText(
                                context,
                                "Please fill out all Bingo Cards",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            generateCards(cardTexts.toList())
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun BingoGameScreen(gridSize: Int, cardTexts: List<String>) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray),
        contentAlignment = Alignment.Center
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(gridSize),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(0.9f)
                .aspectRatio(1f)
        ) {
            items(cardTexts) { cardText ->
                BingoCard(cardText = cardText)
            }
        }
    }
}

@Composable
fun F1Button(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier
) {
    Button(
        modifier = modifier.padding(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Red,
            contentColor = Color.White
        ),
        onClick = onClick
    ) {
        Text(text = text)
    }
}

@Composable
fun BingoCard(cardText: String) {
    var isCrossed by remember { mutableStateOf(false) }
    val backgroundColor = if (isCrossed) Color(0xFF292929) else Color(0xFF696969)

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .background(backgroundColor)
            .border(1.dp, Color.Black)
            .clickable { isCrossed = !isCrossed },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = cardText,
            fontSize = 16.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(8.dp)
        )
        if (isCrossed) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 4.dp.toPx()
                val color = Color.Red
                drawLine(
                    color = color,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, size.height),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = color,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, 0f),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

@Composable
fun F1OutlinedTextField() {
    TODO()
}