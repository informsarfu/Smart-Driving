package com.example.emergencyfeature;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_CODE_CALL = 100;
    private static final int PERMISSION_CODE_SMS = 101;
    private static final int PERMISSION_CODE_LOCATION = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button emergencyBtn = findViewById(R.id.emergencyBtn);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.CALL_PHONE,
                            Manifest.permission.SEND_SMS,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    PERMISSION_CODE_CALL);
        }

        emergencyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeEmergencyCall();
                sendEmergencySMS();
            }
        });
    }

    private void makeEmergencyCall() {
        Intent dialIntent = new Intent(Intent.ACTION_CALL);
        dialIntent.setData(Uri.parse("tel:911"));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED) {
            if (dialIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(dialIntent);
            } else {
                Toast.makeText(this, "No app to handle dialer", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Call permission not granted", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendEmergencySMS() {
        // Replace with a demo contact number for testing
        String demoContact = "1234567890";

        String emergencyMessage = "Emergency! I need help. Location: " + getCurrentLocation();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(demoContact, null, emergencyMessage, null, null);
            Toast.makeText(this, "Emergency SMS sent to " + demoContact, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "SMS permission not granted", Toast.LENGTH_SHORT).show();
        }
    }

    private String getCurrentLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (locationManager != null &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

            // Get the last known location
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            // If the last known location is not available, request location updates
            if (lastKnownLocation == null) {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        0, 0, new LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {
                                // Location updates received, use the updated location
                                String locationString = getLocationString(location);
                                sendEmergencySMS();

                                // Stop further location updates to conserve resources
                                locationManager.removeUpdates(this);
                            }

                        }
                );
            } else {
                // Last known location is available
                return getLocationString(lastKnownLocation);
            }
        }

        // Return a placeholder if location is unavailable
        return "Location: Unknown";
    }

    private String getLocationString(Location location) {
        if (location != null) {
            return "Latitude: " + location.getLatitude() + ", Longitude: " + location.getLongitude();
        } else {
            return "Location: Unknown";
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE_CALL) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Both call and SMS permissions granted
                makeEmergencyCall();
                sendEmergencySMS();
            } else {
                Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
