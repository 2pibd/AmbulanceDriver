package com.twopibd.dactarbari.ambulance.drivers.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.twopibd.dactarbari.ambulance.drivers.R;
import com.twopibd.dactarbari.ambulance.drivers.model.AmbulanceModel;
import com.twopibd.dactarbari.ambulance.drivers.model.FeesModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class FareChartAdapter extends RecyclerView.Adapter<FareChartAdapter.MyViewHolder> {
    private Context context;
    private List<FeesModel> listFiltered = new ArrayList<>();


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_perKm, tv_baseFare,tv_type;
        ImageView img_status;


        public MyViewHolder(View view) {
            super(view);
            //  tv_name = view.findViewById(R.id.tv_name);
            tv_perKm = view.findViewById(R.id.tv_perKm);
            tv_baseFare = view.findViewById(R.id.tv_baseFare);
            tv_type = view.findViewById(R.id.tv_type);



        }
    }


    public FareChartAdapter(Context context, List<FeesModel> list_) {
        this.context = context;
        this.listFiltered = list_;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fees_item_model, parent, false);

        return new MyViewHolder(itemView);
    }

    //https://gl-images.condecdn.net/image/lN39xbMKeop/crop/405/f/Gal-Gadot-1.jpg
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final FeesModel data = listFiltered.get(position);
        holder.tv_baseFare.setText("BDT "+data.getBasefee());
        holder.tv_perKm.setText("BDT "+data.getPerKm());
        holder.tv_type.setText(data.getType());



        //holder.tv_licence.setText(data.getLicenseNumber());

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