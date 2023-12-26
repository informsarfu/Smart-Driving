package com.example.app_dev_for_life;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.video.MediaStoreOutputOptions;
import androidx.camera.video.Quality;
import androidx.camera.video.QualitySelector;
import androidx.camera.video.Recorder;
import androidx.camera.video.Recording;
import androidx.camera.video.VideoCapture;
import androidx.camera.video.VideoRecordEvent;
import androidx.camera.view.PreviewView;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.hardware.camera2.CameraAccessException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android.Manifest;

import java.io.File;

import android.hardware.camera2.CameraManager;

import java.util.concurrent.ExecutionException;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.HashMap;


class RespiratoryRateDetector implements Runnable {
    public int respRate = 0;

    RespiratoryRateDetector(int accl) {
        this.respRate = accl;
    }

    @Override
    public void run() {
        Log.i("DETECTOR", "Respiratory rate" + respRate);
    }

}

class HeartRateDetector implements Runnable {
    public int heartRate = 0;

    HeartRateDetector(int rate) {
        this.heartRate = rate;
    }

    @Override
    public void run() {
        Log.i("DETECTOR", "Heart rate" + heartRate);
    }

}

class DBClassHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "symptoms";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAUSEA = "nausea";
    public static final String COLUMN_HEADACHE = "head_ache";
    public static final String COLUMN_DIARRHEA = "diarrhea";
    public static final String COLUMN_SOAR_THROAT = "sore_throat";
    public static final String COLUMN_FEVER = "fever";
    public static final String COLUMN_MUSCLE_ACHE = "muscle_ache";
    public static final String COLUMN_LOSS_OF_SMELL_TASTE = "lossofsmelltaste";
    public static final String COLUMN_COUGH = "cough";
    public static final String COLUMN_SHORTNESS_OF_BREATH = "shortnessofbreath";
    public static final String COLUMN_FEELING_TIRED = "tiredness";
    public static final String COLUMN_HEART_RATE = "heart_rate";
    public static final String COLUMN_RESPIRATORY_RATE = "respiratory_rate";

    String[] symptoms = {
            "Nausea", "Headache", "Diarrhea", "Soar Throat", "Fever",
            "Muscle Ache", "Loss Of Smell Or Taste", "Cough", "Shortness Of Breath", "Feeling Tired"
    };

    HashMap<String, Float> my_tempmap = new HashMap<String, Float>();

    Float column1;
    Float column2;
    Float column3;
    Float column4;
    Float column5;
    Float column6;
    Float column7;
    Float column8;
    Float column9;
    Float column10;
    Float column11;
    Float column12;

    // Define the SQL statement to create the table
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAUSEA + " REAL CHECK(" + COLUMN_NAUSEA + " >= 0 AND " + COLUMN_NAUSEA + " <= 5), " +
                    COLUMN_HEADACHE + " REAL CHECK(" + COLUMN_HEADACHE + " >= 0 AND " + COLUMN_HEADACHE + " <= 5), " +
                    COLUMN_DIARRHEA + " REAL CHECK(" + COLUMN_DIARRHEA + " >= 0 AND " + COLUMN_DIARRHEA + " <= 5), " +
                    COLUMN_SOAR_THROAT + " REAL CHECK(" + COLUMN_SOAR_THROAT + " >= 0 AND " + COLUMN_SOAR_THROAT + " <= 5), " +
                    COLUMN_FEVER + " REAL CHECK(" + COLUMN_FEVER + " >= 0 AND " + COLUMN_FEVER + " <= 5), " +
                    COLUMN_MUSCLE_ACHE + " REAL CHECK(" + COLUMN_MUSCLE_ACHE + " >= 0 AND " + COLUMN_MUSCLE_ACHE + " <= 5), " +
                    COLUMN_LOSS_OF_SMELL_TASTE + " REAL CHECK(" + COLUMN_LOSS_OF_SMELL_TASTE + " >= 0 AND " + COLUMN_LOSS_OF_SMELL_TASTE + " <= 5), " +
                    COLUMN_COUGH + " REAL CHECK(" + COLUMN_COUGH + " >= 0 AND " + COLUMN_COUGH + " <= 5), " +
                    COLUMN_SHORTNESS_OF_BREATH + " REAL CHECK(" + COLUMN_SHORTNESS_OF_BREATH + " >= 0 AND " + COLUMN_SHORTNESS_OF_BREATH + " <= 5), " +
                    COLUMN_FEELING_TIRED + " REAL CHECK(" + COLUMN_FEELING_TIRED + " >= 0 AND " + COLUMN_FEELING_TIRED + " <= 5), " +
                    COLUMN_HEART_RATE + " REAL, " +
                    COLUMN_RESPIRATORY_RATE + " REAL)";

    public DBClassHelper(Context context) {
        super(context, "Symptomrecorder.db", null, 1);
        Log.i("project4_DB", "Constructor");
        for (String symptom : symptoms) {
            my_tempmap.put(symptom, 0.0F);
        }
        my_tempmap.put(COLUMN_RESPIRATORY_RATE, 0.0F);
        my_tempmap.put(COLUMN_HEART_RATE,0.0F);
        column1=0.0F;
        column2=0.0F;
        column3=0.0F;
        column4=0.0F;
        column5=0.0F;
        column6=0.0F;
        column7=0.0F;
        column8=0.0F;
        column9=0.0F;
        column10=0.0F;
        column11=0.0F;
        column12=0.0F;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        Log.i("project4_DB","creating table");
        database.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int i, int i1) {
        database.execSQL("drop Table if exists symptoms");
        onCreate(database);
    }

    public boolean insertSymptomData(float nausea, float headache, float diarrhea, float soarThroat, float fever,
                                     float muscleAche, float lossOfSmellTaste, float cough,
                                     float shortnessOfBreath, float feelingTired) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        Log.i("project4_DB", "Inserting Symptom Data!!");

        values.put(COLUMN_NAUSEA, nausea);
        values.put(COLUMN_HEADACHE, headache);
        values.put(COLUMN_DIARRHEA, diarrhea);
        values.put(COLUMN_SOAR_THROAT, soarThroat);
        values.put(COLUMN_FEVER, fever);
        values.put(COLUMN_MUSCLE_ACHE, muscleAche);
        values.put(COLUMN_LOSS_OF_SMELL_TASTE, lossOfSmellTaste);
        values.put(COLUMN_COUGH, cough);
        values.put(COLUMN_SHORTNESS_OF_BREATH, shortnessOfBreath);
        values.put(COLUMN_FEELING_TIRED, feelingTired);
        values.put(COLUMN_HEART_RATE, column11);
        values.put(COLUMN_RESPIRATORY_RATE, column12);

        // Insert the data into the table and return the row ID
        long newRowId = db.insert(TABLE_NAME, null, values);

        db.close();

        return newRowId > 0;

    }

    public boolean insertSignData(float HeartRate, float RespRate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        Log.i("project4_DB", "Inserting Sign Data!!");

        values.put(COLUMN_HEART_RATE, HeartRate);
        values.put(COLUMN_RESPIRATORY_RATE, RespRate);
        values.put(COLUMN_NAUSEA, column1);
        values.put(COLUMN_HEADACHE, column2);
        values.put(COLUMN_DIARRHEA, column3);
        values.put(COLUMN_SOAR_THROAT, column4);
        values.put(COLUMN_FEVER, column5);
        values.put(COLUMN_MUSCLE_ACHE, column6);
        values.put(COLUMN_LOSS_OF_SMELL_TASTE, column7);
        values.put(COLUMN_COUGH, column8);
        values.put(COLUMN_SHORTNESS_OF_BREATH, column9);
        values.put(COLUMN_FEELING_TIRED, column10);

        // Insert the data into the table and return the row ID
        long newRowId = db.insert(TABLE_NAME, null, values);

        db.close();

        return newRowId > 0;

    }

    public HashMap<String, Float> getSavedData() {

        SQLiteDatabase database = this.getReadableDatabase();

//        String[] projection = {
//                COLUMN_ID,
//                COLUMN_NAUSEA,
//                COLUMN_HEADACHE,
//                COLUMN_DIARRHEA,
//                COLUMN_SOAR_THROAT,
//                COLUMN_FEVER,
//                COLUMN_MUSCLE_ACHE,
//                COLUMN_LOSS_OF_SMELL_TASTE,
//                COLUMN_COUGH,
//                COLUMN_SHORTNESS_OF_BREATH,
//                COLUMN_FEELING_TIRED,
//                COLUMN_HEART_RATE,
//                COLUMN_RESPIRATORY_RATE
//        };

        // Filter results WHERE "id" = 'MAX(id)'
//        String selection = "id" + " = ?";
//        String[] selectionArgs = {" SELECT MAX(id)"};
        Cursor cursor = database.query(
                "symptoms",   // The table to query
                null,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        try {
            Log.i("project4_DB", "TRY BLOCK INSIDE");
            // Check if the cursor is not null and move to the first row (if available)
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    Log.i("project4_DB", "Inside IF");
                    // Retrieve data from the cursor for each column
                    Log.i("project4_DB", "INSIDE DO");
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                    column1 = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_NAUSEA));
                    column2 = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_HEADACHE));
                    column3 = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_DIARRHEA));
                    column4 = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_SOAR_THROAT));
                    column5 = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_FEVER));
                    column6 = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_MUSCLE_ACHE));
                    column7 = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_LOSS_OF_SMELL_TASTE));
                    column8 = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_COUGH));
                    column9 = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_SHORTNESS_OF_BREATH));
                    column10 = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_FEELING_TIRED));
                    column11 = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_HEART_RATE));
                    column12 = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_RESPIRATORY_RATE));
                    // Retrieve data from your other columns as needed

                    // Print or process the retrieved data
                    Log.i("project4_DB", "ID : " + id);
                    Log.i("project4_DB", COLUMN_NAUSEA + " : " + column1);
                    Log.i("project4_DB", COLUMN_HEADACHE + " : " + column2);
                    Log.i("project4_DB", COLUMN_DIARRHEA + " : " + column3);
                    Log.i("project4_DB", COLUMN_SOAR_THROAT + " : " + column4);
                    Log.i("project4_DB", COLUMN_FEVER + " : " + column5);
                    Log.i("project4_DB", COLUMN_MUSCLE_ACHE + " : " + column6);
                    Log.i("project4_DB", COLUMN_LOSS_OF_SMELL_TASTE + " : " + column7);
                    Log.i("project4_DB", COLUMN_COUGH + " : " + column8);
                    Log.i("project4_DB", COLUMN_SHORTNESS_OF_BREATH + " : " + column9);
                    Log.i("project4_DB",  COLUMN_FEELING_TIRED + " : " + column10);
                    Log.i("project4_DB",  COLUMN_HEART_RATE + " : " + column11);
                    Log.i("project4_DB",  COLUMN_RESPIRATORY_RATE + " : " + column12);

                    my_tempmap.put(symptoms[0], column1);
                    my_tempmap.put(symptoms[1], column2);
                    my_tempmap.put(symptoms[2], column3);
                    my_tempmap.put(symptoms[3], column4);
                    my_tempmap.put(symptoms[4], column5);
                    my_tempmap.put(symptoms[5], column6);
                    my_tempmap.put(symptoms[6], column7);
                    my_tempmap.put(symptoms[7], column8);
                    my_tempmap.put(symptoms[8], column9);
                    my_tempmap.put(symptoms[9], column10);
                }
            } else {
            }
        } finally {
            Log.i("project4_DB", "FInALLy BLOCK");
            // Always close the cursor when you're done with it
            if (cursor != null) {
                cursor.close();
            }
        }

        return my_tempmap;
    }
}



