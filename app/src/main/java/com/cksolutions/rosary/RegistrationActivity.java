package com.cksolutions.rosary;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import static com.cksolutions.rosary.MainActivity.IsNewUser;
import static com.cksolutions.rosary.MainActivity.UName;


public class RegistrationActivity extends AppCompatActivity {

    private EditText inputName, inputAge, inputParish, inputMob, inputCountry;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseDatabase database;
    private DatabaseReference databaseRef;
    private DatabaseReference databaseRef2;
    static FirebaseUser user;
    private String uid = "", email = "";
    private static final String TAG = MainActivity.class.getSimpleName();
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("User Profile");
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


        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        btnRegister = (Button) findViewById(R.id.btRegister);
        inputName = (EditText) findViewById(R.id.etName);
        inputAge = (EditText) findViewById(R.id.etAge);
        inputParish = (EditText) findViewById(R.id.etParish);
        inputMob = (EditText) findViewById(R.id.etMobileNumber);
        inputCountry = (EditText) findViewById(R.id.etCountry);


        //if user already exists then show details
        //progressBar.setVisibility(View.VISIBLE);
        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            uid = user.getUid();
            email = user.getEmail();
        }
        if (!uid.isEmpty() && uid.trim().length() > 0) {

            //get firebase database instance
            database = FirebaseDatabase.getInstance();
            databaseRef2 = database.getReference("Users");

            databaseRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.child(uid).exists()) {

                        if (snapshot.child(uid).child("name").getValue() != null) {
                            inputName.setText(snapshot.child(uid).child("name").getValue().toString());
                        }
                        if (snapshot.child(uid).child("age").getValue() != null) {
                            inputAge.setText(snapshot.child(uid).child("age").getValue().toString());
                        }
                        if (snapshot.child(uid).child("mobile").getValue() != null) {
                            inputMob.setText(snapshot.child(uid).child("mobile").getValue().toString());
                        }
                        if (snapshot.child(uid).child("parish").getValue() != null) {
                            inputParish.setText(snapshot.child(uid).child("parish").getValue().toString());
                        }
                        if (snapshot.child(uid).child("country").getValue() != null) {
                            inputCountry.setText(snapshot.child(uid).child("country").getValue().toString());
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = inputName.getText().toString().trim();
                String age = inputAge.getText().toString().trim();
                String parish = inputParish.getText().toString().trim();
                String mobilenumber = inputMob.getText().toString().trim();
                String country = inputCountry.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getApplicationContext(), "Enter name!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(age)) {
                    Toast.makeText(getApplicationContext(), "Enter age!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(parish)) {
                    Toast.makeText(getApplicationContext(), "Enter parish!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(mobilenumber)) {
                    Toast.makeText(getApplicationContext(), "Enter mobile number!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(country)) {
                    Toast.makeText(getApplicationContext(), "Enter country!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!uid.isEmpty() && uid.trim().length() > 0) {
                    databaseRef = database.getReference("Users").child(uid);
                    databaseRef.child("email").setValue(email);

                    CreateUser(age, mobilenumber, name, parish, country);

                } else {
                    Log.e(TAG, "User data is null!");
                    //Toast
                }
            }

        });
    }


    public void CreateUser(String age, String mobile, String name, String parish, String country) {
        Post post = new Post(age, mobile, name, parish, country);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(uid, postValues);

        databaseRef.updateChildren(postValues);

        if (IsNewUser) {
            String strreplace = (!name.isEmpty()) ? name : user.getEmail();
            //sInternetRequestHandler objInternetHandler = new InternetRequestHandler();
            //objInternetHandler.formUrlNewJoin(strreplace, "Hi " + strreplace + " just joined Rosary Bank..");
            //objInternetHandler.execute();


            InternetRequestHandler2 obj2 = new InternetRequestHandler2();
            obj2.formUrlNewJoin(strreplace, "Hi " + strreplace + " just joined Rosary Bank..");
            obj2.Send();
        }

        // User data change listener
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Check for null
                if (user != null) {

                    Log.e(TAG, "User data is changed!" + user.getUid() + ", " + user.getEmail());

                    Toast.makeText(RegistrationActivity.this, "Profile Updated",
                            Toast.LENGTH_SHORT).show();
                    IsNewUser = false;
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Toast.makeText(RegistrationActivity.this, "Registration Failed. Try again Later",
                //      Toast.LENGTH_SHORT).show();
                finish();
                Log.e(TAG, "Failed to read user", error.toException());
            }
        });


    }

    // Changing button text
    private void toggleButton() {
        if (TextUtils.isEmpty(uid)) {
            btnRegister.setText("Register");
        } else {
            btnRegister.setText("Update");
        }
    }

}
