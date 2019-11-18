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


public class CreateAccountFragment extends Fragment {

    //Testing for basic log in function
    private EditText username;
    private EditText password;
    private EditText passwordConfirm;
    private Button createBtn;
    private TextView result;

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


            //2.0: get response from backend, Confirming that this combination is allowed


            //2.1: If user gave viable information, then display "Successful Login". Set activity variable 'LoginCode' to key given by backend


            //2.2: If user did not give correct information, then display "Failed Login".  Do not move from this fragment




            if((user.equals("James")) && pass.equals("Bond")){
                hold = "Account Created";
                result.setText(hold);
            }
            else{
                hold = "Username already in use";
                result.setText(hold);
            }

        }

        else{
            hold = "Password does not match, please re-enter information";
            result.setText(hold);
        }




    }
}
