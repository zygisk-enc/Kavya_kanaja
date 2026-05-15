package com.zygisk.kavya_kanaja.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.zygisk.kavya_kanaja.R
import com.zygisk.kavya_kanaja.ui.viewmodel.PoemViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    poemViewModel: PoemViewModel,
    onPoemClick: (Int) -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateToLibrary: () -> Unit
) {
    val allPoems by poemViewModel.poems.collectAsState()
    val searchQuery by poemViewModel.searchQuery.collectAsState()
    val playlists by poemViewModel.playlists.collectAsState()
    
    var showPlaylistDialog by remember { mutableStateOf(false) }
    var selectedPoemId by remember { mutableStateOf<Int?>(null) }

    if (showPlaylistDialog && selectedPoemId != null) {
        com.zygisk.kavya_kanaja.ui.components.AddToPlaylistDialog(
            playlists = playlists,
            onPlaylistSelected = { playlistId ->
                poemViewModel.addPoemToPlaylist(playlistId, selectedPoemId!!)
            },
            onDismiss = { showPlaylistDialog = false }
        )
    }

    Box(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Explore All Poems",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { poemViewModel.setSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Search by title or poet...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { poemViewModel.setSearchQuery("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear search")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${allPoems.size} ${if (allPoems.size == 1) "poem" else "poems"} available",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            items(allPoems) { poem ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clickable { onPoemClick(poem.id) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        AsyncImage(
                            model = R.drawable.poem_thumbnail,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = poem.title,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = poem.poet,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    IconButton(onClick = { 
                        selectedPoemId = poem.id
                        showPlaylistDialog = true
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Add to playlist")
                    }
                    IconButton(onClick = { onPoemClick(poem.id) }) {
                        Icon(Icons.Default.PlayCircleOutline, contentDescription = "Play")
                    }
                }
            }
        }
    }
}
