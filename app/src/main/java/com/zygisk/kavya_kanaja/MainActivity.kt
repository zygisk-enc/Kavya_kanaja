package com.zygisk.kavya_kanaja

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import com.zygisk.kavya_kanaja.auth.AuthManager
import com.zygisk.kavya_kanaja.data.local.AppDatabase
import com.zygisk.kavya_kanaja.ui.screens.AuthorPoemsScreen
import com.zygisk.kavya_kanaja.ui.screens.DetailScreen
import com.zygisk.kavya_kanaja.ui.screens.HomeScreen
import com.zygisk.kavya_kanaja.ui.screens.LoginScreen
import com.zygisk.kavya_kanaja.ui.screens.ProfileScreen
import com.zygisk.kavya_kanaja.ui.screens.RegistrationScreen
import com.zygisk.kavya_kanaja.ui.screens.LibraryScreen
import com.zygisk.kavya_kanaja.ui.screens.ExploreScreen
import com.zygisk.kavya_kanaja.ui.screens.QuizScreen
import com.zygisk.kavya_kanaja.ui.theme.KavyaKanajaTheme
import com.zygisk.kavya_kanaja.ui.viewmodel.AuthViewModel
import com.zygisk.kavya_kanaja.ui.viewmodel.PoemViewModel
import com.zygisk.kavya_kanaja.ui.components.BottomPlaybackBar

import com.zygisk.kavya_kanaja.ui.screens.PoetBiographyScreen
import com.zygisk.kavya_kanaja.ui.screens.PoetDetailScreen
import com.zygisk.kavya_kanaja.ui.screens.AuthorsListScreen
import com.zygisk.kavya_kanaja.ui.screens.CategoriesListScreen
import com.zygisk.kavya_kanaja.ui.screens.CategoryPoemsScreen

