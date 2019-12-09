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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import static com.android.volley.VolleyLog.TAG;


public class CreateAccountFragment extends Fragment {


    private EditText username;
    private EditText password;
    private EditText passwordConfirm;
    private Button createBtn;
    private TextView result;
    private RequestQueue mQueue;
    private String session;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_account, container, false);

        username = rootView.findViewById(R.id.etUsername_AccountCreation);
        password = rootView.findViewById(R.id.etPassword_AccountCreation);
        passwordConfirm = rootView.findViewById(R.id.etPasswordConfirm_AccountCreation);
        createBtn = rootView.findViewById(R.id.btnCreate_AccountCreation);
        result = rootView.findViewById(R.id.tvResult_AccountCreation);

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usernameTxt;
                usernameTxt = username.getText().toString();
                String passwordTxt;
                passwordTxt = password.getText().toString();
                String passwordConfirmTxt;
                passwordConfirmTxt = passwordConfirm.getText().toString();


                createAccount(usernameTxt, passwordTxt, passwordConfirmTxt);
            }
        });

        return rootView;
    }

    public void createAccount(String user, String pass, String passConfirm){
        String hold;
        //0: Check if both passwords match
        if(pass.equals(passConfirm)){
            //1: send username and password strings to backend
            String url = "http://coms-309-vb-1.misc.iastate.edu:8080/user/";
            url = url + user;
            mQueue = Volley.newRequestQueue(getActivity());

            JSONObject json = new JSONObject();
            try {
                json.put("password",pass);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ((LoginActivity)getActivity()).setUsername(user);
            jsonSend(url, json, user, pass);
        }
        else{
            hold = "Password does not match, please re-enter information";
            result.setText(hold);
            ((LoginActivity)getActivity()).setUsername(null);
        }
    }

    public void jsonSend(String url, JSONObject json, final String u, final String p) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, json, new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                try {
                    String s = response.getString("session");
                    session = s;

                    String hold;
                    if(session != null){
                        hold = "Account Created";
                        result.setText(hold);
                        ((LoginActivity)getActivity()).setSession(session);
                        ((LoginActivity)getActivity()).setUsername(u);
                        ((LoginActivity)getActivity()).setPassword(p);

                        //Move to menu activity
                        Intent intent = new Intent(getActivity(), MenuActivity.class);
                        intent.putExtra("username", u);
                        intent.putExtra("token", session);
                        intent.putExtra("password", p);
                        startActivity(intent);

                    }
                    //2.2: If user did not give correct information, then display "Failed Login".  Do not move from this fragment
                    else{
                        hold = "Username already in use";
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

        mQueue.add(request);
    }


}
