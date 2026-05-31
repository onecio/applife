package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppDatabase
import com.example.data.repository.LocalMindFitRepository
import com.example.ui.screens.*
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.DividerSlate
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PrimaryTeal
import com.example.ui.theme.SurfaceDark
import com.example.ui.viewmodel.MindFitViewModel
import com.example.ui.viewmodel.MindFitViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Core Room Repository Setup
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = LocalMindFitRepository(database.mindFitDao())
        val factory = MindFitViewModelFactory(repository)

        setContent {
            MyApplicationTheme {
                // InstantiatingViewModel with Factory cleanly
                val mViewModel: MindFitViewModel = viewModel(factory = factory)
                val profile by mViewModel.userProfile.collectAsState()

                if (!profile.onboardingCompleted) {
                    OnboardingAndAuthScreen(
                        viewModel = mViewModel,
                        modifier = Modifier
                            .fillMaxSize()
                            .windowInsetsPadding(WindowInsets.safeDrawing)
                    )
                } else {
                    MindFitAppMainWorkspace(viewModel = mViewModel)
                }
            }
        }
    }
}

@Composable
fun MindFitAppMainWorkspace(
    viewModel: MindFitViewModel
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceDark,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .background(BackgroundDark)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("mindfit_bottom_navigation")
            ) {
                // Tab 0: Início
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Início") },
                    label = { Text("Início", style = MaterialTheme.typography.labelSmall) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryTeal,
                        selectedTextColor = PrimaryTeal,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = SurfaceDark
                    )
                )

                // Tab 1: Coach Chat
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Sms, contentDescription = "Coach IA") },
                    label = { Text("Coach IA", style = MaterialTheme.typography.labelSmall) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryTeal,
                        selectedTextColor = PrimaryTeal,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = SurfaceDark
                    )
                )

                // Tab 2: Foco & Respiração
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Timer, contentDescription = "Foco") },
                    label = { Text("Foco", style = MaterialTheme.typography.labelSmall) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryTeal,
                        selectedTextColor = PrimaryTeal,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = SurfaceDark
                    )
                )

                // Tab 3: Progresso
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.TrendingUp, contentDescription = "Progresso") },
                    label = { Text("Progresso", style = MaterialTheme.typography.labelSmall) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryTeal,
                        selectedTextColor = PrimaryTeal,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = SurfaceDark
                    )
                )

                // Tab 4: Perfil Settings
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                    label = { Text("Perfil", style = MaterialTheme.typography.labelSmall) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryTeal,
                        selectedTextColor = PrimaryTeal,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = SurfaceDark
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark)
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToTab = { selectedTab = it },
                    modifier = Modifier.fillMaxSize()
                )
                1 -> CoachScreen(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
                2 -> FocusScreen(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
                3 -> ProgressScreen(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
                4 -> ProfileScreen(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
