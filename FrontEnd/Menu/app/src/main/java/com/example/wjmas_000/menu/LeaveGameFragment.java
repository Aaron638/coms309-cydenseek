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


public class LeaveGameFragment extends Fragment {

    //Testing for basic log in function

    private Button leaveBtn;
    private TextView result;
    private RequestQueue mQueue;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_leave_game, container, false);


        leaveBtn = rootView.findViewById(R.id.btn_leaveGame);
        result = rootView.findViewById(R.id.tv_leaveGame_result);

        leaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leave();
            }
        });

        return rootView;
    }

    private void leave(){
        String hold;
        hold = "Leaving Game";
        result.setText(hold);
        Intent intent = new Intent(getActivity(), MenuActivity.class);
        intent.putExtra("username", ((GameActivity)getActivity()).getUsername());
        intent.putExtra("token", ((GameActivity)getActivity()).getUserSession());
        intent.putExtra("password", ((GameActivity)getActivity()).getPassword());
        startActivity(intent);

    }




}
