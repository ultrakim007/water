package com.example

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppDatabase
import com.example.data.DailyHealthRepository
import com.example.ui.DailyCareRoute
import com.example.ui.DailyCareViewModel
import com.example.ui.theme.SeniorCareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = Color.TRANSPARENT,
                darkScrim = Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                scrim = Color.TRANSPARENT,
                darkScrim = Color.TRANSPARENT
            )
        )

        val database = AppDatabase.getInstance(applicationContext)
        val repository = DailyHealthRepository(database.dailyHealthDao())
        val viewModelFactory = DailyCareViewModel.Factory(repository)

        setContent {
            SeniorCareTheme {
                val viewModel: DailyCareViewModel = viewModel(factory = viewModelFactory)
                DailyCareRoute(viewModel = viewModel)
            }
        }
    }
}
