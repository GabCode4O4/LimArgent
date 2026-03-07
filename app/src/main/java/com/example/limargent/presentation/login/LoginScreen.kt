package com.example.limargent.presentation.login
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.limargent.presentation.util.LogoLimArgent


@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onNavigateToMain: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.clearError()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .verticalScroll(scrollState)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        LogoLimArgent(
            modifier = Modifier.fillMaxWidth(0.7f).aspectRatio(1f)
        )
        OutlinedTextField(
            value = viewModel.identifiant,
            onValueChange = { viewModel.identifiant = it },
            label = { Text("Identifiant") },
            modifier = Modifier.fillMaxWidth(),
            isError = viewModel.errorMessage != null,
            shape = MaterialTheme.shapes.medium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = viewModel.motDePasse,
            onValueChange = { viewModel.motDePasse = it },
            label = { Text("Mot de passe") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            isError = viewModel.errorMessage != null,
            shape = MaterialTheme.shapes.medium
        )

        viewModel.errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.onLog(onLoginSuccess = onNavigateToMain) },
            enabled = !viewModel.isLoading,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = MaterialTheme.shapes.large,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Se connecter", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onNavigateToRegister) {
            Text("Créer un compte", color = MaterialTheme.colorScheme.onBackground)
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}
