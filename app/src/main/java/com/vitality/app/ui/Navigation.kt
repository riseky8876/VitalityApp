package com.vitality.app.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vitality.app.ui.screens.*
import com.vitality.app.viewmodel.HealthViewModel

object VitalityDestinations {
    const val DASHBOARD      = "dashboard"
    const val BATTERY_DETAIL = "battery_detail"
    const val STORAGE_DETAIL = "storage_detail"
    const val APPS           = "apps"
    const val OPTIMIZE       = "optimize"
    const val HISTORY        = "history"
}

@Composable
fun VitalityNavGraph(
    viewModel: HealthViewModel,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController    = navController,
        startDestination = VitalityDestinations.DASHBOARD,
        enterTransition  = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec  = tween(280),
            ) + fadeIn(animationSpec = tween(280))
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { -it / 3 },
                animationSpec = tween(280),
            ) + fadeOut(animationSpec = tween(200))
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -it / 3 },
                animationSpec  = tween(280),
            ) + fadeIn(animationSpec = tween(280))
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(280),
            ) + fadeOut(animationSpec = tween(200))
        },
    ) {
        composable(VitalityDestinations.DASHBOARD) {
            DashboardScreen(
                viewModel           = viewModel,
                onNavigateToBattery = { navController.navigate(VitalityDestinations.BATTERY_DETAIL) },
                onNavigateToStorage = { navController.navigate(VitalityDestinations.STORAGE_DETAIL) },
                onNavigateToApps    = { navController.navigate(VitalityDestinations.APPS) },
                onNavigateToOptimize = { navController.navigate(VitalityDestinations.OPTIMIZE) },
                onNavigateToHistory = { navController.navigate(VitalityDestinations.HISTORY) },
            )
        }

        composable(VitalityDestinations.BATTERY_DETAIL) {
            BatteryDetailScreen(
                viewModel = viewModel,
                onBack    = { navController.popBackStack() },
            )
        }

        composable(VitalityDestinations.STORAGE_DETAIL) {
            StorageDetailScreen(
                viewModel  = viewModel,
                onBack     = { navController.popBackStack() },
                onOptimize = {
                    navController.popBackStack()
                    navController.navigate(VitalityDestinations.OPTIMIZE)
                },
            )
        }

        composable(VitalityDestinations.APPS) {
            AppsScreen(
                viewModel = viewModel,
                onBack    = { navController.popBackStack() },
            )
        }

        composable(VitalityDestinations.OPTIMIZE) {
            OptimizeScreen(
                viewModel = viewModel,
                onBack    = { navController.popBackStack() },
            )
        }

        composable(VitalityDestinations.HISTORY) {
            HistoryScreen(
                viewModel = viewModel,
                onBack    = { navController.popBackStack() },
            )
        }
    }
}
