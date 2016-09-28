package com.moritzschramm.parkinghunt;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Moritz on 12.08.2016.
 */
public class SearchActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);
        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Suchen");
    }
}
