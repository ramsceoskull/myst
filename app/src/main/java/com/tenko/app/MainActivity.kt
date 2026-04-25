package com.tenko.app

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.tenko.app.data.api.ApiClient
import com.tenko.app.data.api.TokenManager
import com.tenko.app.navigation.AppNavigation
import com.tenko.app.ui.theme.MystTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tokenManager = TokenManager(applicationContext)
        ApiClient.init(tokenManager)
        enableEdgeToEdge()
        setContent {
            MystTheme {
                Scaffold( modifier = Modifier.fillMaxSize() ) { _ ->
                    AppNavigation(tokenManager)
                }
            }
        }
    }
}