package com.example.limargent.Presentation.Main
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.limargent.Presentation.Dashboard.DashboardActivity
import kotlin.jvm.java

@Composable
fun MainScreen(){
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Bouton vers DashBoardActivity
        Button(onClick = {
            val intent = Intent(context, DashboardActivity::class.java)
            context.startActivity(intent)
        }) {
            Text("Dashboard")
        }


        }
    }
