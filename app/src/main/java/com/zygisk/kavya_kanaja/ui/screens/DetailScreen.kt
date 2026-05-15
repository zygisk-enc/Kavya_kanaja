package com.zygisk.kavya_kanaja.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.zygisk.kavya_kanaja.R
import com.zygisk.kavya_kanaja.ui.viewmodel.PoemViewModel
import com.zygisk.kavya_kanaja.ui.components.AddToPlaylistDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(poemId: Int, viewModel: PoemViewModel, onBack: () -> Unit) {
    LaunchedEffect(poemId) {
        viewModel.selectPoem(poemId)
    }

    val currentPoem by viewModel.currentPoem.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val isPreparing by viewModel.isPreparing.collectAsState()
    val favoritePoems by viewModel.favoritePoems.collectAsState()
    val playlists by viewModel.playlists.collectAsState()
    val isFavorite = favoritePoems.any { it.id == poemId }
    val lifecycleOwner = LocalLifecycleOwner.current
    
    var showPlaylistDialog by remember { mutableStateOf(false) }

    if (showPlaylistDialog) {
        AddToPlaylistDialog(
            playlists = playlists,
            onPlaylistSelected = { playlistId ->
                viewModel.addPoemToPlaylist(playlistId, poemId)
            },
            onDismiss = { showPlaylistDialog = false }
        )
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text(currentPoem?.title ?: "Loading...") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showPlaylistDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add to Playlist")
                    }
                    IconButton(onClick = { viewModel.toggleFavorite(poemId, isFavorite) }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) Color.Red else LocalContentColor.current
                        )
                    }
                }
            )
        },
        bottomBar = {
            currentPoem?.let { poem ->
                val currentPosition by viewModel.currentPosition.collectAsState()
                val duration by viewModel.duration.collectAsState()
                
                var sliderPosition by remember { mutableStateOf(0f) }
                var isDragging by remember { mutableStateOf(false) }
                
                LaunchedEffect(currentPosition) {
                    if (!isDragging) {
                        sliderPosition = currentPosition
                    }
                }

                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    tonalElevation = 3.dp
                ) {
                    Column(modifier = Modifier.navigationBarsPadding()) {
                        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Now Playing",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = poem.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                FilledIconButton(
                                    onClick = { viewModel.toggleAudio() },
                                    colors = IconButtonDefaults.filledIconButtonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    if (isPreparing) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Icon(
                                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                            contentDescription = if (isPlaying) "Pause" else "Play"
                                        )
                                    }
                                }
                            }
                            
                            if (duration > 0f) {
                                Slider(
                                    value = sliderPosition,
                                    onValueChange = { 
                                        isDragging = true
                                        sliderPosition = it 
                                    },
                                    onValueChangeFinished = {
                                        isDragging = false
                                        viewModel.seekTo(sliderPosition)
                                    },
                                    valueRange = 0f..duration,
                                    colors = SliderDefaults.colors(
                                        thumbColor = MaterialTheme.colorScheme.primary,
                                        activeTrackColor = MaterialTheme.colorScheme.primary,
                                        inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.24f)
                                    ),
                                    modifier = Modifier.height(32.dp)
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    val currentTotalSeconds = (sliderPosition / 1000).toInt()
                                    val currentMin = currentTotalSeconds / 60
                                    val currentSec = currentTotalSeconds % 60
                                    
                                    val durationTotalSeconds = (duration / 1000).toInt()
                                    val durationMin = durationTotalSeconds / 60
                                    val durationSec = durationTotalSeconds % 60
                                    
                                    Text(
                                        text = String.format("%02d:%02d", currentMin, currentSec),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                    Text(
                                        text = String.format("%02d:%02d", durationMin, durationSec),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        currentPoem?.let { poem ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                
                // YouTube Player or Thumbnail Section
                if (!poem.youtubeVideoId.isNullOrBlank()) {
                    val videoId = poem.youtubeVideoId!!
                    
                    key(videoId) {
                        AndroidView(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            factory = { context ->
                                YouTubePlayerView(context).apply {
                                    enableAutomaticInitialization = false
                                    val listener = object : AbstractYouTubePlayerListener() {
                                        override fun onReady(youTubePlayer: YouTubePlayer) {
                                            youTubePlayer.loadVideo(videoId, 0f)
                                        }
                                    }
                                    val options = com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions.Builder()
                                        .controls(1)
                                        .build()
                                    
                                    initialize(listener, options)
                                    lifecycleOwner.lifecycle.addObserver(this)
                                }
                            },
                            onRelease = { view ->
                                view.release()
                                lifecycleOwner.lifecycle.removeObserver(view)
                            }
                        )
                    }
                } else {
                    // Placeholder Thumbnail
                    AsyncImage(
                        model = R.drawable.poem_thumbnail,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = poem.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "By ${poem.poet}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Poem Content",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = poem.content,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.3
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Word Meanings",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        poem.meanings.forEach { (word, meaning) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Text(
                                    text = "$word: ",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = meaning,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}
