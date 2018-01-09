package com.cksolutions.rosary;

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

public class NotificationHandler {

    private FirebaseDatabase database;
    private DatabaseReference databaseRef;
    static FirebaseUser user;
    private FirebaseAuth.AuthStateListener authListener;
    private String uid = "";


    NotificationHandler() {
        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }
        if (!uid.isEmpty() && uid.trim().length() > 0) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
            database = FirebaseDatabase.getInstance();
            //get firebase database instance
            databaseRef = database.getReference("Notifications").child(timeStamp);
        }

    }


    //Request Alert
    public void RequestPrayer(final String name,final String message,final String type)
    {
        final String timeStamp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a").format(new Date());
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // get total available quest
                long size = dataSnapshot.getChildrenCount();
                databaseRef.child("N" + (size + 1)).child("Title")
                        .setValue("Prayer Request by " + name + " ");
                databaseRef.child("N" + (size + 1)).child("Message")
                        .setValue(" " + message + " ");
                databaseRef.child("N" + (size + 1)).child("Image")
                        .setValue("http://api.haizenetworks.com/prayerrequest.jpg");
                databaseRef.child("N" + (size + 1)).child("Severity")
                        .setValue("Normal: "+timeStamp+"");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void RequestRosary()
    {

    }


    //Deposit Alert
    public void DepositPrayer()
    {

    }
    public void GivesThanks(final String name,final String message,final String type)
    {
        final String timeStamp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a").format(new Date());
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // get total available quest
                long size = dataSnapshot.getChildrenCount();
                databaseRef.child("N" + (size + 1)).child("Title")
                        .setValue("Thanks Given by " + name + " ");
                databaseRef.child("N" + (size + 1)).child("Message")
                        .setValue(" " + message + " ");
                databaseRef.child("N" + (size + 1)).child("Image")
                        .setValue("http://api.haizenetworks.com/marythanks.jpg");
                databaseRef.child("N" + (size + 1)).child("Severity")
                        .setValue("Normal: "+timeStamp+"");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void DepositRosary(final String name,final String message)
    {
        final String timeStamp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a").format(new Date());
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // get total available quest
                long size = dataSnapshot.getChildrenCount();

                databaseRef.child("N" + (size + 1)).child("Title")
                        .setValue("Rosary Deposit by " + name + " ");
                databaseRef.child("N" + (size + 1)).child("Message")
                        .setValue(" " + message + " ");
                databaseRef.child("N" + (size + 1)).child("Image")
                        .setValue("http://api.haizenetworks.com/rosarydeposit.jpg");
                databaseRef.child("N" + (size + 1)).child("Severity")
                        .setValue("Normal: "+timeStamp+"");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    //Common Alerts
    public void NewJoin(final String name,final String message)
    {
        final String timeStamp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a").format(new Date());
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // get total available quest
                long size = dataSnapshot.getChildrenCount();

                databaseRef.child("N" + (size + 1)).child("Title")
                        .setValue("NewUser Join " + name + " ");
                databaseRef.child("N" + (size + 1)).child("Message")
                        .setValue(" " + message + " ");
                databaseRef.child("N" + (size + 1)).child("Image")
                        .setValue("http://api.haizenetworks.com/newuser.png");
                databaseRef.child("N" + (size + 1)).child("Severity")
                        .setValue("Normal: "+timeStamp+"");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



}
