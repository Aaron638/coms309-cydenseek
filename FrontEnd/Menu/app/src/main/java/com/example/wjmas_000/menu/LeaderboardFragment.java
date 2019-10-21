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

import static com.android.volley.VolleyLog.TAG;


public class LeaderboardFragment extends Fragment {

    private TextView mTextViewResult;
    private RequestQueue mQueue;
    private int error = 0;      //Testing and error detection

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        mTextViewResult = (TextView) rootView.findViewById(R.id.tvResult);
        mTextViewResult.setText("Refresh");
        mTextViewResult.setMovementMethod(new ScrollingMovementMethod());

        Button buttonParse = (Button) rootView.findViewById(R.id.btnLookup);

        //usually use "this"
        mQueue = Volley.newRequestQueue(getActivity());

        buttonParse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTextViewResult.setText("");
                jsonParse1();
            }
        });


        //return inflater.inflate(R.layout.fragment_player_list, container, false);
        //trying this instead
        return rootView;





        //return inflater.inflate(R.layout.fragment_leaderboard, container, false);
    }


    //Returns 0 if successful, -1 if error occurs in connection

    public int jsonParse1(){
        String url = "https://api.myjson.com/bins/14tcp8";
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



                        String result = "User:"+ username +
                                "\nTotal Time Played:"+ tottime +
                                "\nTotal distance walked:"+ totdistance +
                                "\nGames played as seeker:"+ gpseeker +
                                "\nGames won as seeker:"+ gwseeker +
                                "\nGames played as hider:"+ gphider +
                                "\nGames won as hider:"+ gwhider +
                                "\n\n";
                        mTextViewResult.append(result);
                        error = 0;

                    }
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
                mTextViewResult.setText("Error");
            }
        });

        mQueue.add(request);
        return error;
    }




        /*
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                try {
                    JSONArray jsonArray = response.getJSONArray("users");
                    //iterates through all users in the json array
                    for (int i =0; i < jsonArray.length(); i++){
                        JSONObject user = jsonArray.getJSONObject(i);
                        int gpseeker = user.getInt("gpseeker");         //Games played as seeker
                        int totdistance = user.getInt("totdistance");   //Total distance walked
                        int tottime = user.getInt("tottime");           //Total time played
                        int gphider = user.getInt("gphider");
                        int gwhider = user.getInt("gwhider");
                        String username = user.getString("username");   //Username
                        int gwseeker = user.getInt("gwseeker");         //Games won as seeker



                        String result = "User:"+ username +
                                "\nTotal Time Played:"+ tottime +
                                "\nTotal distance walked:"+ totdistance +
                                "\nGames played as seeker:"+ gpseeker +
                                "\nGames won as seeker:"+ gwseeker +
                                "\nGames played as hider:"+ gphider +
                                "\nGames won as hider:"+ gwhider +
                                "\n\n";
                        mTextViewResult.append(result);

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
                mTextViewResult.setText("Error: No data found");
            }

        });
        mQueue.add(request);
    }
    */


}
