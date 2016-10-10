package com.moritzschramm.parkinghunt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.moritzschramm.parkinghunt.parkingHuntAPIClient.AuthClient;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Moritz on 28.08.2016.
 */
public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getSupportActionBar() != null)
            getSupportActionBar().hide();

        setContentView(R.layout.activity_register);
    }

    public void register(View view) {

        final View loader = findViewById(R.id.loader);
        final TextView err = (TextView) findViewById(R.id.err);

        final String email = ((EditText)findViewById(R.id.email)).getText().toString().trim();
        final String firstname = ((EditText)findViewById(R.id.firstname)).getText().toString().trim();
        final String lastname = ((EditText)findViewById(R.id.lastname)).getText().toString().trim();
        final String password = ((EditText)findViewById(R.id.password)).getText().toString().trim();
        final String confirm = ((EditText)findViewById(R.id.confirm)).getText().toString().trim();

        final CheckBox privacy = (CheckBox)findViewById(R.id.privacy_policy);

        if(email.equals("") || firstname.equals("") || lastname.equals("") || password.equals("")) {

            err.setText("Bitte alle Felder ausfüllen");
            err.setVisibility(View.VISIBLE);
            return;
        }

        if(password.length() < 8) {

            err.setText("Passwort muss mindestens 8 Zeichen lang sein");
            err.setVisibility(View.VISIBLE);

            ((EditText)findViewById(R.id.password)).setText("");
            ((EditText)findViewById(R.id.confirm)).setText("");
            return;
        }

        if(!confirm.equals(password)) {

            err.setText("Passwörter stimmen nicht überein");
            err.setVisibility(View.VISIBLE);

            ((EditText)findViewById(R.id.password)).setText("");
            ((EditText)findViewById(R.id.confirm)).setText("");
            return;
        }

        if(!privacy.isChecked()) {

            err.setText("Um die App nutzen zu können, müssen Sie der Datenschutzerklärung zustimmen");
            err.setVisibility(View.VISIBLE);
            return;
        }

        AuthClient.register(this, email, firstname, lastname, password, new JsonHttpResponseHandler() {

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

                    Cookie.set("ApiKey", user.getString("ApiKey"), getApplicationContext());
                    Cookie.set("accID", user.getString("accID"), getApplicationContext());
                    Cookie.set("firstname", firstname, getApplicationContext());
                    Cookie.set("lastname", lastname, getApplicationContext());
                    Cookie.set("email", email, getApplicationContext());
                    Cookie.set("alreadyStarted", "true", getApplicationContext());

                    startActivity(new Intent(getActivity(), MainActivity.class));
                    finish();

                } catch(JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

                loader.setVisibility(View.GONE);

                try {

                    String msg = errorResponse.getJSONObject("error").getString("message");

                    switch(msg) {

                        case "Registration Error: E-mail already exists":

                            err.setText("E-mail ist bereits registriert");
                            err.setVisibility(View.VISIBLE);
                            break;

                        default:

                            err.setText("Ein Fehler ist bei der Registrierung aufgetreten");
                            err.setVisibility(View.VISIBLE);

                            Log.e("Registration Error", msg);

                            break;
                    }

                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void redirectLogin(View view) {

        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    public Context getActivity(){return this;}
}
