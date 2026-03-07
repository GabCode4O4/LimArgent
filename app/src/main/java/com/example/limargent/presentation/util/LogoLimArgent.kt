package com.example.limargent.presentation.util

import androidx.compose.foundation.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import com.example.limargent.R

@Composable
fun LogoLimArgent(
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val imageId = if (isDark) {
        R.drawable.logo_blanc
    } else {
        R.drawable.logo_noir
    }

    Image(
        painter = painterResource(id = imageId),
        contentDescription = "Logo de l'application LimArgent",
        modifier = modifier
    )
}