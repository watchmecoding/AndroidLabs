package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.view.Gravity
import android.widget.LinearLayout
import androidx.media3.exoplayer.ExoPlayer
import android.widget.SeekBar
import androidx.media3.common.MediaItem

class AudioOnlinePlayerActivity : AppCompatActivity() {

    private lateinit var exoPlayer: ExoPlayer
    private lateinit var seekBar: SeekBar
    private lateinit var playButton: Button
    private lateinit var pauseButton: Button
    private lateinit var stopButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
        }

        seekBar = SeekBar(this).apply {
            max = 100
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        exoPlayer.seekTo((progress * exoPlayer.duration / 100).toLong())
                    }
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }

        playButton = Button(this).apply { text = "Play" }
        pauseButton = Button(this).apply { text = "Pause" }
        stopButton = Button(this).apply { text = "Stop" }

        layout.addView(seekBar, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layout.addView(playButton)
        layout.addView(pauseButton)
        layout.addView(stopButton)

        setContentView(layout)

        val mediaUri = intent.getStringExtra("mediaUri")
        val mediaItem = MediaItem.fromUri(mediaUri!!)

        exoPlayer = ExoPlayer.Builder(this).build()
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()

        playButton.setOnClickListener { exoPlayer.play() }
        pauseButton.setOnClickListener { exoPlayer.pause() }
        stopButton.setOnClickListener {
            exoPlayer.stop()
            exoPlayer.seekTo(0)
        }

        // Оновлюємо SeekBar
        Thread {
            while (exoPlayer.isPlaying) {
                runOnUiThread {
                    seekBar.progress = (exoPlayer.currentPosition * 100 / exoPlayer.duration).toInt()
                }
                Thread.sleep(1000)
            }
        }.start()
    }

    override fun onDestroy() {
        exoPlayer.release()
        super.onDestroy()
    }
}

