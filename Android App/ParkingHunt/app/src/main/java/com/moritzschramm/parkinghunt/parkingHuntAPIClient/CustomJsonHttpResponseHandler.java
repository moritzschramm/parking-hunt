package com.moritzschramm.parkinghunt.parkingHuntAPIClient;

import com.loopj.android.http.JsonHttpResponseHandler;

/**
 * Created by Moritz on 30.08.2016.
 */
public class CustomJsonHttpResponseHandler extends JsonHttpResponseHandler {

    public void emptyInput() {}
    public void incorrectPassword() {}
    public void unequalConfirm() {}
    public void passwortToShort() {}
    public void weakPassword() {}
}
