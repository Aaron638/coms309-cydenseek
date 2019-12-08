package com.example.wjmas_000.menu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class LobbyFragment extends Fragment {

    private TextView player1;
    private TextView player2;
    private TextView player3;
    private TextView player4;
    private TextView player5;
    private TextView player6;
    private TextView player7;
    private TextView player8;
    private TextView player9;
    private TextView player10;

    private Button leave;
    private Button swap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_statistics, container, false);

        //All of the views for the lobby
        player1 = rootView.findViewById(R.id.tv_player1);
        player2 = rootView.findViewById(R.id.tv_player2);
        player3 = rootView.findViewById(R.id.tv_player3);
        player4 = rootView.findViewById(R.id.tv_player4);
        player5 = rootView.findViewById(R.id.tv_player5);
        player6 = rootView.findViewById(R.id.tv_player6);
        player7 = rootView.findViewById(R.id.tv_player7);
        player8 = rootView.findViewById(R.id.tv_player8);
        player9 = rootView.findViewById(R.id.tv_player9);
        player10 = rootView.findViewById(R.id.tv_player10);

        //The buttons
        leave = rootView.findViewById(R.id.btn_lobby_leave);
        swap = rootView.findViewById(R.id.btn_lobby_swap);

        swap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                swap(0);
            }
        });

        leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leave(0);
            }
        });



        return inflater.inflate(R.layout.fragment_lobby, container, false);
    }

    public void swap(int playerNumber){

    }

    public void leave(int playerNumber){

    }
}
