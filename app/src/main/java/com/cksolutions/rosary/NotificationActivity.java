package com.cksolutions.rosary;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.cksolutions.rosary.app.Config;
import com.cksolutions.rosary.util.NotificationUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;
//import static com.cksolutions.rosary.service.MyFirebaseMessagingService.listitems;


public class NotificationActivity extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {


   // public static boolean IsMessageRecv = false;

    private static final String TAG = MainActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private TextView txtRegId, txtMessage;
    private TextView txtNotifications;

    //private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private List<NotificationItems> cartList;
    public  CartListAdapter mAdapter;
    private CoordinatorLayout coordinatorLayout;
    private FirebaseDatabase database;
    private DatabaseReference databaseRef;
    private DatabaseReference databaseRef2;
    static FirebaseUser user;
    private FirebaseAuth.AuthStateListener authListener;
    private String uid = "";
    private ProgressBar progressBar;
    List<NotificationItems> alllistitems = new LinkedList<NotificationItems>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Notifications");
        setSupportActionBar(toolbar);

        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(NotificationActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
        progressBar = (ProgressBar) findViewById(R.id.progressBar5);
        //progress bar
        progressBar.setVisibility(View.VISIBLE);
        displayFirebaseRegId();

        txtNotifications = (TextView) findViewById(R.id.tvNotifications);
        txtNotifications.setText("Loading....");
        recyclerView = findViewById(R.id.recycler_view);
        coordinatorLayout = findViewById(R.id.coordinator_layout);
        cartList = new ArrayList<>();
        //if(mAdapter == null)
        mAdapter = new CartListAdapter(this, cartList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        // adding item touch helper
        // only ItemTouchHelper.LEFT added to detect Right to Left swipe
        // if you want both Right -> Left and Left -> Right
        // add pass ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT as param
        /*ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);*/


        // making http call and fetching menu json
        //prepareCart();
        //prepareitems();
        prepareallitems();

       /* ItemTouchHelper.SimpleCallback itemTouchHelperCallback1 = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // Row is swiped from recycler view
                // remove it from adapter
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }

        // attaching the touch helper to recycler view
        new ItemTouchHelper(itemTouchHelperCallback1).attachToRecyclerView(recyclerView);*/

        /*final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(IsMessageRecv) {
                    prepareitems();
                    IsMessageRecv = false;
                }
                handler.postDelayed(this, 1000);
            }
        }, 2000);*/



    }

    public void prepareitems() {

        if (user != null) {
            uid = user.getUid();
        }
        if (!uid.isEmpty() && uid.trim().length() > 0) {

            String timeStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
            //get firebase database instance
            database = FirebaseDatabase.getInstance();
            //get firebase database instance
            databaseRef = database.getReference("Notifications").child(timeStamp);
            databaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    long count =  snapshot.getChildrenCount();
                    List<NotificationItems> listitems = new LinkedList<NotificationItems>();
                    ;
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        if (snapshot.child("N" + Long.toString(count)).exists()) {

                            if (snapshot.child("N" + Long.toString(count)).child("Title").exists()) {

                                String title = snapshot.child("N" + Long.toString(count)).child("Title").getValue().toString();

                                if (snapshot.child("N" + Long.toString(count)).child("Message").exists()) {

                                    String message = snapshot.child("N" + Long.toString(count)).child("Message").getValue().toString();

                                    if (snapshot.child("N" + Long.toString(count)).child("Image").exists()) {

                                        String image = snapshot.child("N" + Long.toString(count)).child("Image").getValue().toString();

                                        if (snapshot.child("N" + Long.toString(count)).child("Severity").exists()) {

                                            String severity = snapshot.child("N" + Long.toString(count)).child("Severity").getValue().toString();

                                            NotificationItems objnotitems = new NotificationItems();
                                            objnotitems.setTitle(title);
                                            objnotitems.setMessage(message);
                                            objnotitems.setThumbnail(image);
                                            objnotitems.setSeverity(severity);
                                            listitems.add(objnotitems);
                                        }
                                    }

                                }

                            }
                            Log.e(snap.getKey(), snap.getChildrenCount() + "");

                        }
                        count--;
                    }

                    if (listitems.size() > 0) {
                        cartList.clear();
                        cartList.addAll(listitems);
                        // refreshing recycler view
                        mAdapter.notifyDataSetChanged();
                        txtNotifications.setVisibility(View.INVISIBLE);
                        //Toast.makeText(NotificationActivity.this, "Swipe Left to delete notifications",
                                //Toast.LENGTH_LONG).show();
                    } else {
                        txtNotifications.setVisibility(View.VISIBLE);
                        txtNotifications.setText("There are no new notifications...");
                    }
                    //progress bar
                    progressBar.setVisibility(View.INVISIBLE);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
    public void prepareallitems() {

        if (user != null) {
            uid = user.getUid();
        }
        if (!uid.isEmpty() && uid.trim().length() > 0) {

            //get firebase database instance
            database = FirebaseDatabase.getInstance();
            //get firebase database instance
            databaseRef2 = database.getReference("Notifications");
            databaseRef2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    long countnotifications = 0;

                    for (DataSnapshot snaptotal : snapshot.getChildren()) {

                        String timeStamp = new SimpleDateFormat("yyyyMMdd").format(new Date(System.currentTimeMillis() - countnotifications * 24 * 60 * 60 * 1000));
                        long count = snapshot.child(timeStamp).getChildrenCount();

                        for (DataSnapshot snap : snapshot.child(timeStamp).getChildren()) {
                            if (snapshot.child(timeStamp).child("N" + Long.toString(count)).exists()) {

                                if (snapshot.child(timeStamp).child("N" + Long.toString(count)).child("Title").exists()) {

                                    String title = snapshot.child(timeStamp).child("N" + Long.toString(count)).child("Title").getValue().toString();

                                    if (snapshot.child(timeStamp).child("N" + Long.toString(count)).child("Message").exists()) {

                                        String message = snapshot.child(timeStamp).child("N" + Long.toString(count)).child("Message").getValue().toString();

                                        if (snapshot.child(timeStamp).child("N" + Long.toString(count)).child("Image").exists()) {

                                            String image = snapshot.child(timeStamp).child("N" + Long.toString(count)).child("Image").getValue().toString();

                                            if (snapshot.child(timeStamp).child("N" + Long.toString(count)).child("Severity").exists()) {

                                                String severity = snapshot.child(timeStamp).child("N" + Long.toString(count)).child("Severity").getValue().toString();

                                                NotificationItems objnotitems = new NotificationItems();
                                                objnotitems.setTitle(title);
                                                objnotitems.setMessage(message);
                                                objnotitems.setThumbnail(image);
                                                objnotitems.setSeverity(severity);
                                                alllistitems.add(objnotitems);
                                            }
                                        }

                                    }

                                }
                                Log.e(snap.getKey(), snap.getChildrenCount() + "");
                            }
                            count--;
                            Log.e(snaptotal.getKey(), snaptotal.getChildrenCount() + "");
                        }
                        countnotifications++;
                    }


                    if (alllistitems.size() > 0) {
                        cartList.clear();
                        cartList.addAll(alllistitems);
                        // refreshing recycler view
                        mAdapter.notifyDataSetChanged();
                        txtNotifications.setVisibility(View.INVISIBLE);
                        //Toast.makeText(NotificationActivity.this, "Swipe Left to delete notifications",
                        //Toast.LENGTH_LONG).show();
                        alllistitems.clear();
                    } else {
                        txtNotifications.setVisibility(View.VISIBLE);
                        txtNotifications.setText("There are no new notifications...");
                    }
                    //progress bar
                    progressBar.setVisibility(View.INVISIBLE);


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    /**
     * method make volley network call and parses json
     */
    private void prepareCart() {
        JsonArrayRequest request = new JsonArrayRequest(URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null) {
                            Toast.makeText(getApplicationContext(), "Couldn't fetch the menu! Pleas try again.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        //List<NotificationItems> items = new Gson().fromJson(response.toString(), new TypeToken<List<ClipData.Item>>() {
                        //}.getType());

                        // adding items to cart list
                        cartList.clear();
                       // cartList.addAll(items);

                        // refreshing recycler view
                        mAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json
                Log.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //MyApplication.getInstance().addToRequestQueue(request);
    }

    /**
     * callback when recycler view is swiped
     * item will be removed on swiped
     * undo option will be provided in snackbar to restore the item
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CartListAdapter.MyViewHolder) {
            // get the removed item name to display it in snack bar
            String name = cartList.get(viewHolder.getAdapterPosition()).getTitle();

            // backup of removed item for undo purpose
            final NotificationItems deletedItem = cartList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            mAdapter.removeItem(viewHolder.getAdapterPosition());
            mAdapter.notifyDataSetChanged();

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, name + " removed from notifications!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // undo is selected, restore the deleted item
                    mAdapter.restoreItem(deletedItem, deletedIndex);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
            //listitems = mAdapter.cartList;
            //listitems.remove(viewHolder.getAdapterPosition());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds cartList to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.e(TAG, "Firebase reg id: " + regId);

        /*if (!TextUtils.isEmpty(regId))
            txtRegId.setText("Firebase Reg Id: " + regId);
        else
            txtRegId.setText("Firebase Reg Id is not received yet!");*/
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }


    //old code
    //txtRegId = (TextView) findViewById(R.id.txtRegId);
    //txtMessage = (TextView) findViewById(R.id.txtMessage);
    // mRegistrationBroadcastReceiver = new BroadcastReceiver()
    // @Override
//public void onReceive(Context context, Intent intent) {
//
//// checking for type intent filter
//if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
//// gcm successfully registered
//// now subscribe to `global` topic to receive app wide notifications
////FirebaseMessaging.getInstance().subscribeToTopic("global");
//
////displayFirebaseRegId();
//
//} else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
//// new push notification is received
//
//String title = intent.getStringExtra("title");
//String message = intent.getStringExtra("message");
////Bitmap bp = intent.
//
////Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
//
////Need to implement to save notifications and images and list view
//
//
//
//txtMessage.setText(message + title);
//}
//}
//};
//
////displayFirebaseRegId();
}
