package com.financeia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.financeia.ui.navigation.*
import com.financeia.ui.theme.FinanceIATheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            FinanceIATheme {
                FinanceIAApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceIAApp() {
    val navController = rememberNavController()
    val currentEntry  by navController.currentBackStackEntryAsState()
    val currentRoute  = currentEntry?.destination?.route

    // Rotas que mostram a bottom bar
    val showBottomBar = bottomNavItems.any { currentRoute == it.route }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            selected = currentRoute == screen.route,
                            onClick  = { navController.navigateSingleTop(screen.route) },
                            icon     = { Icon(screen.icon, contentDescription = screen.label) },
                            label    = { Text(screen.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            FinanceIANavGraph(navController)
        }
    }
}
