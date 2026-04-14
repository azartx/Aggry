package com.solo4.aggry

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import com.solo4.aggry.data.initSettingsFactory
import com.solo4.aggry.db.AggryDatabaseProvider
import com.solo4.aggry.db.DatabaseDriverFactory
import com.solo4.aggry.db.FileCache

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color(0xFFE6DEFF).toArgb(),
                Color(0xFFBEBEBE).toArgb()
            )
        )
        super.onCreate(savedInstanceState)

        initSettingsFactory(applicationContext)
        AndroidContextHolder.context = applicationContext
        AggryDatabaseProvider.init(
            driverFactory = DatabaseDriverFactory(applicationContext),
            cache = FileCache(applicationContext)
        )

        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
