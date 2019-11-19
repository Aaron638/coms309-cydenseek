package com.example.wjmas_000.menu;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


public class JoinFragment extends Fragment {

    private static final String TAG = CardViewFragment.class.getSimpleName();

    /** The CardView widget. */
    //@VisibleForTesting
    CardView mCardView;

    /**
     * SeekBar that changes the cornerRadius attribute for the {@link #mCardView} widget.
     */
    //@VisibleForTesting
    SeekBar mRadiusSeekBar;

    /**
     * SeekBar that changes the Elevation attribute for the {@link #mCardView} widget.
     */
    //@VisibleForTesting
    SeekBar mElevationSeekBar;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NotificationFragment.
     */

    private RequestQueue mQueue;
    String gameID;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_join, container, false);

        Button buttonJoin = (Button) rootView.findViewById(R.id.button_join_game);
        mQueue = Volley.newRequestQueue(getActivity());

        buttonJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callBackend();
            }
        });

        return rootView;
    }

    private void callBackend(){

        String urlGames = "http://coms-309-vb-1.misc.iastate.edu:8080/games/";


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCardView = (CardView) view.findViewById(R.id.cardview);
        mRadiusSeekBar = (SeekBar) view.findViewById(R.id.cardview_radius_seekbar);
        mRadiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(TAG, String.format("SeekBar Radius progress : %d", progress));
                mCardView.setRadius(progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Do nothing
            }
        });

        mElevationSeekBar = (SeekBar) view.findViewById(R.id.cardview_elevation_seekbar);
        mElevationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(TAG, String.format("SeekBar Elevation progress : %d", progress));
                mCardView.setElevation(progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Do nothing
            }
        });
    }

}
