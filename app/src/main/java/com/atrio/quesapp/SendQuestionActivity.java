package com.atrio.quesapp;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.atrio.quesapp.model.UserDetail;
import com.atrio.quesapp.sendmail.SendMail;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class SendQuestionActivity extends AppCompatActivity {

    EditText et_ques, et_ans;
    Button btn_send;
    Spinner spinner;
    String sender_email, subject, currentdeviceid;
    private FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_question);
        mAuth = FirebaseAuth.getInstance();


        currentdeviceid = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        user = FirebaseAuth.getInstance().getCurrentUser();

        et_ques = (EditText) findViewById(R.id.et_ques);
        et_ans = (EditText) findViewById(R.id.et_ans);
        btn_send = (Button) findViewById(R.id.btn_send);
        spinner = (Spinner) findViewById(R.id.spinner);

        ConnectivityManager connMgr = (ConnectivityManager) getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null) {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        } else {

            mAuth = FirebaseAuth.getInstance();
            user = mAuth.getCurrentUser();
            try {
                checkuser();
            } catch (NullPointerException e) {

                Log.i("Exception33", e.getMessage());
            }

            Bundle b = getIntent().getExtras();

            if (b != null) {
                ArrayList<String> arr = (ArrayList<String>) b.getStringArrayList("array_list");
                ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, arr);
                spinner.setAdapter(adapter);

            }
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    subject = parent.getItemAtPosition(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            btn_send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!TextUtils.isEmpty(et_ques.getText()) && !TextUtils.isEmpty(et_ans.getText())) {
                        sender_email = user.getEmail();
                        String email = "info@atrio.co.in";
                        String mail_subject = "Question";
                        String message = "Category-" + subject + "\n\nQuestion-" + et_ques.getText() + "\n\nAnswer-" + et_ans.getText();
                       /* SendMail sm = new SendMail(v.getContext(), email, mail_subject, message);
                        sm.execute();*/
                        Intent send = new Intent(Intent.ACTION_SENDTO);
                        String uriText = "mailto:" + Uri.encode("" + email) + "?subject=" + Uri.encode("" + mail_subject) + "&body=" + Uri.encode("" + message);
                        Uri uri = Uri.parse(uriText);
                        send.setData(uri);
                        startActivity(Intent.createChooser(send, "Send Email..."));
                        et_ques.setText("");
                        et_ans.setText("");
                    } else {
                        Toast.makeText(getApplicationContext(), "Write Question and Answer", Toast.LENGTH_LONG).show();

                    }


                }
            });

        }


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
                      /*  Toast.makeText(SendQuestionActivity.this, "add" + deviceid, Toast.LENGTH_SHORT).show();
                        Toast.makeText(SendQuestionActivity.this, "addcurrent" + currentdeviceid, Toast.LENGTH_SHORT).show();*/
                    if (deviceid.equals(currentdeviceid)) {
                        // Toast.makeText(SendQuestionActivity.this, "add" + deviceid, Toast.LENGTH_SHORT).show();

                    } else {
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(SendQuestionActivity.this, "You are logged in other device", Toast.LENGTH_SHORT).show();
                        // Toast.makeText(SendQuestionActivity.this, "addelse" + deviceid, Toast.LENGTH_SHORT).show();


                    }


                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

//                    Toast.makeText(SubjectActivity.this,""+dataSnapshot.getValue(),Toast.LENGTH_SHORT).show();
                    // Toast.makeText(SendQuestionActivity.this, "change" + currentdeviceid, Toast.LENGTH_SHORT).show();
                    UserDetail userDetail = dataSnapshot.getValue(UserDetail.class);
                    String deviceid = "data";
                    deviceid = userDetail.getDeviceId();
                    //Toast.makeText(SendQuestionActivity.this, "changecurrent" + deviceid, Toast.LENGTH_SHORT).show();
                    if (!deviceid.equals("data")) {

                        if (deviceid.equals(currentdeviceid)) {
                            //Toast.makeText(SendQuestionActivity.this, "chabgeif", Toast.LENGTH_SHORT).show();
                        } else {
                            mAuth.signOut();
                            //Toast.makeText(SendQuestionActivity.this, "changeelse", Toast.LENGTH_SHORT).show();
                            Toast.makeText(SendQuestionActivity.this, "You are logged in other device", Toast.LENGTH_SHORT).show();
                            Intent isend = new Intent(SendQuestionActivity.this, SplashActivity.class);
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
                    Toast.makeText(SendQuestionActivity.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }

    }
}
