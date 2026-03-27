package com.localhelp.app.data

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
        enableEdgeToEdge()

        val mainViewModel: MainViewModel by viewModels()
        setContent {
            LocalHelpTheme {
                LocalHelpApp(mainViewModel)
            }
        }
    }
}
