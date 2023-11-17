package com.example.decathlon.ui.composables

import android.view.LayoutInflater
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.decathlon.R
import com.facebook.shimmer.ShimmerFrameLayout

@Composable
fun ComposeShimmer(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val shimmer = remember {
        ShimmerFrameLayout(context).apply {
            addView(LayoutInflater.from(context).inflate(R.layout.shimmer_placeholder, null))
        }
    }
    AndroidView(
        modifier = modifier,
        factory = { shimmer }
    ) { it.startShimmer() }
}