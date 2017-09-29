package com.cpm.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cpm.voto.R;
import com.cpm.download.CompleteDownloadActivity;
import com.cpm.voto.MainMenuActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {


    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        Activity activity = getActivity();
        if(activity instanceof CompleteDownloadActivity){
            CompleteDownloadActivity myactivity = (CompleteDownloadActivity) activity;
            myactivity.getSupportActionBar().setTitle("Main Menu");
        }
        else{
            ((MainMenuActivity) getActivity()).getSupportActionBar().setTitle("Main Menu");
        }


    }
}
