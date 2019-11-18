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


public class LogoutFragment extends Fragment {

    //Testing for basic log in function

    private Button logoutBtn;
    private TextView result;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_logout, container, false);


        logoutBtn = rootView.findViewById(R.id.btnLogout);
        result = rootView.findViewById(R.id.tvResult);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        return rootView;
    }

    public void logout(){
        //1: Send notification to backend


        //2: Clear local information


        //3: Display information to user to let them know that they have logged out
        String hold;
        hold = "Bye Bye";
        result.setText(hold);

    }
}
