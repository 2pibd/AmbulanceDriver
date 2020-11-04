package com.twopibd.dactarbari.ambulance.drivers.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.twopibd.dactarbari.ambulance.drivers.R;
import com.twopibd.dactarbari.ambulance.drivers.adapters.FareChartAdapter;
import com.twopibd.dactarbari.ambulance.drivers.adapters.MyAmbulanceAdapter;
import com.twopibd.dactarbari.ambulance.drivers.model.FeesModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingActivity extends AppCompatActivity {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    FirebaseDatabase firebaseDatabase;
    List<FeesModel> feesModels = new ArrayList<>();
    FareChartAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        initRecycler();

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference("ambulance_fee").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot FareSnapshot : dataSnapshot.getChildren()) {
                    //Farelist.add(FareSnapshot.getKey());
                    FeesModel feesModel = FareSnapshot.getValue(FeesModel.class);

                    mAdapter.notifyItemInserted(feesModels.size());
                    Gson gson=new Gson();

                    HashMap<String,Long> hashMap= (HashMap<String, Long>) FareSnapshot.getValue();
                   // HashMap<String,String> hashStrring= (HashMap<String, String>) FareSnapshot.getValue();
                    feesModel.setPerKm(hashMap.get("per_km"));
                    Log.i("mkl_v", gson.toJson(feesModel));
                    feesModel.setType(FareSnapshot.getKey());

                    feesModels.add(feesModel);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void initRecycler() {

        mAdapter = new FareChartAdapter(getBaseContext(), feesModels);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getBaseContext(), RecyclerView.HORIZONTAL, false);
        recyclerview.setLayoutManager(mLayoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        // recyclerview.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
       // recyclerview.addItemDecoration(new DividerItemDecoration(getBaseContext(), LinearLayoutManager.HORIZONTAL));
        recyclerview.setAdapter(mAdapter);
    }

    public void back(View view) {
        onBackPressed();
    }
}
