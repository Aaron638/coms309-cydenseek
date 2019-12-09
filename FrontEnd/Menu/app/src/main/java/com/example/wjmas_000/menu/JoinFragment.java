package com.example.wjmas_000.menu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static com.android.volley.VolleyLog.TAG;


public class JoinFragment extends Fragment {

    private RequestQueue rq;
    String username;
    String userSession;
    TextView gameListTextView;
    String creatorUsername;
    HashMap<String, String> gamesList;
    EditText creatorNameEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_join, container, false);
        username = ((MenuActivity) getActivity()).getUsername();
        userSession = ((MenuActivity) getActivity()).getSession();
        rq = Volley.newRequestQueue(getActivity());

        gameListTextView = (TextView) rootView.findViewById(R.id.text_game_list);
        creatorNameEditText = (EditText) rootView.findViewById(R.id.edit_creator_user_name);


        //usernames, sessions
        gamesList = new HashMap<String, String>();

        //When refreshing, we should see the list of games from the GET /games Volley Request
        Button refreshButton = (Button) rootView.findViewById(R.id.button_refresh);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameListTextView.setText("");
                gamesList.clear();
                backendCallGames();
            }
        });

        //When we press this button, we check the editText to see if that creator actually made a game
        //Then we launch the game
        Button joinGameButton = (Button) rootView.findViewById(R.id.button_join);
        joinGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                creatorUsername = creatorNameEditText.getText().toString();
                if (gamesList.containsKey(creatorUsername)){
                    launchGame(gamesList.get(creatorUsername));
                } else {
                    Toast.makeText(getContext(), "Incorrect username", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Refresh on open
        gameListTextView.setText("");
        gamesList.clear();
        backendCallGames();

        return rootView;
    }

    //launches the game activity
    private void launchGame(String gameSession){
        Intent intent = new Intent(getActivity(), GameActivity.class);
        intent.putExtra("GAME_SESSION_ID", gameSession);
        intent.putExtra("username", username);
        intent.putExtra("userSession", userSession);
        intent.putExtra("password", ((MenuActivity)getActivity()).getPassword());
        startActivity(intent);
    }

    //Use Volley to send a GET request to /games
    private void backendCallGames() {
        String uri = "http://coms-309-vb-1.misc.iastate.edu:8080/games";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, uri, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                try {
                    JSONArray jsonArray = response.getJSONArray("games");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject game = jsonArray.getJSONObject(i);

                        String creator = game.getString("creator");
                        String session = game.getString("session");
                        gamesList.put(creator, session);

                        String result = "Creator: " + creator +
                                "\nGame Session: " + gamesList.get(creator) +
                                "\n\n";

                        gameListTextView.append(result);
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
                gameListTextView.setText("Error");
            }
        }
                //end JsonObjectRequest
        );
        rq.add(request);
    }


}
