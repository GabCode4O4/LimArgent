package com.example.limargent.Presentation.Main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.limargent.Presentation.Login.LoginScreen
import com.example.limargent.Presentation.Login.LoginViewModel
import com.example.limargent.Presentation.Register.RegisterScreen
import com.example.limargent.Presentation.Register.RegisterViewModel
import com.example.limargent.ui.theme.LimArgentTheme
import dagger.hilt.android.AndroidEntryPoint


enum class ScreenState { LOGIN, REGISTER, MAIN }

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val loginViewModel: LoginViewModel by viewModels()
        val registerViewModel: RegisterViewModel by viewModels()

        setContent {
            LimArgentTheme {
                var currentScreen by remember { mutableStateOf(ScreenState.LOGIN) }

                when (currentScreen) {
                    ScreenState.LOGIN -> {
                        LoginScreen(
                            viewModel = loginViewModel,
                            onNavigateToMain = { currentScreen = ScreenState.MAIN },
                            onNavigateToRegister = { currentScreen = ScreenState.REGISTER }
                        )
                    }
                    ScreenState.REGISTER -> {
                        RegisterScreen(
                            viewModel = registerViewModel,
                            onNavigateToMain = { currentScreen = ScreenState.MAIN },
                            onNavigateToLogin = { currentScreen = ScreenState.LOGIN }
                        )
                    }
                    ScreenState.MAIN -> {
                        MainScreen()
                    }
                }
            }
        }
    }
}