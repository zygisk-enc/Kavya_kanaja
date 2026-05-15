package com.zygisk.kavya_kanaja.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.zygisk.kavya_kanaja.MainActivity
import com.zygisk.kavya_kanaja.R
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AudioService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private var lastUrl: String? = null
    private val binder = AudioBinder()
    private var mediaSession: MediaSessionCompat? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _isPreparing = MutableStateFlow(false)
    val isPreparing = _isPreparing.asStateFlow()

    private val _currentPosition = MutableStateFlow(0)
    val currentPosition = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0)
    val duration = _duration.asStateFlow()

    private val _currentPoemTitle = MutableStateFlow("")
    val currentPoemTitle = _currentPoemTitle.asStateFlow()

    private val _currentPoet = MutableStateFlow("")
    val currentPoet = _currentPoet.asStateFlow()

    private var job: Job? = null
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val NOTIFICATION_ID = 101
    private val CHANNEL_ID = "audio_playback_channel"

    companion object {
        const val ACTION_PLAY_PAUSE = "com.zygisk.kavya_kanaja.ACTION_PLAY_PAUSE"
        const val ACTION_STOP = "com.zygisk.kavya_kanaja.ACTION_STOP"
    }

    inner class AudioBinder : Binder() {
        fun getService(): AudioService = this@AudioService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY_PAUSE -> togglePlayPause()
            ACTION_STOP -> {
                mediaPlayer?.pause()
                _isPlaying.value = false
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        mediaSession = MediaSessionCompat(this, "AudioService").apply {
            isActive = true
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Audio Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Controls for audio playback"
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun getNotification(title: String, poet: String): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val playPauseAction = if (_isPlaying.value) {
            NotificationCompat.Action(
                android.R.drawable.ic_media_pause, "Pause",
                PendingIntent.getService(this, 1, Intent(this, AudioService::class.java).apply { action = ACTION_PLAY_PAUSE }, PendingIntent.FLAG_IMMUTABLE)
            )
        } else {
            NotificationCompat.Action(
                android.R.drawable.ic_media_play, "Play",
                PendingIntent.getService(this, 1, Intent(this, AudioService::class.java).apply { action = ACTION_PLAY_PAUSE }, PendingIntent.FLAG_IMMUTABLE)
            )
        }

        val stopAction = NotificationCompat.Action(
            android.R.drawable.ic_menu_close_clear_cancel, "Stop",
            PendingIntent.getService(this, 2, Intent(this, AudioService::class.java).apply { action = ACTION_STOP }, PendingIntent.FLAG_IMMUTABLE)
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(poet)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(_isPlaying.value)
            .setContentIntent(pendingIntent)
            .addAction(playPauseAction)
            .addAction(stopAction)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(0, 1)
                .setMediaSession(mediaSession?.sessionToken))
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSilent(true)
            .build()
    }

    fun playAudio(url: String, title: String, poet: String, startPosition: Int = 0) {
        lastUrl = url
        _currentPoemTitle.value = title
        _currentPoet.value = poet
        _isPreparing.value = true
        _isPlaying.value = false

        // Start foreground immediately
        val notification = getNotification(title, "Loading audio...")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }

        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            
            try {
                if (url.startsWith("asset:///")) {
                    val assetName = url.substring("asset:///".length)
                    val afd = assets.openFd(assetName)
                    setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                    afd.close()
                } else {
                    setDataSource(url)
                }
            } catch (e: Exception) {
                _isPlaying.value = false
                _isPreparing.value = false
                stopForeground(STOP_FOREGROUND_REMOVE)
                return
            }

            setOnPreparedListener { 
                _isPreparing.value = false
                if (startPosition > 0) {
                    it.seekTo(startPosition)
                }
                it.start()
                _isPlaying.value = true
                _duration.value = it.duration
                startProgressUpdate()
                // Update notification when ready
                val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.notify(NOTIFICATION_ID, getNotification(title, poet))
            }
            setOnCompletionListener {
                _isPlaying.value = false
                _currentPosition.value = 0
                stopForeground(STOP_FOREGROUND_DETACH)
                job?.cancel()
            }
            setOnErrorListener { mp, _, _ ->
                _isPlaying.value = false
                _isPreparing.value = false
                mp.reset()
                stopForeground(STOP_FOREGROUND_REMOVE)
                true
            }
            prepareAsync()
        }
    }

    fun togglePlayPause() {
        if (_isPreparing.value) return // Ignore clicks during loading

        mediaPlayer?.let {
            try {
                if (it.isPlaying) {
                    it.pause()
                    _isPlaying.value = false
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        stopForeground(STOP_FOREGROUND_DETACH)
                    } else {
                        stopForeground(false)
                    }
                } else {
                    // Force re-download on resume by calling playAudio again
                    lastUrl?.let { url ->
                        playAudio(url, _currentPoemTitle.value, _currentPoet.value, _currentPosition.value)
                    } ?: run {
                        it.start()
                        _isPlaying.value = true
                        val notification = getNotification(_currentPoemTitle.value, _currentPoet.value)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
                        } else {
                            startForeground(NOTIFICATION_ID, notification)
                        }
                        startProgressUpdate()
                    }
                }
                // Force notification update
                val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.notify(NOTIFICATION_ID, getNotification(_currentPoemTitle.value, _currentPoet.value))
            } catch (e: IllegalStateException) {
                // Player not ready, ignore
            }
        }
    }

    fun seekTo(position: Int) {
        // Force re-download on seek by calling playAudio again
        lastUrl?.let { url ->
            playAudio(url, _currentPoemTitle.value, _currentPoet.value, position)
        } ?: run {
            mediaPlayer?.seekTo(position)
            _currentPosition.value = position
        }
    }

    private fun startProgressUpdate() {
        job?.cancel()
        job = serviceScope.launch {
            while (_isPlaying.value) {
                mediaPlayer?.let {
                    _currentPosition.value = it.currentPosition
                }
                delay(1000)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaSession?.release()
        serviceScope.cancel()
    }
}
