package com.kdmny.linkedinsdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.utils.Scope;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.APIHelper;

import org.json.JSONObject;
import org.json.JSONException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LinkedInSDKModule extends ReactContextBaseJavaModule {

    private final String CALLBACK_TYPE_SUCCESS = "success";
    private final String CALLBACK_TYPE_ERROR = "error";
    private final String CALLBACK_TYPE_CANCEL = "cancel";

    private Context mActivityContext;
    private LISessionManager mCallbackManager;
    private Callback mTokenCallback;
    private Callback mLogoutCallback;
    private APIHelper apiHelper;

    private static final String host = "api.linkedin.com";
    private static final String topCardUrl = "https://" + host + "/v1/people/~:(first-name,last-name,public-profile-url)";
    private static final String shareUrl = "https://" + host + "/v1/people/~/shares";


    public LinkedInSDKModule(ReactApplicationContext reactContext, Context activityContext) {
        super(reactContext);

        mActivityContext = activityContext;

        mCallbackManager = LISessionManager.getInstance(activityContext);
        apiHelper = APIHelper.getInstance(activityContext);
    }

    private void consumeCallback(String type, WritableMap map) {
        if (mTokenCallback != null) {
            map.putString("type", type);
            map.putString("provider", "linkedin");

            if(type == CALLBACK_TYPE_SUCCESS){
                mTokenCallback.invoke(null, map);
            }else{
                mTokenCallback.invoke(map, null);
            }

            mTokenCallback = null;
        }
    }

    @Override
    public String getName() {
        return "LinkedInSDKManager";
    }

    @ReactMethod
    public void getProfile(final Callback callback){
        mTokenCallback = callback;
        Log.i("Calling getProfile", "test");

        apiHelper.getRequest(this.mActivityContext, topCardUrl, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse s) {
                JSONObject response = s.getResponseDataAsJson();
                WritableMap map = Arguments.createMap();
                Log.i("LI PROFILE RESPONSE", s.getResponseDataAsString());
                map.putString("response", s.getResponseDataAsString());
                try{
                    map.putString("firstName", response.get("firstName").toString());
                    map.putString("lastName", response.get("lastName").toString());
                    map.putString("publicProfileUrl", response.get("publicProfileUrl").toString());
                } catch(JSONException e){

                }
                consumeCallback(CALLBACK_TYPE_SUCCESS, map);   
            }

            @Override
            public void onApiError(LIApiError error) {
                Log.i("API Error", "2");
                WritableMap map = Arguments.createMap();
                map.putString("LIAuthError", error.toString());
                consumeCallback(CALLBACK_TYPE_ERROR, map);
            }
        });
    }

    @ReactMethod
    public void login(final Callback callback){
        mTokenCallback = callback;
        Log.i("LISessionManager init", "test");
        LISessionManager.getInstance(mActivityContext.getApplicationContext()).init((Activity) mActivityContext, 
            Scope.build(Scope.R_BASICPROFILE), 
            new AuthListener() {
                @Override
                public void onAuthSuccess() {
                    // Authentication was successful.  You can now do
                    // other calls with the SDK.
                    WritableMap map = Arguments.createMap();
                    consumeCallback(CALLBACK_TYPE_SUCCESS, map);
                }

                @Override
                public void onAuthError(LIAuthError error) {
                    // Handle authentication errors
                    Log.i("API Error", "2");
                    WritableMap map = Arguments.createMap();
                    map.putString("LIAuthError", error.toString());
                    consumeCallback(CALLBACK_TYPE_ERROR, map);
                }
            },
            true);
    }

    @ReactMethod
    public void getCurrentToken(final Callback callback) {
        // AccessToken currentAccessToken = AccessToken.getCurrentAccessToken();
        // if(currentAccessToken != null){
        //     callback.invoke(currentAccessToken.getToken());
        // }else{
        //     callback.invoke("");
        // }
        callback.invoke("hello, world!");
    }

    // public boolean handleActivityResult(final int requestCode, final int resultCode, final Intent data) {
    //     return mCallbackManager.onActivityResult(requestCode, resultCode, data);
    // }
}
