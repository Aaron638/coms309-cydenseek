package com.example.wjmas_000.menu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import static com.android.volley.VolleyLog.TAG;


public class LogoutFragment extends Fragment {

    //Testing for basic log in function

    private Button logoutBtn;
    private TextView result;
    private RequestQueue mQueue;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_logout, container, false);


        logoutBtn = rootView.findViewById(R.id.btnLogout);
        result = rootView.findViewById(R.id.tvResult_Logout);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        return rootView;
    }

    public void logout(){
        String url = "http://coms-309-vb-1.misc.iastate.edu:8080/user/";
        url = url + ((MenuActivity)getActivity()).getUsername();
        url = url + "/logout";

        mQueue = Volley.newRequestQueue(getActivity());
        JSONObject json = new JSONObject();
        try {
            json.put("session",((MenuActivity)getActivity()).getSession());
        } catch (JSONException e) {
            e.printStackTrace();
        }





        //Remove the session token
        ((MenuActivity)getActivity()).setUsername(null);
        ((MenuActivity)getActivity()).setSession(null);
        jsonSend(url, json);

        String hold;
        hold = "Successful Logout";
        result.setText(hold);
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);

    }

    public void jsonSend(String url, JSONObject json) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, json, new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                try {
                    String s = response.getString("session");
                    //This doesnt do anything right now.  Dont know what i need in return from back end for a log out



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
