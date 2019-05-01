package com.cksolutions.rosary;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.cksolutions.rosary.MainActivity.UName;

public class PrayerRequestActivity extends AppCompatActivity {
    private EditText  inputPrayer; //inputRequesterName,
    private FirebaseAuth.AuthStateListener authListener;
    private static final String TAG = MainActivity.class.getSimpleName();
    private FirebaseDatabase database;
    private DatabaseReference databaseRef;
    private DatabaseReference databaseRef2;
    private String uid = "";
    static FirebaseUser user;
    private Button btnSendRequest;
    private String RosaryType = "";
    private Integer SelectedType;
    private long usercount;
    public static NetworkInfo activeNetwork;
    public static ConnectivityManager cm;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prayer_request);


        //User Authentication
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(PrayerRequestActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
        progressBar = (ProgressBar) findViewById(R.id.progressBar3);

        //Checking Internet Connection
        cm = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetwork = cm.getActiveNetworkInfo();
        if (null == activeNetwork) {

            Toast.makeText(getApplicationContext(), "Please Connect to internet", Toast.LENGTH_LONG).show();
        }

        //Spinner
        final Spinner spinner = (Spinner) findViewById(R.id.spSendTo);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Request_Queue_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {

                SelectedType = spinner.getSelectedItemPosition();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });


        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }


        if (!uid.isEmpty() && uid.trim().length() > 0) {

            btnSendRequest = (Button) findViewById(R.id.btSendRequest);
            btnSendRequest.setEnabled(true);
            //inputRequesterName = (EditText) findViewById(R.id.etName);
            inputPrayer = (EditText) findViewById(R.id.etGiveThanks);


            btnSendRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Isconnected()) {

                        //final String reqname = inputRequesterName.getText().toString().trim();
                        final String prayer = inputPrayer.getText().toString().trim();

                    /*if (TextUtils.isEmpty(reqname)) {
                        Toast.makeText(getApplicationContext(), "Enter your name!", Toast.LENGTH_SHORT).show();
                        return;
                    }*/
                        if (TextUtils.isEmpty(prayer)) {
                            Toast.makeText(getApplicationContext(), "Enter your prayer request!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String queuetype = "";
                        if (SelectedType == 0)
                            queuetype = "global";
                        if (SelectedType == 1)
                            queuetype = "jesusyouth";



                        //Needed progress bar if any waiting required for confirmation
                    /*while (!objPrayerHandler.IsPostExecute) {

                        //progressbar
                        Integer i = 0;
                    }*/

                        //get firebase database instance
                        database = FirebaseDatabase.getInstance();
                        databaseRef = database.getReference("Users");
                        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // get total available users
                                usercount = dataSnapshot.getChildrenCount();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        databaseRef2 = database.getReference("TransactionHistory").child(uid).child("PrayerRequest");
                        databaseRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // get total available quest
                                long size = dataSnapshot.getChildrenCount();
                                String timeStamp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.ENGLISH).format(new Date());

                                databaseRef2.child("TimeLine" + (size + 1)).child("Activity")
                                        .setValue("Requested for prayer. Note:" + prayer);
                                databaseRef2.child("TimeLine" + (size + 1)).child("Date")
                                        .setValue(timeStamp);
                                Toast.makeText(PrayerRequestActivity.this, "Prayer requested to " + (usercount - 1) + " users." +
                                        " Your prayer request will be answered soon. God bless you", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });



                        btnSendRequest.setEnabled(false);
                        //progress bar
                        progressBar.setVisibility(View.VISIBLE);

                        //Notifications
                        //InternetRequestHandler objInternetHandler = new InternetRequestHandler();
                        //objInternetHandler.formUrlPrayer(UName, prayer, queuetype);
                        //objInternetHandler.execute();



                        InternetRequestHandler2 obj2 = new InternetRequestHandler2();
                        obj2.formUrlPrayer(UName, prayer, queuetype);
                        obj2.Send();

                        finish();

                    }
                    else {
                        Snackbar.make(v, "Check Internet Connection", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
            });

        } else {
            Log.e(TAG, "User data is null!");
            //Toast
        }


    }
    public boolean Isconnected()
    {
        cm = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork)
            return true;
        else
            return false;
    }
}
