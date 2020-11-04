package com.twopibd.dactarbari.ambulance.drivers.adapters;


import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.twopibd.dactarbari.ambulance.drivers.R;
import com.twopibd.dactarbari.ambulance.drivers.model.TripHistory;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class DriverEarningsAdapter extends RecyclerView.Adapter<DriverEarningsAdapter.MyViewHolder> {
    private Context context;
    private List<TripHistory> listFiltered = new ArrayList<>();


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_to, tv_from,tv_fair;
        ImageView img_status;


        public MyViewHolder(View view) {
            super(view);
            //  tv_name = view.findViewById(R.id.tv_name);
            tv_to = view.findViewById(R.id.tv_to);
            tv_from = view.findViewById(R.id.tv_from);
            tv_fair = view.findViewById(R.id.tv_fair);
            img_status = view.findViewById(R.id.img_status);


        }
    }


    public DriverEarningsAdapter(Context context, List<TripHistory> list_) {
        this.context = context;
        this.listFiltered = list_;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trip_history_item, parent, false);

        return new MyViewHolder(itemView);
    }


    //https://gl-images.condecdn.net/image/lN39xbMKeop/crop/405/f/Gal-Gadot-1.jpg
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final TripHistory data = listFiltered.get(position);
        if (data.getData().getHasRideCompleated()) {
             Glide.with(holder.itemView.getContext()).load(R.drawable.check_mark_green).into(holder.img_status);
        }else {
            Glide.with(holder.itemView.getContext()).load(R.drawable.missed_call).into(holder.img_status);
            holder.tv_fair.setText(""+data.getData().getEstimated());
        }

        holder.tv_from.setText(data.getData().getFrom());
        holder.tv_to.setText(data.getData().getTo());


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