package com.example.myapplication

import android.content.res.Configuration

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.example.myapplication.databinding.ActivityVideoPlayerBinding

class VideoPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoPlayerBinding

    private var currentPosition: Int = 0
    private var isPlaying: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mediaUri = intent.getStringExtra("mediaUri")?.toUri()
        binding.videoView.setVideoURI(mediaUri)

        binding.seekBar.max = 100

        binding.seekBar.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser && binding.videoView.duration > 0) {
                    binding.videoView.seekTo(progress * binding.videoView.duration / 100)
                }
            }

            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {}
        })

        binding.playButton.setOnClickListener {
            if (!binding.videoView.isPlaying) {
                binding.videoView.start()
                isPlaying = true
                updateSeekBar()
            }
        }

        binding.pauseButton.setOnClickListener {
            if (binding.videoView.isPlaying) {
                binding.videoView.pause()
                isPlaying = false
            }
        }

        binding.stopButton.setOnClickListener {
            binding.videoView.pause()
            binding.videoView.stopPlayback()
            binding.videoView.setVideoURI(mediaUri)
            isPlaying = false
            currentPosition = 0
            binding.seekBar.progress = 0
        }

        // Повернення стану
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt("currentPosition", 0)
            isPlaying = savedInstanceState.getBoolean("isPlaying", false)
            binding.videoView.seekTo(currentPosition)
            if (isPlaying) {
                binding.videoView.start()
                updateSeekBar()
            }
        }

        // Перевірка орієнтації
        adjustLayoutForOrientation(resources.configuration.orientation)
    }

    private fun updateSeekBar() {
        Thread {
            while (isPlaying) {
                runOnUiThread {
                    try {
                        // Перевіряємо, чи відео все ще відтворюється
                        if (binding.videoView.isPlaying) {
                            binding.seekBar.progress = binding.videoView.currentPosition * 100 / binding.videoView.duration
                        }
                    } catch (e: Exception) {
                        // Логуємо помилку, якщо вона виникає
                        e.printStackTrace()
                    }
                }
                Thread.sleep(100)
            }
        }.start()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        adjustLayoutForOrientation(newConfig.orientation)
    }

    private fun adjustLayoutForOrientation(orientation: Int) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Повноекранний режим - кнопки сховані
            binding.seekBar.visibility = View.GONE
            binding.controlsLayout.visibility = View.GONE
            supportActionBar?.hide()

            // Розтягнути відео на весь екран
            val layoutParams = binding.videoView.layoutParams
            layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT
            layoutParams.height = LinearLayout.LayoutParams.MATCH_PARENT
            binding.videoView.layoutParams = layoutParams

        } else {
            // Портретний режим — кнопки видно
            binding.seekBar.visibility = View.VISIBLE
            binding.controlsLayout.visibility = View.VISIBLE
            supportActionBar?.show()

            // Розміри відео для портретного режиму
            val layoutParams = binding.videoView.layoutParams
            layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT
            layoutParams.height = LinearLayout.LayoutParams.MATCH_PARENT
            binding.videoView.layoutParams = layoutParams
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("currentPosition", binding.videoView.currentPosition)
        outState.putBoolean("isPlaying", binding.videoView.isPlaying)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentPosition = savedInstanceState.getInt("currentPosition", 0)
        isPlaying = savedInstanceState.getBoolean("isPlaying", false)
        binding.videoView.seekTo(currentPosition)
        if (isPlaying) {
            binding.videoView.start()
            updateSeekBar()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.videoView.stopPlayback()
    }
}
