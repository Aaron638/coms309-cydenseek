package com.example.wjmas_000.menu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.android.volley.VolleyLog.TAG;


public class CreateGameFragment extends Fragment {

    private RequestQueue rq;
    EditText editMaxPlayers, editGPeriod, editDuration;
    int maxPlayers, gperiod, duration = 0;
    String username = "aaron";
    String userSession = "abc-123-xyz" /*"52f0178f-c72d-45f7-854a-5eff850d1ab7"*/;//TODO HARD CODED VALUE FOR NOW
    String gameSession = "abc-123-xyz";

    public Date getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime() {
        currentTime = Calendar.getInstance().getTime();
    }

    Date currentTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_create_game, container, false);

        rq = Volley.newRequestQueue(getActivity());

        editMaxPlayers = (EditText) rootView.findViewById(R.id.edit_maxPlayers);
        //maxPlayers = Integer.parseInt(editMaxPlayers.getText().toString());
        editGPeriod = (EditText) rootView.findViewById(R.id.edit_session);
        //gperiod = Integer.parseInt(editGPeriod.getText().toString());
        editDuration = (EditText) rootView.findViewById(R.id.edit_username);
        //duration = Integer.parseInt(editDuration.getText().toString());

        Button buttonCreateGame = (Button) rootView.findViewById(R.id.button_create_game);
        buttonCreateGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//Press button, if callBackend returns true, launch the game

                setCurrentTime();
                if (callBackend()){
                    //launchGame();
                }
            }
        });

        Button buttonfakeGame = (Button) rootView.findViewById(R.id.button_dev_gameStart);
        buttonfakeGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchGame();
            }
        });


        return rootView;
    }

    //launches the game activity
    private void launchGame(){
        Intent intent = new Intent(getActivity(), GameActivity.class);
        intent.putExtra("GAME_SESSION_ID", gameSession);
        startActivity(intent);
    }


    private boolean callBackend(){

        String url = "http://coms-309-vb-1.misc.iastate.edu:8080/game/new";

        JSONObject response = new JSONObject();
        try {
            response.put("session", "0ac7e5b3-ab02-4b47-a1d3-c7eb642b047c");
            response.put("maxplayers", "10");
            response.put("startTime", "20:00:00");
            response.put("mode", "1");
            response.put("hider", "true");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, response, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                try {
                    gameSession = response.getString("session");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: error");
                error.printStackTrace();
                //mTextViewResult.setText("Error");
            }
        });

        rq.add(request);

        return true;
    }



}
