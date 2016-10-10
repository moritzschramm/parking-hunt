package com.moritzschramm.parkinghunt;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.moritzschramm.parkinghunt.Model.Spot;
import com.moritzschramm.parkinghunt.parkingHuntAPIClient.AuthClient;
import com.moritzschramm.parkinghunt.parkingHuntAPIClient.SpotClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Moritz on 28.08.2016.
 *
 * Navbar Click Handler: handles all clicks of the navbar and the fab button as well
 */
public class NavbarClickHandler {

    public interface ClickInterface {
        //void addClicked();
        void updateOwnSpots();
    }
    public interface ClickInterfaceMainActivity {
        void findPlace();
    }

    public static void handleClick(final View view, Activity activity,
                                   final int currentTabPos,
                                   final GoogleApiClient mGoogleApiClient,
                                   final Tabs tabs) {

        Context context = (Context)activity;

        switch(view.getId()) {

            case R.id.fab_button: {

                if (currentTabPos == Tabs.TAB_ONE) {

                    //((ClickInterfaceMainActivity)activity).findPlace();
                    searchClicked(activity, mGoogleApiClient);


                } else {
                    addClicked(activity, mGoogleApiClient, tabs);
                }

            }
            break;

            case R.id.profileBox: {

                Intent intent = new Intent(context, ProfileActivity.class);
                context.startActivity(intent);
            }
            break;

            case R.id.btn_search: {

                searchClicked(activity, mGoogleApiClient);

                //Intent intent = new Intent(context, SearchActivity.class);
                //context.startActivity(intent);
            }
            break;

            case R.id.btn_add: {

                addClicked(activity, mGoogleApiClient, tabs);
            }
            break;

            case R.id.btn_rent: {

                //rent
            }
            break;

            case R.id.btn_rent_out: {

                //rent out
            }
            break;

            case R.id.btn_logout: {

                AuthClient.logout(context);
                context.startActivity(new Intent(context, LoginActivity.class));
                activity.finish();
            }
            break;

            case R.id.btn_impressum: {

                //imp
            }
            break;

            default: break;
        }
    }

    private static void searchClicked(final Activity activity, final GoogleApiClient mGoogleApiClient) {

        Location loc = getLocation(activity, mGoogleApiClient);

        if(loc != null) {

            double lat = loc.getLatitude();
            double lng = loc.getLongitude();

            Intent intent = new Intent(activity, MapsActivity.class);
            intent.putExtra("lat", lat);
            intent.putExtra("lng", lng);
            activity.startActivity(intent);

        } else {

            //gps maybe off
            Log.e("GPS", "null");

            AlertDialog dialog;
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);

            builder.setTitle("Fehler")
                    .setMessage("Es kann nicht auf den Standort zugegriffen werden")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            dialog = builder.create();

            dialog.show();
        }
    }

    private static void addClicked(final Activity activity, final GoogleApiClient mGoogleApiClient, final Tabs tabs) {

        Location mLastLocation = getLocation(activity, mGoogleApiClient);

        if(mLastLocation != null) {

            final double lat = mLastLocation.getLatitude();
            final double lng = mLastLocation.getLongitude();
            final int amount = 1; //todo ONLY DEBUG

            final Address address = getAddress(activity, mLastLocation);
            String place = "", street = "", streetNumber = "";

            if(address != null) {

                place = address.getLocality()  == null ? "" : address.getLocality();
                place += address.getPostalCode() == null ? "" : " (" + address.getPostalCode() + ")";
                street = address.getThoroughfare() == null ? "" : address.getThoroughfare();
                streetNumber = address.getSubThoroughfare() == null ? "" : address.getSubThoroughfare();
            }

            final Spot spot = new Spot(lat, lng, amount, "created_by_user");
            spot.setPlace(place);
            spot.setStreetName(street);
            spot.setStreetNumber(streetNumber);


            AlertDialog dialog;
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Parkplatz hinzufügen")
                    .setMessage("Aktueller Standort wird als Parkplatzstandort verwendet")
                    .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("Hinzufügen", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            SpotClient.addSpot(activity, spot, new JsonHttpResponseHandler() {

                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                                    tabs.updateOwnSpots();
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

                                    try {
                                        Toast.makeText(activity, errorResponse.toString(4), Toast.LENGTH_LONG).show();
                                    } catch(JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    });

            dialog = builder.create();

            dialog.show();

        } else {

            //gps maybe off
            Log.e("GPS", "null");

            AlertDialog dialog;
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);

            builder.setTitle("Fehler")
                    .setMessage("Es kann nicht auf den Standort zugegriffen werden")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            dialog = builder.create();

            dialog.show();
        }
    }

    public static Location getLocation(Activity activity, GoogleApiClient mGoogleApiClient) {

        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        return mLastLocation;
    }
    public static Address getAddress(Activity activity, Location location) {

        Context context = activity;
        Geocoder geocoder = new Geocoder(context);

        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1);
            return addresses.get(0);
        } catch (IOException ioException) {
            Log.e("Address", "IOException");
        } catch (IllegalArgumentException illegalArgumentException) {
            Log.e("Address", "IllegalArgumentException");
        }
        return null;
    }
}
