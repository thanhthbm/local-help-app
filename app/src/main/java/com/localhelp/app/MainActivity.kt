package com.localhelp.app
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.localhelp.app.data.local.MainViewModel
import com.localhelp.app.ui.LocalHelpApp
import com.localhelp.app.ui.theme.LocalHelpTheme
import dagger.hilt.android.AndroidEntryPoint
import java.net.URI

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handleDeepLink(intent)

        enableEdgeToEdge()
        setContent {
            LocalHelpTheme{
                LocalHelpApp(mainViewModel)
            }
        }
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }
    private fun handleDeepLink(intent: Intent?) {
        val data = intent?.data

        val oobCode = data?.getQueryParameter("oobCode")
        val mode = data?.getQueryParameter("mode")

        if(mode == "resetPassword" && oobCode != null){
            mainViewModel.openResetPassword(oobCode)
        }
    }
}

