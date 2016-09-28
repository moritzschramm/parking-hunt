package com.moritzschramm.parkinghunt.parkingHuntAPIClient;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by Moritz on 27.08.2016.
 */
public class ApiClient {

    private static final String BASE_URL = "http://localhost:3000/api/v1/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String path, JsonHttpResponseHandler handler) {

        client.get(getUrl(path), handler);
    }
    public static void post(Context context, String path, JSONObject jsonParams, JsonHttpResponseHandler handler) {

        StringEntity params = null;

        try {
            params = new StringEntity(jsonParams.toString());
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        client.post(context, getUrl(path), params, "application/json", handler);
    }
    public static void put(Context context, String path, JSONObject jsonParams, JsonHttpResponseHandler handler) {

        StringEntity params = null;

        try {
            params = new StringEntity(jsonParams.toString());
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        client.put(context, getUrl(path), params, "application/json", handler);
    }
    public static void delete(Context context, String path, JSONObject jsonParams, JsonHttpResponseHandler handler) {

        StringEntity params = null;

        try {
            params = new StringEntity(jsonParams.toString());
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        client.delete(context, getUrl(path), params, "application/json", handler);
    }

    private static String getUrl(String path) {

        return BASE_URL + path;
    }
}
