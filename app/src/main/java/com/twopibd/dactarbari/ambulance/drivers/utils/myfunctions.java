package com.twopibd.dactarbari.ambulance.drivers.utils;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.twopibd.dactarbari.ambulance.drivers.R;
import com.twopibd.dactarbari.ambulance.drivers.listeners.MapPermissionsListener;
import com.twopibd.dactarbari.ambulance.drivers.listeners.PicUploadListener;

import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class myfunctions {

    public  int sum(int a,int b){
        return  a+b;
    }

    public static void uploadPhoto(Uri resultUri, Context context, PicUploadListener.myPicUploadListener listener) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();


        final ProgressDialog progressDialog = new ProgressDialog(context);
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
                                Toast.makeText(context, uri.toString(), Toast.LENGTH_SHORT).show();
                                listener.onPicUploadSucced(uri.toString());



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
                        listener.onPicUploadFailed( e.getMessage());
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


    public static void showDriverProfile(Context context, ImageView imageView) {
        FirebaseDatabase.getInstance().getReferenceFromUrl("https://dactarbari-ambulance.firebaseio.com/driver/" + FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //  Toast.makeText(context, dataSnapshot.child("photo").getValue().toString(), Toast.LENGTH_SHORT).show();

               if (dataSnapshot.exists()&&dataSnapshot.child("photo").exists()) {
                   Glide.with(context).load(dataSnapshot.child("photo").getValue().toString()).transform(new CircleCrop()).into(imageView);
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public static void showDriverProfileAndLicenseNid(Context context, ImageView imageView, ImageView license, ImageView img_nid) {
        FirebaseDatabase.getInstance().getReferenceFromUrl("https://dactarbari-ambulance.firebaseio.com/driver/" + FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //  Toast.makeText(context, dataSnapshot.child("photo").getValue().toString(), Toast.LENGTH_SHORT).show();

                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("photo").exists()) Glide.with(context).load(dataSnapshot.child("photo").getValue().toString()).transform(new CircleCrop()).into(imageView);
                    if ( dataSnapshot.child("driving_license").exists()) {
                        Glide.with(context).load(dataSnapshot.child("driving_license").getValue().toString()).into(license);

                    }
                    if (dataSnapshot.child("nid").exists()) {
                        Glide.with(context).load(dataSnapshot.child("nid").getValue().toString()).into(img_nid);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515 * 1000;
        return (dist);
    }

    public static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    public static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public static void initMapShow(final Activity context, final OnMapReadyCallback onMapReadyCallback, final FragmentManager supportFragmentManager) {

        Dexter.withActivity(context)
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Log.i("mkl", "Permitted");

                            // do you work now
                            SupportMapFragment mapFragment = (SupportMapFragment) supportFragmentManager
                                    .findFragmentById(R.id.map);
                            mapFragment.getMapAsync(onMapReadyCallback);
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // permission is denied permenantly, navigate user to app settings
                            Log.i("mkl", "denied");

                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();


    }
    public static void initializeLocationPermissions(final Activity context, MapPermissionsListener.myMapPermissionListener listener) {

        Dexter.withActivity(context)
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Log.i("mkl", "Permitted");

                            // do you work now
                            listener.onPermissionGivenSucced();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // permission is denied permenantly, navigate user to app settings
                            Log.i("mkl", "denied");
                            listener.onPermissionGivenFailed();

                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();


    }
}
