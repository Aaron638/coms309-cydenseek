package com.example.wjmas_000.menu;

import android.app.DownloadManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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


public class PlayerListFragment extends Fragment {

    private TextView mTextViewResult;
    private RequestQueue mQueue;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_player_list, container, false);

        mTextViewResult = (TextView) rootView.findViewById(R.id.text_view_result);
        //mTextViewResult.setText("HI");
        //maybe use to set text
        Button buttonParse = (Button) rootView.findViewById(R.id.button_parse);

        //usually this is for this
        mQueue = Volley.newRequestQueue(getActivity());

        buttonParse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jsonParse();
            }
        });


        //return inflater.inflate(R.layout.fragment_player_list, container, false);
        //trying this instead
        return rootView;
    }

    private void jsonParse(){
        String url = "http://coms-309-vb-1.misc.iastate.edu:8080/users";
        //maybe pass json array in the future?
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("users");
                    //iterates through all users in the json array
                    for (int i =0; i < jsonArray.length(); i++){
                        JSONObject user = jsonArray.getJSONObject(i);

                        //we get all user attributes here
                        int id = user.getInt("id");
                        String username = user.getString("username");
                        String password = user.getString("password");
                        String session = user.getString("session");
                        int gameid = user.getInt("gameId");
                        //implement when latt and long arent null
                        //double latitude = user.getDouble("latitude");
                        //double longitude = user.getDouble("longitude");
                        boolean dev = user.getBoolean("developer");
                        boolean hider = user.getBoolean("hider");
                        boolean found = user.getBoolean("found");
                        int gwhider = user.getInt("gwhider");
                        int gwseeker = user.getInt("gwseeker");
                        int gphider = user.getInt("gphider");
                        int gpseeker = user.getInt("gpseeker");
                        int totdistance = user.getInt("totdistance");
                        int tottime = user.getInt("tottime");

                        mTextViewResult.append("User:"+ username + "\nTime Played:"+ tottime+ "\n\n");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }
}
