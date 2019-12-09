package com.example.wjmas_000.menu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class FoundPlayerFragment extends Fragment {

    Button foundPlayer;
    TextView myCode;
    EditText playerCode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_found_player, container, false);

        foundPlayer = (Button) rootView.findViewById(R.id.button_player_found);
        myCode = (TextView) rootView.findViewById(R.id.text_players_code);
        playerCode = (EditText) rootView.findViewById(R.id.edit_player_code);

        myCode.setText(((GameActivity)getActivity()).getPlayerCode());

        foundPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputCode = playerCode.getText().toString();
                ((GameActivity)getActivity()).websocketSend("{session: \"" + inputCode + "\"}");
            }
        });

        return rootView;
    }


}
