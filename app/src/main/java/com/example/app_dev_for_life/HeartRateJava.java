package com.example.app_dev_for_life;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HeartRateJava extends Service {

    int finalRate;

    Uri myRecording = MainActivity.recordingUri;

    public String convertMediaUriToPath(Uri uri) {
        if (uri == null) {
            return "/";
        }
        String[] proj = {MediaStore.Images.Media.DATA};
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(uri, proj, null, null, null);

        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        } else {
            return "/";
        }

    }

    @Override
    public void onCreate(){

//        Toast.makeText(getApplicationContext(), "Heart Rate Service object created...",
//                Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        System.gc();
//        Toast.makeText(this, "Processing video...", Toast.LENGTH_LONG).show();
        Log.i("log", "Heart Rate service started");


        MyAsyncTasks runnable = new MyAsyncTasks();
        Thread thread = new Thread(runnable);
        thread.start();

        return START_STICKY;
    }

    public class MyAsyncTasks implements Runnable {

        private MediaMetadataRetriever retriever;
        private List<Bitmap> frameList = new ArrayList<>();

        int scalingFactor = 6;

        @Override
        public void run() {
            Bitmap bitmap;
            long redBucket;
            long pixelCount;
            List<Long> a = new ArrayList<>();
            List<Long> b = new ArrayList<>();

            Log.i("log", "Heart Rate Runnable started");

            try {
                retriever = new MediaMetadataRetriever();
                File moviesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
                File path = new File(moviesDir, "HR_recording1.mp4");

                Log.i("project4_HR", "File Exists : "+ path.exists());
                Log.i("HEART_RATE", convertMediaUriToPath(myRecording));
                Log.i("project4_HR", "File Exists : "+ String.valueOf(path));
                retriever.setDataSource(String.valueOf(path));

                int duration = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT));

                Log.i("project4_HR", "Duration : " + String.valueOf(duration));
//                Toast.makeText(getApplicationContext(), "Retrieving the frames from the video...", Toast.LENGTH_LONG).show();

                for (int i = 10; i < duration; i+=25) {
                    Log.i("HEART_RATE", String.format("At Seconds Duration : %d out of %d", i, duration));

                    if ((bitmap = retriever.getFrameAtIndex(i)) != null) {
                        frameList.add(bitmap);
                    } else {
                        Log.i("HEART_RATE", "Encountered a NULL frame!!!");
                    }
                }
            } catch (Exception e) {
//                Toast.makeText(getApplicationContext(), "Exception while capturing the frames from the video...", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            } finally {
                if (retriever != null) {
                    try {
                        retriever.release();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            for (Bitmap frame : frameList) {
                redBucket = 0;
                pixelCount = 0;

                for (int y = 300; y < 650; y++) {
                    for (int x = 300; x < 650; x++) {
                        int c = frame.getPixel(x, y);
                        pixelCount++;
//                        redBucket += Color.red(c) + Color.blue(c) + Color.green(c);
                        redBucket += Color.red(c);
                    }
                }
                Log.i("HEART_RATE", String.format("RedBucket %d", redBucket));
                a.add(redBucket);
            }

            for (int i = 0; i < a.size() - 5; i+=5) {
                b.add((a.get(i) + a.get(i + 1) + a.get(i + 2) + a.get(i + 3) + a.get(i + 4)) / 5);
            }

            long x = b.get(0);
            int count = 0;

            for (int i = 1; i < b.size(); i++) {
                long p = b.get(i);
                Log.i("HEART_RATE", String.format("p - x %d", (p-x)));
                if (Math.abs(p - x) > 500) {
                    count++;
                }
                x = b.get(i);
            }
            Log.i("HEART_RATE", String.format("Count %d", count));
            int rate = (count * 60) / 45;
            finalRate = rate*scalingFactor;
            Log.i("HEART_RATE", String.format("finalRate %d", finalRate));
            stopSelf();
        }

    }

    @Override
    public void onDestroy(){

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                //Send the Accelerometer XValues via Broadcasts
                Intent intent = new Intent("HeartrateBcast");

                Bundle bundle = new Bundle();
                bundle.putInt("heartRateValue", finalRate);
                intent.putExtras(bundle);

                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }
        });
        thread.start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

