package com.localhelp.app
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.localhelp.app.ui.LocalHelpApp
import com.localhelp.app.ui.theme.LocalHelpTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LocalHelpTheme{
                LocalHelpApp()
            }
        }
    }
}

