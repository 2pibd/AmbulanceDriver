package com.twopibd.dactarbari.ambulance.drivers.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.twopibd.dactarbari.ambulance.drivers.R;
import com.twopibd.dactarbari.ambulance.drivers.adapters.DriverEarningsAdapter;
import com.twopibd.dactarbari.ambulance.drivers.listeners.locatiionRetrivedListener;
import com.twopibd.dactarbari.ambulance.drivers.model.RideRequestModel;
import com.twopibd.dactarbari.ambulance.drivers.model.TripHistory;
import com.twopibd.dactarbari.ambulance.drivers.model.UserStatusModel;
import com.twopibd.dactarbari.ambulance.drivers.utils.CustomDrawerButton;
import com.twopibd.dactarbari.ambulance.drivers.utils.SessionManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.twopibd.dactarbari.ambulance.drivers.utils.myfunctions.initMapShow;
import static com.twopibd.dactarbari.ambulance.drivers.utils.myfunctions.showDriverProfile;

public class MapsActivity extends GPSOpenActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String userID = "";
    @BindView(R.id.drawerImage)
    ImageView drawerImage;
    @BindView(R.id.tv_drawer_displayName)
    TextView tv_drawer_displayName;
    @BindView(R.id.onDuitySwitch)
    Switch onDuitySwitch;
    Context context = this;
    LatLng PicLocation;
    boolean isDriverOccupied = false;
    @BindView(R.id.bottom)
    LinearLayout bottom;
    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.linearRequest)
    LinearLayout linearRequest;
    @BindView(R.id.tv_estimated_fair)
    TextView tv_estimated_fair;
    @BindView(R.id.tv_distance)
    TextView tv_distance;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.driver_image)
    ImageView driver_image;
    @BindView(R.id.profile_image)
    CircleImageView profile_image;
    @BindView(R.id.tv_caller_name)
    TextView tv_caller_name;
    boolean isBottomSheetOn = true;
    TextView picup, tv_destination;
    CardView cardSubmit, cardCancel;
    String USER_STATUS = "user_status";
    String DATA = "data";
    String AMBULANCE_REQUEST = "ambulance_request";
    String USER_INFO = "user_info";

    @BindView(R.id.customDrawer)
    CustomDrawerButton customDrawerButton;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer_layout;
    BottomSheetBehavior bottomSheetBehavior;
    SessionManager sessionManager;
   


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        //showDriverProfile(context, driver_image);
        sessionManager = new SessionManager(this);

        userID = FirebaseAuth.getInstance().getUid();
        picup = (TextView) findViewById(R.id.tv_picup);
        tv_destination = (TextView) findViewById(R.id.tv_destination);
        cardSubmit = (CardView) findViewById(R.id.cardSubmit);
        cardCancel = (CardView) findViewById(R.id.cardCancel);


        customDrawerButton.setDrawerLayout(drawer_layout);
        customDrawerButton.getDrawerLayout().addDrawerListener(customDrawerButton);
        customDrawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDrawerButton.changeState();
            }
        });

        driver_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, DriverProfileActivity.class));
            }
        });


     /*   bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBottomSheetOn) {
                    bottom.animate().translationY(bottom.getHeight() - 200);
                    isBottomSheetOn = false;


                } else {
                    bottom.animate().translationY(0);
                    isBottomSheetOn = true;


                }
            }
        });

      */

        firebaseDatabase = FirebaseDatabase.getInstance();
        initMapShow(MapsActivity.this, this, getSupportFragmentManager());
        locatiionRetrivedListener.setMyLocationRetriver_(new locatiionRetrivedListener.myLocationRetriver() {
            @Override
            public Location onLocationretrived(Location location, String address) {
                Toast.makeText(context, address, Toast.LENGTH_SHORT).show();
                if (location != null) {
                    if (sessionManager.getOnlineVehicle() != null && sessionManager.getOnlineVehicle().length() > 0) {
                        firebaseDatabase.getReference("ambulanceLatestLocations").child(sessionManager.getOnlineVehicle()).child("lat").setValue(location.getLatitude());
                        firebaseDatabase.getReference("ambulanceLatestLocations").child(sessionManager.getOnlineVehicle()).child("lng").setValue(location.getLongitude());
                        firebaseDatabase.getReference("ambulanceLatestLocations").child(sessionManager.getOnlineVehicle()).child("city").setValue(address);
                    }

                    // Toast.makeText(context, location.toString(), Toast.LENGTH_SHORT).show();

                    // picLocationAddress = address;
                    // autoCompleteTextView.setText(picLocationAddress);
                    //  autoCompleteTextView.clearFocus();
                    //  ac_destination.requestFocus();
                    PicLocation = new LatLng(location.getLatitude(), location.getLongitude());

                    Log.i("mkl", "location updated from listener" + location.toString());

                    LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());
                    CameraUpdate location1 = CameraUpdateFactory.newLatLngZoom(sydney, 17);
                    if (mMap == null) {
                        //  mMap.animateCamera(location1);
                        initMap();
                    } else {
                        mMap.animateCamera(location1);
                    }


                } else {
                    Toast.makeText(context, "Null location", Toast.LENGTH_SHORT).show();
                }
                return null;
            }
        });
        setUpStatusbar();

        databaseReference = firebaseDatabase.getReference("ambulanceLatestLocations");
        Log.i("mkl", "Going to hit fire");
        //  downloadUserInfo();
        //undo
        //initDriverTripHistory();

