package com.guardianangel.myapplication;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private Button searchButton;
    private TextView displayTextView;
    private String apiKey = "AIzaSyCfcM6wj7OGrV-sbAGS96tY2ZqPwDQY0DQ";
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        searchButton = findViewById(R.id.searchArtist);
        displayTextView = findViewById(R.id.displayTextView);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build());

        Button btnPlay = findViewById(R.id.play);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playMusic();
            }
        });

        Button btnPause = findViewById(R.id.pause);
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseMusic();
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String artistName = editText.getText().toString();
                if (!artistName.isEmpty()) {
                    new FetchTopSongsTask().execute(artistName);
                }
            }
        });
    }

    private class FetchTopSongsTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String artistName = params[0];
            String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=10&q=" + artistName + "&type=video&key=" + apiKey;

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();

            try {
                Response response = client.newCall(request).execute();
                return response.body() != null ? response.body().string() : "";
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            displayTopSongs(result);
        }
    }

    private void displayTopSongs(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray items = jsonObject.getJSONArray("items");

            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < 5; i++) {
                String videoTitle = items.getJSONObject(i).getJSONObject("snippet").getString("title");
                stringBuilder.append(videoTitle).append("\n");
            }
            displayTextView.setText(stringBuilder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void playTopResultAudio(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray items = jsonObject.getJSONArray("items");

            if (items != null && items.length() > 0) {
                String videoId = items.getJSONObject(0).getJSONObject("id").getString("videoId");
                String audioUrl = "https://www.youtube.com/watch?v=" + videoId;

                try {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }

                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(audioUrl);

                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mediaPlayer.start();
                        }
                    });

                    mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer mp, int what, int extra) {
                            Toast.makeText(MainActivity.this, "Error preparing audio", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    });

                    if (!mediaPlayer.isPlaying()) {
                        mediaPlayer.prepareAsync();
                    } else {
                        Log.e("mediaMessage", "MediaPlayer is already playing");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Error playing audio", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "No results found", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void playMusic() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    private void pauseMusic() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        new FetchTopSongsTask().cancel(true);
        mediaPlayer.release();
    }
}
