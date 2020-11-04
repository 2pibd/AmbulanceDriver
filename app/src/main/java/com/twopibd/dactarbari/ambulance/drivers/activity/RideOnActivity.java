package com.twopibd.dactarbari.ambulance.drivers.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.twopibd.dactarbari.ambulance.drivers.R;
import com.twopibd.dactarbari.ambulance.drivers.listeners.MapPermissionsListener;
import com.twopibd.dactarbari.ambulance.drivers.listeners.locatiionRetrivedListener;
import com.twopibd.dactarbari.ambulance.drivers.utils.SessionManager;
import com.twopibd.dactarbari.ambulance.drivers.utils.myfunctions;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.twopibd.dactarbari.ambulance.drivers.utils.myfunctions.initializeLocationPermissions;

public class RideOnActivity extends GPSOpenActivity {
    @BindView(R.id.tv_ride_status)
    TextView tv_ride_status;
    @BindView(R.id.relativeDashbody)
    RelativeLayout relativeDashbody;
    @BindView(R.id.tv_fair)
    TextView tv_fair;
    @BindView(R.id.cardAmbulanceArrived)
    CardView cardAmbulanceArrived;
    @BindView(R.id.cardRideCompleate)
    CardView cardRideCompleate;
    @BindView(R.id.cardCall)
    CardView cardCall;
    @BindView(R.id.tv_distance)
    TextView tv_distance;
    @BindView(R.id.cardStartTrip)
    CardView cardStartTrip;
    @BindView(R.id.cardRideCancel)
    CardView cardRideCancel;
    FirebaseDatabase firebaseDatabase;
    @BindView(R.id.tv_from)
    TextView tv_from;
    @BindView(R.id.tv_to)
    TextView tv_to;
    @BindView(R.id.tv_ride_id)
    TextView tv_ride_id;
    Context context = this;
    MapView rosterMapView;
    String rideID;
    LatLng LAST_KNOWN_LOCATION;
    Double COVERED_DISTANCE = 0d;
    String passenger;
    boolean stopListener = false;
    static boolean active = false;
    Double distance = 0d;
    @BindView(R.id.view_black_transparent)
    View view_black_transparent;
    @BindView(R.id.tv_total_fair)
    TextView tv_total_fair;
    @BindView(R.id.tv_pay)
    TextView tv_pay;
    boolean continueBill = true;
    Long BASE_FAIR = 0l;
    Long FAIR_PER_KM = 0l;
    Double FINAL_COST = 0d;
    String destination = "";
    String USER_STATUS = "user_status";
    String DATA = "data";
    String AMBULANCE_REQUEST = "ambulance_request";
    String USER_INFO = "user_info";
    String userID;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_on);
        ButterKnife.bind(this);
        //USER_ID= FirebaseAuth.getInstance()
        sessionManager = new SessionManager(this);
        userID = FirebaseAuth.getInstance().getUid();

        rosterMapView = (MapView) findViewById(R.id.roster_map_view);
        rosterMapView.onCreate(savedInstanceState);
        initializeLocationPermissions(this, new MapPermissionsListener.myMapPermissionListener() {
            @Override
            public String onPermissionGivenSucced() {
                initLocation();
                return null;
            }

            @Override
            public String onPermissionGivenFailed() {
                return null;
            }
        });

        firebaseDatabase = FirebaseDatabase.getInstance();
        // databaseReference = firebaseDatabase.getReference("ambulanceLatestLocations");

        downloadOwnDetails();

        locatiionRetrivedListener.setMyLocationRetriver_(new locatiionRetrivedListener.myLocationRetriver() {
            @Override
            public Location onLocationretrived(Location location, String address) {

                if (active) {
                    if (location != null) {

                        if (location.getLatitude() > 0 && location.getLongitude() > 0) {
                            firebaseDatabase.getReference("ambulanceLatestLocations").child(sessionManager.getOnlineVehicle()).child("lat").setValue(Double.valueOf(location.getLatitude()));
                            firebaseDatabase.getReference("ambulanceLatestLocations").child(sessionManager.getOnlineVehicle()).child("lng").setValue(Double.valueOf(location.getLongitude()));
                        }

                    } else {
                        Toast.makeText(context, "Null location", Toast.LENGTH_SHORT).show();
                    }
                }
                return null;

            }
        });


    }

    private void initLocation() {
        rosterMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Toast.makeText(context, "Loaded", Toast.LENGTH_SHORT).show();
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location location) {
                        LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());
                        CameraUpdate location1 = CameraUpdateFactory.newLatLngZoom(sydney, 17);
                        googleMap.animateCamera(location1);

                        if (location.getLatitude() > 0 && location.getLongitude() > 0) {
                            firebaseDatabase.getReference("ambulanceLatestLocations").child(userID).child("car_current_lat").setValue(Double.valueOf(location.getLatitude()));
                            firebaseDatabase.getReference("ambulanceLatestLocations").child(userID).child("car_current_lng").setValue(Double.valueOf(location.getLongitude()));
                        }


                    }
                });

            }
        });
    }

    private void init_businessLogic() {
        //following listener checks if the user is on active ride or not
        //if not riding then onBackPressed()
        //if riding then initiate listener as ambulance_request/driver/rideID
        //has an ride cancel button and listener and redirect to main activity
        //
        Log.i("tmkl", " here");
        //3. get vehicle status
        firebaseDatabase.getReference(USER_STATUS).child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.i("tmkl", " data existts");

                    String isRidingNow = dataSnapshot.child("isRidingNow").getValue().toString();
                    String passengerID = dataSnapshot.child("passengerID").getValue().toString();
                    rideID = dataSnapshot.child("rideID").getValue().toString();
                    Toast.makeText(context, "" + isRidingNow + "\n" + passengerID + "\n" + rideID, Toast.LENGTH_LONG).show();
                    Log.i("mkl__", "" + isRidingNow + "\n" + passengerID + "\n" + rideID);

                    if (isRidingNow.equals("2")) {
                        // showRequestPanelForRideID(rideID, userID, passengerID);
                    }
                    if (isRidingNow.equals("1")) {
                        firebaseDatabase.getReference("ambulance_request").child(rideID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                tv_from.setText(dataSnapshot.child("from").getValue().toString());
                                tv_to.setText(dataSnapshot.child("to").getValue().toString());
                              //  rideID =dataSnapshot.getKey();
                                Log.i("mkl__1", dataSnapshot.getValue().toString());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        cardRideCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(context, "Cancel clicked", Toast.LENGTH_SHORT).show();
                                firebaseDatabase.getReference("ambulance_request").child(rideID).child("hasDriverCanceled").setValue(true);
                                firebaseDatabase.getReference("user_status").child(passengerID).child("isRidingNow").setValue(0);
                                firebaseDatabase.getReference("user_status").child(userID).child("isRidingNow").setValue(0).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // onBackPressed();
                                        startActivity(new Intent(context, MapsActivity.class));
                                        finishAffinity();
                                    }
                                });
                            }
                        });



                        cardRideCompleate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                relativeDashbody.setVisibility(View.VISIBLE);
                                view_black_transparent.setVisibility(View.VISIBLE);
                                tv_total_fair.setText("Total Fair : 450 TK");
                                tv_total_fair.setVisibility(View.VISIBLE);
                                tv_pay.setVisibility(View.VISIBLE);

                                tv_pay.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(new Intent(context, MapsActivity.class));
                                        finishAffinity();

                                    }
                                });

                                firebaseDatabase.getReference("ambulance_request").child(rideID).child("hasRideCompleated").setValue(true);
                                firebaseDatabase.getReference("user_status").child(userID).child("data").child("isRidingNow").setValue(0);
                                firebaseDatabase.getReference("user_status").child(passengerID).child("data").child("isRidingNow").setValue(0);
                                // firebaseDatabase.goOffline();
                                continueBill = false;
                                cardRideCompleate.setEnabled(false);
                                cardRideCompleate.setClickable(false);
                                cardRideCompleate.setAlpha(0.5f);

                            }
                        });


                    }
                } else {
                    Log.i("tmkl", "No data");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        // following block to be deleted
        /*
        firebaseDatabase.getReference("user_status").child(USER_ID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i("mkl_v", dataSnapshot.toString());


                Log.i("mkl", dataSnapshot.toString());
                if (dataSnapshot.exists()) {
                    //  Log.i("mkl_v", "VIEW =>  "+dataSnapshot.toString());
                    HashMap<String, Integer> hashMapInt = (HashMap<String, Integer>) dataSnapshot.getValue();
                    HashMap<String, Long> hashMapLong = (HashMap<String, Long>) dataSnapshot.getValue();
                    HashMap<String, String> hashMapString = (HashMap<String, String>) dataSnapshot.getValue();
                    Log.i("mkl_v", "VIEW =>  " + hashMapInt.get("isRidingNow"));
                    Log.i("mkl_v", "VIEW =>  " + hashMapString.get("rideID"));

                    Long isRiding = hashMapLong.get("isRidingNow");
                    rideID = hashMapString.get("rideID");
                    passenger = hashMapString.get("passengerID");
                    String driver = USER_ID;
                    //   Log.i("mkl_v", "VIEW =>  "+hashMapInt.get("rideID"));
                    //  Log.i("mkl_v", "VIEW =>  "+hashMapInt.get("isRidingNow"));
                    Toast.makeText(context, "ride id => " + rideID, Toast.LENGTH_SHORT).show();
                    tv_ride_id.setText("ride id => " + rideID);

                    cardRideCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            firebaseDatabase.getReference("ambulance_request").child(driver).child(rideID).child("data").child("status").setValue("cancel");
                            firebaseDatabase.getReference("user_status").child(passenger).child("data").child("isRidingNow").setValue(0);
                            firebaseDatabase.getReference("user_status").child(USER_ID).child("data").child("isRidingNow").setValue(0).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // onBackPressed();
                                    startActivity(new Intent(context, MapsActivity.class));
                                    finishAffinity();
                                }
                            });
                        }
                    });


                    if (isRiding == 0) {
                        //driver is not on ride
                        Log.i("mkl", "passenger was free");
                        onBackPressed();


                    } else {
                        //driver is hired


                        Log.i("mkl", "passenger is allready occupied");


                        // Toast.makeText(context, "driver  id => " + driver, Toast.LENGTH_SHORT).show();
                        Log.i("mkl1", "driver " + driver);
                        Log.i("mkl1", "ride id  " + rideID);


                        cardRideCompleate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                relativeDashbody.setVisibility(View.VISIBLE);
                                view_black_transparent.setVisibility(View.VISIBLE);
                                tv_total_fair.setText("Total Fair : 450 TK");
                                tv_total_fair.setVisibility(View.VISIBLE);
                                tv_pay.setVisibility(View.VISIBLE);

                                tv_pay.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(new Intent(context, MapsActivity.class));
                                        finishAffinity();

                                    }
                                });

                                firebaseDatabase.getReference("ambulance_request").child(driver).child(rideID).child("data").child("hasRideCompleated").setValue(true);
                                firebaseDatabase.getReference("user_status").child(driver).child("data").child("isRidingNow").setValue(0);
                                firebaseDatabase.getReference("user_status").child(passenger).child("data").child("isRidingNow").setValue(0);
                                // firebaseDatabase.goOffline();
                                continueBill = false;
                                cardRideCompleate.setEnabled(false);
                                cardRideCompleate.setClickable(false);
                                cardRideCompleate.setAlpha(0.5f);

                            }
                        });

                        firebaseDatabase.getReference("ambulance_request").child(driver).child(rideID).addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot2, @Nullable String s) {
                                Log.i("mkl_v1", dataSnapshot2.getValue().toString());

                                //   HashMap<String ,HashMap<String,String>>hashMap= (HashMap<String, HashMap<String,String>>) dataSnapshot2.getValue();
                                HashMap<String, String> hashMapN = (HashMap<String, String>) dataSnapshot2.getValue();
                                HashMap<String, Boolean> hashMapBoolean = (HashMap<String, Boolean>) dataSnapshot2.getValue();
                                tv_ride_status.setText("Status : " + hashMapN.get("status"));
                                tv_from.setText(hashMapN.get("from"));
                                tv_to.setText(hashMapN.get("to"));
                                destination = hashMapN.get("to");

                                if (hashMapBoolean.get("hasCancelled") == true) {
                                    //ride is cancelled

                                } else if (hashMapBoolean.get("hasRideCompleated")) {
                                    //
                                    cardRideCompleate.setAlpha(0.5f);
                                    cardRideCompleate.setEnabled(false);
                                    cardRideCompleate.setClickable(false);
                                    if (stopListener == false) {
                                        //  startActivity(new Intent(context, RideCompleatedActivity.class));
                                        stopListener = true;

                                    }
                                } else if (hashMapBoolean.get("hasTripStarted")) {


                                    String URL = "https://www.google.com/maps/dir/?api=1&travelmode=driving&dir_action=navigate&destination=" + destination;
                                    Uri location = Uri.parse(URL);
                                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
                                    //     startActivity(mapIntent);


                                    retriveLocationAndPost();
                                    cardStartTrip.setAlpha(0.5f);
                                    cardStartTrip.setEnabled(false);
                                    cardStartTrip.setClickable(false);
                                    //  cardAmbulanceArrived.
                                    cardAmbulanceArrived.setAlpha(0.5f);
                                    cardAmbulanceArrived.setEnabled(false);
                                    cardAmbulanceArrived.setClickable(false);


                                    cardRideCancel.setAlpha(0.5f);
                                    cardRideCancel.setEnabled(false);
                                    cardRideCancel.setClickable(false);

                                    // cardRideCompleate
                                    cardRideCompleate.setAlpha(1f);
                                    cardRideCompleate.setEnabled(true);
                                    cardRideCompleate.setClickable(true);

                                    showtraveledDistance();


                                } else {

                                    cardRideCompleate.setAlpha(0.5f);
                                    cardRideCompleate.setEnabled(false);
                                    cardRideCompleate.setClickable(false);

                                    cardStartTrip.setAlpha(1.0f);
                                    if (hashMapBoolean.get("hasArrived")) {

                                        cardAmbulanceArrived.setAlpha(0.5f);
                                        cardAmbulanceArrived.setEnabled(false);
                                        cardAmbulanceArrived.setClickable(false);

                                        cardRideCancel.setAlpha(1f);
                                        cardRideCancel.setEnabled(true);
                                        cardRideCancel.setClickable(true);

                                        cardStartTrip.setAlpha(1f);
                                        cardStartTrip.setEnabled(true);
                                        cardStartTrip.setClickable(true);
                                    } else {
                                        //trip not started+ambulance not arrived

                                        cardStartTrip.setAlpha(0.5f);
                                        cardStartTrip.setEnabled(false);
                                        cardStartTrip.setClickable(false);

                                        cardRideCancel.setAlpha(1f);
                                        cardRideCancel.setEnabled(true);
                                        cardRideCancel.setClickable(true);


                                        cardAmbulanceArrived.setAlpha(1f);
                                        cardAmbulanceArrived.setEnabled(true);
                                        cardAmbulanceArrived.setClickable(true);

                                    }

                                }


                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                //   HashMap<String ,HashMap<String,String>>hashMap= (HashMap<String, HashMap<String,String>>) dataSnapshot2.getValue();
                                HashMap<String, String> hashMapN = (HashMap<String, String>) dataSnapshot.getValue();
                                HashMap<String, Boolean> hashMapBoolean = (HashMap<String, Boolean>) dataSnapshot.getValue();
                                tv_ride_status.setText("Status : " + hashMapN.get("status"));
                                tv_from.setText(hashMapN.get("from"));
                                tv_to.setText(hashMapN.get("to"));

                                if (hashMapBoolean.get("hasCancelled") == true) {
                                    //ride is cancelled

                                } else if (hashMapBoolean.get("hasRideCompleated")) {


                                    //
                                    if (stopListener == false) {
                                        // startActivity(new Intent(context, RideCompleatedActivity.class));


                                        stopListener = true;
                                    }

                                } else if (hashMapBoolean.get("hasTripStarted")) {
                                    retriveLocationAndPost();
                                    cardStartTrip.setAlpha(0.5f);
                                    cardStartTrip.setEnabled(false);
                                    cardStartTrip.setClickable(false);
                                    //  cardAmbulanceArrived.
                                    cardAmbulanceArrived.setAlpha(0.5f);
                                    cardAmbulanceArrived.setEnabled(false);
                                    cardAmbulanceArrived.setClickable(false);


                                    cardRideCancel.setAlpha(0.5f);
                                    cardRideCancel.setEnabled(false);
                                    cardRideCancel.setClickable(false);

                                    cardRideCompleate.setAlpha(1f);
                                    cardRideCompleate.setEnabled(true);
                                    cardRideCompleate.setClickable(true);

                                    showtraveledDistance();


                                } else {
                                    cardStartTrip.setAlpha(1.0f);
                                    if (hashMapBoolean.get("hasArrived")) {

                                        cardAmbulanceArrived.setAlpha(0.5f);
                                        cardAmbulanceArrived.setEnabled(false);
                                        cardAmbulanceArrived.setClickable(false);

                                        cardRideCancel.setAlpha(1f);
                                        cardRideCancel.setEnabled(true);
                                        cardRideCancel.setClickable(true);

                                        cardStartTrip.setAlpha(1f);
                                        cardStartTrip.setEnabled(true);
                                        cardStartTrip.setClickable(true);
                                    } else {
                                        //trip not started+ambulance not arrived

                                        cardStartTrip.setAlpha(0.5f);
                                        cardStartTrip.setEnabled(false);
                                        cardStartTrip.setClickable(false);

                                        cardRideCancel.setAlpha(1f);
                                        cardRideCancel.setEnabled(true);
                                        cardRideCancel.setClickable(true);


                                        cardAmbulanceArrived.setAlpha(1f);
                                        cardAmbulanceArrived.setEnabled(true);
                                        cardAmbulanceArrived.setClickable(true);

                                    }

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


                    }


                } else {
                    Log.i("mkl_v", "no  data");

                }


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                HashMap<String, Long> hashMapLong = (HashMap<String, Long>) dataSnapshot.getValue();
                Long isRiding = hashMapLong.get("isRidingNow");
                if (isRiding == 0) {
                    //driver is not on ride
                    Log.i("mkl", "passenger was free");
                    //  onBackPressed();
                    onBackPressed();


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
        cardAmbulanceArrived.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    firebaseDatabase.getReference("ambulance_request").child(rideID).child("hasArrived").setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "Ambulance has arrived", Toast.LENGTH_SHORT).show();

                        }
                    });

                } catch (Exception e) {
                    Toast.makeText(context, "userID  " +userID+"\n"+"rideID  "+rideID, Toast.LENGTH_SHORT).show();
                    Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
        cardStartTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseDatabase.getReference("ambulance_request").child(rideID).child("status").setValue("trip_started");
                firebaseDatabase.getReference("ambulance_request").child(rideID).child("hasTripStarted").setValue(true);
                firebaseDatabase.getReference("ambulance_request").child(rideID).child("trip_started_time").setValue(Calendar.getInstance().getTime().toString());
                firebaseDatabase.getReference("ride_history_driver").child(rideID).child("distance_covered").setValue(0);
                firebaseDatabase.getReference("ride_history_driver").child(rideID).child("distance_covered").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //tv_distance.setText(df2.format( dataSnapshot.getValue().toString()));
                        double distance = Double.parseDouble(dataSnapshot.getValue().toString());
                        DecimalFormat df = new DecimalFormat("#.##");
                        distance = Double.valueOf(df.format(distance));
                        tv_distance.setText("" + distance);
                        //calculate fair

                        // Long fair = BASE_FAIR;
                        FINAL_COST = BASE_FAIR + (FAIR_PER_KM * distance / 1000);
                        tv_fair.setText("" + FINAL_COST);
                        //  tv_total_fair.setText("" + System.currentTimeMillis());
                        firebaseDatabase.getReference("ride_history_driver").child(userID).child(rideID).child("ride_cost").setValue(FINAL_COST);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                retriveLocationAndPost();

            /*    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr=Dhaka&daddr=Mirpur"));
                startActivity(intent);



                Intent intent = getPackageManager().getLaunchIntentForPackage("com.google.android.apps.maps");
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("google.navigation:/?free=1&mode=d&entry=fnls"));
                startActivity(intent);

             */

                String URL = "https://www.google.com/maps/dir/?api=1&travelmode=driving&dir_action=navigate&destination=Mirpur";
                Uri location = Uri.parse(URL);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
              //  startActivity(mapIntent);

            }
        });
    }

    private void downloadOwnDetails() {
        if (sessionManager.getOnlineVehicle() != null && sessionManager.getOnlineVehicle().length() > 0) {
            //1.get vehice id and then vehicle type
            firebaseDatabase.getReference("ambulanceLatestLocations").child(sessionManager.getOnlineVehicle()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.i("latest_loc", dataSnapshot.toString());

                    String vehicleType = dataSnapshot.child("select_vehicle_type").getValue().toString();
                    Log.i("tmkl_", "ve type => " + vehicleType);
                    Log.i("tmkl", dataSnapshot.getValue().toString());
                    //2. get fees by thte vehicle type
                    firebaseDatabase.getReference("ambulance_fee").child(vehicleType).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot fair) {
                            BASE_FAIR = Long.valueOf(fair.child("basefee").getValue().toString());
                            FAIR_PER_KM = Long.valueOf(fair.child("per_km").getValue().toString());
                            Log.i("tmkl_", BASE_FAIR + "==" + FAIR_PER_KM);
                            if (BASE_FAIR != null && FAIR_PER_KM != null) {

                                init_businessLogic();
                                Toast.makeText(context, dataSnapshot.toString(), Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(context, BASE_FAIR + "\n" + FAIR_PER_KM, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


            /*    HashMap<String, Long> hashMap = (HashMap<String, Long>) dataSnapshot.getValue();
                HashMap<String, Integer> hashMapInt = (HashMap<String, Integer>) dataSnapshot.getValue();
                BASE_FAIR = hashMap.get("car_base_charge");
                FAIR_PER_KM = hashMap.get("car_fair_per_km");

             */

                    //  int fair=hashMapInt.get()


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            Log.i("tmkl_", "no ambulance is at service");
        }
    }

    private void showtraveledDistance() {
        firebaseDatabase.getReference("ride_history_driver").child(userID).child(rideID).child("trace").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //  distance += Double.valueOf(dataSnapshot.getValue().toString());
                HashMap<String, Double> hashMap = (HashMap<String, Double>) dataSnapshot.getValue();
                Double d = hashMap.get("traveled");
                distance += d;

                DecimalFormat df = new DecimalFormat("#.##");
                distance = Double.valueOf(df.format(distance));
                tv_distance.setText("" + distance);
                Log.i("ddd", "Travelded => " + d);
                //Toast.makeText(RideOnActivity.this, "ok", Toast.LENGTH_SHORT).show();


                FINAL_COST = BASE_FAIR + (distance * FAIR_PER_KM / 1000);

                FINAL_COST = Double.valueOf(df.format(FINAL_COST));
                tv_fair.setText("" + FINAL_COST);


                firebaseDatabase.getReference("ride_history_driver").child(userID).child(rideID).child("distance_covered").setValue(distance);
                firebaseDatabase.getReference("ride_history_driver").child(userID).child(rideID).child("ride_cost").setValue(FINAL_COST);
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

    private void retriveLocationAndPost() {
        locatiionRetrivedListener.setMyLocationRetriver_(new locatiionRetrivedListener.myLocationRetriver() {
            @Override
            public Location onLocationretrived(Location location, String address) {

                if (active && continueBill) {
                    if (location != null) {

                        if (location.getLatitude() > 0 && location.getLongitude() > 0) {
                            String key = firebaseDatabase.getReference("ride_history_driver").child(userID).child(rideID).child("trace").push().getKey();
                            Double coveredDistance = 0d;
                            if (LAST_KNOWN_LOCATION == null) {
                                firebaseDatabase.getReference("ride_history_driver").child(userID).child(rideID).child("distance_covered").setValue(0);
                                LAST_KNOWN_LOCATION = new LatLng(location.getLatitude(), location.getLongitude());

                            } else {
                                coveredDistance = myfunctions.distance(LAST_KNOWN_LOCATION.latitude, LAST_KNOWN_LOCATION.longitude, location.getLatitude(), location.getLongitude());
                                //coveredDistance=coveredDistance/1000;

                                COVERED_DISTANCE += coveredDistance;
                                // COVERED_DISTANCE += 1;

                                if (firebaseDatabase != null && userID != null && rideID != null && COVERED_DISTANCE != null) {
                                    try {
                                        //  firebaseDatabase.getReference("ride_history_driver").child(USER_ID).child(rideID).child("distance_covered").setValue(COVERED_DISTANCE);
                                    } catch (Exception e) {
                                        Log.i("mkl_error", e.toString());
                                        Toast.makeText(context, "Error Occureed " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        retriveLocationAndPost();


                                    }
                                }

                                LAST_KNOWN_LOCATION = new LatLng(location.getLatitude(), location.getLongitude());

                            }

                            if (coveredDistance > 0) {


                                firebaseDatabase.getReference("ride_history_driver").child(userID).child(rideID).child("trace").child(key).child("traveled").setValue(coveredDistance);
                            }
                            // firebaseDatabase.getReference("ride_history_driver").child(USER_ID).child(rideID).child("trace").child(key).child("lng").setValue(Double.valueOf(location.getLongitude()));
                        }

                    } else {
                        Toast.makeText(context, "Null location", Toast.LENGTH_SHORT).show();
                    }
                }
                return null;
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        active = false;
    }


    @Override
    public void onBackPressed() {
        Toast.makeText(context, "Complete or Cancel the ride first", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onStart() {
        super.onStart();
        rosterMapView.onStart();
        active = true;
    }

    @Override
    protected void onRestart() {

        Toast.makeText(context, "on restart", Toast.LENGTH_SHORT).show();
        super.onRestart();
    }

    @Override
    protected void onResume() {
        Toast.makeText(context, "on restart", Toast.LENGTH_SHORT).show();


        super.onResume();
    }

    public void back(View view) {

    }
}
