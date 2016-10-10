package com.moritzschramm.parkinghunt;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.moritzschramm.parkinghunt.parkingHuntAPIClient.SearchClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Moritz on 01.09.2016.
 */
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private double initLat;
    private double initLng;
    private int initZoom = 16;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getIntent() != null) {

            initLat = getIntent().getDoubleExtra("lat", 0);
            initLng = getIntent().getDoubleExtra("lng", 0);

        } else {
            finish();
        }

        setContentView(R.layout.activity_maps);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if(getSupportActionBar() != null)
            getSupportActionBar().setTitle("Parkplatz suchen");
    }

    @Override
    public void onMapReady(final GoogleMap map) {

        final View loader = findViewById(R.id.loader);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(initLat, initLng), initZoom));

        SearchClient.search(this, initLat, initLng, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {

                loader.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                loader.setVisibility(View.GONE);

                try {

                    JSONArray spotsJson = response.getJSONArray("spots");

                    for(int i = 0; i < spotsJson.length(); i++) {

                        JSONObject spotJson = spotsJson.getJSONObject(i);

                        double lat = spotJson.getDouble("lat");
                        double lng = spotJson.getDouble("lng");
                        int amount = spotJson.getInt("amount");

                        LatLng latLng = new LatLng(lat, lng);

                        map.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title("Öffentlicher Parkplatz")
                                .snippet("Anzahl Parkplätze: " + amount)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                        Log.e("spot", "pos:"+i + " lat:"+lat);
                    }

                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

                loader.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Fehler beim Laden: " + statusCode, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void debug(View view) {}
}
