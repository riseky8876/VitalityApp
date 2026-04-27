package com.vitality.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vitality.app.ui.VitalityNavGraph
import com.vitality.app.ui.theme.NeuBackground
import com.vitality.app.ui.theme.VitalityTheme
import com.vitality.app.viewmodel.HealthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VitalityTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(NeuBackground),
                    color = NeuBackground,
                ) {
                    val viewModel: HealthViewModel = viewModel()
                    VitalityNavGraph(viewModel = viewModel)
                }
            }
        }
    }
}
