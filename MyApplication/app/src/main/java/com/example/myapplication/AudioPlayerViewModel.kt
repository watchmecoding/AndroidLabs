package com.example.myapplication

import android.app.Application
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel

class AudioPlayerViewModel(application: Application) : AndroidViewModel(application) {

    private var mediaPlayer: MediaPlayer? = null
    private var handler: Handler = Handler(Looper.getMainLooper())
    private var updateCallback: ((Int) -> Unit)? = null

    var isPrepared = false
        private set

    fun initMediaPlayer(uri: Uri) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(getApplication(), uri)
                prepare()
                isPrepared = true
                setOnCompletionListener {
                    stopSeekBarUpdates()
                    updateCallback?.invoke(0)
                }
            }
        }
    }

    fun play() {
        mediaPlayer?.start()
        startSeekBarUpdates()
    }

    fun pause() {
        mediaPlayer?.pause()
    }

    fun stop(uri: Uri) {
        mediaPlayer?.apply {
            stop()
            reset()
            setDataSource(getApplication(), uri)
            prepare()
        }
        updateCallback?.invoke(0)
        stopSeekBarUpdates()
    }

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }

    fun getDuration(): Int {
        return mediaPlayer?.duration ?: 0
    }

    fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }

    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
    }

    fun setSeekBarUpdateListener(callback: (Int) -> Unit) {
        updateCallback = callback
    }

    private fun startSeekBarUpdates() {
        handler.post(object : Runnable {
            override fun run() {
                if (mediaPlayer?.isPlaying == true) {
                    val progress = (mediaPlayer!!.currentPosition * 100) / mediaPlayer!!.duration
                    updateCallback?.invoke(progress)
                    handler.postDelayed(this, 1000)
                }
            }
        })
    }

    private fun stopSeekBarUpdates() {
        handler.removeCallbacksAndMessages(null)
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
        mediaPlayer = null
        stopSeekBarUpdates()
    }
}
