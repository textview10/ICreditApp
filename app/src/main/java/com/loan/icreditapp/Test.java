package com.loan.icreditapp;

import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Test {

    private void test1() {
        JSONArray array = new JSONArray();
        for (int i =0; i < array.length(); i++){
            JSONObject jsonObject = array.optJSONObject(i);
            String key = jsonObject.optString("key");
            String value = jsonObject.optString("val");
        }
    }
}
