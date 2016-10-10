package com.moritzschramm.parkinghunt.parkingHuntAPIClient;

import android.content.Context;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Moritz on 02.09.2016.
 */
public class SearchClient {

    public static void search(Context context, double lat, double lng, JsonHttpResponseHandler handler) {

        JSONObject params = null;

        try {

            params = new JSONObject();

            params.put("Credentials", AuthClient.getCredentials(context));

            params.put("lat", lat);
            params.put("lng", lng);

        } catch(JSONException e) {
            e.printStackTrace();
        }

        ApiClient.post(context, "spots", params, handler); //todo change back to "search/geo", spots returns ALL spots
    }
}
