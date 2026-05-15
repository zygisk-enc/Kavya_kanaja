package com.zygisk.kavya_kanaja.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.zygisk.kavya_kanaja.R
import com.zygisk.kavya_kanaja.ui.viewmodel.AuthViewModel
import com.zygisk.kavya_kanaja.ui.viewmodel.PoemViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    poemViewModel: PoemViewModel,
    authViewModel: AuthViewModel,
    onPoemClick: (Int) -> Unit,
    onPlaylistClick: (com.zygisk.kavya_kanaja.data.local.PlaylistEntity) -> Unit,
    onProfileClick: () -> Unit,
    onNavigateToExplore: () -> Unit,
    onNavigateToLibrary: () -> Unit
) {
    val poems by poemViewModel.poems.collectAsState()
    val searchQuery by poemViewModel.searchQuery.collectAsState()
    val userState by authViewModel.userState.collectAsState()
    val categories by poemViewModel.categories.collectAsState()
    val selectedCategory by poemViewModel.selectedCategory.collectAsState()
    val recentlyPlayed by poemViewModel.recentlyPlayed.collectAsState()
    val playlists by poemViewModel.playlists.collectAsState()

    var isSearchActive by remember { mutableStateOf(false) }
    var showCreatePlaylistDialog by remember { mutableStateOf(false) }
    var newPlaylistName by remember { mutableStateOf("") }

    if (showCreatePlaylistDialog) {
        AlertDialog(
            onDismissRequest = { showCreatePlaylistDialog = false },
            title = { Text("Create New Playlist") },
            text = {
                OutlinedTextField(
                    value = newPlaylistName,
                    onValueChange = { newPlaylistName = it },
                    label = { Text("Playlist Name") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newPlaylistName.isNotBlank()) {
                        poemViewModel.createPlaylist(newPlaylistName)
                        newPlaylistName = ""
                        showCreatePlaylistDialog = false
                    }
                }) { Text("Create") }
            },
            dismissButton = {
                TextButton(onClick = { showCreatePlaylistDialog = false }) { Text("Cancel") }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            item {
                if (isSearchActive) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { poemViewModel.setSearchQuery(it) },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Search poems or poets...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            trailingIcon = {
                                IconButton(onClick = { 
                                    isSearchActive = false 
                                    poemViewModel.setSearchQuery("")
                                }) {
                                    Icon(Icons.Default.Close, contentDescription = "Close search")
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "ಕಾವ್ಯ ಕಣಜ",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                modifier = Modifier
                                    .size(28.dp)
                                    .clickable { isSearchActive = true }
                            )
                            Spacer(modifier = Modifier.width(20.dp))
                            AsyncImage(
                                model = userState?.photoUrl,
                                contentDescription = "Profile",
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { onProfileClick() },
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }

            // Categories
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    item {
                        val isSelected = selectedCategory == null && searchQuery.isBlank()
                        Surface(
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.clickable { poemViewModel.resetFilters() }
                        ) {
                            Text(
                                text = "Home",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    items(categories) { category ->
                        val isSelected = category == selectedCategory
                        Surface(
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.clickable { poemViewModel.setCategory(category) }
                        ) {
                            Text(
                                text = category,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            if (selectedCategory != null || searchQuery.isNotBlank()) {
                // Filtered Results Header
                item {
                    Text(
                        text = if (selectedCategory != null) "Songs in $selectedCategory" else "Search results",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                if (poems.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("No poems found", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                items(poems) { poem ->
                    PoemListItem(poem, onPoemClick)
                }
                
                // Add some padding at the bottom for search results
                item { Spacer(modifier = Modifier.height(100.dp)) }
            } else {
                // Default Home Layout
                // Playlists Section
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Your Playlists",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = { showCreatePlaylistDialog = true }) {
                                Icon(Icons.Default.Add, contentDescription = "Create Playlist")
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            items(playlists) { playlist ->
                                Column(
                                    modifier = Modifier
                                        .width(140.dp)
                                        .clickable { onPlaylistClick(playlist) }
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(140.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(MaterialTheme.colorScheme.primaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AsyncImage(
                                            model = R.drawable.poem_thumbnail,
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                    Text(
                                        text = playlist.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(top = 8.dp),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "Playlist",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                // Speed Dial (Recently Played)
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Last Listened",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        if (recentlyPlayed.isEmpty()) {
                            Text(
                                text = "No songs played recently",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        } else {
                            val speedDialItems = recentlyPlayed.take(6)
                            Column {
                                for (i in 0 until (speedDialItems.size + 2) / 3) {
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        for (j in 0 until 3) {
                                            val index = i * 3 + j
                                            if (index < speedDialItems.size) {
                                                val poem = speedDialItems[index]
                                                Column(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .padding(4.dp)
                                                        .clickable { onPoemClick(poem.id) }
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .aspectRatio(1f)
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
                                                    Text(
                                                        text = poem.title,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis,
                                                        fontSize = 12.sp,
                                                        modifier = Modifier.padding(top = 4.dp)
                                                    )
                                                }
                                            } else {
                                                Spacer(modifier = Modifier.weight(1f))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Quick Picks
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Quick picks",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = { }) {
                            Text("Play all", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                items(poems.takeLast(6)) { poem ->
                    PoemListItem(poem, onPoemClick)
                }
                
                // Bottom padding for player bar
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun PoemListItem(poem: com.zygisk.kavya_kanaja.data.model.Poem, onPoemClick: (Int) -> Unit) {
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
                .clip(RoundedCornerShape(4.dp))
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
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = poem.poet,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        IconButton(onClick = { }) {
            Icon(Icons.Default.MoreVert, contentDescription = "More")
        }
    }
}
