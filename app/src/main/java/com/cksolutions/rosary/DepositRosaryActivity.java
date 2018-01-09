package com.cksolutions.rosary;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import static com.cksolutions.rosary.MainActivity.UName;


public class DepositRosaryActivity extends AppCompatActivity {
    private EditText inputRosaryCount, inputRosaryType, inputShortNote;
    private FirebaseAuth.AuthStateListener authListener;
    private static final String TAG = MainActivity.class.getSimpleName();
    private FirebaseDatabase database;
    private DatabaseReference databaseRef;
    private DatabaseReference databaseRef2;
    private DatabaseReference databaseRef3;
    private DatabaseReference databaseRef4;
    private DatabaseReference databaseRef5;
    private DatabaseReference databaseRef6;
    private DatabaseReference databaseRef7;
    private String uid = "";
    static FirebaseUser user;
    private Button btnSave;
    private String RosaryType = "";
    private Integer SelectedType;
    private Integer SelectedDedicatedType;
    static boolean IsNewUser;
    public static NetworkInfo activeNetwork;
    public static ConnectivityManager cm;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit_rosary);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Rosary Deposit");
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        //Checking Internet Connection
        cm = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetwork = cm.getActiveNetworkInfo();
        if (null == activeNetwork) {

            Toast.makeText(getApplicationContext(), "Please Connect to internet", Toast.LENGTH_LONG).show();
        }

        //User Authentication
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(DepositRosaryActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        //Spinner Rosary Type
        final Spinner spinner = (Spinner) findViewById(R.id.spinnerRosaryType);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.rosary_type, android.R.layout.simple_spinner_item);
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


        //Spinner Dedicated To
        final Spinner spinner2 = (Spinner) findViewById(R.id.spinnerDedicatedTo);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.dedicated_to, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {

                SelectedDedicatedType = spinner2.getSelectedItemPosition();
                if (SelectedDedicatedType == 0) {
                    inputShortNote.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "This will be deposited in Rosary Bank", Toast.LENGTH_SHORT).show();
                } else {
                    inputShortNote.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(),
                            "This will be directly dedicated to selected one and can see in your account..\nThis wont be deposited in Rosary Bank",
                            Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });


        //progressBar.setVisibility(View.VISIBLE);


        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }


        if (!uid.isEmpty() && uid.trim().length() > 0) {

            //get firebase database instance
            database = FirebaseDatabase.getInstance();

            databaseRef4 = database.getReference("Rosaries").child(uid);
            databaseRef4.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        databaseRef4.child("RosaryA").setValue("0");
                        databaseRef4.child("RosaryB").setValue("0");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            btnSave = (Button) findViewById(R.id.btnSave);
            btnSave.setEnabled(true);
            inputRosaryCount = (EditText) findViewById(R.id.etRosaryCount);
            inputShortNote = (EditText) findViewById(R.id.etShortNote);

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(Isconnected()) {
                        final String count = inputRosaryCount.getText().toString().trim();
                        final String note = inputShortNote.getText().toString().trim();

                        if (TextUtils.isEmpty(count) || Integer.parseInt(count) <= 0) {
                            Toast.makeText(getApplicationContext(), "Enter valid count!", Toast.LENGTH_SHORT).show();
                            inputRosaryCount.setText("");
                            return;
                        }
                        if (SelectedDedicatedType != 0) {

                            if (TextUtils.isEmpty(note)) {
                                Toast.makeText(getApplicationContext(), "Enter note!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                        if (SelectedType == 0) {
                            databaseRef = database.getReference("Rosaries").child(uid).child("RosaryA");

                            RosaryType = "RosaryA";

                            databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot balanceSnapshot) {
                                    if (balanceSnapshot.getValue() != null) {
                                        String balanceRosary = balanceSnapshot.getValue().toString();

                                        if (balanceRosary.trim().length() > 0) {

                                            int Rosary = Integer.parseInt(balanceRosary);
                                            databaseRef.setValue(Integer.toString(Rosary + Integer.parseInt(count)));
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // ...
                                }
                            });

                        } else if (SelectedType == 1) {

                            databaseRef2 = database.getReference("Rosaries").child(uid).child("RosaryB");

                            RosaryType = "RosaryB";

                            databaseRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot balanceSnapshot) {
                                    if (balanceSnapshot.getValue() != null) {
                                        String balanceRosary = balanceSnapshot.getValue().toString();

                                        if (balanceRosary.trim().length() > 0) {

                                            int Rosary = Integer.parseInt(balanceRosary);
                                            databaseRef2.setValue(Integer.toString(Rosary + Integer.parseInt(count)));
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // ...
                                }
                            });
                        }

                        if (SelectedDedicatedType == 0) {
                            //get firebase database instance


                            if (SelectedType == 0) {
                                databaseRef5 = database.getReference("Bank").child("Rosary").child("RosaryDeposit");
                                databaseRef5.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot balanceSnapshot) {
                                        if (balanceSnapshot.getValue() != null) {
                                            String globalRosary = balanceSnapshot.getValue().toString();

                                            if (globalRosary.trim().length() > 0) {

                                                int Rosary = Integer.parseInt(globalRosary);

                                                databaseRef5.setValue(Integer.toString(Rosary + Integer.parseInt(count)));

                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        // ...
                                    }
                                });
                            }
                            if (SelectedType == 1) {
                                databaseRef7 = database.getReference("Bank").child("MercyRosary").child("RosaryDeposit");

                                databaseRef7.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot balanceSnapshot) {
                                        if (balanceSnapshot.getValue() != null) {
                                            String globalRosary = balanceSnapshot.getValue().toString();

                                            if (globalRosary.trim().length() > 0) {

                                                int Rosary = Integer.parseInt(globalRosary);

                                                databaseRef7.setValue(Integer.toString(Rosary + (Integer.parseInt(count))));
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        // ...
                                    }
                                });
                            }
                        }


                        databaseRef6 = database.getReference("UserFeatures");

                        databaseRef6.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                int rewardpoints = 10;
                                if (snapshot.child(uid).exists()) {
                                    if (snapshot.child(uid).child("points").getValue() != null) {
                                        rewardpoints = Integer.parseInt(snapshot.child(uid).child("points").getValue().toString()) + 10;
                                        databaseRef6.child(uid).child("points").setValue(rewardpoints);
                                    }
                                /*if (snapshot.child(uid).child("rank").getValue() != null) {
                                    txtvwRank.setText(snapshot.child(uid).child("rank").getValue().toString());
                                }*/
                                } else {
                                    databaseRef6.child(uid).child("points").setValue(rewardpoints);

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        final String strNote = (note.trim().length() == 0) ? "For Rosary Bank" : note;
                        String strreplace = (SelectedType == 0) ? "Normal Rosary" : "Mercy Rosary";



                        databaseRef3 = database.getReference("TransactionHistory").child(uid).child("RosaryDeposit");
                        databaseRef3.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // get total available quest
                                long size = dataSnapshot.getChildrenCount();
                                String timeStamp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a").format(new Date());

                                databaseRef3.child("TimeLine" + (size + 1)).child("Activity")
                                        .setValue("Added " + count + " " + RosaryType + ". Note:" + strNote);
                                databaseRef3.child("TimeLine" + (size + 1)).child("Date")
                                        .setValue(timeStamp);
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        btnSave.setEnabled(false);
                        //progress bar
                        progressBar.setVisibility(View.VISIBLE);

                        //Notifications
                        InternetRequestHandler objInternetHandler = new InternetRequestHandler();
                        objInternetHandler.formUrlDepositRosary(UName, "Added " + count + " " + strreplace + ". Note:" + strNote);
                        objInternetHandler.execute();

                        Toast.makeText(DepositRosaryActivity.this, "Rosary deposited. Thanks for your prayers. You got 10 points",
                                Toast.LENGTH_SHORT).show();
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
