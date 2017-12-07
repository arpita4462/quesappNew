package com.atrio.quesapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.atrio.quesapp.custom.CustomRestpwd;
import com.atrio.quesapp.custom.CustomUserVerification;
import com.atrio.quesapp.model.UserDetail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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

import dmax.dialog.SpotsDialog;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText mEmailView, mPasswordView;
    private TextView newUser, tv_forgetpwd,tv_verify,bt_verify;
    TextInputLayout input_email, input_pwd;
    private Button mEmailSignInButton;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String email, password, timeSettings, deviceid, currentdeviceid,installDate, currentDate;
    private SpotsDialog dialog;
    private CustomRestpwd customRestpwd;
    public static final String MyPREFERENCES = "MyPrefs";
    boolean clicked=false,signinclicked=false;
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    private final long ONE_DAY = 24 * 60 * 60 * 1000;
    long days, diff, days_left;
    Date now, before;
    CustomUserVerification customUserVerification;
    SharedPreferences sharedpreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);
        newUser = (TextView) findViewById(R.id.tv_newuser);
        tv_forgetpwd = (TextView) findViewById(R.id.tv_forgotpwd);
        tv_verify = (TextView) findViewById(R.id.tv_very);
        bt_verify = (TextView) findViewById(R.id.bt_verify);
        mPasswordView = (EditText) findViewById(R.id.password);
        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        input_email = (TextInputLayout) findViewById(R.id.input_email_id);
        input_pwd = (TextInputLayout) findViewById(R.id.input_password);

        dialog = new SpotsDialog(LoginActivity.this, R.style.Custom);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        currentdeviceid = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
//        Log.i("currentdevice",""+user);
//        checkdeviceID();
//        Log.i("print55","create");

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        Log.i("currentdeviceuser234",""+user);

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
                dialog.show();
                Intent intenttrail = new Intent(LoginActivity.this, TrialActivity.class);
                startActivity(intenttrail);
                finish();
            } else {
                dialog.dismiss();
                mAuth.signOut();
            }
        }
       /* mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                // Log.i("User90",""+user);

                *//*info_data = (sharedpreferences.getString(userinfo, ""));
                if (!info_data.equals("arpita") && user != null) {
                    Log.i("shared90", "" + info_data);
                    Intent i = new Intent(LoginActivity.this, SubjectActivity.class);
                    startActivity(i);
                    finish();

                } else {


                }*//*


            }
        };*/

                bt_verify.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clicked = true;
                        sendEmailVerify();

                    }
                });
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                signinclicked=true;
                attemptLogin();


            }
        });


        newUser.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistraionActivity.class);
                startActivity(intent);
            }
        });

        tv_forgetpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customRestpwd = new CustomRestpwd(LoginActivity.this);
                customRestpwd.requestWindowFeature(Window.FEATURE_NO_TITLE);
                customRestpwd.show();
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();

        //Log.i("print55","start");
        try {
            int autoTime = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.AUTO_TIME);
            if (autoTime != 1) {
               // Toast.makeText(getBaseContext(), "Turn on Automatic Date and Time", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS));
                finish();

            } else {
                //mAuth.addAuthStateListener(mAuthListener);
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
       /* Log.i("print", "" + mAuthListener);
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);

        }*/

        //Log.i("print55","stop");

       clicked = false;
      // LoginActivity.this.finish();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //Log.i("print55","Resume");

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (user != null){
            if (user.isEmailVerified()) {

            }else{
                FirebaseAuth.getInstance().signOut();
            }
            if ( customUserVerification !=null ){
                customUserVerification.dismiss();
                LoginActivity.this.finish();
            }
        }


        //Log.i("print55","Pause");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //Log.i("print55","Restart");
    }

    private void attemptLogin() {
        email = mEmailView.getText().toString();
        password = mPasswordView.getText().toString();
        if (TextUtils.isEmpty(email) || !isEmailValid(email)) {
            input_email.setError(getString(R.string.err_msg_email));
        } else if (TextUtils.isEmpty(password)) {
            input_email.setErrorEnabled(false);
            input_pwd.setError(getString(R.string.error_incorrect_password));
        } else {
            dialog.show();
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {

                        user = mAuth.getCurrentUser();
                        if (user != null) {
                           // Log.i("currentdevice", ""+user);

                            checkIfEmailVerified(password);

                            //Log.i("User90", "" + user.getUid());
                        } else {
                            FirebaseAuth.getInstance().signOut();
                            // User is signed out
//                    Log.i("signed_out",""+user);
                        }
                        //Log.i("success111", "" + task.isSuccessful());

                    } else {
                       // Log.i("failure", "" + task.getException());
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), " Incorrect EmailId or Password.", Toast.LENGTH_SHORT).show();
//                                    updateUI(null);
                    }

                }
            });
        }
    }

    private void checkIfEmailVerified(final String password) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//         Log.i("Status999",""+user.isEmailVerified());

        if (user.isEmailVerified()) {
            try {

                final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

                Query userquery = rootRef.child("UserDetail").orderByChild("emailId").equalTo(user.getEmail());
                userquery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            UserDetail userDetail = dataSnapshot1.getValue(UserDetail.class);
                            deviceid = userDetail.getDeviceId();
                            installDate = userDetail.getCreatedDated();

                           /* try {
                                before = formatter.parse(installDate);
                                now = formatter.parse(currentDate);
                                diff = now.getTime() - before.getTime();
                                days = diff / ONE_DAY;
                                days_left = 30 - days;

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }*/
                            if (!deviceid.equals(currentdeviceid)) {
//                                Log.i("Status99", "" + user.isEmailVerified());
                                 //FirebaseAuth.getInstance().signOut();
                                dialog.dismiss();

                                 customUserVerification = new CustomUserVerification(LoginActivity.this, password);
                                  customUserVerification.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                  customUserVerification.setCanceledOnTouchOutside(false);
                                if(!LoginActivity.this.isFinishing()&&signinclicked) {
                                  customUserVerification.show();
                              }else {
                                    customUserVerification.dismiss();
                                }
                            } else {
                               /* Log.i("print56",""+days);
                                if (days_left <=30) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(LoginActivity.this, TrialActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {*/
                                    dialog.dismiss();
                           /*           try{
            checkuser();
        }catch (NullPointerException e){

            Log.i("Exception33", e.getMessage());
        }*/
                                    Intent intenttrail = new Intent(LoginActivity.this, TrialActivity.class);
                                    startActivity(intenttrail);
                                    finish();
//                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            dialog.dismiss();
//            checktrail();
//            Log.i("Status98", "" + user);
            if (clicked){
//                Log.i("Status98", "if" + user);
                //FirebaseAuth.getInstance().signOut();
            }else{
//                Log.i("Status98", "else" + user);


            }
            //sendEmailVerify();
            tv_verify.setVisibility(View.VISIBLE);
            bt_verify.setVisibility(View.VISIBLE);
            Toast.makeText(LoginActivity.this, "Verify your Email.", Toast.LENGTH_SHORT).show();

        }
    }


    private void sendEmailVerify() {
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                               // FirebaseAuth.getInstance().signOut();
                                Toast.makeText(LoginActivity.this, "Sent verification link to your Email", Toast.LENGTH_SHORT).show();

//                  Log.d(TAG, "Email sent.");
                            }else{
                                //FirebaseAuth.getInstance().signOut();
                            }
                        }
                    });
        }
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}


