package com.cksolutions.rosary;

import android.database.DefaultDatabaseErrorHandler;

import com.loopj.android.http.*;
import cz.msebera.android.httpclient.Header;
import static com.cksolutions.rosary.MainActivity.flag;


public class InternetRequestHandler2 {


    public static String urlformat = "";
    public Boolean IsPostExecute;
    NotificationHandler objNHandler = new NotificationHandler();


    public void formUrlPrayer(String name, String message, String type) {
        IsPostExecute = false;
        urlformat = "http://api.haizenet.com/?title=Prayer+Request+by+" + name + "&message=" + message + "&include_image=on&push_type=topic";
        objNHandler.RequestPrayer(name, message, type);

    }

    public void formUrlThanks(String name, String message, String type) {
        IsPostExecute = false;
        urlformat = "http://api.haizenet.com/?title=Thanks+Given+by+" + name + "&message=" + message + "&include_image=on&push_type=topic";
        objNHandler.GivesThanks(name, message, type);

    }

    public void formUrlDepositRosary(String name, String message) {
        IsPostExecute = false;
        urlformat = "http://api.haizenet.com/?title=Rosary+Deposit+by+" + name + "&message=" + message + "&include_image=on&push_type=topic";
        objNHandler.DepositRosary(name, message);

    }

    public void formUrlRequestRosary(String name, String message, final String severity) {
        IsPostExecute = false;
        urlformat = "http://api.haizenet.com/?title=Rosary+Request+by+" + name + "&message=" + message + "&include_image=on&push_type=topic";
        objNHandler.RequestRosary(name, message, severity);

    }

    public void formUrlNewJoin(String name, String message) {
        IsPostExecute = false;
        urlformat = "http://api.haizenet.com/?title=New+User+Join+" + name + "&message=" + message + "&include_image=on&push_type=topic";
        objNHandler.NewJoin(name, message);

    }

    public void Send()
    {
        final int DEFAULT_TIMEOUT = 20 * 1000;
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(DEFAULT_TIMEOUT);
        client.setConnectTimeout(DEFAULT_TIMEOUT);
        client.get(urlformat, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                if (statusCode == 200) {

                    flag = true;
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }

}