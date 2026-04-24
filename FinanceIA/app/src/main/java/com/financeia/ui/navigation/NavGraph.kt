package com.financeia.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.financeia.ui.screens.chat.AIChatScreen
import com.financeia.ui.screens.dashboard.DashboardScreen
import com.financeia.ui.screens.goals.GoalsScreen
import com.financeia.ui.screens.transactions.AddTransactionScreen
import com.financeia.ui.screens.transactions.TransactionsScreen

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Dashboard     : Screen("dashboard",     "Início",      Icons.Filled.Home)
    object Transactions  : Screen("transactions",  "Transações",  Icons.Filled.Receipt)
    object Goals         : Screen("goals",         "Metas",       Icons.Filled.Flag)
    object AIChat        : Screen("ai_chat",       "IA",          Icons.Filled.AutoAwesome)
    object AddTransaction: Screen("add_transaction?id={id}", "Nova", Icons.Filled.Add)
}

val bottomNavItems = listOf(
    Screen.Dashboard, Screen.Transactions, Screen.Goals, Screen.AIChat
)

@Composable
fun FinanceIANavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onAddTransaction = { navController.navigate("add_transaction?id=-1") },
                onVerTodas       = { navController.navigate(Screen.Transactions.route) }
            )
        }
        composable(Screen.Transactions.route) {
            TransactionsScreen(
                onAddClick  = { navController.navigate("add_transaction?id=-1") },
                onEditClick = { id -> navController.navigate("add_transaction?id=$id") }
            )
        }
        composable(Screen.Goals.route) {
            GoalsScreen()
        }
        composable(Screen.AIChat.route) {
            AIChatScreen()
        }
        composable(
            route = "add_transaction?id={id}",
            arguments = listOf(navArgument("id") {
                type = NavType.IntType; defaultValue = -1
            })
        ) { backStack ->
            val id = backStack.arguments?.getInt("id") ?: -1
            AddTransactionScreen(
                transacaoId = id,
                onDone = { navController.popBackStack() }
            )
        }
    }
}

fun NavHostController.navigateSingleTop(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState    = true
    }
}