import com.zygisk.kavya_kanaja.ui.screens.PlaylistDetailScreen

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.zygisk.kavya_kanaja.service.AudioService

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Permission result handled if needed
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private var audioService: AudioService? = null
    private var isBound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as AudioService.AudioBinder
            audioService = binder.getService()
            isBound = true
            // Inject service into ViewModel once connected
            poemViewModelInstance?.setAudioService(audioService)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
            poemViewModelInstance?.setAudioService(null)
        }
    }

    private var poemViewModelInstance: PoemViewModel? = null

    override fun onStart() {
        super.onStart()
        Intent(this, AudioService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        checkNotificationPermission()

        val db = AppDatabase.getDatabase(applicationContext)
        val authManager = AuthManager(applicationContext)
        
        val authViewModelFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(authManager, db.userProfileDao()) as T
            }
        }

        setContent {
            KavyaKanajaTheme {
                val navController = rememberNavController()
                val poemViewModel: PoemViewModel = viewModel()
                poemViewModelInstance = poemViewModel
                
                // Initialize service if already bound
                if (this@MainActivity.isBound) {
                    poemViewModel.setAudioService(this@MainActivity.audioService)
                }
                
                val authViewModel: AuthViewModel = viewModel(factory = authViewModelFactory)

                val userState by authViewModel.userState.collectAsState()
                val playlists by poemViewModel.playlists.collectAsState()
                val currentBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = currentBackStackEntry?.destination?.route
                val currentPoem by poemViewModel.currentPoem.collectAsState()

                Scaffold(
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                    bottomBar = {
                        if (userState != null) {
                            val showPlayback = currentPoem != null && currentRoute != null && !currentRoute.startsWith("detail")
                            val showNav = currentRoute == "home" || currentRoute == "explore" || currentRoute == "library" || currentRoute == "poet_biographies" || currentRoute == "quizzes"
                            
                            if (showPlayback || showNav) {
                                Surface(
                                    color = MaterialTheme.colorScheme.surface,
                                    tonalElevation = 0.dp,
                                    shadowElevation = 8.dp
                                ) {
                                    Column(modifier = Modifier.navigationBarsPadding()) {
                                        if (showPlayback) {
                                            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                                            BottomPlaybackBar(
                                                viewModel = poemViewModel,
                                                onClick = {
                                                    navController.navigate("detail/${currentPoem!!.id}")
                                                }
                                            )
                                        }

                                        if (showNav) {
                                            if (!showPlayback) {
                                                HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                                            }
                                            NavigationBar(
                                                containerColor = Color.Transparent,
                                                tonalElevation = 0.dp
                                            ) {
                                                NavigationBarItem(
                                                    selected = currentRoute == "home",
                                                    onClick = {
                                                        poemViewModel.resetFilters()
                                                        navController.navigate("home") {
                                                            popUpTo("home") { saveState = true }
                                                            launchSingleTop = true
                                                            restoreState = true
                                                        }
                                                    },
                                                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                                    label = { Text("Home") }
                                                )
                                                NavigationBarItem(
                                                    selected = currentRoute == "poet_biographies",
                                                    onClick = {
                                                        navController.navigate("poet_biographies") {
                                                            popUpTo("home") { saveState = true }
                                                            launchSingleTop = true
                                                            restoreState = true
                                                        }
                                                    },
                                                    icon = { Icon(Icons.Default.Person, contentDescription = "Poets") },
                                                    label = { Text("Poets") }
                                                )
                                                NavigationBarItem(
                                                    selected = currentRoute == "explore",
                                                    onClick = {
                                                        navController.navigate("explore") {
                                                            popUpTo("home") { saveState = true }
                                                            launchSingleTop = true
                                                            restoreState = true
                                                        }
                                                    },
                                                    icon = { Icon(Icons.Default.Explore, contentDescription = "Explore") },
                                                    label = { Text("Explore") }
                                                )
                                                NavigationBarItem(
                                                    selected = currentRoute == "library",
                                                    onClick = {
                                                        navController.navigate("library") {
                                                            popUpTo("home") { saveState = true }
                                                            launchSingleTop = true
                                                            restoreState = true
                                                        }
                                                    },
                                                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Library") },
                                                    label = { Text("Library") }
                                                )
                                                NavigationBarItem(
                                                    selected = currentRoute == "quizzes",
                                                    onClick = {
                                                        navController.navigate("quizzes") {
                                                            popUpTo("home") { saveState = true }
                                                            launchSingleTop = true
                                                            restoreState = true
                                                        }
                                                    },
                                                    icon = { Icon(Icons.Default.Star, contentDescription = "Quizzes") },
                                                    label = { Text("Quizzes") }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                ) { padding ->
                    NavHost(
                        navController = navController,
                        startDestination = if (userState == null) "login" else "home",
                        modifier = Modifier.padding(bottom = padding.calculateBottomPadding())
                    ) {
                        composable("login") {
                            LoginScreen(
                                authViewModel = authViewModel,
                                onLoginSuccess = {
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onNavigateToRegister = {
                                    navController.navigate("register")
                                }
                            )
                        }
                        composable("register") {
                            RegistrationScreen(
                                authViewModel = authViewModel,
                                onRegistrationSuccess = {
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onBackToLogin = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable("home") {
                            HomeScreen(
                                poemViewModel = poemViewModel,
                                authViewModel = authViewModel,
                                onPoemClick = { poemId ->
                                    navController.navigate("detail/$poemId")
                                },
                                onPlaylistClick = { playlist ->
                                    navController.navigate("playlist/${playlist.id}")
                                },
                                onProfileClick = {
                                    navController.navigate("profile")
                                },
                                onNavigateToExplore = {
                                    navController.navigate("explore") {
                                        popUpTo("home") { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                onNavigateToLibrary = {
                                    navController.navigate("library") {
                                        popUpTo("home") { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                        composable("playlist/{playlistId}") { backStackEntry ->
                            val playlistId = backStackEntry.arguments?.getString("playlistId")?.toIntOrNull()
                            val playlist = playlists.find { it.id == playlistId }
                            if (playlist != null) {
                                PlaylistDetailScreen(
                                    playlist = playlist,
                                    viewModel = poemViewModel,
                                    onPoemClick = { poemId ->
                                        navController.navigate("detail/$poemId")
                                    },
                                    onBack = { navController.popBackStack() }
                                )
                            }
                        }
                        composable("explore") {
                            ExploreScreen(
                                poemViewModel = poemViewModel,
                                onPoemClick = { poemId ->
                                    navController.navigate("detail/$poemId")
                                },
                                onNavigateHome = {
                                    navController.navigate("home") {
                                        popUpTo("home") { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                onNavigateToLibrary = {
                                    navController.navigate("library") {
                                        popUpTo("home") { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                        composable("library") {
                            LibraryScreen(
                                poemViewModel = poemViewModel,
                                onPoemClick = { poemId ->
                                    navController.navigate("detail/$poemId")
                                },
                                onNavigateToAuthors = {
                                    navController.navigate("authors_list")
                                },
                                onNavigateToCategories = {
                                    navController.navigate("categories_list")
                                },
                                onNavigateToExplore = {
                                    navController.navigate("explore") {
                                        popUpTo("home") { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                onNavigateHome = {
                                    navController.navigate("home") {
                                        popUpTo("home") { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                        composable("authors_list") {
                            AuthorsListScreen(
                                poemViewModel = poemViewModel,
                                onAuthorClick = { authorName ->
                                    navController.navigate("author/${android.net.Uri.encode(authorName)}")
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("categories_list") {
                            CategoriesListScreen(
                                poemViewModel = poemViewModel,
                                onCategoryClick = { categoryName ->
                                    navController.navigate("category_poems/${android.net.Uri.encode(categoryName)}")
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("category_poems/{categoryName}") { backStackEntry ->
                            val categoryName = backStackEntry.arguments?.getString("categoryName")?.let {
                                android.net.Uri.decode(it)
                            }
                            if (categoryName != null) {
                                CategoryPoemsScreen(
                                    categoryName = categoryName,
                                    viewModel = poemViewModel,
                                    onPoemClick = { poemId ->
                                        navController.navigate("detail/$poemId")
                                    },
                                    onBack = { navController.popBackStack() }
                                )
                            }
                        }
                        composable("profile") {
                            ProfileScreen(
                                authViewModel = authViewModel,
                                onBack = {
                                    navController.popBackStack()
                                },
                                onLogout = {
                                    navController.navigate("login") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("quizzes") {
                            QuizScreen(
                                poemViewModel = poemViewModel,
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable("poet_biographies") {
                            PoetBiographyScreen(
                                viewModel = poemViewModel,
                                onPoetClick = { poetName ->
                                    navController.navigate("poet_detail/${android.net.Uri.encode(poetName)}")
                                }
                            )
                        }
                        composable("poet_detail/{poetName}") { backStackEntry ->
                            val poetName = backStackEntry.arguments?.getString("poetName")?.let {
                                android.net.Uri.decode(it)
                            }
                            if (poetName != null) {
                                PoetDetailScreen(
                                    poetName = poetName,
                                    viewModel = poemViewModel,
                                    onBack = { navController.popBackStack() }
                                )
                            }
                        }
                        composable("author/{authorName}") { backStackEntry ->
                            val authorName = backStackEntry.arguments?.getString("authorName")?.let {
                                android.net.Uri.decode(it)
                            }
                            if (authorName != null) {
                                AuthorPoemsScreen(
                                    authorName = authorName,
                                    viewModel = poemViewModel,
                                    onPoemClick = { poemId ->
                                        navController.navigate("detail/$poemId")
                                    },
                                    onBack = { navController.popBackStack() }
                                )
                            }
                        }
                        composable("detail/{poemId}") { backStackEntry ->
                            val poemId = backStackEntry.arguments?.getString("poemId")?.toIntOrNull()
                            if (poemId != null) {
                                DetailScreen(
                                    poemId = poemId,
                                    viewModel = poemViewModel,
                                    onBack = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
