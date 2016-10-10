package com.moritzschramm.parkinghunt;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Moritz on 14.08.2016.
 */
public class LastSearchesFragment extends Fragment {

    public LastSearchesFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_one, container, false);

        ((TextView) rootView.findViewById(R.id.tv1)).setText("Gesucht");

        return rootView;
    }
}
