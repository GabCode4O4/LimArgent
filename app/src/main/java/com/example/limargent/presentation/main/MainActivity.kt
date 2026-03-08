package com.example.limargent.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.limargent.application.classe.SessionManager
import com.example.limargent.presentation.configuration.ConfigurationViewModel
import com.example.limargent.presentation.login.LoginScreen
import com.example.limargent.presentation.login.LoginViewModel
import com.example.limargent.presentation.register.RegisterScreen
import com.example.limargent.presentation.register.RegisterViewModel
import com.example.limargent.ui.theme.LimArgentTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.limargent.application.classe.MouvementRecWorker
import java.util.concurrent.TimeUnit

enum class ScreenState {LOGIN, REGISTER, HOME}
@AndroidEntryPoint
class MainActivity : ComponentActivity(){

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val loginViewModel: LoginViewModel by viewModels()
        val registerViewModel: RegisterViewModel by viewModels()
        val configurationViewModel: ConfigurationViewModel by viewModels()

        setContent {
            val theme by configurationViewModel.theme.collectAsStateWithLifecycle()
            val currentUser by sessionManager.currentUser.collectAsStateWithLifecycle()
            var screenAct by remember(currentUser) {
                mutableStateOf(if (currentUser != null) ScreenState.HOME else ScreenState.LOGIN)
            }

            LaunchedEffect(currentUser) {
                if (currentUser != null) {
                    configurationViewModel.loadUserPreferences()
                    screenAct = ScreenState.HOME
                    planifierMouvRec()
                } else {
                    if (screenAct == ScreenState.HOME) {
                        screenAct = ScreenState.LOGIN
                    }
                }
            }
            LimArgentTheme(theme = theme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Crossfade(
                        targetState = screenAct,
                        animationSpec = tween(durationMillis = 300),
                        label = "AnimationTransitionEcran"
                    ) { ecranCourant ->

                        when (ecranCourant) {
                            ScreenState.LOGIN -> {
                                LoginScreen(
                                    viewModel = loginViewModel, onNavigateToMain = { screenAct = ScreenState.HOME }, onNavigateToRegister = { screenAct = ScreenState.REGISTER }
                                )
                            }
                            ScreenState.REGISTER -> {
                                RegisterScreen(
                                    viewModel = registerViewModel, onNavigateToMain = { screenAct = ScreenState.HOME }, onNavigateToLogin = { screenAct = ScreenState.LOGIN }
                                )
                            }
                            ScreenState.HOME -> {
                                MainAppScreen(onLogout = {
                                    sessionManager.logout()
                                    loginViewModel.clearError()
                                })
                            }
                        }
                    }
                }
            }
        }
    }
    private fun planifierMouvRec() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<MouvementRecWorker>(24, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "RecurrentMouvementWork",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}