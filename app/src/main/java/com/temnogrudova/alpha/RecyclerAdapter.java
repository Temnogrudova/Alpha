package com.temnogrudova.alpha;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<Item> mDataArrayList;
    View view;
    Context context;

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        context = recyclerView.getContext();
    }

    public RecyclerAdapter( ArrayList<Item> DataArrayList) {
        mDataArrayList = DataArrayList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        view = LayoutInflater.from(context).inflate(R.layout.pattern, parent, false);
        return ItemViewHolder.newInstance(view, context);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        ItemViewHolder holder = (ItemViewHolder) viewHolder;

        String url = mDataArrayList.get(position).avatar;
        String name= mDataArrayList.get(position).name;
        String text= mDataArrayList.get(position).text;
        String time= mDataArrayList.get(position).time;
        holder.setItemText(url,text,time,name);


    }

    @Override
    public int getItemCount() {
        return mDataArrayList == null ? 0 : mDataArrayList.size();
    }

}
