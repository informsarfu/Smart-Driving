package com.example.app_dev_for_life;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import org.json.*;

import java.util.concurrent.atomic.AtomicReference;

public class Third_page extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_page);

        TextView id_textview3 = (TextView) findViewById(R.id.textView3);
        EditText editText1 = (EditText) findViewById(R.id.editTextText);
        EditText editText2 = (EditText) findViewById(R.id.editTextText2);
        //EditText editText3 = (EditText) findViewById(R.id.editTextText3);
        //EditText editText4 = (EditText) findViewById(R.id.editTextText4);
        Button measureButton = (Button) findViewById(R.id.button8);

        measureButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                String strValue1 = editText1.getText().toString();
                String strValue2 = editText2.getText().toString();
                //String strValue3 = editText3.getText().toString();
                //String strValue4 = editText4.getText().toString();
				
				AtomicReference<String> Duration_In_Trff = new AtomicReference<>("");

                Log.i("project4_DBG", String.format(" strValue1 : %s , strValue2 : %s", strValue1, strValue2));

                Thread thread = new Thread(() -> {
                    URL url = null;
                    String API_URL = "https://maps.googleapis.com/maps/api/distancematrix/json";
                    String origin = strValue1; // latitude,longitude format
                    String destination = strValue2; // latitude,longitude format
                    String API_KEY = "";

                    String urlString = String.format("%s?origins=%s&destinations=%s&mode=driving&departure_time=now&key=%s", API_URL, origin, destination, API_KEY);
                    try {
                        url = new URL(urlString);
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }

                    HttpURLConnection connection = null;
                    try {
                        connection = (HttpURLConnection) url.openConnection();
                    } catch (IOException e) {
                        Log.i("project4_DBG", "URL Excception");
                        throw new RuntimeException(e);
                    }

                    try {
                        connection.setRequestMethod("GET");
                    } catch (ProtocolException e) {
                        Log.i("project4_DBG", "URL GET Exception");
                        throw new RuntimeException(e);
                    }

                    int responseCode = 0;
                    Log.i("project4_DBG", "Till Here!!!!");
                    try {
                        responseCode = connection.getResponseCode();
                        Log.i("project4_DBG", String.format("URL Response Code %d", responseCode));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = null;
                        try {
                            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        } catch (IOException e) {
                            Log.i("project4_DBG", "Buffer Reader Exception");
                            throw new RuntimeException(e);
                        }
                        String inputLine;
                        StringBuffer response = new StringBuffer();
                        while (true) {
                            try {
                                if (!((inputLine = in.readLine()) != null)) break;
                            } catch (IOException e) {
                                Log.i("project4_DBG", " READ Exception");
                                throw new RuntimeException(e);
                            }
                            response.append(inputLine);
                        }
                        try {
                            in.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        String jsonResponse = response.toString();

                        Log.i("project4_dbg", "JSON Output : "+jsonResponse);

                        // Do something with the JSON response
                        try {
                            // Parse the JSON response
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            JSONArray rows = jsonObject.getJSONArray("rows");
                            for (int i = 0; i < rows.length(); i++) {
                                JSONObject row = rows.getJSONObject(i);
                                JSONArray elements = row.getJSONArray("elements");
                                for (int j = 0; j < elements.length(); j++) {
                                    JSONObject element = elements.getJSONObject(j);
                                    JSONObject duration = element.getJSONObject("duration");
									  JSONObject duration_in_traffic = element.getJSONObject("duration_in_traffic");
                                    JSONObject distance = element.getJSONObject("distance");
                                    float durValue = (float) duration.getInt("value");
                                    float distValue = (float) distance.getInt("value");
									  float durTraffic = (float) duration_in_traffic.getInt("value");
                                    // Do something with the duration and disatance values
                                    Log.i("project4_DBG", String.format("duration : %f", durValue));
                                    Log.i("project4_DBG", String.format("distance : %f", distValue));
                                    Log.i("project4_DBG", String.format("duration_in_traffic : %f", durTraffic));
                                    //Duration_In_Trff = Duration_In_Trff.get().concat(String.format("Duration In Traffic : %.2f Hrs", durTraffic/3600));
                                    id_textview3.setText(String.format("Duration : %.2f Hrs \nDistance : %.2f Kms \nDuration in Traffic : %.2f Hrs \nVelocity : %.2f Kmph \nVelocity Traffic : %.2f Kmph", (durValue/3600),(distValue/1000), (durTraffic/3600), ((distValue/1000)/(durValue/3600)), ((distValue/1000)/(durTraffic/3600))));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        // Handle error
                        Log.i("project4_LOG", "Error Occurred while calling MAPS API !!!!!!");
                    }
                });

                /*Thread thread = new Thread(() -> {
//                    // Set up the HTTP connection
//                    URL url = null;
//                    try {
//                        url = new URL("https://routes.googleapis.com/directions/v2:computeRoutes");
//                    } catch (MalformedURLException e) {
//                        throw new RuntimeException(e);
//                    }
//                    HttpURLConnection conn = null;
//                    try {
//                        conn = (HttpURLConnection) url.openConnection();
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                    try {
//                        conn.setRequestMethod("POST");
//                    } catch (ProtocolException e) {
//                        throw new RuntimeException(e);
//                    }
//                    conn.setRequestProperty("Content-Type", "application/json");
//                    conn.setRequestProperty("X-Goog-Api-Key", "");
//                    conn.setRequestProperty("X-Goog-FieldMask", "routes.duration,routes.distanceMeters,routes.polyline,routes.legs.polyline,routes.travelAdvisory,routes.legs.travelAdvisory");
//                    conn.setDoOutput(true);
//
//                    // Set up the request body
//                    JSONObject requestBody = new JSONObject();
//                    JSONObject origin = new JSONObject();
//                    try {
//                        origin.put("address", "1600 Amphitheatre Parkway, Mountain View, CA");
//                    } catch (JSONException e) {
//                        throw new RuntimeException(e);
//                    }
//                    JSONObject destination = new JSONObject();
//                    try {
//                        destination.put("address", "24 Willie Mays Plaza, San Francisco, CA 94107");
//                    } catch (JSONException e) {
//                        throw new RuntimeException(e);
//                    }
//                    try {
//                        requestBody.put("origin", origin);
//                    } catch (JSONException e) {
//                        throw new RuntimeException(e);
//                    }
//                    try {
//                        requestBody.put("destination", destination);
//                    } catch (JSONException e) {
//                        throw new RuntimeException(e);
//                    }
//                    try {
//                        requestBody.put("travelMode", "DRIVE");
//                    } catch (JSONException e) {
//                        throw new RuntimeException(e);
//                    }
//                    try {
//                        requestBody.put("extraComputations", new String[] {"TRAFFIC_ON_POLYLINE"});
//                    } catch (JSONException e) {
//                        throw new RuntimeException(e);
//                    }
//                    try {
//                        requestBody.put("routingPreference", "TRAFFIC_AWARE_OPTIMAL");
//                    } catch (JSONException e) {
//                        throw new RuntimeException(e);
//                    }
//
//                    // Send the request
////                    DataOutputStream wr = null;
////                    try {
////                        wr = new DataOutputStream(conn.getOutputStream());
////                    } catch (IOException e) {
////                        throw new RuntimeException(e);
////                    }
////                    try {
////                        wr.writeBytes(requestBody.toString());
////                    } catch (IOException e) {
////                        throw new RuntimeException(e);
////                    }
////                    try {
////                        wr.flush();
////                    } catch (IOException e) {
////                        throw new RuntimeException(e);
////                    }
////                    try {
////                        wr.close();
////                    } catch (IOException e) {
////                        throw new RuntimeException(e);
////                    }
//
//                    // For POST only - START
//                    OutputStream os = null;
//                    try {
//                        os = conn.getOutputStream();
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                    try {
//                        os.write(requestBody.toString().getBytes());
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                    try {
//                        os.flush();
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                    try {
//                        os.close();
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                    // For POST only - END
//
//                    int responseCode = 0;
//                    try {
//                        responseCode = conn.getResponseCode();
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                    System.out.println("POST Response Code :: " + responseCode);
//
//                    // Read the response
//                    BufferedReader in = null;
//                    in = new BufferedReader(new InputStreamReader(process.getInputStream()));
//                    String inputLine;
//                    StringBuffer response = new StringBuffer();
//                    while (true) {
//                        try {
//                            if (!((inputLine = in.readLine()) != null)) break;
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        }
//                        response.append(inputLine);
//                    }
//                    try {
//                        in.close();
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//
//                    Log.i("project4_dbg","RESPONSE JSON : " + response.toString());
//                    // Parse the JSON response
//                    try {
//                        JSONObject responseBody = new JSONObject(response.toString());
//                    } catch (JSONException e) {
//                        throw new RuntimeException(e);
//                    }

                    String url = "https://routes.googleapis.com/directions/v2:computeRoutes";
                    String apiKey = "";
                    String requestJson = "{\n" +
                            "  \"origin\":{\n" +
                            String.format("    \"address\": \"%s\"\n", strValue1) +
                            "  },\n" +
                            "  \"destination\":{\n" +
                            String.format("    \"address\": \"%s\"\n", strValue2) +
                            "  },\n" +
                            "  \"travelMode\": \"DRIVE\",\n" +
                            "  \"extraComputations\": [\"TRAFFIC_ON_POLYLINE\"],\n" +
                            "  \"routingPreference\": \"TRAFFIC_AWARE_OPTIMAL\"\n" +
                            "}";

                    List<String> speedList = new ArrayList<>();

                    URL obj = null;
                    try {
                        obj = new URL(url);
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                    HttpURLConnection con = null;
                    try {
                        con = (HttpURLConnection) obj.openConnection();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        con.setRequestMethod("POST");
                    } catch (ProtocolException e) {
                        throw new RuntimeException(e);
                    }
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setRequestProperty("X-Goog-Api-Key", apiKey);
                    con.setRequestProperty("X-Goog-FieldMask", "routes.duration,routes.distanceMeters,routes.polyline,routes.legs.polyline,routes.travelAdvisory,routes.legs.travelAdvisory");
                    con.setDoOutput(true);
                    try {
                        con.getOutputStream().write(requestJson.getBytes("UTF-8"));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    int responseCode = 0;
                    try {
                        responseCode = con.getResponseCode();
                        System.out.println("Response code: " + responseCode);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    BufferedReader in = null;
                    try {
                        in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    while (true) {
                        try {
                            if (!((inputLine = in.readLine()) != null)) break;
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        response.append(inputLine);
                    }
                    try {
                        in.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    System.out.println("Response body: " + response.toString());

                    // Do something with the JSON response
                    String speedStr = "";
                    try {
                        // Parse the JSON response
                        float dist = 0;
                        float dur = 0;

                        JSONObject jsonObject = new JSONObject(response.toString());
                        JSONArray routes = jsonObject.getJSONArray("routes");

                        for (int i = 0; i < routes.length(); i++) {
                            JSONObject route = routes.getJSONObject(i);

                            float distanceMeters = route.getInt("distanceMeters");
                            String duration = route.getString("duration");

                            dist = distanceMeters/1000;
                            dur = Float.valueOf(duration.substring(0, duration.length()-1));

                            JSONObject trvlAdvisory = route.getJSONObject("travelAdvisory");
                            JSONArray speedIntrvls = trvlAdvisory.getJSONArray("speedReadingIntervals");
                            for (int j = 0; j < speedIntrvls.length(); j++) {
                                JSONObject element = speedIntrvls.getJSONObject(j);
                                String speed = element.getString("speed");
                                speedList.add(speed);
                                speedStr = speedStr.concat(speed + ",");
                                Log.i("project4_DG", speed);
                            }
                        }

                        // Do something with the duration and disatance values
//                        Log.i("project4_DBG", ("duration : " + dur));
//                        Log.i("project4_DBG", String.format("distance : %f", dist));

                        Log.i("project4_DG", speedStr);

                        id_textview3.setText(String.format("Duration : %f hrs \nDistance : %.2f Kms \n Road Conditions : %s", dur/3600,dist, speedStr));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                });*/

                thread.start();

            }

        });
    }
}
