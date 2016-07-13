package com.example.rounaksalim95.transit_hub;

import com.loopj.android.http.*;

/**
 * Created by rounaksalim95 on 7/6/16.
 */
public class TransitRestClient {
    // The base url to our rest api
    private static final String BASE_URL = "http://10.0.2.2:5000/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
