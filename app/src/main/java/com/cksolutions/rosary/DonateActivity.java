package com.cksolutions.rosary;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;
//import com.google.android.gms.ads.MobileAds;
//import com.google.android.gms.ads.reward.RewardItem;
//import com.google.android.gms.ads.reward.RewardedVideoAd;
//import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;


public class DonateActivity extends AppCompatActivity /*implements RewardedVideoAdListener*/{

    //private RewardedVideoAd mRewardedVideoAd;
    //private AdView mAdView;
    private DatabaseReference databaseRef;
    static FirebaseUser user;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseDatabase database;
    private String uid = "";
    private TextView txtvwDonation;
    private TextView txtvwRank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);

        //Ad unit
        //mAdView = findViewById(R.id.adView);
        //AdRequest adRequest = new AdRequest.Builder().build();
        //mAdView.loadAd(adRequest);

        //mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
       // mRewardedVideoAd.setRewardedVideoAdListener(this);
        //loadRewardedVideoAd();


        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(DonateActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };


        if (user != null) {
            uid = user.getUid();
        }
        if (!uid.isEmpty() && uid.trim().length() > 0) {
            //Toast.makeText(this, uid.toString(), Toast.LENGTH_LONG).show();

            //get firebase database instance
            database = FirebaseDatabase.getInstance();

            //get firebase database instance
            databaseRef = database.getReference("UserFeatures");
            txtvwDonation = findViewById(R.id.tvDonation);
            txtvwRank = findViewById(R.id.tvRank);

            databaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.child(uid).exists()) {
                        if (snapshot.child(uid).child("points").getValue() != null) {
                            txtvwDonation.setText(snapshot.child(uid).child("points").getValue().toString());
                        }
                        if (snapshot.child(uid).child("rank").getValue() != null) {
                            txtvwRank.setText(snapshot.child(uid).child("rank").getValue().toString());
                        }
                    } else {

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });




        }else {
            Toast.makeText(this, "User Id failed to retrieve", Toast.LENGTH_LONG).show();
        }



        //Button click
        Button clickButton = (Button) findViewById(R.id.btDonate);
        clickButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //loadRewardedVideoAd();
            }
        });
    }


    /*private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-7271379408439527/7826956778",
                new AdRequest.Builder().build());
    }



    @Override
    public void onRewarded(RewardItem reward) {
        Toast.makeText(this, "Congrats you got "+reward.getAmount()+" "+ reward.getType(), Toast.LENGTH_SHORT).show();
        int rewardpoints = Integer.parseInt(txtvwDonation.getText().toString()) + reward.getAmount();
        databaseRef.child(uid).child("points").setValue(rewardpoints);
        txtvwDonation.setText(String.format(Locale.getDefault(),"%d", rewardpoints));

    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        //Toast.makeText(this, "onRewardedVideoAdLeftApplication",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdClosed() {
        //Toast.makeText(this, "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {
        //Toast.makeText(this, "onRewardedVideoAdFailedToLoad", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        //Toast.makeText(this, "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show();
        if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        }

    }

    @Override
    public void onRewardedVideoAdOpened() {
        //Toast.makeText(this, "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoStarted() {
        //Toast.makeText(this, "onRewardedVideoStarted", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onResume() {
        mRewardedVideoAd.resume(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        mRewardedVideoAd.pause(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mRewardedVideoAd.destroy(this);
        super.onDestroy();
    }*/
}
