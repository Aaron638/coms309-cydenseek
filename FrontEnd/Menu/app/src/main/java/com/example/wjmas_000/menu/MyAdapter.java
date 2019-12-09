package com.example.wjmas_000.menu;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private final ClickListener listener;
    private  final List<MyItems> itemsList;

    public MyAdapter(List<MyItems> itemsList, ClickListener listener){
        this.listener = listener;
        this.itemsList = itemsList;
    }

    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.my_row_layout), parent, false), listener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
