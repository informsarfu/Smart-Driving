package com.guardianangel.myapplication

import android.media.AudioAttributes
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.OkHttpClient
import okhttp3.Request
import java.lang.Exception
import org.json.JSONObject
import android.media.MediaPlayer
import android.widget.Toast
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var editText: EditText
    private lateinit var searchButton: Button
    private lateinit var displayTextView: TextView
    private val apiKey = "AIzaSyDRopxcwMb3OG_380CmBAI7Ak4RUnr2YFs"
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editText = findViewById(R.id.editText)
        searchButton = findViewById(R.id.searchArtist)
        displayTextView = findViewById(R.id.displayTextView)
        mediaPlayer = MediaPlayer()
        mediaPlayer.setAudioAttributes(AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())

        val btnPlay: Button = findViewById(R.id.play)
        btnPlay.setOnClickListener {
            playMusic()
        }

        // Set up the Pause button
        val btnPause: Button = findViewById(R.id.pause)
        btnPause.setOnClickListener {
            pauseMusic()
        }

        searchButton.setOnClickListener {
            val artistName = editText.text.toString()
            if (artistName.isNotEmpty()) {
                // Make API call to get top 10 songs
                FetchTopSongsTask().execute(artistName)
            }
        }
    }

    private inner class FetchTopSongsTask : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String): String {
            val artistName = params[0]
            val url =
                "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=10&q=$artistName&type=video&key=$apiKey"

            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()

            try {
                val response = client.newCall(request).execute()
                return response.body?.string() ?: ""
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return ""
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            displayTopSongs(result)
            playTopResultAudio(result)
        }
    }

    private fun displayTopSongs(result: String) {
        val jsonObject = JSONObject(result)
        val items = jsonObject.getJSONArray("items")

        // Process the items array to get the information you need
        // For example, you can extract video titles and display them in the TextView
        val stringBuilder = StringBuilder()
        for (i in 0 until 5) {
            val videoTitle = items.getJSONObject(i).getJSONObject("snippet").getString("title")
            stringBuilder.append("$videoTitle\n")
        }
        displayTextView.text = stringBuilder.toString()
    }

    private fun playTopResultAudio(result: String) {
        val jsonObject = JSONObject(result)
        val items = jsonObject.getJSONArray("items")

        if (items.length() > 0) {
            val videoId = items.getJSONObject(0).getJSONObject("id").getString("videoId")
            val audioUrl = "https://www.youtube.com/watch?v=$videoId"

            try {
                mediaPlayer.reset()
                mediaPlayer.setDataSource(audioUrl)
                mediaPlayer.prepareAsync()

                // Set up a listener to start playing once prepared
                mediaPlayer.setOnPreparedListener {
                    mediaPlayer.start()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this@MainActivity, "Error playing audio", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this@MainActivity, "No results found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun playMusic() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
    }

    // Function to pause the music
    private fun pauseMusic() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release the MediaPlayer resources when the activity is destroyed
        mediaPlayer.release()
    }
}