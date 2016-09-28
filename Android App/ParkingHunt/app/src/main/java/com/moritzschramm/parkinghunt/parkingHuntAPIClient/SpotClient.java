package com.moritzschramm.parkinghunt.parkingHuntAPIClient;

import android.content.Context;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.moritzschramm.parkinghunt.Model.Spot;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Moritz on 29.08.2016.
 */
public class SpotClient {

    //TODO simplify repeating try catch statements


    public static void getSpots(Context context, JsonHttpResponseHandler handler) {

        JSONObject jsonParams = null;

        try {
            jsonParams = new JSONObject();

            jsonParams.put("Credentials", AuthClient.getCredentials(context));


        } catch(JSONException e) {
            e.printStackTrace();
        }

        ApiClient.post(context, "spots", jsonParams, handler);
    }

    public static void addSpot(Context context, Spot spot, JsonHttpResponseHandler handler) {

        JSONObject jsonParams = null;

        try {
            jsonParams = new JSONObject();

            jsonParams.put("Credentials", AuthClient.getCredentials(context));

            jsonParams.put("lat", spot.getLat());
            jsonParams.put("lng", spot.getLng());
            jsonParams.put("amount", spot.getAmount());

            jsonParams.put("place", spot.getPlace());
            jsonParams.put("street", spot.getStreetName());
            jsonParams.put("street_number", spot.getStreetNumber());

            jsonParams.put("type", spot.getType());

        } catch(JSONException e) {
            e.printStackTrace();
        }

        ApiClient.post(context, "spot", jsonParams, handler);
    }
}
