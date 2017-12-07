package com.atrio.quesapp;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.atrio.quesapp.model.UserDetail;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class TermsActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseUser user;
    String currentdeviceid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        mAuth = FirebaseAuth.getInstance();


        currentdeviceid = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        user = FirebaseAuth.getInstance().getCurrentUser();


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
            mAuth = FirebaseAuth.getInstance();
            user = mAuth.getCurrentUser();
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
/*                    Toast.makeText(ReportActivity.this, "add" + deviceid, Toast.LENGTH_SHORT).show();
                    Toast.makeText(ReportActivity.this, "addcurrent" + currentdeviceid, Toast.LENGTH_SHORT).show();
                   */
                    if (deviceid.equals(currentdeviceid)) {
                        //   Toast.makeText(ReportActivity.this, "add" + deviceid, Toast.LENGTH_SHORT).show();

                    } else {
                        mAuth.signOut();
                        // Toast.makeText(ReportActivity.this, "addelse" + deviceid, Toast.LENGTH_SHORT).show();
                        Toast.makeText(TermsActivity.this, "You are logged in other device", Toast.LENGTH_SHORT).show();


                    }


                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    //Toast.makeText(SubjectActivity.this,""+dataSnapshot.getValue(),Toast.LENGTH_SHORT).show();
                    //Toast.makeText(ReportActivity.this, "change" + currentdeviceid, Toast.LENGTH_SHORT).show();
                    UserDetail userDetail = dataSnapshot.getValue(UserDetail.class);
                    String deviceid = "data";
                    deviceid = userDetail.getDeviceId();
                    //Toast.makeText(ReportActivity.this, "changecurrent" + deviceid, Toast.LENGTH_SHORT).show();
                    if (!deviceid.equals("data")) {

                        if (deviceid.equals(currentdeviceid)) {
                            //      Toast.makeText(ReportActivity.this, "chabgeif", Toast.LENGTH_SHORT).show();
                        } else {
                            mAuth.signOut();
                            Toast.makeText(TermsActivity.this, "You are logged in other device", Toast.LENGTH_SHORT).show();
                            Intent isend = new Intent(TermsActivity.this, SplashActivity.class);
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
                    Toast.makeText(TermsActivity.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }

    }
}
