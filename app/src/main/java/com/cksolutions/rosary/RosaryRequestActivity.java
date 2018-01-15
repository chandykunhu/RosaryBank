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
import android.widget.CheckBox;
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

public class RosaryRequestActivity extends AppCompatActivity {

    private EditText inputRosarydedicatedTo, inputRosaryType, inputShortNote;
    private FirebaseAuth.AuthStateListener authListener;
    private static final String TAG = MainActivity.class.getSimpleName();
    private FirebaseDatabase database;
    public static NetworkInfo activeNetwork;
    public static ConnectivityManager cm;
    private ProgressBar progressBar;
    private DatabaseReference databaseRef;
    private DatabaseReference databaseRef2;
    private DatabaseReference databaseRef3;
    private Integer SelectedType;
    private String RosaryType = "";
    private Integer SelectedRequestedType;
    private String uid = "";
    private String severity = "";
    static FirebaseUser user;
    private Button btnRequest;
    private CheckBox chkboxAgree;
    private Integer rosaryAllocated = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rosary_request);

        progressBar = (ProgressBar) findViewById(R.id.progressBar10);
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
                    startActivity(new Intent(RosaryRequestActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        inputRosarydedicatedTo = (EditText) findViewById(R.id.etRosaryDedicatedTo);
        inputShortNote = (EditText) findViewById(R.id.etShortNote);
        chkboxAgree = (CheckBox) findViewById(R.id.checkBoxAgree);

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

        //Spinner Requested Type
        final Spinner spinner2 = (Spinner) findViewById(R.id.spinnerRequestType);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.rosary_request_type, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {

                SelectedRequestedType = spinner2.getSelectedItemPosition();
                if (SelectedRequestedType == 0) {
                    Toast.makeText(getApplicationContext(), "You will be allocated 10 Rosaries from Rosary Bank", Toast.LENGTH_SHORT).show();
                    rosaryAllocated = 10;
                    severity = "Normal";
                } else if (SelectedRequestedType == 1) {
                    Toast.makeText(getApplicationContext(), "You will be allocated 20 Rosaries from Rosary Bank", Toast.LENGTH_SHORT).show();
                    rosaryAllocated = 20;
                    severity = "Urgent";
                } else if (SelectedRequestedType == 2) {
                    Toast.makeText(getApplicationContext(), "You will be allocated 30 Rosaries from Rosary Bank", Toast.LENGTH_SHORT).show();
                    rosaryAllocated = 30;
                    severity = "Critical";
                }
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

            //get firebase database instance
            database = FirebaseDatabase.getInstance();


            btnRequest = (Button) findViewById(R.id.btnRequest);
            btnRequest.setEnabled(true);

            btnRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (Isconnected()) {


                        final String name = inputRosarydedicatedTo.getText().toString().trim();
                        final String note = inputShortNote.getText().toString().trim();

                        if (TextUtils.isEmpty(name)) {
                            Toast.makeText(getApplicationContext(), "Enter name!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (TextUtils.isEmpty(note)) {
                            Toast.makeText(getApplicationContext(), "Enter note!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (!chkboxAgree.isChecked()) {
                            Toast.makeText(getApplicationContext(), "Please tick the box to Agree!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (SelectedType == 0) {
                            databaseRef = database.getReference("Bank").child("Rosary");
                            RosaryType = "RosaryA";
                        } else {
                            databaseRef = database.getReference("Bank").child("MercyRosary");
                            RosaryType = "RosaryB";
                        }
                        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot balanceSnapshot) {
                                if (balanceSnapshot.child("RosaryDeposit").getValue() != null) {
                                    String balanceRosary = balanceSnapshot.child("RosaryDeposit").getValue().toString();

                                    if (balanceRosary.trim().length() > 0) {

                                        int Rosary = Integer.parseInt(balanceRosary);
                                        if (Rosary > rosaryAllocated) {
                                            databaseRef.child("RosaryDeposit").setValue(Integer.toString(Rosary - rosaryAllocated));

                                            if (balanceSnapshot.child("RosaryRequest").getValue() != null) {
                                                String balanceRequest = balanceSnapshot.child("RosaryRequest").getValue().toString();
                                                if (balanceRequest.trim().length() > 0) {

                                                    int Request = Integer.parseInt(balanceRequest);
                                                    databaseRef.child("RosaryRequest").setValue(Integer.toString(Request + rosaryAllocated));
                                                }
                                            }
                                        } else {
                                            Toast.makeText(RosaryRequestActivity.this, "Sorry.There is not enough balance in RosaryBank to " +
                                                    "allocate " + rosaryAllocated + " Rosaries. Ask for prayer request to deposit rosaries.", Toast.LENGTH_LONG).show();
                                            finish();
                                        }
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // ...
                            }
                        });



                        databaseRef2 = database.getReference("Rosaries").child(uid);
                        databaseRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot balanceSnapshot) {
                                String balanceRosary = "";
                                String balanceRosaryBank = "";
                                if (SelectedType == 0) {
                                    if (balanceSnapshot.child("RosaryA").getValue() != null) {
                                        balanceRosary = balanceSnapshot.child("RosaryA").getValue().toString();
                                    }
                                    if (balanceSnapshot.child("RosaryBankA").getValue() != null) {
                                        balanceRosaryBank = balanceSnapshot.child("RosaryBankA").getValue().toString();
                                    }

                                    if (balanceRosary.trim().length() > 0) {

                                        if (balanceRosaryBank.trim().length() > 0) {

                                            int RosaryBank = Integer.parseInt(balanceRosaryBank);
                                            int balance = RosaryBank - rosaryAllocated;
                                            databaseRef2.child("RosaryBankA").setValue(Integer.toString(balance));
                                            int Rosary = Integer.parseInt(balanceRosary);

                                            if (balance >= 0) {
                                                databaseRef2.child("RosaryA").setValue(Integer.toString(Rosary - rosaryAllocated));

                                            } else if (balance < 0) {
                                                databaseRef2.child("RosaryA").setValue(Integer.toString(Rosary - RosaryBank));
                                            }

                                        }
                                    }
                                } else if (SelectedType == 1) {
                                    if (balanceSnapshot.child("RosaryB").getValue() != null) {
                                        balanceRosary = balanceSnapshot.child("RosaryB").getValue().toString();
                                    }
                                    if (balanceSnapshot.child("RosaryBankB").getValue() != null) {
                                        balanceRosaryBank = balanceSnapshot.child("RosaryBankB").getValue().toString();
                                    }

                                    if (balanceRosary.trim().length() > 0) {

                                        if (balanceRosaryBank.trim().length() > 0) {

                                            int RosaryBank = Integer.parseInt(balanceRosaryBank);
                                            int balance = RosaryBank - rosaryAllocated;
                                            databaseRef2.child("RosaryBankB").setValue(Integer.toString(balance));
                                            int Rosary = Integer.parseInt(balanceRosary);

                                            if (balance >= 0) {
                                                databaseRef2.child("RosaryB").setValue(Integer.toString(Rosary - rosaryAllocated));

                                            } else if (balance < 0) {
                                                databaseRef2.child("RosaryB").setValue(Integer.toString(Rosary - RosaryBank));
                                            }

                                        }
                                    }
                                }
                            }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // ...
                                }
                            });

                        String strreplace = (SelectedType == 0) ? "Normal Rosary" : "Mercy Rosary";

                        databaseRef3 = database.getReference("TransactionHistory").child(uid).child("RosaryRequest");
                        databaseRef3.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // get total available quest
                                long size = dataSnapshot.getChildrenCount();
                                String timeStamp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a").format(new Date());

                                databaseRef3.child("TimeLine" + (size + 1)).child("Activity")
                                        .setValue("Requested " + rosaryAllocated + " " +  RosaryType + " for "+name+". Note:" + note);
                                databaseRef3.child("TimeLine" + (size + 1)).child("Date")
                                        .setValue(timeStamp);
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                        btnRequest.setEnabled(false);
                        //progress bar
                        progressBar.setVisibility(View.VISIBLE);

                        //Notifications
                        InternetRequestHandler objInternetHandler = new InternetRequestHandler();
                        objInternetHandler.formUrlRequestRosary(UName, "Gifted " + rosaryAllocated + " " + strreplace + " to " + name + " by Rosary Bank. Note:" + note, severity);
                        objInternetHandler.execute();

                        Toast.makeText(RosaryRequestActivity.this, "By grace of GOD you are gifted " + rosaryAllocated + " Rosaries " +
                                "from Rosary Bank. \n GOD bless you", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
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


    public boolean Isconnected() {
        cm = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork)
            return true;
        else
            return false;
    }
}
