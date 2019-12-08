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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import static com.android.volley.VolleyLog.TAG;


public class StatisticsFragment extends Fragment {


    private RequestQueue mQueue;
    private int error = 0;      //Testing and error detection
    private TextView tv1;
    private TextView tv2;
    private TextView tv3;
    private TextView tv4;
    private TextView tv5;
    private TextView tv6;
    private TextView tv7;
    private TextView tv8;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_statistics, container, false);

        tv1 = rootView.findViewById(R.id.tv_statistics_Username);
        tv2 = rootView.findViewById(R.id.tv_statistics_TotalTimePlayed);
        tv3 = rootView.findViewById(R.id.tv_statistics_TotalDistanceWalked);
        tv4 = rootView.findViewById(R.id.tv_statistics_TotalGamesWon);
        tv5 = rootView.findViewById(R.id.tv_statistics_GamesWonHider);
        tv6 = rootView.findViewById(R.id.tv_statistics_GamesWonSeeker);
        tv7 = rootView.findViewById(R.id.tv_statistics_GamesPlayedHider);
        tv8 = rootView.findViewById(R.id.tv_statistics_GamesPlayedSeeker);




        mQueue = Volley.newRequestQueue(getActivity());
        jsonParse2();


        return rootView;
    }



    public int jsonParse2(){
        String url = "http://coms-309-vb-1.misc.iastate.edu:8080/user/";
        url = url + ((MenuActivity)getActivity()).getUsername();


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                try {
                    //iterates through all users in the json array
                    JSONObject user = response;

                    //we get all user attributes here
                    String gpseeker = user.getString("gpseeker");         //Games played as seeker
                    String totdistance = user.getString("totdistance");   //Total distance walked
                    String tottime = user.getString("tottime");           //Total time played
                    String gphider = user.getString("gphider");
                    String gwhider = user.getString("gwhider");
                    String gwseeker = user.getString("gwseeker");         //Games won as seeker

                    tv1.setText(((MenuActivity)getActivity()).getUsername());
                    tv2.setText(tottime);
                    tv3.setText(totdistance);

                    int a = Integer.parseInt(gwhider);
                    int b = Integer.parseInt(gwseeker);
                    int c = a + b;
                    String d = Integer.toString(c);
                    tv4.setText(d);

                    tv5.setText(gwhider);
                    tv6.setText(gwseeker);
                    tv7.setText(gphider);
                    tv8.setText(gpseeker);



                    error = 0;

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


}
