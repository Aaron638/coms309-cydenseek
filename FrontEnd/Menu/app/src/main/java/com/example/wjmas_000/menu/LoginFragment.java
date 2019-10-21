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

    public int validate(String x, String y){
        //Retrieve stings from input



        String hold;
        if((x.equals("James")) && y.equals("Bond")){
            hold = "Successful Login";
            result.setText(hold);
            return 0;
        }
        else{
            hold = "Failed Login";
            result.setText(hold);
            return -1;
        }

    }
}
