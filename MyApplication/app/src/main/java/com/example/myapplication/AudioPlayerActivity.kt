package com.example.myapplication

import android.net.Uri
import android.os.Bundle
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.example.myapplication.databinding.ActivityAudioPlayerBinding

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioPlayerBinding
    private val viewModel: AudioPlayerViewModel by viewModels()
    private lateinit var mediaUri: Uri
    private var isUserSeeking = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mediaUri = intent.getStringExtra("mediaUri")?.toUri() ?: return

        if (!viewModel.isPrepared) {
            viewModel.initMediaPlayer(mediaUri)
        }

        binding.seekBar.max = 100
        viewModel.setSeekBarUpdateListener { progress ->
            if (!isUserSeeking) {
                binding.seekBar.progress = progress
            }
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isUserSeeking = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                isUserSeeking = false
                viewModel.getDuration().takeIf { it > 0 }?.let { duration ->
                    val newPos = binding.seekBar.progress * duration / 100
                    viewModel.seekTo(newPos)
                }
            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}
        })

        binding.playButton.setOnClickListener {
            viewModel.play()
        }

        binding.pauseButton.setOnClickListener {
            viewModel.pause()
        }

        binding.stopButton.setOnClickListener {
            viewModel.stop(mediaUri)
        }
    }
}