public class MainActivity extends AppCompatActivity {

    boolean heart_check_already_running = false;
    boolean resp_rate_check_already_running = false;

    boolean is_recording_in_progress = false;

//    static SQLiteDatabase sqldb;

    static  DBClassHelper database;

    static Uri recordingUri;
    CameraManager cameraManager;

    Camera cameraHandle;

    Recording recording = null;
    PreviewView previewView;
    VideoCapture<Recorder> videoCapture = null;
    int cameraFacing = CameraSelector.LENS_FACING_BACK;

    HeartRateDetector detector;
    RespiratoryRateDetector respDetector;


    private void getVideoRecordingPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
//            Toast.makeText(MainActivity.this, "Requested camera permissions...",
//                    Toast.LENGTH_SHORT).show();
        }
    }

    private void getAudioRecordingPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 101);
//            Toast.makeText(MainActivity.this, "Requested camera permissions...",
//                    Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isCameraPresent() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }


    class CaptureVideo implements Runnable {


        @Override
        public void run() {

//            Toast.makeText(this, "Called capture video", Toast.LENGTH_SHORT).show();

            try {
                Thread.sleep(5000);
            }
            catch(Exception e) {
                System.out.println(e);
            }

            Log.i("START_CAMERA", "START CAPTURE VIDEO API BEGIN ");

            ContentValues contentValues = new ContentValues();

            String name = new String("HR_Recording1.mp4");
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");
            contentValues.put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/");


            MediaStoreOutputOptions options = new MediaStoreOutputOptions.Builder(getContentResolver(), MediaStore.Video.Media.EXTERNAL_CONTENT_URI).setContentValues(contentValues).build();

            Log.i("START_CAMERA", "RECORDIING API BEGIN ");

            recording = videoCapture.getOutput().prepareRecording(MainActivity.this, options).start(ContextCompat.getMainExecutor(MainActivity.this), videoRecordEvent -> {
                if (videoRecordEvent instanceof VideoRecordEvent.Start) {
//                    Toast.makeText(this, "Video Capture In progress", Toast.LENGTH_SHORT).show();
                } else if (videoRecordEvent instanceof VideoRecordEvent.Finalize) {
                    if (((VideoRecordEvent.Finalize) videoRecordEvent).getError() == VideoRecordEvent.Finalize.ERROR_DURATION_LIMIT_REACHED) {
//                        String msg = "Video capture succeeded: " + ((VideoRecordEvent.Finalize) videoRecordEvent).getOutputResults().getOutputUri();
                        recordingUri = ((VideoRecordEvent.Finalize) videoRecordEvent).getOutputResults().getOutputUri();

//                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    } else {
                        recording.close();
                        recording = null;
//                        String msg = "Error: " + ((VideoRecordEvent.Finalize) videoRecordEvent).getError();
//                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                } else {
//                    Toast.makeText(this, "Failure Encountered!!!", Toast.LENGTH_SHORT).show();
                }
            });

        }

    }

    public void captureVideo() {

        Toast.makeText(this, "Started Recording!!", Toast.LENGTH_SHORT).show();

        is_recording_in_progress = true;

        toggleFlash(cameraHandle);

        Recording record = recording;
        if (record != null) {
            record.stop();
            recording = null;
        }

        Log.i("START_CAMERA", "START CAPTURE VIDEO API BEGIN ");

        ContentValues contentValues = new ContentValues();

        String name = new String("HR_Recording1.mp4");
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");
        contentValues.put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/");


        MediaStoreOutputOptions options = new MediaStoreOutputOptions.Builder(getContentResolver(), MediaStore.Video.Media.EXTERNAL_CONTENT_URI).setContentValues(contentValues).setDurationLimitMillis(45000).build();

        Log.i("START_CAMERA", "RECORDING API BEGIN ");

        recording = videoCapture.getOutput().prepareRecording(MainActivity.this, options).start(ContextCompat.getMainExecutor(MainActivity.this), videoRecordEvent -> {
            if (videoRecordEvent instanceof VideoRecordEvent.Start) {
//                Toast.makeText(this, "Video Capture In progress", Toast.LENGTH_SHORT).show();
                Log.i("VIDEO_RECORDING", "VIDEO CAPTURE IN PROGRESS!!!");
            } else if (videoRecordEvent instanceof VideoRecordEvent.Finalize) {
                if (((VideoRecordEvent.Finalize) videoRecordEvent).getError() == VideoRecordEvent.Finalize.ERROR_DURATION_LIMIT_REACHED) {

                    recordingUri = ((VideoRecordEvent.Finalize) videoRecordEvent).getOutputResults().getOutputUri();
                    String msg = "Video capture succeeded: " + String.valueOf(recordingUri);
                    Toast.makeText(this, "Video Recording Finished!! Press Measure Heart Rate Button", Toast.LENGTH_SHORT).show();
                    Log.i("VIDEO_RECORDING", msg);
                    toggleFlash(cameraHandle);
                    is_recording_in_progress = false;
//                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                } else {
                    recording.close();
                    recording = null;
                    String msg = "Error: " + ((VideoRecordEvent.Finalize) videoRecordEvent).getError();
                    Log.i("VIDEO_RECORDING", msg);
                    toggleFlash(cameraHandle);
                    is_recording_in_progress = false;
//                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            } else {
//                Toast.makeText(this, "Failure Encountered!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void startCamera(int cameraFacing) {

        ListenableFuture<ProcessCameraProvider> processCameraProvider = ProcessCameraProvider.getInstance(MainActivity.this);

        Log.i("START_CAMERA", "START CAMERA API BEGIN ");

        processCameraProvider.addListener(() -> {
            try {

                Log.i("START_CAMERA", "START CAMERA BEGIN ");

                ProcessCameraProvider cameraProvider = processCameraProvider.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                Recorder recorder = new Recorder.Builder()
                        .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
                        .build();

                Log.i("START_CAMERA", "video capture CAMERA BEGIN ");

                videoCapture = VideoCapture.withOutput(recorder);

                Log.i("START_CAMERA", "end CAMERA BEGIN ");

                if(videoCapture == null) {
                    Log.i("START_CAMERA", "VIDEO CAPTURE FIELD IS NULL ");
                } else {
                    Log.i("START_CAMERA", "VIDEO CAPTURE FIELD IS NOT NULL ");
                }

                cameraProvider.unbindAll();

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(cameraFacing).build();

                cameraHandle = cameraProvider.bindToLifecycle(this, cameraSelector, preview, videoCapture);

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(MainActivity.this));
        Log.i("START_CAMERA", "START CAMERA API END ");

    }

    private void toggleFlash(Camera camera) {
        if (camera.getCameraInfo().hasFlashUnit()) {
            camera.getCameraControl().enableTorch(camera.getCameraInfo().getTorchState().getValue() == 0);
        } else {
            runOnUiThread(() -> Toast.makeText(MainActivity.this, "Flash is not available currently", Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        previewView = findViewById(R.id.viewFinder);

        TextView id_textview = (TextView) findViewById(R.id.textView2);

        cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);

        database = new DBClassHelper(this);
        database.getSavedData();


        if (isCameraPresent()) {
//            Toast.makeText(MainActivity.this, "Detected camera...",
//                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, " Failed to Detect a camera...",
                    Toast.LENGTH_SHORT).show();
        }

        //getAudioRecordingPermissions();
        getVideoRecordingPermissions();


        ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                startCamera(cameraFacing);
            }
        });

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            activityResultLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }


//        try{
//
//            File moviesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
//            File fname = new File(getExternalFilesDir(null), "symptomRegister.db");
//            String path = fname.getPath();
//
//            sqldb = SQLiteDatabase.openOrCreateDatabase(path,  null);
//
//            sqldb.beginTransaction();
//
//            try {
//                sqldb.execSQL("create table symptoms ("
//                        + "id integer PRIMARY KEY autoincrement, "
//                        + "heart_rate real, " + "respiratory_rate real, "
//                        + "nausea real, " + "head_ache real, " + "diarrhea real, "
//                        + "sore_throat real, " + "fever real, " + "muscle_ache real, "
//                        + "lossofsmellortaste real, " + "cough real,"
//                        + "shortnessofbreath real, " + "tiredness real);" );
////                Toast.makeText(  MainActivity.this, "Created a database", Toast.LENGTH_SHORT).show();
//                Log.i("project4_DB", "CREATED A DATABASE!! ");
//
//            } catch (SQLiteException e) {
////                Toast.makeText(  MainActivity.this, "Failed to create a database", Toast.LENGTH_SHORT).show();
////                Toast.makeText(  MainActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
//                Log.i("project4_DB", "Error : " + e.getMessage());
//            }
//            finally {
//                sqldb.setTransactionSuccessful();
//                sqldb.endTransaction();
//            }
//        } catch (SQLiteException sqLiteException){
////            Toast.makeText(  MainActivity.this, "Error creating a database", Toast.LENGTH_SHORT).show();
//            Log.i("project4_DB","Exception caugh while creating a DB");
//            Toast.makeText(  MainActivity.this, sqLiteException.getMessage(), Toast.LENGTH_SHORT).show();
//        }


        Button id_button = (Button) findViewById(R.id.button);
        id_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Do something when the button is clicked.
                File moviesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
                File recording1 = new File(moviesDir, "HR_recording1.mp4");

//                File recording1 = new File(convertMediaUriToPath(recordingUri));

                if(heart_check_already_running) {
                    Toast.makeText(MainActivity.this, "Service already in Progress!",
                            Toast.LENGTH_SHORT).show();
                } else if (recording1.exists()) {
                    heart_check_already_running = true;
                    Intent HRMeasuringIntent = new Intent(MainActivity.this, HeartRateJava.class);
//                    Toast.makeText(MainActivity.this, "Starting to Measure HR...",
//                            Toast.LENGTH_SHORT).show();
                    id_textview.setText("Measuring Heart Rate..... ");
                    startService(HRMeasuringIntent);
                } else {
                    Toast.makeText(getApplicationContext(), "No recording found...Record a video to measure the heart rate", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                // The activity was successful.
//                Toast.makeText(MainActivity.this, "Finished taking the video...",
//                        Toast.LENGTH_SHORT).show();
                try {
                    cameraManager.setTorchMode("0", false);
                } catch (CameraAccessException e) {
                    throw new RuntimeException(e);
                }
            } else if (result.getResultCode() == RESULT_CANCELED) {
                // The activity was canceled.
                Toast.makeText(MainActivity.this, "Failed to take the video...",
                        Toast.LENGTH_SHORT).show();
            }

            File recording2 = new File(getExternalFilesDir(null), "HR_recording1.mp4");
            if (recording2.exists()) {
                Log.i("log", "Recording created successfully");
//                Toast.makeText(MainActivity.this, "Recording created successfully...",
//                        Toast.LENGTH_SHORT).show();
            } else {
                Log.i("log", "Recording creation failure");
//                Toast.makeText(MainActivity.this, "Recording creation failure...",
//                        Toast.LENGTH_SHORT).show();
            }

        });

        Button id_button2 = (Button) findViewById(R.id.button2);
        id_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Toast.makeText(MainActivity.this, " Readying the camera...",
//                        Toast.LENGTH_SHORT).show();

                if(is_recording_in_progress) {
                    Toast.makeText(MainActivity.this, "Recording already in Progress!!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    File moviesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
                    File path = new File(moviesDir, "HR_recording1.mp4");
                    if (path.exists()) {
                        Log.i("log", "Recording already available with same name, Deleting it.");
                        if (path.delete()) {
                            Log.i("project4_FILE", "File deleted successfully");
                        }
                        else {
                            Log.i("project4_FILE", "Failed to delete the file");
                        }
                    } else {
                        Log.i("log", "Preparing to record!!");
                    }

                    id_textview.setText("Recoding the Video...Place your finger on the camera");
                    captureVideo();
                }

//                Toast.makeText(MainActivity.this, " Registering intent the camera...",
//                        Toast.LENGTH_SHORT).show();

//                startCamera(cameraFacing);
//                Runnable runnable;
//                Thread thread = new Thread(runnable);
//                thread.start();


//                CaptureVideo runnable1 = new CaptureVideo();
//                Thread thread = new Thread(runnable1);
//                thread.start();
//
//                try {
//                    thread.join();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }

            }
        });

        Button id_button3 = (Button) findViewById(R.id.button3);

        id_button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view) {
                // Checks if there is an existing respiratory rate detection process running
                if(resp_rate_check_already_running)
                {
                    Toast.makeText(MainActivity.this, " Please Wait!! Previous Measurement still in progress!!!",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "Place your phone on your abdomen for 45 seconds", Toast.LENGTH_LONG).show();
                    Intent res = new Intent(MainActivity.this, Accelerometer.class);
//                    Toast.makeText(MainActivity.this, " Starting to Measure...",
//                            Toast.LENGTH_SHORT).show();
                    id_textview.setText("Measuring Respiratory Rate..... ");
                    startService(res);
                    resp_rate_check_already_running = true;
                }
            }
        });

        Button id_button4 = (Button) findViewById(R.id.button4);

        id_button4.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent it = new Intent(MainActivity.this,Second_page.class);
                startActivity(it);
            }
        });

        //Button for creating db
        Button id_button5 = (Button) findViewById(R.id.button5);

        id_button5.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
