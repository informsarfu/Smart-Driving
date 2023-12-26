package com.example.app_dev_for_life;

import androidx.appcompat.app.AppCompatActivity;

//import android.annotation.SuppressLint;
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteException;
//import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.Spinner;
//import android.widget.Toast;

import android.widget.ArrayAdapter;
import android.widget.Button;

import java.util.HashMap;


public class Second_page extends AppCompatActivity {

    HashMap<String, Float> my_map = new HashMap<String, Float>();

    DBClassHelper dataTrack = MainActivity.database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_page);

        String[] symptoms = {
                "Nausea", "Headache", "Diarrhea", "Soar Throat", "Fever",
                "Muscle Ache", "Loss Of Smell Or Taste", "Cough", "Shortness Of Breath", "Feeling Tired"
        };

        Spinner ddown;
        ddown = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, symptoms);
        ddown.setAdapter(adapter);


        Log.i("project4_DB", "Calling init get data");
        my_map = dataTrack.getSavedData();

        //star-rating widget
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener()
        {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float val, boolean t)
            {
                int index = ddown.getSelectedItemPosition();

                Log.i("project4_DB", String.format(" The Index is : %d and Rating is %f", index, val));

                my_map.put(symptoms[index], val);
            }
        });

        //Interface the database
        Button id_button6 = (Button) findViewById(R.id.button6);
        id_button6.setOnClickListener(new View.OnClickListener()
        {

            public void onClick(View view) {

                float nausea = my_map.containsKey("Nausea") ? (my_map.get("Nausea")) : 0.0F;
                float headache = my_map.containsKey("Headache") ? (my_map.get("Headache")) : 0.0F;
                float diarrhea = my_map.containsKey("Diarrhea") ? (my_map.get("Diarrhea")) : 0.0F;
                float soarThroat = my_map.containsKey("Soar Throat") ? (my_map.get("Soar Throat")) : 0.0F;
                float fever = my_map.containsKey("Fever") ? (my_map.get("Fever")) : 0.0F;
                float muscleAche = my_map.containsKey("Muscle Ache") ? (my_map.get("Muscle Ache")) : 0.0F;
                float lossOfSmellTaste = my_map.containsKey("Loss Of Smell Or Taste") ? (my_map.get("Loss Of Smell Or Taste")) : 0.0F;
                float cough = my_map.containsKey("Cough") ? (my_map.get("Cough")) : 0.0F;
                float shortnessOfBreath = my_map.containsKey("Shortness Of Breath") ? (my_map.get("Shortness Of Breath")) : 0.0F;
                float feelingTired = my_map.containsKey("Feeling Tired") ? (my_map.get("Feeling Tired")) : 0.0F;

                Log.i("project4_DB", "OnClick Upload Button!!");
                
                // Call the original insertSymptomData function with individual column values
                dataTrack.insertSymptomData(nausea, headache, diarrhea, soarThroat, fever, muscleAche, lossOfSmellTaste,
                        cough, shortnessOfBreath, feelingTired);

                dataTrack.getSavedData();
//                Toast.makeText(MainActivity2.this,"New Entry Inserted",Toast.LENGTH_SHORT).show();
            }


//            @Override
//            public void onClick(View view)
//            {
//                SQLiteDatabase db = MainActivity.sqldb;
//
//
//
//                db.beginTransaction();
//                try {
//                    Toast.makeText(getApplicationContext(),
//                            "Inserting into the Table",
//                            Toast.LENGTH_SHORT).show();
//                    db.execSQL( "update symptoms set nausea='"+my_map.get(symptoms[0])+"', head_ache='"+my_map.get(symptoms[1])+"', diarrhea='"+my_map.get(symptoms[2])+"', sore_throat='"+my_map.get(symptoms[3])+"', fever='"+my_map.get(symptoms[4])+"', muscle_ache='"+my_map.get(symptoms[5])+"', lossofsmellortaste='"+my_map.get(symptoms[6])+"', cough='"+my_map.get(symptoms[7])+"', shortnessofbreath='"+my_map.get(symptoms[8])+"', tiredness='"+my_map.get(symptoms[9])+"' where id = (SELECT MAX(id) FROM symptoms)" );
//                    db.execSQL("select * from symptoms");
//                    db.setTransactionSuccessful();
//                } catch(SQLiteException e) {
//                    Toast.makeText(getApplicationContext(),
//                            "Caught Exception : " + e.getMessage(),
//                            Toast.LENGTH_SHORT).show();
//                    Log.i("project4_DB", "EXCEPTION CAUGHT WHILE UPDATING");
//                } finally {
////                    Toast.makeText(getApplicationContext(),
////                            "finished inserting into the Table",
////                            Toast.LENGTH_SHORT).show();
//                    db.endTransaction();
//                }
////                Toast.makeText(getApplicationContext(),
////                        "Added into the Table",
////                        Toast.LENGTH_SHORT).show();
//
//                String[] projection = {
//                        "id",
//                        "nausea",
//                        "head_ache", "diarrhea", "sore_throat", "fever",
//                        "muscle_ache", "lossofsmellortaste", "cough", "shortnessofbreath", "tiredness"
//                };
//                // Filter results WHERE "id" = 'MAX(id)'
//                String selection = "id" + " = ?";
//                String[] selectionArgs = { "MAX(id)" };
//                Cursor cursor = db.query(
//                        "symptoms",   // The table to query
//                        projection,             // The array of columns to return (pass null to get all)
//                        selection,              // The columns for the WHERE clause
//                        selectionArgs,          // The values for the WHERE clause
//                        null,                   // don't group the rows
//                        null,                   // don't filter by row groups
//                        null               // The sort order
//                );
//
//                try {
//                    // Check if the cursor is not null and move to the first row (if available)
//                    if (cursor != null && cursor.moveToFirst()) {
//                        do {
//                            // Retrieve data from the cursor for each column
//                            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
//                            Float column1 = cursor.getFloat(cursor.getColumnIndex("nausea"));
//                            Float column2 = cursor.getFloat(cursor.getColumnIndex("head_ache"));
//                            Float column3 = cursor.getFloat(cursor.getColumnIndex("diarrhea"));
//                            Float column4 = cursor.getFloat(cursor.getColumnIndex("sore_throat"));
//                            Float column5 = cursor.getFloat(cursor.getColumnIndex("fever"));
//                            Float column6 = cursor.getFloat(cursor.getColumnIndex("muscle_ache"));
//                            Float column7 = cursor.getFloat(cursor.getColumnIndex("lossofsmellortaste"));
//                            Float column8 = cursor.getFloat(cursor.getColumnIndex("cough"));
//                            Float column9 = cursor.getFloat(cursor.getColumnIndex("shortnessofbreath"));
//                            Float column10 = cursor.getFloat(cursor.getColumnIndex("tiredness"));
//                            // Retrieve data from your other columns as needed
//
//                            // Print or process the retrieved data
//                            Log.i("project4_DB", "ID: " + id);
//                            Log.i("project4_DB","Column1: " + column1);
//                            Log.i("project4_DB","Column2: " + column2);
//                            Log.i("project4_DB","Column3: " + column3);
//                            Log.i("project4_DB","Column4: " + column4);
//                            Log.i("project4_DB","Column5: " + column5);
//                            Log.i("project4_DB","Column6: " + column6);
//                            Log.i("project4_DB","Column7: " + column7);
//                            Log.i("project4_DB","Column8: " + column8);
//                            Log.i("project4_DB","Column9: " + column9);
//                            Log.i("project4_DB","Column10: " + column10);
//
//
//                        } while (cursor.moveToNext()); // Move to the next row, if available
//                    } else {
//                    }
//                } finally {
//                    // Always close the cursor when you're done with it
//                    if (cursor != null) {
//                        cursor.close();
//                    }
//                }
//
//
//            }
        });
    }
}