/*
        databaseReference.child("userID").child("car_is_on_trip").setValue(false);
        onDuitySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    databaseReference.child(userID).child("car_is_on_trip").setValue(true);
                } else {

                    databaseReference.child(userID).child("car_is_on_trip").setValue(false);
                }
            }
        });

 */
        Log.i("mkl", "hey");

        checkStatus();


      showDriverNamePhoto();

        ConstraintLayout bottomSheetLayout = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        // update toggle button text
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        // update collapsed button text
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

    }

    private void showDriverNamePhoto() {
        FirebaseDatabase.getInstance().getReferenceFromUrl("https://dactarbari-ambulance.firebaseio.com/driver/" + FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //  Toast.makeText(context, dataSnapshot.child("photo").getValue().toString(), Toast.LENGTH_SHORT).show();
                    if (dataSnapshot.child("photo").exists()) {
                        Glide.with(context).load(dataSnapshot.child("photo").getValue().toString()).into(drawerImage);
                        Glide.with(context).load(dataSnapshot.child("photo").getValue().toString()).into(driver_image);
                    }
                    if (dataSnapshot.child("name").exists()) {
                        tv_drawer_displayName.setText(dataSnapshot.child("name").getValue().toString());
                        //  Glide.with(context).load(photo).transform(new CircleCrop()).into(drawerImage);
                        tv_name.setText(dataSnapshot.child("name").getValue().toString());
                        //   tv_drawer_displayName.setText(name);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void downloadUserInfo() {
        firebaseDatabase.getReference(USER_INFO).child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    HashMap<String, String> hashMap = (HashMap<String, String>) dataSnapshot.getValue();
                    String photo = hashMap.get("photo");
                    String name = hashMap.get("name");
                    Glide.with(context).load(photo).into(driver_image);
                    //  Glide.with(context).load(photo).transform(new CircleCrop()).into(drawerImage);
                    tv_name.setText(name);
                    //   tv_drawer_displayName.setText(name);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void initDriverTripHistory() {

        List<TripHistory> tripHistories = new ArrayList<>();

        DriverEarningsAdapter mAdapter = new DriverEarningsAdapter(context, tripHistories);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        recycler.setLayoutManager(mLayoutManager);
        recycler.setItemAnimator(new DefaultItemAnimator());
        // recyclerview.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recycler.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
        recycler.setAdapter(mAdapter);


        firebaseDatabase.getReference("ambulance_request").child(userID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                // HashMap<String, TripHistoryModel> hashMap = (HashMap<String, TripHistoryModel>) dataSnapshot.getValue();
                //  TripHistoryModel tripHistoryModel=hashMap.get("data");

                Toast.makeText(context,"okok"+ dataSnapshot.toString(), Toast.LENGTH_LONG).show();

                TripHistory tripHistory = dataSnapshot.getValue(TripHistory.class);

               // Log.i("mkl_history", tripHistory.getData().getFrom());

                tripHistories.add(0, tripHistory);
                // mAdapter.notifyItemInserted(tripHistories.size()-1);
               mAdapter.notifyDataSetChanged();


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void checkStatus() {

        firebaseDatabase.getReference(USER_STATUS).child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()&&dataSnapshot.child("isRidingNow").exists()&& dataSnapshot.child("rideID").exists()&&dataSnapshot.child("passengerID").exists()) {
                    String isRidingNow = dataSnapshot.child("isRidingNow").getValue().toString();
                    String passengerID = dataSnapshot.child("passengerID").getValue().toString();
                    String rideID = dataSnapshot.child("rideID").getValue().toString();
                    Toast.makeText(context, "" + isRidingNow + "\n" + passengerID + "\n" + rideID, Toast.LENGTH_LONG).show();

                    if (isRidingNow.equals("2")) {
                        showRequestPanelForRideID(rideID, userID, passengerID);
                    }
                    if (isRidingNow.equals("1")) {
                        startActivity(new Intent(context, RideOnActivity.class));
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

/*
        firebaseDatabase.getReference(USER_STATUS).child(userID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapsho2t, @Nullable String s) {

                if (dataSnapsho2t.exists()) {

                    try {


                        Log.i("mkl", dataSnapsho2t.getValue().toString());

                        HashMap<String, Long> hashMapLong = (HashMap<String, Long>) dataSnapsho2t.getValue();
                        HashMap<String, String> hashMapString = (HashMap<String, String>) dataSnapsho2t.getValue();
                        Long isRiding = hashMapLong.get("isRidingNow");
                        if (isRiding == 0) {
                            //driver is not on ride
                            Log.i("mkl", "driver was free");


                            isDriverOccupied = false;
                            //init_new_call_listener();
                        } else if (isRiding == 2) {

                            String rideID = hashMapString.get("rideID");
                            String passengerID = hashMapString.get("passengerID");
                            showRequestPanelForRideID(rideID, userID, passengerID);

                            //pending or engagged
                            //init_new_call_listener();

                        } else if (isRiding == 1) {
                            //driver is hired
                            Log.i("mkl", "driver is allready occupied");
                            isDriverOccupied = true;

                            startActivity(new Intent(context, RideOnActivity.class));
                            //firebaseDatabase.goOffline();
                            //  finish();


                        } else {
                            Toast.makeText(context, "Unknown Error", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {

                    }
                } else {
                    // init_new_call_listener();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {

                    try {


                        Log.i("mkl", dataSnapshot.getValue().toString());

                        HashMap<String, Long> hashMapLong = (HashMap<String, Long>) dataSnapshot.getValue();
                        HashMap<String, String> hashMapString = (HashMap<String, String>) dataSnapshot.getValue();
                        Long isRiding = hashMapLong.get("isRidingNow");
                        if (isRiding == 0) {

                            hideRequestpanel();
                            bottom.animate().translationY(0);
                            // init_new_call_listener();


                        } else if (isRiding == 2) {
                            //pending or engagged
                            //init_new_call_listener();
                            //  linearRequest.animate().translationY(-linearRequest.getHeight());
                            String rideID = hashMapString.get("rideID");
                            String passengerID = hashMapString.get("passengerID");

                            showRequestPanelForRideID(rideID, userID, passengerID);
                        } else if (isRiding == 1) {
                            //driver is hired
                            Log.i("mkl", "driver is allready occupied");
                            isDriverOccupied = true;

                            startActivity(new Intent(context, RideOnActivity.class));
                            //   finish();


                        } else {
                            Toast.makeText(context, "Unknwon Error", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {

                    }
                } else {
                    // init_new_call_listener();
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

 */


    }

    private void showRequestPanelForRideID(String rideID, String userID, String passenegID) {

        firebaseDatabase.getReference(AMBULANCE_REQUEST).child(rideID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                showCallerInfo(passenegID);
                playSound();
                showRequestpanel();
                tv_destination.setText(dataSnapshot.child("to").getValue().toString());
                tv_estimated_fair.setText(dataSnapshot.child("est_cost").getValue().toString());
                picup.setText(dataSnapshot.child("from").getValue().toString());
                tv_distance.setText(dataSnapshot.child("distance").getValue().toString());

                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                //  bottom.animate().translationY(bottom.getHeight() - 200);
                cardSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //for driver
                        firebaseDatabase.getReference(USER_STATUS).child(userID).child("isRidingNow").setValue(1);
                        firebaseDatabase.getReference(USER_STATUS).child(userID).child("currentAmbulance").setValue(sessionManager.getOnlineVehicle());

                        //for passenger
                        firebaseDatabase.getReference(USER_STATUS).child(passenegID).child("isRidingNow").setValue(1);
                        firebaseDatabase.getReference(USER_STATUS).child(passenegID).child("currentAmbulance").setValue(sessionManager.getOnlineVehicle());


                    }
                });
                cardCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //for driver
                        firebaseDatabase.getReference(USER_STATUS).child(userID).child("isRidingNow").setValue(0);

                        //for passenger
                        firebaseDatabase.getReference(USER_STATUS).child(passenegID).child("isRidingNow").setValue(0);
                        hideRequestpanel();


                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

/*
        firebaseDatabase.getReference(AMBULANCE_REQUEST).child(this.userID).child(rideID).child(DATA).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("mkl_1J", "2=>" + dataSnapshot.getValue().toString());
                Log.i("mkl_1J", "1=>" + dataSnapshot.getValue().toString());

                RideRequestModel rideRequestModel = dataSnapshot.getValue(RideRequestModel.class);

                Gson gson = new Gson();
                // Log.i("mkl_1J", gson.toJson(rideRequestModel));

                tv_destination.setText(rideRequestModel.getTo());
                picup.setText(rideRequestModel.getFrom());
                Double distance = Double.valueOf(rideRequestModel.getDistance());
                distance = distance / 1000;
                tv_distance.setText(distance + " KM");
                tv_estimated_fair.setText(rideRequestModel.getEstimated() + " BDT");
                // tv_destination.setText(rideRequestModel.getTo());

                showCallerInfo(passenegID);

                playSound();
                showRequestpanel();
                bottom.animate().translationY(bottom.getHeight() - 200);


                cardSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //for driver
                        firebaseDatabase.getReference(USER_STATUS).child(userID).child(DATA).child("isRidingNow").setValue(1);

                        //for passenger
                        firebaseDatabase.getReference(USER_STATUS).child(passenegID).child(DATA).child("isRidingNow").setValue(1);


                    }
                });

                cardCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //for driver
                        firebaseDatabase.getReference(USER_STATUS).child(userID).child(DATA).child("isRidingNow").setValue(0);

                        //for passenger
                        firebaseDatabase.getReference(USER_STATUS).child(passenegID).child(DATA).child("isRidingNow").setValue(0);

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

 */





/*

        firebaseDatabase.getReference(AMBULANCE_REQUEST).child(this.userID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot1, @Nullable String s) {

                Log.i("kkk", dataSnapshot1.getKey());

                HashMap<String, HashMap<String, String>> hashMap = (HashMap<String, HashMap<String, String>>) dataSnapshot1.getValue();
                HashMap<String, HashMap<String, Double>> hashMapD = (HashMap<String, HashMap<String, Double>>) dataSnapshot1.getValue();
                HashMap<String, HashMap<String, Long>> hashMapI = (HashMap<String, HashMap<String, Long>>) dataSnapshot1.getValue();
                HashMap<String, String> hashMapN = hashMap.get("data");
                HashMap<String, Double> hashMapDouble = hashMapD.get("data");
                HashMap<String, Long> hashMapIN = hashMapI.get("data");

                Log.i("m_", "request came");

//!isDriverOccupied
                if (hashMapN.get("status").equals("pending")) {

                    RideRequestModel rideRequestModel = new RideRequestModel();

                    rideRequestModel.setFrom(hashMapN.get("from"));
                    rideRequestModel.setTo(hashMapN.get("to"));
                    rideRequestModel.setStatus(hashMapN.get("status"));
                    rideRequestModel.setUSER_ID(hashMapN.get("user_ID"));
                    rideRequestModel.setEstimated(hashMapIN.get("estimated"));


                    Log.i("mkl", dataSnapshot1.toString());
                    // Log.i("mkl", hashMap.toString());


                    //  Toast.makeText(context, "destinmation " + rideRequestModel.getTo(), Toast.LENGTH_SHORT).show();
                    String targetUID = hashMapN.get("user_ID");
                    if (targetUID != null && targetUID.length() > 0) {


                        // Dialog dialog = doForMe.showDialog(context, R.layout.ride_request_dialog);
                        // TextView picup = (TextView) dialog.findViewById(R.id.tv_picup);
                        // TextView tv_destination = (TextView) dialog.findViewById(R.id.tv_destination);
                        // CardView cardSubmit = (CardView) dialog.findViewById(R.id.cardSubmit);
                        // CardView cardCancel = (CardView) dialog.findViewById(R.id.cardCancel);
                        picup.setText(rideRequestModel.getFrom());
                        tv_destination.setText(rideRequestModel.getTo());
                        // tv_estimated_fair.se
                        Long dista = hashMapIN.get("distance");
                        Float f = Float.parseFloat(String.valueOf(dista));
                        tv_distance.setText("" + (f / 1000) + " KM");

                        tv_estimated_fair.setText("" + rideRequestModel.getEstimated() + " BDT");

                        playSound();
                        //  linearRequest.animate().translationY(-(linearRequest.getHeight() + 100));

                        // showRequestpanel();
                        // showRequestPanelForRideID();

                        //botom sheet clode

                        bottom.animate().translationY(bottom.getHeight() - 200);
                        isBottomSheetOn = false;

                        cardSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                firebaseDatabase.getReference("ambulance_request").child(MapsActivity.this.userID).child(dataSnapshot1.getKey()).child("data").child("status").setValue("accepted").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //  dialog.dismiss();
                                        //opponent user
                                        //for passenger
                                        firebaseDatabase.getReference("user_status").child(rideRequestModel.getUSER_ID()).child("data").child("isRidingNow").setValue(1);
                                        firebaseDatabase.getReference("user_status").child(rideRequestModel.getUSER_ID()).child("data").child("rideID").setValue(dataSnapshot1.getKey());
                                        firebaseDatabase.getReference("user_status").child(rideRequestModel.getUSER_ID()).child("data").child("driverID").setValue(MapsActivity.this.userID);

                                        //for driver
                                        firebaseDatabase.getReference("user_status").child(MapsActivity.this.userID).child("data").child("rideID").setValue(dataSnapshot1.getKey());
                                        firebaseDatabase.getReference("user_status").child(MapsActivity.this.userID).child("data").child("passengerID").setValue(rideRequestModel.getUSER_ID());

                                        firebaseDatabase.getReference("user_status").child(MapsActivity.this.userID).child("data").child("isRidingNow").setValue(1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {


                                                Toast.makeText(context, "accept done", Toast.LENGTH_SHORT).show();

                                                Intent intent = new Intent(context, RideOnActivity.class);
                                                //   intent.putExtra("from", hashMapN.get("from"));
                                                //   intent.putExtra("to", hashMapN.get("to"));
                                                // intent.putExtra("user_id", hashMapN.get("user_ID"));

                                                startActivity(intent);
                                                //firebaseDatabase.goOffline();


                                            }
                                        });


                                    }
                                });
                            }
                        });
                        cardCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                hideRequestpanel();
                                // DatabaseReference databaseReference = firebaseDatabase.getReference("ambulance_request");
                                // String key = databaseReference.push().getKey();


                                firebaseDatabase.getReference(USER_STATUS).child(rideRequestModel.getUSER_ID()).child("data").child("isRidingNow").setValue(0);

                                firebaseDatabase.getReference("ambulance_request").child(MapsActivity.this.userID).child(dataSnapshot1.getKey()).child("data").child("status").setValue("cancel").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(context, "rejected done", Toast.LENGTH_SHORT).show();
                                        //dialog.dismiss();
                                        firebaseDatabase.getReference("user_status").child(MapsActivity.this.userID).child("data").child("isRidingNow").setValue(0).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(context, "Cancel done", Toast.LENGTH_SHORT).show();
                                                //bottom sheet open
                                                bottom.animate().translationY(0);
                                                isBottomSheetOn = true;


                                            }
                                        });


                                    }
                                });
                            }
                        });


                    } else {
                        Toast.makeText(context, "target uid null or enplty", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                //ride caneled by user code here

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

 */


    }

    private void showCallerInfo(String passenegID) {
        FirebaseDatabase.getInstance().getReference("users").child(passenegID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.i("mkl123", dataSnapshot.toString());
                    HashMap<String, String> hashMap = (HashMap<String, String>) dataSnapshot.getValue();
                    String photo = hashMap.get("photo");
                    String name = hashMap.get("name");

                    Glide.with(context).load(photo).into(profile_image);
                    tv_caller_name.setText(name);

                } else {
                    tv_caller_name.setText(passenegID);
                    Log.i("mkl123", "no data");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Toast.makeText(context, "onRestart", Toast.LENGTH_SHORT).show();
        showDriverNamePhoto();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //checkStatus();


    }

    private void initMap() {
        initMapShow(MapsActivity.this, this, getSupportFragmentManager());
    }


    private void init_new_call_listener() {
        //   Toast.makeText(context, "Listener initialized", Toast.LENGTH_SHORT).show();
        /*

        firebaseDatabase.getReference(AMBULANCE_REQUEST).child(userID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot1, @Nullable String s) {

                Log.i("kkk", dataSnapshot1.getKey());

                HashMap<String, HashMap<String, String>> hashMap = (HashMap<String, HashMap<String, String>>) dataSnapshot1.getValue();
                HashMap<String, HashMap<String, Double>> hashMapD = (HashMap<String, HashMap<String, Double>>) dataSnapshot1.getValue();
                HashMap<String, HashMap<String, Long>> hashMapI = (HashMap<String, HashMap<String, Long>>) dataSnapshot1.getValue();
                HashMap<String, String> hashMapN = hashMap.get("data");
                HashMap<String, Double> hashMapDouble = hashMapD.get("data");
                HashMap<String, Long> hashMapIN = hashMapI.get("data");

                Log.i("m_", "request came");

//!isDriverOccupied
                if (hashMapN.get("status").equals("pending")) {

                    RideRequestModel rideRequestModel = new RideRequestModel();

                    rideRequestModel.setFrom(hashMapN.get("from"));
                    rideRequestModel.setTo(hashMapN.get("to"));
                    rideRequestModel.setStatus(hashMapN.get("status"));
                    rideRequestModel.setUSER_ID(hashMapN.get("user_ID"));
                    rideRequestModel.setEstimated(hashMapIN.get("estimated"));


                    Log.i("mkl", dataSnapshot1.toString());
                    // Log.i("mkl", hashMap.toString());


                    //  Toast.makeText(context, "destinmation " + rideRequestModel.getTo(), Toast.LENGTH_SHORT).show();
                    String targetUID = hashMapN.get("user_ID");
                    if (targetUID != null && targetUID.length() > 0) {


                        // Dialog dialog = doForMe.showDialog(context, R.layout.ride_request_dialog);
                        // TextView picup = (TextView) dialog.findViewById(R.id.tv_picup);
                        // TextView tv_destination = (TextView) dialog.findViewById(R.id.tv_destination);
                        // CardView cardSubmit = (CardView) dialog.findViewById(R.id.cardSubmit);
                        // CardView cardCancel = (CardView) dialog.findViewById(R.id.cardCancel);
                        picup.setText(rideRequestModel.getFrom());
                        tv_destination.setText(rideRequestModel.getTo());
                        // tv_estimated_fair.se
                        Long dista = hashMapIN.get("distance");
                        Float f = Float.parseFloat(String.valueOf(dista));
                        tv_distance.setText("" + (f / 1000) + " KM");

                        tv_estimated_fair.setText("" + rideRequestModel.getEstimated() + " BDT");

                        playSound();
                        //  linearRequest.animate().translationY(-(linearRequest.getHeight() + 100));

                        // showRequestpanel();
                        // showRequestPanelForRideID();

                        //botom sheet clode

                        bottom.animate().translationY(bottom.getHeight() - 200);
                        isBottomSheetOn = false;

                        cardSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                firebaseDatabase.getReference("ambulance_request").child(userID).child(dataSnapshot1.getKey()).child("data").child("status").setValue("accepted").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //  dialog.dismiss();
                                        //opponent user
                                        //for passenger
                                        firebaseDatabase.getReference("user_status").child(rideRequestModel.getUSER_ID()).child("data").child("isRidingNow").setValue(1);
                                        firebaseDatabase.getReference("user_status").child(rideRequestModel.getUSER_ID()).child("data").child("rideID").setValue(dataSnapshot1.getKey());
                                        firebaseDatabase.getReference("user_status").child(rideRequestModel.getUSER_ID()).child("data").child("driverID").setValue(userID);

                                        //for driver
                                        firebaseDatabase.getReference("user_status").child(userID).child("data").child("rideID").setValue(dataSnapshot1.getKey());
                                        firebaseDatabase.getReference("user_status").child(userID).child("data").child("passengerID").setValue(rideRequestModel.getUSER_ID());

                                        firebaseDatabase.getReference("user_status").child(userID).child("data").child("isRidingNow").setValue(1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {


                                                Toast.makeText(context, "accept done", Toast.LENGTH_SHORT).show();

                                                Intent intent = new Intent(context, RideOnActivity.class);
                                                //   intent.putExtra("from", hashMapN.get("from"));
                                                //   intent.putExtra("to", hashMapN.get("to"));
                                                // intent.putExtra("user_id", hashMapN.get("user_ID"));

                                                startActivity(intent);
                                                //firebaseDatabase.goOffline();


                                            }
                                        });


                                    }
                                });
                            }
                        });
                        cardCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                hideRequestpanel();
                                // DatabaseReference databaseReference = firebaseDatabase.getReference("ambulance_request");
                                // String key = databaseReference.push().getKey();


                                firebaseDatabase.getReference(USER_STATUS).child(rideRequestModel.getUSER_ID()).child("data").child("isRidingNow").setValue(0);

                                firebaseDatabase.getReference("ambulance_request").child(userID).child(dataSnapshot1.getKey()).child("data").child("status").setValue("cancel").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(context, "rejected done", Toast.LENGTH_SHORT).show();
                                        //dialog.dismiss();
                                        firebaseDatabase.getReference("user_status").child(userID).child("data").child("isRidingNow").setValue(0).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(context, "Cancel done", Toast.LENGTH_SHORT).show();
                                                //bottom sheet open
                                                bottom.animate().translationY(0);
                                                isBottomSheetOn = true;


                                            }
                                        });


                                    }
                                });
                            }
                        });


                    } else {
                        Toast.makeText(context, "target uid null or enplty", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                //ride caneled by user code here

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

         */
    }

    private void showRequestpanel() {
        linearRequest.animate().translationY(00);
    }

    private void hideRequestpanel() {
        linearRequest.animate().translationY(-(linearRequest.getHeight()));

    }

    private void playSound() {
        MediaPlayer mMediaPlayer = new MediaPlayer();
        Uri mediaPath = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.eventually);
        try {
            mMediaPlayer.setDataSource(getApplicationContext(), mediaPath);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void setUpStatusbar() {
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setPadding(0, 000, 0, 300);

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        // mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setMyLocationEnabled(true);


        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {

            @Override
            public void onCameraIdle() {


                databaseReference.child(sessionManager.getOnlineVehicle()).child("bearer").setValue((int) mMap.getCameraPosition().bearing);
                if (mMap.getCameraPosition().target.latitude > 0 && mMap.getCameraPosition().target.longitude > 0) {
                    databaseReference.child(sessionManager.getOnlineVehicle()).child("lat").setValue(Double.valueOf(mMap.getCameraPosition().target.latitude));
                    databaseReference.child(sessionManager.getOnlineVehicle()).child("lng").setValue(Double.valueOf(mMap.getCameraPosition().target.longitude));
                }
                databaseReference.child(sessionManager.getOnlineVehicle()).child("last_seen").setValue(Calendar.getInstance().getTime().toString());




            }
            

        });


        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                //   CameraUpdate location_ = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17);
                //   mMap.animateCamera(location_);


                Log.i("mkl", "location => " + location.getLongitude() + "<=>" + location.getLongitude());
                CameraUpdate location_ = CameraUpdateFactory.newLatLngZoom(
                        new LatLng(location.getLatitude(), location.getLongitude()), 17);
                mMap.animateCamera(location_);

                databaseReference.child(sessionManager.getOnlineVehicle()).child("bearer").setValue((int) mMap.getCameraPosition().bearing);

                if (mMap.getCameraPosition().target.latitude > 0 && mMap.getCameraPosition().target.longitude > 0) {
                    databaseReference.child(sessionManager.getOnlineVehicle()).child("lat").setValue(Double.valueOf(mMap.getCameraPosition().target.latitude));
                    databaseReference.child(sessionManager.getOnlineVehicle()).child("lng").setValue(Double.valueOf(mMap.getCameraPosition().target.longitude));
                }


                databaseReference.child(sessionManager.getOnlineVehicle()).child("last_seen").setValue(Calendar.getInstance().getTime().toString());
                //    Toast.makeText(MapsActivity.this, "Location retrived", Toast.LENGTH_SHORT).show();

                //mMap.addMarker(new MarkerOptions().position( new LatLng(location.getLatitude(),location.getLongitude())).icon(BitmapDescriptorFactory.defaultMarker()));


            }
        });


    }

    public void openMyAmbulanceActivity(View view) {
        startActivity(new Intent(context, MyAmbulanceActivity.class));
    }

    public void opensettingActivity(View view) {

        startActivity(new Intent(context, SettingActivity.class));
    }
}
