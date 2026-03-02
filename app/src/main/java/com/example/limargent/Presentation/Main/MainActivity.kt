package com.example.limargent.Presentation.Main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.limargent.Data.SessionManager
import com.example.limargent.Presentation.Login.LoginScreen
import com.example.limargent.Presentation.Login.LoginViewModel
import com.example.limargent.Presentation.Register.RegisterScreen
import com.example.limargent.Presentation.Register.RegisterViewModel
import com.example.limargent.ui.theme.LimArgentTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

enum class ScreenState { LOGIN, REGISTER, HOME }

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val loginViewModel: LoginViewModel by viewModels()
        val registerViewModel: RegisterViewModel by viewModels()

        setContent {
            LimArgentTheme {
                val userAct by sessionManager.UserIdentifiant.collectAsState()

                var screenAct by remember { mutableStateOf(if (userAct != null) ScreenState.HOME else ScreenState.LOGIN) }

                LaunchedEffect(userAct) {
                    if (userAct != null) {
                        screenAct = ScreenState.HOME
                    } else {
                        if (screenAct == ScreenState.HOME) {
                            screenAct = ScreenState.LOGIN
                        }
                    }
                }

                when (screenAct) {
                    ScreenState.LOGIN -> {
                        LoginScreen(viewModel = loginViewModel, onNavigateToMain = {screenAct = ScreenState.HOME}, onNavigateToRegister = { screenAct = ScreenState.REGISTER })
                    }
                    ScreenState.REGISTER -> {
                        RegisterScreen(viewModel = registerViewModel, onNavigateToMain = {screenAct = ScreenState.HOME }, onNavigateToLogin = { screenAct = ScreenState.LOGIN })
                    }
                    ScreenState.HOME -> {
                        MainAppScreen(onLogout = { sessionManager.logout() })
                    }
                }
            }
        }
    }
}