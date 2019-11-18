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
                String usernameTxt;
                usernameTxt = username.getText().toString();
                String passwordTxt;
                passwordTxt = password.getText().toString();
                validate(usernameTxt, passwordTxt);
            }
        });

        return rootView;
    }

    public void validate(String user, String pass){
        //1: send username and password strings to backend


        //2.0: get response from backend


        //2.1: If user gave the correct information, then display "Successful Login". Set activity variable 'LoginCode' to key given by backend


        //2.2: If user did not give correct information, then display "Failed Login".  Do not move from this fragment



        String hold;
        if((user.equals("James")) && pass.equals("Bond")){
            hold = "Successful Login";
            result.setText(hold);
        }
        else{
            hold = "Failed Login";
            result.setText(hold);
        }

    }
}