//                Toast.makeText(getApplicationContext(), "Uploading in progress...!!", Toast.LENGTH_SHORT).show();

                float HR;
                if(detector == null) {
                    HR = 0.0F;
                }
                else {
                    HR = (float) detector.heartRate;
                }

                float RR;
                if(respDetector == null) {
                    RR = 0.0F;
                }
                else {
                    RR = (float) respDetector.respRate;
                }
                Log.i("project4_DB", String.format("HEART RATE : %f ", HR));
                Log.i("project4_DB", String.format("RESP RATE : %f" , RR));
                database.insertSignData(HR, RR);

                //Insert to the table
//                sqldb.beginTransaction();
//                try {
////                    Toast.makeText(getApplicationContext(), "Loading into the database", Toast.LENGTH_SHORT).show();
//                    int HR;
//                    if(detector == null) {
//                        HR = 0;
//                    }
//                    else {
//                        HR = detector.heartRate;
//                    }
//
//                    int RR;
//                    if(respDetector == null) {
//                        RR = 0;
//                    }
//                    else {
//                        RR = respDetector.respRate;
//                    }
//
//                    sqldb.execSQL("insert into symptoms(heart_rate, respiratory_rate) values ('" +  HR + "','" + RR + "');");
//
//                    Log.i("project4_DB", String.format("HEART RATE : %d ", HR));
//                    Log.i("project4_DB", String.format("RESP RATE : %d" , RR));
//
//                    sqldb.setTransactionSuccessful();
//
//                } catch(SQLiteException e) {
//                    //Error cases
//                    Toast.makeText(getApplicationContext(), " Exception caught : " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                } finally{
//                    //close the connection
////                    Toast.makeText(getApplicationContext(), "Finished Loading into the database", Toast.LENGTH_SHORT).show();
//                    sqldb.endTransaction();
//                }

                Toast.makeText(getApplicationContext(), "Inserted into the Database", Toast.LENGTH_SHORT).show();
            }
        });

        //Client For the Broadcast events sent by the accelerometer java class

        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                Bundle bundle = intent.getExtras();

                respDetector = new RespiratoryRateDetector(bundle.getInt("respRateValue"));

                Thread thread = new Thread(respDetector);
                thread.start();

                try {
                    thread.join();
                } catch (InterruptedException exception) {
                    Toast.makeText(MainActivity.this, "Exception occurred", Toast.LENGTH_SHORT).show();
                }

                id_textview.setText("Measured Respiratory Rate : " + respDetector.respRate + "");

                Log.i("RESP_RATE", String.format("Measured Resp Rate is %d", respDetector.respRate));

