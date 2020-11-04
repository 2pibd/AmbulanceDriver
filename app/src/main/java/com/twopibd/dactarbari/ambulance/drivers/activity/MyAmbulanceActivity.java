package com.twopibd.dactarbari.ambulance.drivers.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.twopibd.dactarbari.ambulance.drivers.R;
import com.twopibd.dactarbari.ambulance.drivers.adapters.DriverEarningsAdapter;
import com.twopibd.dactarbari.ambulance.drivers.adapters.MyAmbulanceAdapter;
import com.twopibd.dactarbari.ambulance.drivers.listeners.PicUploadListener;
import com.twopibd.dactarbari.ambulance.drivers.model.AmbulanceModel;
import com.twopibd.dactarbari.ambulance.drivers.utils.SessionManager;
import com.twopibd.dactarbari.ambulance.drivers.utils.myfunctions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyAmbulanceActivity extends AppCompatActivity {
    @BindView(R.id.spinnerAmbulanceTypes)
    Spinner spinnerAmbulanceTypes;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    @BindView(R.id.ed_brandname)
    EditText ed_brandname;
    @BindView(R.id.tv_add_ambulance_tv)
    TextView tv_add_ambulance_tv;

    @BindView(R.id.ed_modelnumber)
    EditText ed_modelnumber;

    @BindView(R.id.ed_licensenumber)
    EditText ed_licensenumber;

    @BindView(R.id.ed_modelyear)
    EditText ed_modelyear;
    @BindView(R.id.ed_certificate_fitness)
    EditText ed_certificate_fitness;
    @BindView(R.id.ed_registration)
    EditText ed_registration;
    @BindView(R.id.ed_taxToken)
    EditText ed_taxToken;
    @BindView(R.id.img_ambulance)
    ImageView img_ambulance;

    String select_vehicle_type;

    @BindView(R.id.signUpDrawer)
    LinearLayout signUpDrawer;

    boolean ifBottomDrawerOpen = false;
    Context context = this;

    FirebaseDatabase firebaseDatabase;
    List<AmbulanceModel> ambulanceModelList = new ArrayList<>();
    Uri delectedPhotoLink;

    MyAmbulanceAdapter mAdapter;
    BottomSheetBehavior bottomSheetBehavior;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ambulance);
        ButterKnife.bind(this);
        sessionManager = new SessionManager(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        // signUpDrawer.animate().translationY(500);
        initializeSpinner();
        initRecycler();
        firebaseDatabase.getReference("driver").child(FirebaseAuth.getInstance().getUid()).child("ambulance").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {

                    firebaseDatabase.getReference("ambulanceLatestLocations").child(dataSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot ambulanceBody1) {
                            if (ambulanceBody1.exists()) {
                                AmbulanceModel ambulanceModel = ambulanceBody1.getValue(AmbulanceModel.class);
                                ambulanceModelList.add(ambulanceModel);
                              if (mAdapter!=null&&ambulanceModelList.size()>0) mAdapter.notifyItemInserted(ambulanceModelList.size() - 1);
                                if (ambulanceModel != null && ambulanceModel.getIsOnService() != null && ambulanceModel.getIsOnService() == 1) {
                                    sessionManager.setOnlineVehicle(ambulanceModel.getAmbulanceID());
                                }
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }


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

    @OnClick(R.id.tv_add_ambulance_tv)
    public void openCreateAmbulanceBottomSheet() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

    }

    @OnClick(R.id.img_ambulance)
    public void openImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(MyAmbulanceActivity.this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Glide.with(getBaseContext()).load(resultUri).into(img_ambulance);
                delectedPhotoLink = result.getUri();


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void initRecycler() {

        mAdapter = new MyAmbulanceAdapter(getBaseContext(), ambulanceModelList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getBaseContext(), RecyclerView.VERTICAL, false);
        recyclerview.setLayoutManager(mLayoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        // recyclerview.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerview.addItemDecoration(new DividerItemDecoration(getBaseContext(), LinearLayoutManager.VERTICAL));
        recyclerview.setAdapter(mAdapter);
        mAdapter.setAmbulanceClickListener(new MyAmbulanceAdapter.AmbulanceClickListener() {
            @Override
            public void OnClicked(int i) {
                if (i == -1) {
                    for (int k = 0; k < ambulanceModelList.size(); k++) {
                        ambulanceModelList.get(k).setIsOnService(0l);
                        mAdapter.notifyDataSetChanged();
                    }
                } else {

                    for (int k = 0; k < ambulanceModelList.size(); k++) {
                        ambulanceModelList.get(k).setIsOnService(0l);
                    }
                    ambulanceModelList.get(i).setIsOnService(1l);
                    mAdapter.notifyDataSetChanged();
                    sessionManager.setOnlineVehicle(ambulanceModelList.get(i).getAmbulanceID());
                }

            }
        });

    }

    private void downloadAmbulanceList() {
    }


    private void initializeSpinner() {
        List<String> AmbulanceTypes = new ArrayList<String>();
        AmbulanceTypes.add("Select");
        firebaseDatabase.getReference("ambulance_fee").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Log.i("mkl", child.getKey());
                    AmbulanceTypes.add(child.getKey());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, AmbulanceTypes);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAmbulanceTypes.setAdapter(dataAdapter);
        spinnerAmbulanceTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    select_vehicle_type = AmbulanceTypes.get(position);
                } else {

                    select_vehicle_type = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    public void CreateAmbulance(View view) {
        //  FirebaseDatabase.getInstance().r
        String brandName = ed_brandname.getText().toString().trim();
        String modelName = ed_modelnumber.getText().toString().trim();
        String licenseNumber = ed_licensenumber.getText().toString().trim();
        String modelyear = ed_modelyear.getText().toString().trim();
        String certificateFitness = ed_certificate_fitness.getText().toString().trim();
        String registrationNumber = ed_registration.getText().toString().trim();
        String taxToken = ed_taxToken.getText().toString().trim();

        if (brandName.length() > 0) {
            if (modelName.length() > 0) {
                if (licenseNumber.length() > 0) {
                    if (select_vehicle_type != null) {
                        if (certificateFitness.length() > 0) {
                            if (registrationNumber.length() > 0) {
                                if (taxToken.length() > 0) {
                                    if (delectedPhotoLink != null) {

                                        myfunctions.uploadPhoto(delectedPhotoLink, context, new PicUploadListener.myPicUploadListener() {
                                            @Override
                                            public String onPicUploadSucced(String link) {
                                                String key = firebaseDatabase.getReference("ambulanceList").push().getKey();
                                                // firebaseDatabase.getReference("ambulanceList").child(key).child("owner_id").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                firebaseDatabase.getReference("driver").child(FirebaseAuth.getInstance().getUid()).child("ambulance").child(key).setValue(key);
                                                firebaseDatabase.getReference("ambulanceLatestLocations").child(key).child("owner_id").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                firebaseDatabase.getReference("ambulanceLatestLocations").child(key).child("brandName").setValue(brandName);
                                                firebaseDatabase.getReference("ambulanceLatestLocations").child(key).child("modelName").setValue(modelName);
                                                firebaseDatabase.getReference("ambulanceLatestLocations").child(key).child("licenseNumber").setValue(licenseNumber);
                                                firebaseDatabase.getReference("ambulanceLatestLocations").child(key).child("photo").setValue(link);

                                                firebaseDatabase.getReference("ambulanceLatestLocations").child(key).child("select_vehicle_type").setValue(select_vehicle_type);
                                                firebaseDatabase.getReference("ambulanceLatestLocations").child(key).child("modelyear").setValue(modelyear);
                                                firebaseDatabase.getReference("ambulanceLatestLocations").child(key).child("isOnService").setValue(0);
                                                firebaseDatabase.getReference("ambulanceLatestLocations").child(key).child("ambulanceID").setValue(key);

                                                firebaseDatabase.getReference("ambulanceLatestLocations").child(key).child("vehicle").child("certificate_fitness").setValue(certificateFitness);
                                                firebaseDatabase.getReference("ambulanceLatestLocations").child(key).child("vehicle").child("registration").setValue(registrationNumber);
                                                firebaseDatabase.getReference("ambulanceLatestLocations").child(key).child("vehicle").child("tax_token").setValue(taxToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(MyAmbulanceActivity.this, "Successfully Added", Toast.LENGTH_SHORT).show();
                                                    }
                                                });


                                                return null;
                                            }

                                            @Override
                                            public String onPicUploadFailed(String errorMessage) {

                                                return null;
                                            }
                                        });


                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void uploadPhoto(Uri delectedPhotoLink) {

    }

    public void openAddAmbulanceView(View view) {
        // setCustomAnimationRight(signUpDrawer);
        signUpDrawer.animate().translationY(0);
    }

    public void back(View view) {
        onBackPressed();
    }
}
