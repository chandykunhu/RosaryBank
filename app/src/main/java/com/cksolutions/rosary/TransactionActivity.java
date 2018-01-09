package com.cksolutions.rosary;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
import java.util.List;

public class TransactionActivity extends AppCompatActivity {

    private DatabaseReference databaseRef;
    static FirebaseUser user;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseDatabase database;
    private String uid = "";
    private ProgressBar progressBar;
    private TextView tvLoading;
    ListView listView;
    final List<String[]> list = new LinkedList<String[]>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Transaction Details");
        toolbar.setTitleTextColor(Color.WHITE);

        tvLoading = findViewById(R.id.tvLoading);
        progressBar = (ProgressBar) findViewById(R.id.progressBar7);
        //progress bar
        progressBar.setVisibility(View.VISIBLE);
        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(TransactionActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };


        if (user != null) {
            uid = user.getUid();
        }
        if (!uid.isEmpty() && uid.trim().length() > 0) {
            //Toast.makeText(this, uid.toString(), Toast.LENGTH_LONG).show();

            listView = findViewById(R.id.lv_transaction);

            //get firebase database instance
            database = FirebaseDatabase.getInstance();
            //get firebase database instance
            databaseRef = database.getReference("TransactionHistory").child(uid).child("RosaryDeposit");
            databaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    long count =  snapshot.getChildrenCount();
                    //Integer count = 1;
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        if (snapshot.child("TimeLine" + Long.toString(count)).exists()) {

                            if (snapshot.child("TimeLine" + Long.toString(count)).child("Date").exists()) {

                                String date = snapshot.child("TimeLine" + Long.toString(count)).child("Date").getValue().toString();

                                if (snapshot.child("TimeLine" + Long.toString(count)).child("Activity").exists()) {

                                    String activity = snapshot.child("TimeLine" + Long.toString(count)).child("Activity").getValue().toString();
                                    activity = activity.replace("RosaryA", "Normal Rosary");
                                    activity = activity.replace("RosaryB", "Mercy Rosary");

                                    list.add(new String[]{date, activity});
                                }

                            }
                            Log.e(snap.getKey(), snap.getChildrenCount() + "");

                        }
                        count--;
                    }


                    ArrayAdapter<String[]> adapter = new ArrayAdapter<String[]>(TransactionActivity.this, android.R.layout.simple_list_item_2,
                            android.R.id.text1, list) {

                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {

                            View view = super.getView(position, convertView, parent);

                            String[] entry = list.get(position);
                            TextView text1 = view.findViewById(android.R.id.text1);
                            TextView text2 = view.findViewById(android.R.id.text2);

                            text1.setTextColor(Color.YELLOW);
                            text2.setTextColor(Color.WHITE);
                            text1.setText(entry[0]);
                            text2.setText(entry[1]);

                            return view;

                        }
                    };

                    //progress bar
                    progressBar.setVisibility(View.INVISIBLE);
                    if(adapter.getCount() > 0) {

                        listView.setAdapter(adapter);
                        tvLoading.setVisibility(View.INVISIBLE);
                    }
                    else {

                        tvLoading.setVisibility(View.VISIBLE);
                        tvLoading.setText("No History available..");
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
