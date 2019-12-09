package com.example.wjmas_000.menu;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static com.android.volley.VolleyLog.TAG;


public class JoinFragment extends Fragment {

    private RequestQueue mQueue;
    JSONArray gamesJsonArray;
    ArrayList<String> gameUserNames;
    ArrayList<String> gameMaxPlayers;
    RecyclerView recyclerView;
    String username;
    String userSession;
    TextView noGames;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_join, container, false);

        username = ((MenuActivity) getActivity()).getUsername();
        userSession = ((MenuActivity) getActivity()).getSession();

        gameUserNames = new ArrayList<>();
        gameMaxPlayers = new ArrayList();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        noGames = (TextView) rootView.findViewById(R.id.text_noGames);
        //areGames = noGames.getText().toString();

        Button buttonRefresh = (Button) rootView.findViewById(R.id.button_refresh);
        mQueue = Volley.newRequestQueue(getActivity());

        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callBackend();
            }


        });


        return rootView;
    }

    private void callBackend() {

        String urlGames = "http://coms-309-vb-1.misc.iastate.edu:8080/games/";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlGames, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                try {
                    gamesJsonArray = response.getJSONArray("games");

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
        mQueue.add(request);


    }


}
