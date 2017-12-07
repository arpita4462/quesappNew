package com.atrio.quesapp;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.atrio.quesapp.model.UserDetail;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    private final long ONE_DAY = 24 * 60 * 60 * 1000;
    long days, diff, days_left;
    Date now, before;
    private String installDate, currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();


        Log.i("checkuser",""+user);
        //Toast.makeText(this, "hii", Toast.LENGTH_SHORT).show();

        DatabaseReference offsetRef = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");
        offsetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                long offset = snapshot.getValue(Long.class);
                double estimatedServerTimeMs = System.currentTimeMillis() + offset;
                currentDate = formatter.format(estimatedServerTimeMs);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
        ConnectivityManager connMgr = (ConnectivityManager) getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null) {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }else {
            if (user != null) {
//                checktrail();
                Intent intent = new Intent(SplashActivity.this, TrialActivity.class);
                startActivity(intent);
                finish();
//            startActivity(new Intent(SplashActivity.this, SelectLangActivity.class));
//            finish();
            } else {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }

        }
}
/*
    private  void checktrail(){

        final DatabaseReference rootRef2 = FirebaseDatabase.getInstance().getReference();
        Query userquery = rootRef2.child("UserDetail").orderByChild("emailId").equalTo(user.getEmail());

        userquery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    UserDetail userDetail = dataSnapshot1.getValue(UserDetail.class);
                    installDate = userDetail.getCreatedDated();

                    try {
                        before = formatter.parse(installDate);
                        now = formatter.parse(currentDate);
                        diff = now.getTime() - before.getTime();
                        days = diff / ONE_DAY;
                        days_left = 30 - days;

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Log.i("print565",""+days_left);

                    if (days>=30) {
                        Intent intent = new Intent(SplashActivity.this, TrialActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intenttrail = new Intent(SplashActivity.this, SelectLangActivity.class);
                        startActivity(intenttrail);
                        finish();
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
*/

}
