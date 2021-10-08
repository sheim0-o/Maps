package com.example.maps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

class MarkerAdapter extends RecyclerView.Adapter<MarkerAdapter.ViewHolder>{

    private ItemListener mListener;
    private final List<Marker> markers;

    MarkerAdapter(List<Marker> markers, ItemListener listener) {
        this.markers = markers;
        mListener = listener;;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bottom_sheet_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MarkerAdapter.ViewHolder holder, int position) {
        holder.setData(markers.get(position));
//        Marker marker = markers.get(position);
//        holder.locView.setText(marker.getLocation().toString());
//        holder.nameView.setText(marker.getName());
    }

    @Override
    public int getItemCount() {
        return markers.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView nameView, locView;
        Marker marker;
        ViewHolder(View view){
            super(view);
            view.setOnClickListener(this);
            nameView = (TextView) view.findViewById(R.id.namePlaceView);
            locView = (TextView) view.findViewById(R.id.locPlaceView);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onItemClick(marker);
            }
        }

        void setData(Marker marker) {
            this.marker = marker;
            nameView.setText(marker.getName());
            locView.setText(marker.getLocation().toString());
        }
    }

    interface ItemListener {
        void onItemClick(Marker marker);
    }

//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        final TextView nameView, locView;
//        ViewHolder(View view){
//            super(view);
//            nameView = (TextView) view.findViewById(R.id.namePlaceView);
//            locView = (TextView) view.findViewById(R.id.locPlaceView);
//        }
//    }
}
