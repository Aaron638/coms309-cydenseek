package com.example.wjmas_000.menu;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.support.v4.content.ContextCompat.startActivity;

//SOURCE: https://abhiandroid.com/programming/json

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    ArrayList<String> userNames;
    ArrayList<String> maxPlayers;
    //ArrayList<String> mobileNumbers;
    Context context;

    public CustomAdapter(Context context, ArrayList<String> userNames, ArrayList<String> maxPlayers) {
        this.context = context;
        this.userNames = userNames;
        this.maxPlayers = maxPlayers;
        //this.mobileNumbers = mobileNumbers;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // infalte the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_card_row_game, parent, false);
        MyViewHolder vh = new MyViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        // set the data in items
        holder.username.setText(userNames.get(position));
        holder.maxplay.setText(maxPlayers.get(position));
        //holder.mobileNo.setText(mobileNumbers.get(position));
        // implement setOnClickListener event on item view.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // display a toast with person name on item click
                Toast.makeText(context, userNames.get(position), Toast.LENGTH_SHORT).show();
                //TODO TAKE THE USER TO A CERTAIN GAME
                if (userNames.get(0).equals("There are no games")){
                    return;
                } else {
                    launchGame();
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return userNames.size();
    }

    //launches the game activity
    private void launchGame(){
        Intent intent = new Intent(context, GameActivity.class);
        context.startActivity(intent);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView username, maxplay, email, mobileNo;// init the item view's

        public MyViewHolder(View itemView) {
            super(itemView);

            // get the reference of item view's
            username = (TextView) itemView.findViewById(R.id.username);
            maxplay = (TextView) itemView.findViewById(R.id.maxPlayers);
            //mobileNo = (TextView) itemView.findViewById(R.id.mobileNo);

        }
    }
}
