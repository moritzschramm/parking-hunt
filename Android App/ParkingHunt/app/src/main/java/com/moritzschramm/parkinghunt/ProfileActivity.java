package com.moritzschramm.parkinghunt;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.moritzschramm.parkinghunt.parkingHuntAPIClient.CustomJsonHttpResponseHandler;
import com.moritzschramm.parkinghunt.parkingHuntAPIClient.UserClient;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Moritz on 28.08.2016.
 */
public class ProfileActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        init();
    }

    private void init() {

        if(getSupportActionBar() != null)
            getSupportActionBar().setTitle("Profil");

        TextView name, email;
        name = (TextView) findViewById(R.id.name);
        email = (TextView) findViewById(R.id.email);

        name.setText(Cookie.get("firstname", this) + " " + Cookie.get("lastname", this));
        email.setText(Cookie.get("email", this));
    }

    public void clickChangeEmail(View view) {

        final View loader = getLoaderView();

        final TextView success = getSuccessMsg("email");
        final TextView err = getErrorMsg("email");

        final TextView emailText = (TextView) findViewById(R.id.new_email);
        final TextView pwText = (TextView) findViewById(R.id.current_password);

        final String email = ((TextView)findViewById(R.id.new_email)).getText().toString();
        final String password = pwText.getText().toString();

        final TextView emailDisplay = (TextView) findViewById(R.id.email);

        loader.setVisibility(View.VISIBLE);
        success.setVisibility(View.GONE);
        err.setVisibility(View.GONE);

        UserClient.changeEmail(this, email, password, new CustomJsonHttpResponseHandler() {

            @Override
            public void emptyInput() {

                loader.setVisibility(View.GONE);

                err.setText("Bitte alle Felder ausfüllen");
                err.setVisibility(View.VISIBLE);
            }
            @Override
            public void incorrectPassword() {

                loader.setVisibility(View.GONE);

                pwText.setText("");

                err.setText("Falsches Passwort");
                err.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                loader.setVisibility(View.GONE);

                emailText.setText("");
                pwText.setText("");

                success.setText("E-mail erfolgreich geändert");
                success.setVisibility(View.VISIBLE);

                emailDisplay.setText(email);
                Cookie.set("email", email, getApplicationContext());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

                loader.setVisibility(View.GONE);

                emailText.setText("");
                pwText.setText("");

                if(statusCode == 400) {

                    err.setText("E-mail ist bereits durch einen anderen Nutzer vergeben");

                } else if(statusCode == 403) {

                    err.setText("Falsches Passwort");

                } else {

                    err.setText("Ein Fehler ist aufgetreten: " + statusCode);
                }
                err.setVisibility(View.VISIBLE);
            }
        });
    }
    public void clickChangePassword(View view) {

        final View loader = getLoaderView();

        final TextView success = getSuccessMsg("password");
        final TextView err = getErrorMsg("password");

        final TextView oldPwText = (TextView) findViewById(R.id.old_password);
        final TextView newPwText = (TextView) findViewById(R.id.new_password);
        final TextView confirmPwText = (TextView) findViewById(R.id.confirm_password);

        final String oldPw, newPw, confirmPw;
        oldPw = ((TextView)findViewById(R.id.old_password)).getText().toString();
        newPw = ((TextView)findViewById(R.id.new_password)).getText().toString();
        confirmPw = ((TextView)findViewById(R.id.confirm_password)).getText().toString();

        loader.setVisibility(View.VISIBLE);
        success.setVisibility(View.GONE);
        err.setVisibility(View.GONE);

        UserClient.changePassword(this, oldPw, newPw, confirmPw, new CustomJsonHttpResponseHandler() {

            @Override
            public void emptyInput() {

                loader.setVisibility(View.GONE);

                oldPwText.setText("");
                newPwText.setText("");
                confirmPwText.setText("");

                err.setText("Bitte alle Felder ausfüllen");
                err.setVisibility(View.VISIBLE);
            }
            @Override
            public void incorrectPassword() {

                loader.setVisibility(View.GONE);

                oldPwText.setText("");

                err.setText("Falsches Passwort");
                err.setVisibility(View.VISIBLE);
            }
            @Override
            public void passwortToShort() {

                loader.setVisibility(View.GONE);

                newPwText.setText("");
                confirmPwText.setText("");

                err.setText("Passwort muss mindestens 8 Zeichen lang sein");
                err.setVisibility(View.VISIBLE);
            }

            @Override
            public void weakPassword() {

                loader.setVisibility(View.GONE);

                newPwText.setText("");
                confirmPwText.setText("");

                err.setText("Passwort zu schwach");
                err.setVisibility(View.VISIBLE);
            }

            @Override
            public void unequalConfirm() {

                loader.setVisibility(View.GONE);

                newPwText.setText("");
                confirmPwText.setText("");

                err.setText("Neue Passwörter stimmen nicht überein");
                err.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                loader.setVisibility(View.GONE);

                oldPwText.setText("");
                newPwText.setText("");
                confirmPwText.setText("");

                success.setText("Passwort erfolgreich geändert");
                success.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

                loader.setVisibility(View.GONE);

                oldPwText.setText("");
                newPwText.setText("");
                confirmPwText.setText("");

                if(statusCode == 403) {

                    err.setText("Falsches Passwort");

                } else {

                    err.setText("Ein Fehler ist aufgetreten: " + statusCode);
                }
                err.setVisibility(View.VISIBLE);
            }
        });
    }

    public View getLoaderView() {

        return findViewById(R.id.loader);
    }
    public TextView getSuccessMsg(String type) {

        switch(type) {

            case "email":

                return (TextView) findViewById(R.id.msg_email);

            case "password":

                return (TextView) findViewById(R.id.msg_password);

            default: return null;
        }
    }
    public TextView getErrorMsg(String type) {

        switch(type) {

            case "email":

                return (TextView) findViewById(R.id.err_email);

            case "password":

                return (TextView) findViewById(R.id.err_password);

            default: return null;
        }
    }
    public void debug(View view){}
}
