package com.moritzschramm.parkinghunt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.moritzschramm.parkinghunt.parkingHuntAPIClient.AuthClient;

import org.json.JSONException;
import org.json.JSONObject;


import cz.msebera.android.httpclient.Header;

/**
 * Created by Moritz on 27.08.2016.
 */
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getSupportActionBar() != null)
            getSupportActionBar().hide();

        setContentView(R.layout.activity_login);
    }

    public void login(View view) {

        final View loader = findViewById(R.id.loader);
        final TextView err = (TextView)findViewById(R.id.err);

        final String email = ((EditText)findViewById(R.id.email)).getText().toString();
        final EditText passwordTextField = (EditText)findViewById(R.id.password);
        String password = passwordTextField.getText().toString();

        AuthClient.login(this, email, password, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {

                loader.setVisibility(View.VISIBLE);
                err.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                loader.setVisibility(View.GONE);

                try {

                    JSONObject user = response.getJSONObject("user");

                    if(user != null) {

                        String accId = user.getString("accID");
                        String ApiKey = user.getString("ApiKey");
                        String firstname = user.getString("firstname");
                        String lastname = user.getString("lastname");

                        Cookie.set("accID", accId, getApplicationContext());
                        Cookie.set("ApiKey", ApiKey, getApplicationContext());
                        Cookie.set("firstname", firstname, getApplicationContext());
                        Cookie.set("lastname", lastname, getApplicationContext());
                        Cookie.set("email", email, getApplicationContext());
                        Cookie.set("alreadyStarted", "true", getApplicationContext());

                        startActivity(new Intent(getActivity(), MainActivity.class));
                        finish();
                    }
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

                loader.setVisibility(View.GONE);
                passwordTextField.setText("");

                try {

                    String msg = errorResponse.getJSONObject("error").getString("message");

                    switch(msg) {

                        case "Login Error: wrong password":

                            err.setText("Falsches Passwort");
                            err.setVisibility(View.VISIBLE);
                            break;

                        case "Login Error: email does not exists":

                            err.setText("Falsche E-mail");
                            err.setVisibility(View.VISIBLE);
                            break;

                        default:

                            err.setText("Ein Fehler ist beim Login aufgetreten");
                            err.setVisibility(View.VISIBLE);
                            break;
                    }

                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void redirectRegister(View view) {

        startActivity(new Intent(this, RegisterActivity.class));
        finish();
    }

    private Context getActivity(){return this;}
    public void debug(View view){}
}
