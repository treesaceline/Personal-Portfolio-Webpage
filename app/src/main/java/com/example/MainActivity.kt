package com.example

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    // Enable seamless edge-to-edge system bars integration
    enableEdgeToEdge()
    
    setContent {
      MyApplicationTheme {
        Scaffold(
          modifier = Modifier.fillMaxSize(),
          containerColor = Color(0xFF0F172A) // Sleek dark navy matching the web background
        ) { innerPadding ->
          Box(
            modifier = Modifier
              .fillMaxSize()
              .padding(innerPadding)
          ) {
            // Fullbleed responsive visual interface
            PortfolioWebView(
              modifier = Modifier.fillMaxSize()
            )

            // Floating copy-extract action button at the bottom right corner
            CopyHtmlFab(
              modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .navigationBarsPadding()
                .testTag("copy_html_fab")
            )
          }
        }
      }
    }
  }
}

@Composable
fun PortfolioWebView(modifier: Modifier = Modifier) {
  AndroidView(
    factory = { context ->
      WebView(context).apply {
        webViewClient = WebViewClient()
        // Match base canvas styling for dark elegant transitions
        setBackgroundColor(android.graphics.Color.parseColor("#0f172a"))
        
        settings.apply {
          javaScriptEnabled = true
          domStorageEnabled = true
          useWideViewPort = true
          loadWithOverviewMode = true
          databaseEnabled = true
          loadsImagesAutomatically = true
          mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        
        loadUrl("file:///android_asset/portfolio.html")
      }
    },
    modifier = modifier
  )
}

@Composable
fun CopyHtmlFab(modifier: Modifier = Modifier) {
  val context = LocalContext.current
  
  // Read target HTML content from assets safely
  val htmlContent = remember {
    try {
      context.assets.open("portfolio.html").bufferedReader().use { it.readText() }
    } catch (e: Exception) {
      ""
    }
  }

  FloatingActionButton(
    onClick = {
      if (htmlContent.isNotEmpty()) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Portfolio Source", htmlContent)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Full HTML Portfolio copied to clipboard!", Toast.LENGTH_SHORT).show()
      } else {
        Toast.makeText(context, "Error reading source portfolio file.", Toast.LENGTH_SHORT).show()
      }
    },
    modifier = modifier,
    // Custom styled colors conforming exactly to requested (#9b7373 background, #ffdede foreground)
    containerColor = Color(0xFF9B7373),
    contentColor = Color(0xFFFFDEDE),
    shape = RoundedCornerShape(16.dp)
  ) {
    Icon(
      imageVector = Icons.Default.Share, 
      contentDescription = "Copy Portfolio HTML code",
      modifier = Modifier.size(24.dp)
    )
  }
}
