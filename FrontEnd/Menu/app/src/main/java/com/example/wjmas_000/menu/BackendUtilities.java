package com.example.wjmas_000.menu;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.android.volley.VolleyLog.TAG;

public class BackendUtilities {

    private RequestQueue requestQueue;
    private JSONArray buJSONArray;

    public JSONArray getBuJSONArray() {
        return buJSONArray;
    }

    public void setBuJSONArray(JSONArray buJSONArray) {
        this.buJSONArray = buJSONArray;
    }



    public BackendUtilities (Context context){
        requestQueue = Volley.newRequestQueue(context);

    }

    //to get users, use jsonArrayGet("http://coms-309-vb-1.misc.iastate.edu:8080/users", "users")
    public void jsonArrayGet(String url, final String nameOfObject){


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray(nameOfObject);
                    setBuJSONArray(jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: error");
                error.printStackTrace();
            }
        });
        requestQueue.add(request);
        //return;
    }
}
