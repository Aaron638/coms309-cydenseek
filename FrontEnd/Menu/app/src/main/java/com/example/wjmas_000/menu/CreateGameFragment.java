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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.lang.reflect.Method;

import static com.android.volley.VolleyLog.TAG;


public class CreateGameFragment extends Fragment {

    private RequestQueue rq;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        View rootView = inflater.inflate(R.layout.fragment_create_game, container, false);

        rq = Volley.newRequestQueue(getActivity());

        Button buttonCreateGame = (Button) rootView.findViewById(R.id.button_create_game);
        buttonCreateGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//Press button, if callBackend returns true, launch the game
                if (callBackend()){
                    launchGame();
                }
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

        return false;
    }



}