//                Toast.makeText(MainActivity.this, "Respiratory rate Measurment Done!", Toast.LENGTH_SHORT).show();

                resp_rate_check_already_running = false;

                bundle.clear();

            }
        }, new IntentFilter("AccelerometerBcast"));

        //Client For the Broadcast events sent by the Heartrate Measurement java class

        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                Bundle bundle = intent.getExtras();

                detector = new HeartRateDetector(bundle.getInt("heartRateValue"));

                Thread thread = new Thread(detector);
                thread.start();

                try {
                    thread.join();
                } catch (InterruptedException exception) {
                    Toast.makeText(MainActivity.this, "Caught an Exception ", Toast.LENGTH_SHORT).show();
                }

                if(detector.heartRate != 0)
                    id_textview.setText("Measured Heart Rate : "+ detector.heartRate + "");

                Log.i("HEART_RATE", String.format("Measured Heart Rate is %d", detector.heartRate));

//                Toast.makeText(MainActivity.this, "Heart rate Measurment Done!", Toast.LENGTH_SHORT).show();

                heart_check_already_running = false;

                bundle.clear();

            }
        }, new IntentFilter("HeartrateBcast"));


        startCamera(cameraFacing);
        
        //service = Executors.newSingleThreadExecutor();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //service.shutdown();
    }

}
