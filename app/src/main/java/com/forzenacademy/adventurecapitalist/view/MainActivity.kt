package com.forzenacademy.adventurecapitalist.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.forzenacademy.adventurecapitalist.Content
import com.forzenacademy.adventurecapitalist.viewmodel.BigMoneyViewModel

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
