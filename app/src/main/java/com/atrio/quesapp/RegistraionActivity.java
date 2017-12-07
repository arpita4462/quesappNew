package com.atrio.quesapp;

import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.atrio.quesapp.model.UserDetail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegistraionActivity extends AppCompatActivity {

    private EditText mEmailView, mPasswordView, name;
    private ProgressBar mProgressView;
    private Button registraionButton;
    private FirebaseAuth mAuth;
    private DatabaseReference db_ref;
    private FirebaseDatabase db_instance;
    SimpleDateFormat formatter;
    private String email, password, userName, createdDated, emailId, userId, deviceId, user_name;
    TextInputLayout input_email, input_pwd, input_name;
    TextView tv_tandc;
    NetworkInfo networkInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registraion);
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        name = (EditText) findViewById(R.id.name);
        tv_tandc=(TextView)findViewById(R.id.tv_tandcr);
        registraionButton = (Button) findViewById(R.id.registraion_button);
        mProgressView = (ProgressBar) findViewById(R.id.registraion_progress);
        input_email = (TextInputLayout) findViewById(R.id.input_email_id);
        input_pwd = (TextInputLayout) findViewById(R.id.input_password);
        input_name = (TextInputLayout) findViewById(R.id.input_name);

        mAuth = FirebaseAuth.getInstance();
        db_instance = FirebaseDatabase.getInstance();
        db_ref = db_instance.getReference("UserDetail");
        formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        ConnectivityManager connMgr = (ConnectivityManager) getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        networkInfo = connMgr.getActiveNetworkInfo();


        registraionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (networkInfo == null) {
                        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
                    } else {
                        registerUser();
                    }
                }
            });


        tv_tandc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("hello90","clicked");
                Intent tandcintent = new Intent(RegistraionActivity.this, TermsActivity.class);
                startActivity(tandcintent);
//                finish();
            }
        });


    }

    private void registerUser() {

        email = mEmailView.getText().toString();
        password = mPasswordView.getText().toString();
        user_name = name.getText().toString().trim();
        if (TextUtils.isEmpty(user_name)) {
            input_name.setError(getString(R.string.err_msg_name));
        } else if (TextUtils.isEmpty(email) || !isEmailValid(email)) {
            input_email.setError(getString(R.string.err_msg_email));
        } else if (TextUtils.isEmpty(password) || password.length() < 6) {
            input_email.setErrorEnabled(false);
            input_pwd.setError(getString(R.string.error_incorrect_password));
        } else {

            mProgressView.setVisibility(View.VISIBLE);

            //creating a new user
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();

                                Date dt = new Date();
                                userId = user.getUid();
                                emailId = user.getEmail();
                                createdDated = formatter.format(dt);
                                deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                                createUserDetail(user_name, createdDated, emailId, userId, deviceId);
                                sendEmailVerify();
                                FirebaseAuth.getInstance().signOut();
//                            Toast.makeText(RegistraionActivity.this,"Successfully registered",Toast.LENGTH_LONG).show();
                                Toast.makeText(RegistraionActivity.this, " Sent Verification to email for UserVerification.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegistraionActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                                mEmailView.setText("");
                                mPasswordView.setText("");
                            } else {
                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {

                                    Toast.makeText(RegistraionActivity.this, "User with this email already exist.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(RegistraionActivity.this, "Registration Error", Toast.LENGTH_LONG).show();
                                }
                            }
                            mProgressView.setVisibility(View.GONE);
                        }
                    });

        }
    }

    private void sendEmailVerify() {
        FirebaseUser user = mAuth.getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

//                            Log.d(TAG, "Email sent.");
                        }
                    }
                });
    }

    private void createUserDetail(String userName, String createdDated, String emailId, String userId, String deviceId) {

        UserDetail userDetail = new UserDetail();
        userDetail.setUserName(userName);
        userDetail.setCreatedDated(createdDated);
        userDetail.setEmailId(emailId);
        userDetail.setUserId(userId);
        userDetail.setDeviceId(deviceId);

        db_ref.child(userId).setValue(userDetail);
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
