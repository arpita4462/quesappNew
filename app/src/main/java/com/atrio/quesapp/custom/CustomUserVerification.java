package com.atrio.quesapp.custom;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.atrio.quesapp.LoginActivity;
import com.atrio.quesapp.R;
import com.atrio.quesapp.RegistraionActivity;
import com.atrio.quesapp.SelectLangActivity;
import com.atrio.quesapp.model.UserDetail;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Admin on 19-07-2017.
 */

public class CustomUserVerification extends Dialog {

    Context mycontext;
    Button bt_send, btn_cancel;
    EditText et_email;
    TextInputLayout inputLayoutName;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener mauthListener;
    private DatabaseReference db_ref;
    private FirebaseDatabase db_instance;
    private ProgressBar progressBar;
    FirebaseUser user;
    String password;
    String currentdeviceid, email, userid, useremail;

    public CustomUserVerification(Context context, String password) {
        super(context);
        mycontext = context;
       this.password = password;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView();
        setContentView(R.layout.custom_emailverification);

        et_email = (EditText) findViewById(R.id.masked);
        bt_send = (Button) findViewById(R.id.btn_send_mail);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        inputLayoutName = (TextInputLayout) findViewById(R.id.visible);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);

        auth = FirebaseAuth.getInstance();
//        user = auth.getCurrentUser();
        db_instance = FirebaseDatabase.getInstance();
        db_ref = db_instance.getReference();
        currentdeviceid = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
//        Log.i("detail45",""+user.getUid());
        Log.i("detail455", "" + currentdeviceid);
      /*  Log.i("detail452", "" + db_ref);

        mauthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    email = et_email.getText().toString().trim();


                }
            }
        };*/




        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                dismiss();

            }
        });
        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(mycontext,"success",Toast.LENGTH_SHORT).show();
                email = et_email.getText().toString().trim();


                if (TextUtils.isEmpty(email) || !isEmailValid(email)) {
                    inputLayoutName.setError(getContext().getString(R.string.err_msg_email));
//                    return;
                } else {
/*
                    auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(mycontext,"success",Toast.LENGTH_SHORT).show();
                            }

                        }
                    });*/
                    Query userquery = db_ref.child("UserDetail").orderByChild("emailId").equalTo(email);
                    userquery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() == 0) {
                                Intent i = new Intent(mycontext, RegistraionActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                mycontext.startActivity(i);
                            } else {
                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                    UserDetail userDetail = dataSnapshot1.getValue(UserDetail.class);
                                    userid = userDetail.getUserId();
                                    useremail = userDetail.getEmailId();
                                    db_ref.child("UserDetail").child(userid).child("deviceId").setValue(currentdeviceid);

                                }

                                Intent intent = new Intent(mycontext, LoginActivity.class);
                               //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                mycontext.startActivity(intent);
                                //auth.signOut();




                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        auth.signOut();

    }

    @Override
    protected void onStart() {
        super.onStart();
       // auth.addAuthStateListener(mauthListener);
    }




    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}
