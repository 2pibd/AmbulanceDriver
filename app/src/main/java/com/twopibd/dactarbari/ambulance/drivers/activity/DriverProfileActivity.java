package com.twopibd.dactarbari.ambulance.drivers.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.twopibd.dactarbari.ambulance.drivers.R;

import java.util.UUID;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.twopibd.dactarbari.ambulance.drivers.utils.myfunctions.showDriverProfile;
import static com.twopibd.dactarbari.ambulance.drivers.utils.myfunctions.showDriverProfileAndLicenseNid;

public class DriverProfileActivity extends AppCompatActivity {
    @BindView(R.id.img_profile)
    ImageView img_profile;
    @BindView(R.id.img_nid)
    ImageView img_nid;
    @BindView(R.id.img_driving_licence)
    ImageView img_driving_licence;
    Context context = this;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference();
    String CURRENTLY_PICKING_PHOTO = "";
    @BindView(R.id.ed_display_name)
    EditText ed_display_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_profile);

        ButterKnife.bind(this);
        ed_display_name.clearFocus();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://dactarbari-ambulance.firebaseio.com/driver/" + FirebaseAuth.getInstance().getUid() + "/name");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ed_display_name.setText(dataSnapshot.getValue().toString());
                    ed_display_name.clearFocus();
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        ed_display_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String name = s.toString();
                ref.setValue(name);


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        img_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CURRENTLY_PICKING_PHOTO = "driver_photo";
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(DriverProfileActivity.this);
            }
        });
        img_driving_licence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CURRENTLY_PICKING_PHOTO = "driver_licence";
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(DriverProfileActivity.this);
            }
        });
        img_nid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CURRENTLY_PICKING_PHOTO = "nid";
                CropImage.activity()

                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(DriverProfileActivity.this);
            }
        });
        showDriverProfileAndLicenseNid(context, img_profile, img_driving_licence, img_nid);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                if (CURRENTLY_PICKING_PHOTO.equals("driver_photo")) {

                    Glide.with(context).load(resultUri).transform(new CircleCrop()).into(img_profile);
                    uploadPhoto(resultUri, "photo");
                } else if (CURRENTLY_PICKING_PHOTO.equals("driver_licence")) {

                    uploadPhoto(resultUri, "driving_license");
                } else if (CURRENTLY_PICKING_PHOTO.equals("nid")) {

                    uploadPhoto(resultUri, "nid");
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void uploadPhoto(Uri resultUri, String path) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Image Uploading...");
        progressDialog.show();
        StorageReference ref;
        ref = storageReference.child("images/" + UUID.randomUUID().toString());


        ref.putFile(resultUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(context, ref.getPath(), Toast.LENGTH_SHORT).show();
                        // Glide.with(context).load(resultUri).into(imgSetQuestionAnswer2);
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // FIRST_UPLOADED_IMAGE_LINK = uri.toString();
                                //upload second  image
                                progressDialog.dismiss();

                                // firebaseDatabase.getReferenceFromUrl()
                                Toast.makeText(context, "Uplaod done", Toast.LENGTH_SHORT).show();
                                Log.i("mkl", uri.toString());
                                FirebaseDatabase.getInstance().getReferenceFromUrl("https://dactarbari-ambulance.firebaseio.com/driver/" + FirebaseAuth.getInstance().getUid()).child(path).setValue(uri.toString());
                                if (path.equals("photo")) {
                                    Glide.with(context).load(uri.toString()).transform(new CircleCrop()).into(img_profile);
                                } else if (path.equals("driving_license")) {
                                    Glide.with(context).load(uri.toString()).into(img_driving_licence);

                                } else if (path.equals("nid")) {
                                    Glide.with(context).load(uri.toString()).into(img_nid);

                                }
                            }
                        });

                        //uploadData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(context, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int) progress + "%");
                    }
                });
    }

    public void back(View view) {
        onBackPressed();
    }
}
