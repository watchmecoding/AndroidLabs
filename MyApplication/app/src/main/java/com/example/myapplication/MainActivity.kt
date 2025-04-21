package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var playAudioButton: Button
    private lateinit var playVideoButton: Button
    private lateinit var playAudioOnlineButton: Button
    private lateinit var playVideoOnlineButton: Button

    // Реєстрація вибору аудіофайлів
    private val audioPicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val intent = Intent(this, AudioPlayerActivity::class.java)
            intent.putExtra("mediaUri", it.toString())
            startActivity(intent)
        }
    }

    // Реєстрація вибору відеофайлів
    private val videoPicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val intent = Intent(this, VideoPlayerActivity::class.java)
            intent.putExtra("mediaUri", it.toString())
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playAudioButton = findViewById(R.id.playAudioButton)
        playVideoButton = findViewById(R.id.playVideoButton)
        playAudioOnlineButton = findViewById(R.id.playAudioOnlineButton)
        playVideoOnlineButton = findViewById(R.id.playVideoOnlineButton)

        playAudioButton.setOnClickListener {
            audioPicker.launch("audio/*")
        }

        playVideoButton.setOnClickListener {
            videoPicker.launch("video/*")
        }

        playAudioOnlineButton.setOnClickListener {
            showMediaDialog("audio")
        }

        playVideoOnlineButton.setOnClickListener {
            showMediaDialog("video")
        }

        // Приховані кнопки для онлайн-програвання (на всяк випадок, хоча вони вже в XML з visibility="gone")
        //playAudioOnlineButton.visibility = android.view.View.GONE
        //playVideoOnlineButton.visibility = android.view.View.GONE
    }

    private fun showMediaDialog(mediaType: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_input_url, null)
        val input = dialogView.findViewById<EditText>(R.id.urlEditText)

        val builder = AlertDialog.Builder(this)
            .setTitle("Enter URL")
            .setView(dialogView)
            .setPositiveButton("Play") { _, _ ->
                val url = input.text.toString()
                if (mediaType == "audio") {
                    playAudioOnline(url)
                } else {
                    playVideoOnline(url)
                }
            }
            .setNegativeButton("Cancel", null)

        builder.show()
    }

    private fun playAudioOnline(url: String) {
        val intent = Intent(this, AudioOnlinePlayerActivity::class.java)
        intent.putExtra("mediaUri", url)
        startActivity(intent)
    }

    private fun playVideoOnline(url: String) {
        val intent = Intent(this, VideoOnlinePlayerActivity::class.java)
        intent.putExtra("mediaUri", url)
        startActivity(intent)
    }
}
