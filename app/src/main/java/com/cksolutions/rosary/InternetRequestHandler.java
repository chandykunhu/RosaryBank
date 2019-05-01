package com.cksolutions.rosary;


import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class InternetRequestHandler extends AsyncTask<String,Void,String> {

    public String urlformat = "";
    public Boolean IsPostExecute;
    NotificationHandler objNHandler = new NotificationHandler();


    public void formUrlPrayer(String name,String message,String type)
    {
        IsPostExecute = false;
        urlformat = "http://api.haizenet.com/?title=Prayer+Request+by+"+name+"&message="+message+"&include_image=on&push_type=topic";
        objNHandler.RequestPrayer(name,message,type);

    }
    public void formUrlThanks(String name,String message,String type)
    {
        IsPostExecute = false;
        urlformat = "http://api.haizenet.com/?title=Thanks+Given+by+"+name+"&message="+message+"&include_image=on&push_type=topic";
        objNHandler.GivesThanks(name,message,type);

    }
    public void formUrlDepositRosary(String name,String message)
    {
        IsPostExecute = false;
        urlformat = "http://api.haizenet.com/?title=Rosary+Deposit+by+"+name+"&message="+message+"&include_image=on&push_type=topic";
        objNHandler.DepositRosary(name,message);

    }
    public void formUrlRequestRosary(String name,String message,final String severity)
    {
        IsPostExecute = false;
        urlformat = "http://api.haizenet.com/?title=Rosary+Request+by+"+name+"&message="+message+"&include_image=on&push_type=topic";
        objNHandler.RequestRosary(name,message,severity);

    }

    public void formUrlNewJoin(String name,String message)
    {
        IsPostExecute = false;
        urlformat = "http://api.haizenet.com/?title=New+User+Join+"+name+"&message="+message+"&include_image=on&push_type=topic";
        objNHandler.NewJoin(name,message);

    }



    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        StringBuilder sb=null;
        BufferedReader reader=null;
        String serverResponse=null;
        try {

            URL url = new URL(urlformat);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("GET");
            connection.connect();
            int statusCode = connection.getResponseCode();

            if (statusCode == 200) {
                sb = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
            }
            connection.disconnect();
            if (sb!=null)
                serverResponse=sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        //Toast.makeText(PrayerRequestHandler.this, serverResponse,Toast.LENGTH_SHORT).show();
        IsPostExecute = true;
        return serverResponse;
    }

    @Override
    protected void onPostExecute(String serverResponse) {
        super.onPostExecute(serverResponse);
        IsPostExecute = true;
    }

}
