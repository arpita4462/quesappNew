package com.atrio.quesapp;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.atrio.quesapp.model.Feedback;
import com.atrio.quesapp.model.UserDetail;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class FeedBackActivity extends AppCompatActivity {

    Button bt_feedback;
    EditText et_feedback;
    private String feedback;
    private DatabaseReference db_ref;
    private FirebaseDatabase db_instance;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    String currentdeviceid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);

        bt_feedback = (Button) findViewById(R.id.bt_feedback);
        et_feedback = (EditText) findViewById(R.id.et_feedback);

        db_instance = FirebaseDatabase.getInstance();
        db_ref = db_instance.getReference("Feedback");
        mAuth = FirebaseAuth.getInstance();
        currentdeviceid = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        ConnectivityManager connMgr = (ConnectivityManager) getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null) {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        } else {
            try {
                checkuser();
            } catch (NullPointerException e) {

                Log.i("Exception33", e.getMessage());
            }

            bt_feedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    user = mAuth.getCurrentUser();

                    feedback = et_feedback.getText().toString().trim();

                    if (!feedback.isEmpty()) {
                        Feedback feedback1 = new Feedback();
                        feedback1.setFeedback(feedback);
                        feedback1.setEmail(user.getEmail());
                        db_ref.child(user.getUid()).setValue(feedback1);
                        et_feedback.setText("");
                        Toast.makeText(FeedBackActivity.this, "Sent your FeedBack", Toast.LENGTH_SHORT).show();


                    } else {

                        Toast.makeText(FeedBackActivity.this, "Give Your FeedBack", Toast.LENGTH_SHORT).show();

                    }


                }
            });
        }


    }

    private void checkuser() {

        if (user == null) {

            throw new NullPointerException("user is null");
        } else {
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            Query query_realtimecheck = rootRef.child("UserDetail").orderByChild("emailId").equalTo(user.getEmail());
            Log.i("Querry66", "" + query_realtimecheck);
            query_realtimecheck.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    UserDetail userDetail = dataSnapshot.getValue(UserDetail.class);
                    String deviceid = userDetail.getDeviceId();
                  /*  Toast.makeText(FeedBackActivity.this, "add" + deviceid, Toast.LENGTH_SHORT).show();
                    Toast.makeText(FeedBackActivity.this, "addcurrent" + currentdeviceid, Toast.LENGTH_SHORT).show();*/
                    if (deviceid.equals(currentdeviceid)) {
                       /* Toast.makeText(FeedBackActivity.this, "add" + deviceid, Toast.LENGTH_SHORT).show();*/

                    } else {
                        mAuth.signOut();
                       /* Toast.makeText(FeedBackActivity.this, "addelse" + deviceid, Toast.LENGTH_SHORT).show();*/
                        Toast.makeText(FeedBackActivity.this, "You are logged in other device", Toast.LENGTH_SHORT).show();


                    }


                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    //Toast.makeText(SubjectActivity.this,""+dataSnapshot.getValue(),Toast.LENGTH_SHORT).show();
                    Toast.makeText(FeedBackActivity.this, "change" + currentdeviceid, Toast.LENGTH_SHORT).show();
                    UserDetail userDetail = dataSnapshot.getValue(UserDetail.class);
                    String deviceid = "data";
                    deviceid = userDetail.getDeviceId();
                    // Toast.makeText(FeedBackActivity.this, "changecurrent" + deviceid, Toast.LENGTH_SHORT).show();
                    if (!deviceid.equals("data")) {

                        if (deviceid.equals(currentdeviceid)) {
                            // Toast.makeText(FeedBackActivity.this, "chabgeif", Toast.LENGTH_SHORT).show();
                        } else {
                            mAuth.signOut();
                            Toast.makeText(FeedBackActivity.this, "You are logged in other device", Toast.LENGTH_SHORT).show();
                            Intent isend = new Intent(FeedBackActivity.this, SplashActivity.class);
                            isend.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(isend);
                            finish();


                        }
                    } else {

                    }


                    //Toast.makeText(SubjectActivity.this,"change"+dataSnapshot.getChildrenCount(),Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(FeedBackActivity.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }

    }

}
