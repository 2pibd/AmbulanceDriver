package com.twopibd.dactarbari.ambulance.drivers.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.twopibd.dactarbari.ambulance.drivers.R;

public class RideCompleatedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_compleated);
    }

    public void home(View view) {
        startActivity(new Intent(this,MapsActivity.class));
    }
}
