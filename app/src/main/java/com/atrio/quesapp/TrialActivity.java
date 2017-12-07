package com.atrio.quesapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import dmax.dialog.SpotsDialog;

public class TrialActivity extends AppCompatActivity {
    TextView tv_username, tv_daysleft;
    Button btn_skip, btn_upgrade;
    ImageView img_trail;
    String installDate, currentDate, currentdeviceid;
    private DatabaseReference db_ref;
    private FirebaseDatabase db_instance;
    private FirebaseAuth mAuth;
    FirebaseUser user;
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    private final long ONE_DAY = 24 * 60 * 60 * 1000;
    long days, diff, days_left;
    Date now, before;
    Timer timer;
    MyTimer mt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trial);
        img_trail = (ImageView) findViewById(R.id.img_trail);
        tv_username = (TextView) findViewById(R.id.tv_username);
        tv_daysleft = (TextView) findViewById(R.id.tv_daysleft);
        btn_skip = (Button) findViewById(R.id.btn_skip);
        btn_upgrade = (Button) findViewById(R.id.btn_upgrade);
        btn_skip.setEnabled(false);


        final SpotsDialog dialog = new SpotsDialog(TrialActivity.this, R.style.Custom);
        dialog.show();

        mAuth = FirebaseAuth.getInstance();
        db_instance = FirebaseDatabase.getInstance();
        db_ref = db_instance.getReference("UserDetail");

        user = mAuth.getCurrentUser();
        Log.i("userid45", "" + user.getUid());
        timer = new Timer();
        mt = new MyTimer();

        Date enow = new Date(System.currentTimeMillis());
//        Log.i("Date25", ""+enow.toString());
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

        currentdeviceid = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        user = mAuth.getCurrentUser();
        dialog.show();
        try {
            checkuser();


            Query userdetailquery = db_ref.orderByKey().equalTo(user.getUid());

            userdetailquery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    if (dataSnapshot.getChildrenCount() != 0) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            UserDetail userDetail = child.getValue(UserDetail.class);
                            tv_username.setText("Welcome : " + userDetail.getUserName());
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
                            if (days >= 30) {
                                dialog.dismiss();
                                Toast.makeText(getBaseContext(), "Trial Expired", Toast.LENGTH_SHORT).show();
                                btn_skip.setEnabled(false);
                                btn_skip.setVisibility(View.INVISIBLE);
                                timer.schedule(mt, 1000, 1000);
                                tv_daysleft.setText("Days Left: 0");

                            } else {
                           /* if (days_left == 30 || days_left == 2 || days_left == 1) {
                                Toast.makeText(getBaseContext(), "Trial Version", Toast.LENGTH_SHORT).show();
                                btn_skip.setEnabled(true);
                                timer.schedule(mt, 1000, 1000);
                                tv_daysleft.setText("Days Left:" + days_left);
                            } else {

                                Intent intenttrail = new Intent(TrialActivity.this, SelectLangActivity.class);
                                startActivity(intenttrail);
                                finish();
                            }*/
                                dialog.dismiss();
                                Toast.makeText(getBaseContext(), "Trial Version", Toast.LENGTH_SHORT).show();
                                btn_skip.setEnabled(true);
                                timer.schedule(mt, 1000, 1000);
                                tv_daysleft.setText("Days Left:" + days_left);

                            }

                        }
                    } else {

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (NullPointerException e) {

            Log.i("Exception33", e.getMessage());
        }


        btn_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrialActivity.this, SelectLangActivity.class);
                startActivity(intent);
                finish();
            }
        });


        btn_upgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrialActivity.this, PaymentActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void checkuser() throws NullPointerException {

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
//                    Toast.makeText(TrialActivity.this, "add" + deviceid, Toast.LENGTH_SHORT).show();
//                    Toast.makeText(TrialActivity.this, "addcurrent" + currentdeviceid, Toast.LENGTH_SHORT).show();
                    if (deviceid.equals(currentdeviceid)) {
//                        Toast.makeText(TrialActivity.this, "add" + deviceid, Toast.LENGTH_SHORT).show();

                    } else {
                        FirebaseAuth.getInstance().signOut();
//                        Toast.makeText(TrialActivity.this, "addelse" + deviceid, Toast.LENGTH_SHORT).show();
                        Toast.makeText(TrialActivity.this, "You are logged in other device", Toast.LENGTH_SHORT).show();


                    }


                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    //Toast.makeText(SubjectActivity.this,""+dataSnapshot.getValue(),Toast.LENGTH_SHORT).show();
//                    Toast.makeText(TrialActivity.this, "change" + currentdeviceid, Toast.LENGTH_SHORT).show();
                    UserDetail userDetail = dataSnapshot.getValue(UserDetail.class);
                    String deviceid = "data";
                    deviceid = userDetail.getDeviceId();
//                    Toast.makeText(TrialActivity.this, "changecurrent" + deviceid, Toast.LENGTH_SHORT).show();
                    if (!deviceid.equals("data")) {

                        if (deviceid.equals(currentdeviceid)) {
//                            Toast.makeText(TrialActivity.this, "chabgeif", Toast.LENGTH_SHORT).show();
                        } else {
                            mAuth.signOut();
//                            Toast.makeText(TrialActivity.this, "changeelse", Toast.LENGTH_SHORT).show();
                            Toast.makeText(TrialActivity.this, "You are logged in other device", Toast.LENGTH_SHORT).show();
                            Intent isend = new Intent(TrialActivity.this, SplashActivity.class);
                            isend.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(isend);
                            finish();


                        }
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
                    Toast.makeText(TrialActivity.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }


    }


    class MyTimer extends TimerTask {

        public void run() {
            runOnUiThread(new Runnable() {

                public void run() {
                    Random rand = new Random();
                    tv_daysleft.setTextSize(20);
                    tv_daysleft.setTextColor(Color.WHITE);
                    tv_daysleft.setBackgroundColor(Color.rgb(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)));
                }
            });
        }
    }
}