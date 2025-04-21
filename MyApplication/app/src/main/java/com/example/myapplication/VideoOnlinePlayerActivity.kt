package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.VideoView
import android.widget.MediaController
import androidx.core.net.toUri

class VideoOnlinePlayerActivity : AppCompatActivity() {

    private lateinit var videoView: VideoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_video_player)

        videoView = findViewById(R.id.video_view)

        val videoUrl = intent.getStringExtra("mediaUri")
        if (videoUrl != null) {
            val uri = videoUrl.toUri()
            videoView.setVideoURI(uri)

            val mediaController = MediaController(this)
            mediaController.setAnchorView(videoView)
            videoView.setMediaController(mediaController)

            videoView.setOnPreparedListener { mediaPlayer ->
                mediaPlayer.isLooping = false // true, щоб зациклити відео
            }

            videoView.start()
        } else {
            // Обробка, коли URL передано
        }
    }

    override fun onPause() {
        super.onPause()
        videoView.pause()
    }

    override fun onResume() {
        super.onResume()
        videoView.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        videoView.stopPlayback()
    }
}
