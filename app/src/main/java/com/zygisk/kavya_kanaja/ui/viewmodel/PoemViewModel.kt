package com.zygisk.kavya_kanaja.ui.viewmodel

import android.app.Application
import android.content.Intent
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zygisk.kavya_kanaja.data.local.AppDatabase
import com.zygisk.kavya_kanaja.data.model.Poem
import com.zygisk.kavya_kanaja.data.model.PoetBio
import com.zygisk.kavya_kanaja.data.repository.PoemRepository
import com.zygisk.kavya_kanaja.service.AudioService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class PoemViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = PoemRepository(application, db.poemDao(), db.playlistDao())

    private val _poems = MutableStateFlow<List<Poem>>(emptyList())
    
    private val _poetBios = MutableStateFlow<List<PoetBio>>(emptyList())
    val poetBios: StateFlow<List<PoetBio>> = _poetBios.asStateFlow()

    private val _audioService = MutableStateFlow<AudioService?>(null)

    fun setAudioService(service: AudioService?) {
        _audioService.value = service
    }

    private val _playlists = repository.getAllPlaylists().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val playlists: StateFlow<List<com.zygisk.kavya_kanaja.data.local.PlaylistEntity>> = _playlists

    fun createPlaylist(name: String) {
        viewModelScope.launch { repository.createPlaylist(name) }
    }

    fun deletePlaylist(playlist: com.zygisk.kavya_kanaja.data.local.PlaylistEntity) {
        viewModelScope.launch { repository.deletePlaylist(playlist) }
    }

    fun renamePlaylist(playlist: com.zygisk.kavya_kanaja.data.local.PlaylistEntity, newName: String) {
        viewModelScope.launch { repository.renamePlaylist(playlist, newName) }
    }

    fun addPoemToPlaylist(playlistId: Int, poemId: Int) {
        viewModelScope.launch { repository.addPoemToPlaylist(playlistId, poemId) }
    }

    fun removePoemFromPlaylist(playlistId: Int, poemId: Int) {
        viewModelScope.launch { repository.removePoemFromPlaylist(playlistId, poemId) }
    }
    
    fun getPoemsInPlaylist(playlistId: Int): StateFlow<List<Poem>> {
        return combine(_poems, repository.getPoemIdsInPlaylist(playlistId)) { poems, refs ->
            val ids = refs.map { it.poemId }.toSet()
            poems.filter { it.id in ids }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val recentlyPlayed: StateFlow<List<Poem>> = combine(_poems, repository.getRecentlyPlayedFlow(10)) { allPoems, recentEntities ->
        val recentIds = recentEntities.map { it.poemId }
        recentIds.mapNotNull { id -> allPoems.find { it.id == id } }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    val favoritePoems: StateFlow<List<Poem>> = combine(_poems, repository.getFavoritePoemsFlow()) { allPoems, favorites ->
        val favoriteIds = favorites.map { it.poemId }.toSet()
        allPoems.filter { it.id in favoriteIds }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun toggleFavorite(poemId: Int, isCurrentlyFavorite: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(poemId, isCurrentlyFavorite)
        }
    }

    val poems: StateFlow<List<Poem>> = combine(_poems, _searchQuery, _selectedCategory) { poems, query, category ->
        var filtered = poems
        if (query.isNotBlank()) {
            filtered = filtered.filter { 
                it.title.contains(query, ignoreCase = true) || 
                it.poet.contains(query, ignoreCase = true) 
            }
        }
        if (category != null) {
            filtered = filtered.filter { it.category == category }
        }
        filtered
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val categories: StateFlow<List<String>> = _poems.map { poems ->
        poems.mapNotNull { it.category }.distinct().sorted()
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _currentPoem = MutableStateFlow<Poem?>(null)
    val currentPoem: StateFlow<Poem?> = _currentPoem.asStateFlow()
    
    val isPlaying: StateFlow<Boolean> = _audioService.flatMapLatest { 
        it?.isPlaying ?: flowOf(false) 
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    val isPreparing: StateFlow<Boolean> = _audioService.flatMapLatest { 
        it?.isPreparing ?: flowOf(false) 
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    val currentPosition: StateFlow<Float> = _audioService.flatMapLatest { 
        it?.currentPosition?.map { p -> p.toFloat() } ?: flowOf(0f) 
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0f)

    val duration: StateFlow<Float> = _audioService.flatMapLatest { 
        it?.duration?.map { d -> d.toFloat() } ?: flowOf(0f) 
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0f)

    init {
        loadPoems()
        loadPoetBios()
        observePlaybackProgress()
    }

    private fun observePlaybackProgress() {
        viewModelScope.launch {
            currentPosition.collect { position ->
                val poem = _currentPoem.value
                if (poem != null && position > 0 && position.toInt() % 5000 < 1000) { // Save every ~5 seconds
                    repository.updatePlaybackPosition(poem.id, position.toInt())
                }
            }
        }
    }

    private fun loadPoetBios() {
        viewModelScope.launch {
            _poetBios.value = repository.getPoetBios()
        }
    }

    private fun loadPoems() {
        viewModelScope.launch {
            val loadedPoems = repository.getAllPoems()
            _poems.value = loadedPoems
            val assetFiles = getApplication<Application>().assets.list("")?.toSet() ?: emptySet()
            val localAssetPoemIds = loadedPoems.filter { poem ->
                poem.audioFileName != null && 
                !poem.audioFileName.startsWith("http") &&
                assetFiles.contains(poem.audioFileName)
            }.map { it.id }
            if (localAssetPoemIds.isNotEmpty()) {
                repository.ensureDefaultPlaylist(localAssetPoemIds)
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategory(category: String?) {
        _selectedCategory.value = if (_selectedCategory.value == category) null else category
    }

    fun resetFilters() {
        _searchQuery.value = ""
        _selectedCategory.value = null
    }

    fun selectPoem(id: Int) {
        viewModelScope.launch {
            val poem = repository.getPoemById(id)
            if (_currentPoem.value?.id != poem?.id) {
                _currentPoem.value = poem
            }
            poem?.let { addToRecentlyPlayed(it) }
        }
    }

    private fun addToRecentlyPlayed(poem: Poem) {
        viewModelScope.launch {
            repository.markAsPlayed(poem.id)
        }
    }

    fun toggleAudio() {
        val poem = _currentPoem.value ?: return
        val audioFile = poem.audioFileName

        if (audioFile == null) {
            android.widget.Toast.makeText(getApplication(), "No audio available", android.widget.Toast.LENGTH_SHORT).show()
            return
        }

        val service = _audioService.value
        if (service != null) {
            // Check if service is already handling THIS poem
            val isSamePoem = service.currentPoemTitle.value == poem.title
            
            // If it's already the current poem (even if still preparing), just toggle it
            if (isSamePoem) {
                service.togglePlayPause()
                
                // Save progress on pause
                if (!service.isPlaying.value) {
                    viewModelScope.launch {
                        repository.updatePlaybackPosition(poem.id, service.currentPosition.value)
                    }
                }
                return
            }

            // Explicitly start the service as foreground if it's a NEW poem
            val intent = Intent(getApplication(), AudioService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getApplication<Application>().startForegroundService(intent)
            } else {
                getApplication<Application>().startService(intent)
            }
            
            // Load and play the new poem
            viewModelScope.launch {
                val assetFiles = getApplication<Application>().assets.list("")?.toSet() ?: emptySet()
                val finalUrl = when {
                    audioFile.startsWith("http") -> audioFile
                    assetFiles.contains(audioFile) -> "asset:///$audioFile"
                    else -> {
                        val base = "https://raw.githubusercontent.com/zygisk-enc/kavya_kanaja_audio_files/main/"
                        if (audioFile.endsWith(".mp3")) "$base$audioFile" else "$base$audioFile.mp3"
                    }
                }

                val progress = repository.getProgressForPoemSync(poem.id)
                val startPos = progress?.lastAudioPosition ?: 0
                service.playAudio(finalUrl, poem.title, poem.poet, startPos)
                addToRecentlyPlayed(poem)
            }
        } else {
            android.widget.Toast.makeText(getApplication(), "Audio Service connecting...", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    fun seekTo(position: Float) {
        _audioService.value?.seekTo(position.toInt())
    }
}
