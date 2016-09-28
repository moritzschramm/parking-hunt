package com.moritzschramm.parkinghunt;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.moritzschramm.parkinghunt.parkingHuntAPIClient.AuthClient;


public class MainActivity extends AppCompatActivity implements NavbarClickHandler.ClickInterfaceMainActivity, Tabs.PageListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final int MY_PERMISSION_ACCESS_COURSE_LOCATION = 44;
    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 41;

    /* TAB OBJECTS */
    private Tabs tabs;

    /* NAV OBJECTS */
    private LinearLayout mDrawerPane;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    private FloatingActionButton fab;

    /* GOOGLE API CLIENT */
    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!AuthClient.checkLogin(getApplicationContext())) {
            if(Cookie.isSet("alreadyStarted", this)) {
                startActivity(new Intent(this, LoginActivity.class));
            } else {
                startActivity(new Intent(this, RegisterActivity.class));
            }
            finish();
        }

        setContentView(R.layout.activity_main);

        fab = (FloatingActionButton) findViewById(R.id.fab_button);

        initTabs();
        initNav();

        checkPermission();
        initGoogleApiClient();
    }

    public void navbarClick(View view) {

        NavbarClickHandler.handleClick(view, this, tabs.getCurrentTab(), mGoogleApiClient, tabs);
    }



    public void findPlace() {
        AutocompleteFilter.Builder typeFilterBuilder = new AutocompleteFilter.Builder();
        typeFilterBuilder.setTypeFilter(AutocompleteFilter.TYPE_FILTER_GEOCODE);
        AutocompleteFilter typeFilter = typeFilterBuilder.build();
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .setFilter(typeFilter)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    // A place has been received; use requestCode to track the request.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i("MAIN", "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("MAIN", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }



    /** TABS - everything that has to do with tabs */
    private void initTabs() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        tabs = new Tabs(this, toolbar, tabLayout, viewPager);
        tabs.init(getSupportFragmentManager());

        setSupportActionBar(toolbar);
    }
    /** onChange: responsible for changing the icon of the fab button */
    public void onChange(int position) {

        if(position == Tabs.TAB_ONE) {
            fab.setImageResource(R.drawable.ic_search_white_48dp);
        } else {
            fab.setImageResource(R.drawable.ic_add_white_48dp);
        }
    }

    /** NAV - everything that has to do with the nav bar */
    public void initNav() {

        ((TextView)findViewById(R.id.userName)).setText(Cookie.get("firstname", this) + " " + Cookie.get("lastname", this));

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mDrawerPane = (LinearLayout) findViewById(R.id.drawerPane);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle other action bar items...

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    /** PERMISSIONS */
    private void checkPermission() {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },
                    MainActivity.MY_PERMISSION_ACCESS_COURSE_LOCATION );
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
                    MainActivity.MY_PERMISSION_ACCESS_COURSE_LOCATION );
        }
    }
    /** GOOGLE API CLIENT */
    private void initGoogleApiClient() {

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }
    @Override
    public void onConnected(Bundle connectionHint) {

    }
    @Override
    public void onConnectionSuspended(int i) {
        Log.e("loc", "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("loc", "onConnectionFailed");
    }

    public void debug(View view){}
    public Context getActivity() {return this;}
}