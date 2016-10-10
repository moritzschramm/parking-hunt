package com.moritzschramm.parkinghunt.parkingHuntAPIClient;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.moritzschramm.parkinghunt.Cookie;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by Moritz on 27.08.2016.
 */
public class AuthClient {

    private static final String BASE_URL = "http://localhost:3000/api/v1/";

    private static AsyncHttpClient client = new AsyncHttpClient();


    public static boolean checkLogin(Context context) {

        return (Cookie.isSet("accID", context) && Cookie.isSet("ApiKey", context));
    }
    public static void logout(Context context) {

        Cookie.set("accID", "", context);
        Cookie.set("ApiKey", "", context);
    }

    public static void login(Context context, String email, String password, JsonHttpResponseHandler handler) {

        StringEntity params = null;

        try {
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("email", email);
            jsonParams.put("password", password);
            params = new StringEntity(jsonParams.toString());
        } catch(UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }

        client.post(context, getUrl("login"), params, "application/json", handler);
    }
    public static void register(Context context,
                                String email, String firstname, String lastname, String password,
                                JsonHttpResponseHandler handler) {

        StringEntity params = null;

        try {
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("email", email);
            jsonParams.put("firstname", firstname);
            jsonParams.put("lastname", lastname);
            jsonParams.put("password", password);
            params = new StringEntity(jsonParams.toString());
        } catch(UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }

        client.post(context, getUrl("register"), params, "application/json", handler);
    }

    public static String getUrl(String path) {

        return BASE_URL + path;
    }

    public static JSONObject getCredentials(Context context) throws JSONException {

        String accID = Cookie.get("accID", context);
        String ApiKey = Cookie.get("ApiKey", context);

        JSONObject jsonCredentials = new JSONObject();

        jsonCredentials.put("accID", accID);
        jsonCredentials.put("ApiKey", ApiKey);

        return jsonCredentials;
    }
    public static boolean checkPassword(String password) {

        return true;//todo implement logic
    }
}
