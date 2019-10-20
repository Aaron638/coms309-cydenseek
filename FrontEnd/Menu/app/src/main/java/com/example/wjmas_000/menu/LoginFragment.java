package com.example.wjmas_000.menu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class LoginFragment extends Fragment {

    //Testing for basic log in function
    private EditText username;
    private EditText password;
    private Button loginBtn;
    private TextView result;

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
                validate();
            }
        });

        return rootView;
    }

    private void validate(){
        //Retrieve stings from input
        String usernameTxt;
        usernameTxt = username.getText().toString();
        String passwordTxt;
        passwordTxt = password.getText().toString();


        String hold;
        if((usernameTxt.equals("James")) && passwordTxt.equals("Bond")){
            hold = "Successful Login";
            result.setText(hold);
        }
        else{
            hold = "Failed Login";
            result.setText(hold);
        }
        return;
    }
}
