package com.moritzschramm.parkinghunt.parkingHuntAPIClient;

import android.content.Context;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.moritzschramm.parkinghunt.Cookie;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Moritz on 30.08.2016.
 */
public class UserClient {

    public static void getUser(Context context, JsonHttpResponseHandler handler) {

        JSONObject params = null;
        try {
            params = new JSONObject();

            params.put("Credentials", AuthClient.getCredentials(context));

        } catch(JSONException e) {
            e.printStackTrace();
        }

        ApiClient.post(context, "acc/" + Cookie.get("accID", context), params, handler);
    }

    public static void changeEmail(final Context context,
                                   final String email, final String password,
                                   final CustomJsonHttpResponseHandler handler) {

        if(email.equals("") || password.equals("")) {
            handler.emptyInput();
            return;
        }

        JSONObject params = null;

        try {
            params = new JSONObject();

            params.put("Credentials", AuthClient.getCredentials(context));

            params.put("email", email);
            params.put("password", password);
            params.put("old_password", password);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiClient.put(context, "acc/" + Cookie.get("accID", context), params, handler);
    }

    public static void changePassword(final Context context,
                                      final String oldPw, final String newPw, final String confirmPw,
                                      final CustomJsonHttpResponseHandler handler) {

        if(oldPw.equals("") || newPw.equals("") || confirmPw.equals("")) {
            handler.emptyInput();
            return;
        }

        if(newPw.length() < 8) {

            handler.passwortToShort();
            return;
        }
        if(!AuthClient.checkPassword(newPw)) {

            handler.weakPassword();
            return;
        }

        if(!newPw.equals(confirmPw)) {

            handler.unequalConfirm();
            return;
        }

        JSONObject params = null;

        try {

            params = new JSONObject();

            params.put("Credentials", AuthClient.getCredentials(context));

            params.put("email", Cookie.get("email", context));
            params.put("password", newPw);
            params.put("old_password", oldPw);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiClient.put(context, "acc/" + Cookie.get("accID", context), params, handler);
    }
}
