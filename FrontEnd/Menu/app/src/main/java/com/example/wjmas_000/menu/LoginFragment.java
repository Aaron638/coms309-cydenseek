package com.example.wjmas_000.menu;

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


public class LoginFragment extends Fragment {

    //Testing for basic log in function
    private EditText username;
    private EditText password;
    private Button loginBtn;
    private TextView result;
    private RequestQueue mQueue;
    private String session;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        username = rootView.findViewById(R.id.etUsername);
        password = rootView.findViewById(R.id.etPassword);
        loginBtn = rootView.findViewById(R.id.btnLogin);
        result = rootView.findViewById(R.id.tvResult);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usernameTxt;
                usernameTxt = username.getText().toString();
                String passwordTxt;
                passwordTxt = password.getText().toString();
                Login(usernameTxt, passwordTxt);
            }
        });

        return rootView;
    }

    public void Login(String user, String pass){
        //1: send username and password strings to backend

        String hold;

        String url = "http://coms-309-vb-1.misc.iastate.edu:8080/user/";
        url = url + user;
        url = url + "/auth";
        mQueue = Volley.newRequestQueue(getActivity());

        JSONObject json = new JSONObject();
        try {
            json.put("password",pass);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        jsonSend(url, json);




        //2.0: get response from backend


        //2.1: If user gave the correct information, then display "Successful Login". Set activity variable 'LoginCode' to key given by backend


        //2.2: If user did not give correct information, then display "Failed Login".  Do not move from this fragment


        //Set the session token


    }

    public void jsonSend(String url, JSONObject json) {


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, json, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                try {
                    String s = response.getString("session");
                    session = s;

                    //Display
                    String hold;
                    if(session != null){
                        hold = "Account Created";
                        result.setText(hold);
                        ((MenuActivity)getActivity()).setSession(session);
                    }
                    //2.2: If user did not give correct information, then display "Failed Login".  Do not move from this fragment
                    else{
                        hold = "Failed Login";
                        result.setText(hold);
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
                //mTextViewResult.setText("Error");
            }
        });

        mQueue.add(request);}

    public void setThisSession(String s){
        session = s;


    }



}
