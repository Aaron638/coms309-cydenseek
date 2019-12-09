package com.example.wjmas_000.menu;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import static com.android.volley.VolleyLog.TAG;


public class PlayerListFragment extends Fragment {

    private TextView u1,u2,u3,u4,u5,u6,u7,u8,u9,u10;
    private TextView s1,s2,s3,s4,s5,s6,s7,s8,s9,s10;
    private TextView[] userTable;
    private RequestQueue mQueue;
    Activity acti;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_player_list, container, false);

        //Games won actually represents user status
        u1 = rootView.findViewById(R.id.et_Username_1);
        s1 = rootView.findViewById(R.id.et_NumGamesWon_1);
        u2 = rootView.findViewById(R.id.et_Username_2);
        s2 = rootView.findViewById(R.id.et_NumGamesWon_2);
        u3 = rootView.findViewById(R.id.et_Username_3);
        s3 = rootView.findViewById(R.id.et_NumGamesWon_3);
        u4 = rootView.findViewById(R.id.et_Username_4);
        s4 = rootView.findViewById(R.id.et_NumGamesWon_4);
        u5 = rootView.findViewById(R.id.et_Username_5);
        s5 = rootView.findViewById(R.id.et_NumGamesWon_5);
        u6 = rootView.findViewById(R.id.et_Username_6);
        s6 = rootView.findViewById(R.id.et_NumGamesWon_6);
        u7 = rootView.findViewById(R.id.et_Username_7);
        s7 = rootView.findViewById(R.id.et_NumGamesWon_7);
        u8 = rootView.findViewById(R.id.et_Username_8);
        s8 = rootView.findViewById(R.id.et_NumGamesWon_8);
        u9 = rootView.findViewById(R.id.et_Username_9);
        s9 = rootView.findViewById(R.id.et_NumGamesWon_9);
        u10 = rootView.findViewById(R.id.et_Username_10);
        s10 = rootView.findViewById(R.id.et_NumGamesWon_10);

        userTable = new TextView[]{u1, s1, u2, s2, u3, s3, u4, s4, u5, s5, u6, s6, u7, s7, u8, s8, u9, s9, u10, s10};

        mQueue = Volley.newRequestQueue(getActivity());

        acti = getActivity();

        Button buttonStart = (Button) rootView.findViewById(R.id.button_startGame);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((GameActivity)acti).sendLatLong();
            }
        });

        Button buttonParse = (Button) rootView.findViewById(R.id.button_parse);
        buttonParse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jsonParse();
            }
        });

        return rootView;
    }

    private void jsonParse() {
        String url = "http://coms-309-vb-1.misc.iastate.edu:8080/game/" + ((GameActivity)getActivity()).getGamesession() + "/users";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                try {
                    JSONArray jsonArray = response.getJSONArray("users");
                    //iterates through all users in the json array
                    for (int i = 0; i < jsonArray.length(); i+=2) {
                        JSONObject user = jsonArray.getJSONObject(i);
                        String username = user.getString("username");
                        boolean hider = user.getBoolean("hider");
                        boolean found = user.getBoolean("found");

                        //if even, place the username, if odd, place found or hiding
                        userTable[i].setText(username);
                        if (found){
                            userTable[i+1].setText("found");
                        } else {
                            userTable[i+1].setText("hiding");
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: error");
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }
}
