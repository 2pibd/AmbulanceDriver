package com.twopibd.dactarbari.ambulance.drivers.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.twopibd.dactarbari.ambulance.drivers.R;
import com.twopibd.dactarbari.ambulance.drivers.model.AmbulanceModel;
import com.twopibd.dactarbari.ambulance.drivers.model.TripHistory;
import com.twopibd.dactarbari.ambulance.drivers.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MyAmbulanceAdapter extends RecyclerView.Adapter<MyAmbulanceAdapter.MyViewHolder> {
    private Context context;
    private List<AmbulanceModel> listFiltered = new ArrayList<>();

    public interface AmbulanceClickListener {
        void OnClicked(int i);
    }

    public AmbulanceClickListener ambulanceClickListener;

    public void setAmbulanceClickListener(AmbulanceClickListener listener) {
        this.ambulanceClickListener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_model, tv_licence, tv_brand;
        ImageView img;
        Switch radio;


        public MyViewHolder(View view) {
            super(view);
            //  tv_name = view.findViewById(R.id.tv_name);
            tv_model = view.findViewById(R.id.tv_model);
            tv_licence = view.findViewById(R.id.tv_licence);
            tv_brand = view.findViewById(R.id.tv_brand);
            radio = view.findViewById(R.id.radio);
            img = view.findViewById(R.id.img);


        }
    }


    public MyAmbulanceAdapter(Context context, List<AmbulanceModel> list_) {
        this.context = context;
        this.listFiltered = list_;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_ambulane_item, parent, false);

        return new MyViewHolder(itemView);
    }

    //https://gl-images.condecdn.net/image/lN39xbMKeop/crop/405/f/Gal-Gadot-1.jpg
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        try {
            SessionManager sessionManager = new SessionManager(holder.itemView.getContext());
            final AmbulanceModel data = listFiltered.get(position);


            holder.tv_model.setText(data.getModelName());
            holder.tv_brand.setText(data.getBrandName());
            holder.tv_licence.setText(data.getLicenseNumber());
            Context context=holder.itemView.getContext();
            Glide.with(context).load(data.getPhoto()).into(holder.img);
            if (data.getIsOnService() == 1) {
                holder.radio.setChecked(true);
            } else {
                holder.radio.setChecked(false);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (data.getIsOnService() == 1) {
                        for (int i = 0; i < listFiltered.size(); i++) {
                            FirebaseDatabase.getInstance().getReference("ambulanceLatestLocations").child(listFiltered.get(i).getAmbulanceID()).child("isOnService").setValue(0);
                            listFiltered.get(i).setIsOnService(0l);

                        }
                        listFiltered.get(position).setIsOnService(0l);
                        ambulanceClickListener.OnClicked(-1);
                    } else {
                        sessionManager.setOnlineVehicle(data.getAmbulanceID());
                        for (int i = 0; i < listFiltered.size(); i++) {
                            FirebaseDatabase.getInstance().getReference("ambulanceLatestLocations").child(listFiltered.get(i).getAmbulanceID()).child("isOnService").setValue(0);
                            listFiltered.get(i).setIsOnService(0l);

                        }
                        listFiltered.get(position).setIsOnService(0l);

                        FirebaseDatabase.getInstance().getReference("ambulanceLatestLocations").child(data.getAmbulanceID()).child("isOnService").setValue(1).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                ambulanceClickListener.OnClicked(position);
                            }
                        });
                    }

                }
            });
        } catch (Exception e) {

        }


    }


    public static String getDate(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss EEE dd MMM yy");
        return formatter.format(new Date(milliSeconds));
    }

    @Override
    public int getItemCount() {
        return listFiltered.size();

    }


}