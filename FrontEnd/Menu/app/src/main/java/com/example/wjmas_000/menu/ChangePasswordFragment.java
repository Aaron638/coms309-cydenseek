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


public class ChangePasswordFragment extends Fragment {

    //Testing for basic log in function
    private EditText oldPassword;
    private EditText newPassword;
    private Button changeBtn;
    private TextView result;
    private RequestQueue mQueue;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_change_password, container, false);

        oldPassword = rootView.findViewById(R.id.et_oldPassword);
        newPassword = rootView.findViewById(R.id.et_newPassword);
        changeBtn = rootView.findViewById(R.id.btnChange);
        result = rootView.findViewById(R.id.tv_ChangePassword_Result);

        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldP;
                oldP = oldPassword.getText().toString();
                String newP;
                newP = newPassword.getText().toString();
                if(oldP.equals(((MenuActivity)getActivity()).getPassword()) == false){
                    result.setText("Error: Incorrect old password");
                }
                else if(newP.equals("")){
                    result.setText("Error: Please enter a new password");
                }
                else{
                    ChangeP(((MenuActivity)getActivity()).getSession(), newP);
                }
            }
        });

        return rootView;
    }

    public void ChangeP(String session, String newP){

        String url = "http://coms-309-vb-1.misc.iastate.edu:8080/user/";
        url = url + ((MenuActivity)getActivity()).getUsername();
        mQueue = Volley.newRequestQueue(getActivity());

        JSONObject json = new JSONObject();
        try {
            json.put("session", session);
            json.put("password", newP);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        jsonSend(url, json, newP);


    }

    public void jsonSend(String url, JSONObject json, final String newP) {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, json, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                try {
                    String s = response.getString("session");



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: error");
                error.printStackTrace();
                result.setText("Error Sending Request");
                //mTextViewResult.setText("Error");
            }
        });
        result.setText("Password Successfully Changed");
        ((MenuActivity)getActivity()).setPassword(newP);

        mQueue.add(request);}




}
