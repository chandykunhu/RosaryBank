package com.cksolutions.rosary;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cksolutions.rosary.prayers.common.RosaryPrayer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
//import com.facebook.FacebookSdk;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //Public
    public static boolean IsNewUser;
    public static String UName = "Rosary Bank";
    public static String UEmail = "";
    public static boolean flag = false;
    public static boolean disableRequestButton = false;
    //Private

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private Button btnRosaryDeposit;
    private Button btnRosaryRequest;
    private Button btnPrayerRequest;
    private Button btnGiveThanks;
    private TextView txtvwBalanceGlobal;
    private TextView txtvwBalanceGlobalMercy;
    private TextView txtvwBalance;
    private TextView txtvwBalance2;
    private TextView txtvwAccount;
    private TextView txtvwEmail;
    private ProgressBar progressBar2;
    private String uid = "";
    private FirebaseDatabase database;
    private DatabaseReference databaseRef;
    private DatabaseReference databaseRef2;
    private DatabaseReference databaseRef3;
    private DatabaseReference databaseRef4;
    private DatabaseReference databaseRef5;
    private DatabaseReference databaseRef6;
    private DatabaseReference databaseRef7;


    static FirebaseUser user;
    static Handler handler;
    private static NetworkInfo activeNetwork;
    private static ConnectivityManager cm;
    private ProgressBar progressBar;
    private int countoflister = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Rosary Bank Account");
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, NotificationActivity.class));
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progressBar4);
        //progress bar
        progressBar.setVisibility(View.VISIBLE);
        //Checking Internet Connection
        cm = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetwork = cm.getActiveNetworkInfo();
        if (null == activeNetwork) {

            Toast.makeText(getApplicationContext(), "Please Connect to internet", Toast.LENGTH_LONG).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if (handler == null) {
            handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    CheckRegistration();
                }
            }, 3000);
        }

        //Registering global notification queue
        FirebaseMessaging.getInstance().subscribeToTopic("global");

        IsNewUser = false;
        handler = null;

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();
        //get firebase database instance
        database = FirebaseDatabase.getInstance();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };


        if (user != null) {
            uid = user.getUid();
        }


        //Global Account Balance
        txtvwBalanceGlobal = findViewById(R.id.tvGlobalRosary);
        txtvwBalanceGlobalMercy = findViewById(R.id.tvMercyRosary);

        //Account balance
        txtvwBalance = findViewById(R.id.tvBalance);
        txtvwBalance2 = findViewById(R.id.tvBalance2);

        //Navigation View
        View hView = navigationView.getHeaderView(0);
        txtvwAccount = (TextView) hView.findViewById(R.id.tvAccountName);
        txtvwEmail = (TextView) hView.findViewById(R.id.tvEmailId);
        btnRosaryDeposit = (Button) findViewById(R.id.btnDepositRosary);
        btnRosaryRequest = (Button) findViewById(R.id.btnRequestRosary);
        btnPrayerRequest = (Button) findViewById(R.id.btnRequestPrayer);
        btnGiveThanks = (Button) findViewById(R.id.btnThanks);

         /* For showing Name in Navigation View*/

        if (!uid.isEmpty() && uid.trim().length() > 0) {

            //get firebase database instance
            databaseRef4 = database.getReference("Bank").child("Rosary").child("RosaryDeposit");
            databaseRef4.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot balanceSnapshot) {
                    if (balanceSnapshot.getValue() != null) {
                        String globalRosary = balanceSnapshot.getValue().toString();

                        if (globalRosary.trim().length() > 0) {
                            txtvwBalanceGlobal.setText(globalRosary);
                        }
                    }
                    //progress bar
                    progressBar.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // ...
                }
            });

            //get firebase database instance
            databaseRef5 = database.getReference("Bank").child("MercyRosary").child("RosaryDeposit");
            databaseRef5.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot balanceSnapshot) {
                    if (balanceSnapshot.getValue() != null) {
                        String globalRosary = balanceSnapshot.getValue().toString();

                        if (globalRosary.trim().length() > 0) {
                            txtvwBalanceGlobalMercy.setText(globalRosary);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // ...
                }
            });


            /* For showing Name in Navigation View*/

            //get firebase database instance
            databaseRef = database.getReference("Users");

            databaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (!snapshot.child(uid).exists()) {
                        //Register new user
                        //
                        IsNewUser = true;
                        //Put it in another thread after some time
                        //startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
                        //finish();
                    } else {
                        if (snapshot.child(uid).child("name").getValue() != null) {
                            UName = snapshot.child(uid).child("name").getValue().toString();
                            txtvwAccount.setText(UName);
                        }
                        if (snapshot.child(uid).child("email").getValue() != null) {
                            UEmail = snapshot.child(uid).child("email").getValue().toString();
                            txtvwEmail.setText(UEmail);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    databaseError.getMessage();

                }
            });


            /* For showing Account Balance*/

            //get firebase database instance
            databaseRef2 = database.getReference("Rosaries").child(uid).child("RosaryA");
            databaseRef3 = database.getReference("Rosaries").child(uid).child("RosaryB");

            databaseRef2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot balanceSnapshot) {
                    if (balanceSnapshot.getValue() != null) {
                        String balanceRosary = balanceSnapshot.getValue().toString();

                        if (balanceRosary.trim().length() > 0) {
                            txtvwBalance.setText(balanceRosary);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // ...
                }
            });

            databaseRef3.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot balanceSnapshot) {
                    if (balanceSnapshot.getValue() != null) {
                        String balanceRosary = balanceSnapshot.getValue().toString();

                        if (balanceRosary.trim().length() > 0) {
                            txtvwBalance2.setText(balanceRosary);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // ...
                }
            });

            databaseRef6 = database.getReference("Rosaries").child(uid).child("RosaryBankA");
            databaseRef7 = database.getReference("Rosaries").child(uid).child("RosaryBankB");
            databaseRef6.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot balanceSnapshot) {
                    if (balanceSnapshot.getValue() != null) {
                        String balanceRosary = balanceSnapshot.getValue().toString();

                        if (balanceRosary.trim().length() > 0) {
                            if(Integer.parseInt(balanceRosary) < 0)
                            {
                                btnRosaryRequest.setEnabled(false);
                                disableRequestButton = true;
                                Toast.makeText(MainActivity.this, "Deposit "+Math.abs(Integer.parseInt(balanceRosary))+" more " +
                                        "Mercy Rosary in Rosary Bank to Request Rosary", Toast.LENGTH_LONG).show();
                            }
                            else {
                                if(!disableRequestButton)
                                btnRosaryRequest.setEnabled(true);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // ...
                }
            });
            databaseRef7.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot balanceSnapshot) {
                    if (balanceSnapshot.getValue() != null) {
                        String balanceRosary = balanceSnapshot.getValue().toString();

                        if (balanceRosary.trim().length() > 0) {
                            if (Integer.parseInt(balanceRosary) < 0) {
                                btnRosaryRequest.setEnabled(false);
                                disableRequestButton = true;
                                Toast.makeText(MainActivity.this, "Deposit " + Math.abs(Integer.parseInt(balanceRosary)) + " more " +
                                        "Mercy Rosary in Rosary Bank to Request Rosary", Toast.LENGTH_LONG).show();
                            } else {
                                if (!disableRequestButton)
                                    btnRosaryRequest.setEnabled(true);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // ...
                }
            });



            //Deposit Rosary button click

            btnRosaryDeposit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (Isconnected()) {
                        if (IsNewUser) {
                            startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
                            //databaseRef.setValue(250);
                        } else {

                            startActivity(new Intent(MainActivity.this, DepositRosaryActivity.class));
                        }
                    } else {
                        Snackbar.make(v, "Check Internet Connection", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
            });

            //Request Rosary button click

            btnRosaryRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (Isconnected()) {
                        if (IsNewUser) {
                            startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
                            //databaseRef.setValue(250);
                        } else {

                            startActivity(new Intent(MainActivity.this, RosaryRequestActivity.class));
                        }
                    } else {
                        Snackbar.make(v, "Check Internet Connection", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
            });

            //Request Prayer button click

            btnPrayerRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Isconnected()) {
                        startActivity(new Intent(MainActivity.this, PrayerRequestActivity.class));
                    }
                    else {
                        Snackbar.make(v, "Check Internet Connection", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
            });


            //Give Thanks button click

            btnGiveThanks.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Isconnected()) {
                        startActivity(new Intent(MainActivity.this, GiveThanksActivity.class));
                    }
                    else {
                        Snackbar.make(v, "Check Internet Connection", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
            });


        } else {
            Toast.makeText(this, "User Id failed to retrieve", Toast.LENGTH_LONG).show();
        }


    }

   public void CheckRegistration() {
        if (IsNewUser) {
            startActivity(new Intent(MainActivity.this, RegistrationActivity.class));

        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        if (id == R.id.action_logout) {
            handler = null;
            auth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return true;
        }
        if (id == R.id.action_transactiondetails) {

            startActivity(new Intent(MainActivity.this, TransactionActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_account) {
            startActivity(new Intent(MainActivity.this, AccountActivity.class));

        } else if (id == R.id.nav_logout) {
            handler = null;
            auth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
        } else if (id == R.id.nav_feedback) {
            final String appPackageName = getPackageName(); // package name of the app
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        } else if (id == R.id.nav_aboutus) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://haizenet.com")));
        } else if (id == R.id.nav_community) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/jykoonammoochi/")));
        } else if (id == R.id.nav_notification) {
            startActivity(new Intent(MainActivity.this, NotificationActivity.class));
            //Toast.makeText(this, "This feature is under construction", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_getrewards) {
            startActivity(new Intent(MainActivity.this, DonateActivity.class));
        } else if (id == R.id.nav_prayers) {
            startActivity(new Intent(MainActivity.this, RosaryPrayer.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
