package com.decalthon.helmet.stability.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.decalthon.helmet.stability.R;

public class SpinnerAdapter extends RecyclerView.Adapter<SpinnerAdapter.SpinnerViewHolder> {

    public static class SpinnerViewHolder extends  RecyclerView.ViewHolder{
        public TextView textView;
        public SpinnerViewHolder(@NonNull TextView tv) {
            super(tv);
            textView = tv;
        }

    }
    @NonNull
    @Override
    public SpinnerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView textView = (TextView) LayoutInflater.from(parent.getContext()).inflate
                (R.layout.list_item_view,parent,false);
        SpinnerViewHolder spinnerViewHolder = new SpinnerViewHolder(textView);
        return spinnerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SpinnerViewHolder holder, int position) {
        int[] mDataset = {0,1,2,3,4,5,6,7,8,9,10};
        holder.textView.setText(mDataset[position]);
    }

    @Override
    public int getItemCount() {
        return 10;
    }


}
