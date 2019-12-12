package com.example.wjmas_000.menu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.android.volley.VolleyLog.TAG;


public class LeaderboardFragment extends Fragment {


    private RequestQueue mQueue;
    private int error = 0;      //Testing and error detection
    private List<LeaderboardPlayer> list = new ArrayList<LeaderboardPlayer>();

    //Views of username and scores
    private TextView u1;
    private TextView s1;

    private TextView u2;
    private TextView s2;

    private TextView u3;
    private TextView s3;

    private TextView u4;
    private TextView s4;

    private TextView u5;
    private TextView s5;

    private TextView u6;
    private TextView s6;

    private TextView u7;
    private TextView s7;

    private TextView u8;
    private TextView s8;

    private TextView u9;
    private TextView s9;

    private TextView u10;
    private TextView s10;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_leaderboard, container, false);
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



        mQueue = Volley.newRequestQueue(getActivity());
        jsonParseLeader();

        return rootView;

    }


    public int jsonParseLeader(){
        String url = "http://coms-309-vb-1.misc.iastate.edu:8080/leaderboard";
        //maybe pass json array in the future


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                try {
                    JSONArray jsonArray = response.getJSONArray("users");
                    //iterates through all users in the json array
                    for (int i =0; i < jsonArray.length(); i++){
                        JSONObject user = jsonArray.getJSONObject(i);

                        //we get all user attributes here
                        int gpseeker = user.getInt("gpseeker");         //Games played as seeker
                        int totdistance = user.getInt("totdistance");   //Total distance walked
                        int tottime = user.getInt("tottime");           //Total time played
                        int gphider = user.getInt("gphider");
                        int gwhider = user.getInt("gwhider");
                        String username = user.getString("username");   //Username
                        int gwseeker = user.getInt("gwseeker");         //Games won as seeker

                        LeaderboardPlayer p = new LeaderboardPlayer(gwhider, gwseeker, username);
                        p.setLeaderUsername(username);
                        list.add(p);


                        error = 0;

                    }
                    display();
                } catch (JSONException e) {
                    e.printStackTrace();
                    error = -1;

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
        return error;
    }

    private void display(){
        List<LeaderboardPlayer> theBest = new ArrayList<LeaderboardPlayer>();

        int initial_size = list.size();
        for(int i = 0; i < 10 && i < initial_size; i++){
            LeaderboardPlayer hold = new LeaderboardPlayer(-1, -1, "test");
            for(int j = 0; j < list.size(); j++){
                if((hold.GamesWonSeeker + hold.GamesWonHider) < (list.get(j).GamesWonHider + list.get(j).GamesWonSeeker)){
                    hold = list.get(j);

                }
            }
            theBest.add(hold);
            list.remove(hold);

        }

        if(theBest.size() > 0) {
            u1.setText(theBest.get(0).Username);
            s1.setText(Integer.toString(theBest.get(0).GamesWonSeeker + theBest.get(0).GamesWonHider));
        }

        if(theBest.size() > 1) {
            u2.setText(theBest.get(1).Username);
            s2.setText(Integer.toString(theBest.get(1).GamesWonSeeker + theBest.get(1).GamesWonHider));
        }

        if(theBest.size() > 2) {
            u3.setText(theBest.get(2).Username);
            s3.setText(Integer.toString(theBest.get(2).GamesWonSeeker + theBest.get(2).GamesWonHider));
        }

        if(theBest.size() > 3) {
            u4.setText(theBest.get(3).Username);
            s4.setText(Integer.toString(theBest.get(3).GamesWonSeeker + theBest.get(3).GamesWonHider));
        }

        if(theBest.size() > 4) {
            u5.setText(theBest.get(4).Username);
            s5.setText(Integer.toString(theBest.get(4).GamesWonSeeker + theBest.get(4).GamesWonHider));
        }

        if(theBest.size() > 5) {
            u6.setText(theBest.get(5).Username);
            s6.setText(Integer.toString(theBest.get(5).GamesWonSeeker + theBest.get(5).GamesWonHider));
        }

        if(theBest.size() > 6) {
            u7.setText(theBest.get(6).Username);
            s7.setText(Integer.toString(theBest.get(6).GamesWonSeeker + theBest.get(6).GamesWonHider));
        }

        if(theBest.size() > 7) {
            u8.setText(theBest.get(7).Username);
            s8.setText(Integer.toString(theBest.get(7).GamesWonSeeker + theBest.get(7).GamesWonHider));
        }

        if(theBest.size() > 8) {
            u9.setText(theBest.get(8).Username);
            s9.setText(Integer.toString(theBest.get(8).GamesWonSeeker + theBest.get(8).GamesWonHider));
        }

        if(theBest.size() > 9) {
            u10.setText(theBest.get(9).Username);
            s10.setText(Integer.toString(theBest.get(9).GamesWonSeeker + theBest.get(9).GamesWonHider));
        }

    }


}
