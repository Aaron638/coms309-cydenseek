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
    String username = "userboi";
    String userSession = "abc-123-xyz";//TODO HARD CODED VALUE FOR NOW

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
        editGPeriod = (EditText) rootView.findViewById(R.id.edit_gperiod);
        //gperiod = Integer.parseInt(editGPeriod.getText().toString());
        editDuration = (EditText) rootView.findViewById(R.id.edit_duration);
        //duration = Integer.parseInt(editDuration.getText().toString());

        Button buttonCreateGame = (Button) rootView.findViewById(R.id.button_create_game);
        buttonCreateGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//Press button, if callBackend returns true, launch the game

                setCurrentTime();
                if (callBackend()){
                    launchGame();
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
        startActivity(intent);
    }


    private boolean callBackend(){

        String url = "http://coms-309-vb-1.misc.iastate.edu:8080/game/new";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: error");
                error.printStackTrace();
                //mTextViewResult.setText("Error");
            }
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String>  params = new HashMap<String, String>();
                params.put("session", "fa2adf23-8ba7-49c3-9214-83dd44810cfa");
                params.put("radius", "10");     //hard coded for now
                params.put("maxplayers", editMaxPlayers.getText().toString());
                params.put("startTime", "20:00:00"/*getCurrentTime().toString()*/);       //TODO this is probably incorrect, maybe should just be hard coded on backend
                //params.put("duration", editDuration.getText().toString());
                params.put("mode", "0");
                //params.put("gperiod", editGPeriod.getText().toString());
                params.put("creator", username);
                params.put("hider", "true");

                return params;
            }
        };
        rq.add(request);

        return true;
    }



}
