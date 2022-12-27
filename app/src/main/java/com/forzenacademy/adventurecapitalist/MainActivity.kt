package com.forzenacademy.adventurecapitalist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.forzenacademy.adventurecapitalist.ui.theme.AdventureCapitalistTheme

class MainActivity : ComponentActivity() {

    private val viewModel: BigMoneyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.startGameLoop()

        setContent {
            Content(viewModel.state.value, viewModel)
        }
    }
}
