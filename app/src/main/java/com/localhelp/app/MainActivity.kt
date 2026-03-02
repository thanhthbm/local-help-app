package com.localhelp.app
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.localhelp.app.data.local.MainViewModel
import com.localhelp.app.ui.LocalHelpApp
import com.localhelp.app.ui.theme.LocalHelpTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mainViewModel: MainViewModel  by viewModels()

        enableEdgeToEdge()
        setContent {
            LocalHelpTheme{
                LocalHelpApp(mainViewModel)
            }
        }
    }
}

