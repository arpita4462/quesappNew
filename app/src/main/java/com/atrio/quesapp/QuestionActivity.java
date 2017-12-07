package com.atrio.quesapp;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.atrio.quesapp.model.QuestionModel;
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

import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

public class QuestionActivity extends AppCompatActivity implements Animation.AnimationListener,View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    RadioGroup rg_option;
    RadioButton rb_opA,rb_opB,rb_opC,rb_opD,rbselect,rbcorrect;
    Button btn_sub,bt_done,bt_pos1,bt_pos2,bt_pos3,bt_pos4,bt_pos5,bt_pos6,bt_pos7,bt_pos8,bt_pos9,bt_pos10;
    TextView tv_sub,tv_ques,tv_quesno,tv_explain,tv_back;
    Animation animFadein,animMove;
    private DatabaseReference db_ref;
    private FirebaseDatabase db_instance;
    private FirebaseAuth mAuth;
    String tittle,correctAns,selectedAns,seriesNo,ques_noList,qno_list,explanation,currentdeviceid;
    int qno=001,correctValue =0,checkedRadioButtonID,total_question=0,quesno_ref;
    SpotsDialog dialog;
    ArrayList<String> arrayList;
    int value ;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);


        tv_sub=(TextView)findViewById(R.id.tv_sub);
        tv_back = (TextView)findViewById(R.id.tv_back);
        tv_ques=(TextView)findViewById(R.id.tv_ques);
        tv_explain=(TextView)findViewById(R.id.tv_explain);
        tv_quesno =(TextView)findViewById(R.id.tv_no);
        rb_opA=(RadioButton) findViewById(R.id.rb_opA);
        rb_opB=(RadioButton) findViewById(R.id.rb_opB);
        rb_opC=(RadioButton) findViewById(R.id.rb_opC);
        rb_opD=(RadioButton) findViewById(R.id.rb_opD);
        btn_sub=(Button) findViewById(R.id.btn_submit);
        bt_done=(Button) findViewById(R.id.bt_sub);
        bt_pos1 = (Button) findViewById(R.id.bt_pos1);
        bt_pos2 = (Button) findViewById(R.id.bt_pos2);
        bt_pos3 = (Button) findViewById(R.id.bt_pos3);
        bt_pos4 = (Button) findViewById(R.id.bt_pos4);
        bt_pos5 = (Button) findViewById(R.id.bt_pos5);
        bt_pos6 = (Button) findViewById(R.id.bt_pos6);
        bt_pos7 = (Button) findViewById(R.id.bt_pos7);
        bt_pos8 = (Button) findViewById(R.id.bt_pos8);
        bt_pos9 = (Button) findViewById(R.id.bt_pos9);
        bt_pos10 = (Button) findViewById(R.id.bt_pos10);
        arrayList = new ArrayList<>();


        rg_option=(RadioGroup) findViewById(R.id.rg_option);
        bt_done.setVisibility(View.GONE);
        tv_explain.setVisibility(View.GONE);
        dialog = new SpotsDialog(QuestionActivity.this, R.style.Custom);

        Intent i =  getIntent();
        tittle = i.getStringExtra("tittle");
        seriesNo = i.getStringExtra("SeriesNo");
        tv_sub.setText(seriesNo);
        btn_sub.setEnabled(false);

        // set animation listener
        animFadein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        animMove = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move);
        animFadein.setAnimationListener(this);
        animMove.setAnimationListener(this);
        bt_pos1.setOnClickListener(this);
        bt_pos2.setOnClickListener(this);
        bt_pos3.setOnClickListener(this);
        bt_pos4.setOnClickListener(this);
        bt_pos5.setOnClickListener(this);
        bt_pos6.setOnClickListener(this);
        bt_pos7.setOnClickListener(this);
        bt_pos8.setOnClickListener(this);
        bt_pos9.setOnClickListener(this);
        bt_pos10.setOnClickListener(this);
        tv_back.setVisibility(View.INVISIBLE);

        /*

        check log in through another device
         */
        db_instance = FirebaseDatabase.getInstance();
        db_ref = db_instance.getReference();

        currentdeviceid = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        user=FirebaseAuth.getInstance().getCurrentUser();


        Query query_realtimecheck = db_ref.child("UserDetail").orderByChild("emailId").equalTo(user.getEmail());
        //Log.i("Querry66", "" + query_realtimecheck);
        query_realtimecheck.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                UserDetail userDetail = dataSnapshot.getValue(UserDetail.class);
                String deviceid = userDetail.getDeviceId();
                Toast.makeText(QuestionActivity.this, "add" + deviceid, Toast.LENGTH_SHORT).show();
                Toast.makeText(QuestionActivity.this, "addcurrent" + currentdeviceid, Toast.LENGTH_SHORT).show();
                if (deviceid.equals(currentdeviceid)) {
                    Toast.makeText(QuestionActivity.this, "add" + deviceid, Toast.LENGTH_SHORT).show();

                } else {
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(QuestionActivity.this, "addelse" + deviceid, Toast.LENGTH_SHORT).show();



                }


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                //Toast.makeText(SubjectActivity.this,""+dataSnapshot.getValue(),Toast.LENGTH_SHORT).show();
              //  Toast.makeText(QuestionActivity.this, "change" + currentdeviceid, Toast.LENGTH_SHORT).show();
                UserDetail userDetail = dataSnapshot.getValue(UserDetail.class);
                String deviceid = userDetail.getDeviceId();
/*
                Toast.makeText(QuestionActivity.this, "changecurrent" + deviceid, Toast.LENGTH_SHORT).show();
*/

                if (deviceid.equals(currentdeviceid)) {
                   // Toast.makeText(QuestionActivity.this, "chabgeif", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseAuth.getInstance().signOut();
                    //Toast.makeText(QuestionActivity.this, "changeelse", Toast.LENGTH_SHORT).show();
                    Intent isend = new Intent(QuestionActivity.this,LoginActivity.class);
                    startActivity(isend);
                    finish();


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
                Toast.makeText(QuestionActivity.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        /*

         */


        qno_list = String.format("%03d",qno);
        getButton(qno_list);
        getQuestion(qno_list);

        checkedRadioButtonID = rg_option.getCheckedRadioButtonId();
        rg_option.setOnCheckedChangeListener(this);

        btn_sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_explain.setVisibility(View.GONE);
                tv_ques.startAnimation(animFadein);
                rg_option.startAnimation(animMove);
                quesno_ref = Integer.parseInt(tv_quesno.getText().toString());
                if (quesno_ref <10){
                    tv_back.setVisibility(View.INVISIBLE);
                }else{
                    tv_back.setVisibility(View.VISIBLE);
                }
                int value = Integer.parseInt(bt_pos1.getText().toString());
                if (value ==1){
                    tv_back.setVisibility(View.INVISIBLE);
                }
                checkedRadioButtonID =rg_option.getCheckedRadioButtonId();
                int value1 = Integer.parseInt(tv_quesno.getText().toString());
                value1++;
                ques_noList = String.format("%03d",value1);
                getQuestion(ques_noList);

                correctAns=null;
                rb_opA.setChecked(false);
                rb_opB.setChecked(false);
                rb_opC.setChecked(false);
                rb_opD.setChecked(false);
                btn_sub.setEnabled(false);
                btn_sub.setBackgroundResource(R.color.centercolor);
                tv_explain.setVisibility(View.GONE);
                int last_btn = Integer.parseInt(bt_pos10.getText().toString());
                String last_btn1 = String.format("%03d",last_btn);

                if (ques_noList.equals(last_btn1)){
                    String next_btn = String.format("%03d",last_btn+1);
                    getButton(next_btn);
                }
            }
        });
        tv_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


                int btn_value = Integer.parseInt(bt_pos1.getText().toString())-1;

                String subvalue1 = String.format("%03d",btn_value);
                dialog.show();

                Query query_back = db_ref.child("subjectList").child(tittle).child(seriesNo).orderByKey().endAt("Q-"+subvalue1).limitToLast(10);

                query_back.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        arrayList.clear();
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            arrayList.add(data.getKey());
                        }
                        for (int i =0;i<1;i++){
                            String questype= arrayList.get(i);
                            int ques = Integer.parseInt(questype.substring(questype.indexOf("-")+1,questype.length()));
                            String sub_quesno = String.format("%03d",ques);
                            getQuestion(sub_quesno);
                            tv_explain.setVisibility(View.GONE);
                            tv_ques.startAnimation(animFadein);
                            rg_option.startAnimation(animMove);
                            rb_opA.setChecked(false);
                            rb_opB.setChecked(false);
                            rb_opC.setChecked(false);
                            rb_opD.setChecked(false);
                            btn_sub.setEnabled(false);
                            btn_sub.setBackgroundResource(R.color.centercolor);

                        }

                        sendData(arrayList);
                        dialog.dismiss();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



            }
        });
    }

    private void getButton(String qno_list) {


        Query query_btn = db_ref.child("subjectList").child(tittle).child(seriesNo).orderByKey().startAt("Q-"+qno_list).limitToFirst(10);
        query_btn.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                arrayList.clear();

                for (DataSnapshot data : dataSnapshot.getChildren()) {
//                    Log.i("key77", "" + data.getKey());
                    arrayList.add(data.getKey());
                }
                sendData(arrayList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void sendData(final ArrayList<String> arrayList) {
        String data = null,data_btn;
        int sub_data;
        // Log.i("arraydata33", "" + arrayList);
        bt_pos1.setVisibility(View.INVISIBLE);
        bt_pos2.setVisibility(View.INVISIBLE);
        bt_pos3.setVisibility(View.INVISIBLE);
        bt_pos4.setVisibility(View.INVISIBLE);
        bt_pos5.setVisibility(View.INVISIBLE);
        bt_pos6.setVisibility(View.INVISIBLE);
        bt_pos7.setVisibility(View.INVISIBLE);
        bt_pos8.setVisibility(View.INVISIBLE);
        bt_pos9.setVisibility(View.INVISIBLE);
        bt_pos10.setVisibility(View.INVISIBLE);

  try{
        quesno_ref = Integer.parseInt(tv_quesno.getText().toString());
        if (quesno_ref <10){
            tv_back.setVisibility(View.INVISIBLE);
        }else{
            tv_back.setVisibility(View.VISIBLE);
        }}catch (Exception e){

    }
        for (int y = 0; y < arrayList.size(); y++) {
            switch (y) {
                case 0:
                    bt_pos1.setVisibility(View.VISIBLE);
                    data = arrayList.get(y);
                    sub_data = Integer.parseInt(data.substring(data.indexOf("-")+1,data.length()));
                    data_btn = String.format("%01d",sub_data);
                    bt_pos1.setText(data_btn);
                    int value = Integer.parseInt(bt_pos1.getText().toString());
                    if (value ==1){
                        tv_back.setVisibility(View.INVISIBLE);
                    }
                    break;
                case 1:
                    bt_pos2.setVisibility(View.VISIBLE);
                    data = arrayList.get(y);
                    sub_data = Integer.parseInt(data.substring(data.indexOf("-")+1,data.length()));
                    data_btn = String.format("%01d",sub_data);
                    bt_pos2.setText(data_btn);
                    break;
                case 2:
                    bt_pos3.setVisibility(View.VISIBLE);
                    data = arrayList.get(y);
                    sub_data = Integer.parseInt(data.substring(data.indexOf("-")+1,data.length()));
                    data_btn = String.format("%01d",sub_data);
                    bt_pos3.setText(data_btn);
                    break;
                case 3:
                    bt_pos4.setVisibility(View.VISIBLE);
                    data = arrayList.get(y);
                    sub_data = Integer.parseInt(data.substring(data.indexOf("-")+1,data.length()));
                    data_btn = String.format("%01d",sub_data);
                    bt_pos4.setText(data_btn);
                    break;
                case 4:
                    bt_pos5.setVisibility(View.VISIBLE);
                    data = arrayList.get(y);
                    sub_data = Integer.parseInt(data.substring(data.indexOf("-")+1,data.length()));
                    data_btn = String.format("%01d",sub_data);
                    bt_pos5.setText(data_btn);
                    break;
                case 5:
                    bt_pos6.setVisibility(View.VISIBLE);
                    data = arrayList.get(y);
                    sub_data = Integer.parseInt(data.substring(data.indexOf("-")+1,data.length()));
                    data_btn = String.format("%01d",sub_data);
                    bt_pos6.setText(data_btn);
                    break;
                case 6:
                    bt_pos7.setVisibility(View.VISIBLE);
                    data = arrayList.get(y);
                    sub_data = Integer.parseInt(data.substring(data.indexOf("-")+1,data.length()));
                    data_btn = String.format("%01d",sub_data);
                    bt_pos7.setText(data_btn);
                    break;
                case 7:
                    bt_pos8.setVisibility(View.VISIBLE);
                    data = arrayList.get(y);
                    sub_data = Integer.parseInt(data.substring(data.indexOf("-")+1,data.length()));
                    data_btn = String.format("%01d",sub_data);
                    bt_pos8.setText(data_btn);
                    break;
                case 8:
                    bt_pos9.setVisibility(View.VISIBLE);
                    data = arrayList.get(y);
                    sub_data = Integer.parseInt(data.substring(data.indexOf("-")+1,data.length()));
                    data_btn = String.format("%01d",sub_data);
                    bt_pos9.setText(data_btn);
                    break;
                case 9:
                    bt_pos10.setVisibility(View.VISIBLE);
                    data = arrayList.get(y);
                    sub_data = Integer.parseInt(data.substring(data.indexOf("-")+1,data.length()));
                    data_btn = String.format("%01d",sub_data);
                    bt_pos10.setText(data_btn);
                    break;

            }
        }


    }

/*
    private void attemptedques(String sub_quesno) {

        Query attemques=db_ref.child("myquiz").orderByChild(sub_quesno);

        attemques.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() !=0) {
                    selectdata=dataSnapshot.getValue().toString();
                    Log.i("Questik1",""+selectdata);
                    selectedAns=selectdata;
//                    checkedRadioButtonID = -1;
                    Log.i("Questik1",""+selectedAns);

                    checkedRadioButtonID =rg_option.getCheckedRadioButtonId();
//                    rg_option.getCheckedRadioButtonId();
//                    rg_option.check(checkedRadioButtonID);
//                    rbselect.setChecked(true);
//                    RadioButton radioButton = (RadioButton) getActivity().findViewById(checkedId);//
//                  rg_option.setOnCheckedChangeListener(this);

                    Log.i("rgoptiond45",""+rg_option.getCheckedRadioButtonId());

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
*/

    private void getQuestion(String qno){
        dialog.show();

        Query getquestion=db_ref.child("subjectList").child(tittle).child(seriesNo).orderByKey().equalTo("Q-"+qno);

        getquestion.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() !=0) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        QuestionModel qModel = child.getValue(QuestionModel.class);

                        String questype= child.getKey();
                        int ques = Integer.parseInt(questype.substring(questype.indexOf("-")+1,questype.length()));
                        String sub_quesno = String.format("%01d",ques);

                        tv_quesno .setText(sub_quesno);
                        tv_ques.setText(qModel.getQuestion());
                        rb_opA.setText(qModel.getOptionA());
                        rb_opB.setText(qModel.getOptionB());
                        rb_opC.setText(qModel.getOptionC());
                        rb_opD.setText(qModel.getOptionD());
                        correctAns = qModel.getCorrect();
                        explanation=qModel.getExplanation();

                        Log.i("explain45",""+explanation);
                        dialog.dismiss();
//                        attemptedques(sub_quesno);
                    }
                }else {

                    dialog.dismiss();
                    tv_quesno.setVisibility(View.GONE);
                    tv_ques.setText("You have  Done your Test.");
                    rb_opA.setVisibility(View.INVISIBLE);
                    rb_opB.setVisibility(View.INVISIBLE);
                    rb_opC.setVisibility(View.INVISIBLE);
                    rb_opD.setVisibility(View.INVISIBLE);
                    btn_sub.setVisibility(View.GONE);
                    tv_explain.setVisibility(View.GONE);
                    bt_done.setVisibility(View.VISIBLE);
                    bt_pos1.setVisibility(View.INVISIBLE);
                    bt_pos2.setVisibility(View.INVISIBLE);
                    bt_pos3.setVisibility(View.INVISIBLE);
                    bt_pos4.setVisibility(View.INVISIBLE);
                    bt_pos5.setVisibility(View.INVISIBLE);
                    bt_pos6.setVisibility(View.INVISIBLE);
                    bt_pos7.setVisibility(View.INVISIBLE);
                    bt_pos8.setVisibility(View.INVISIBLE);
                    bt_pos9.setVisibility(View.INVISIBLE);
                    bt_pos10.setVisibility(View.INVISIBLE);
                    tv_back.setVisibility(View.INVISIBLE);


                    bt_done.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                          /*  Intent intent = new Intent(QuestionActivity.this,final.class);
                            intent.putExtra("tittle",tittle);
                            startActivity(intent);
                            finish();
*/

                            Toast.makeText(getApplicationContext(),"You Have Done Quiz",Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onAnimationStart(Animation animation) {

        rb_opA.setTextColor(ContextCompat.getColor(QuestionActivity.this, R.color.black));
        rb_opB.setTextColor(ContextCompat.getColor(QuestionActivity.this, R.color.black));
        rb_opC.setTextColor(ContextCompat.getColor(QuestionActivity.this, R.color.black));
        rb_opD.setTextColor(ContextCompat.getColor(QuestionActivity.this, R.color.black));

        rb_opA.setChecked(false);
        rb_opB.setChecked(false);
        rb_opC.setChecked(false);
        rb_opD.setChecked(false);
        rb_opA.setClickable(true);
        rb_opB.setClickable(true);
        rb_opC.setClickable(true);
        rb_opD.setClickable(true);

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void onClick(View view) {

        String subvalue = null;
        switch (view.getId()){
            case R.id.bt_pos1:
                tv_ques.startAnimation(animFadein);
                rg_option.startAnimation(animMove);
                checkedRadioButtonID =rg_option.getCheckedRadioButtonId();
                value = Integer.parseInt(bt_pos1.getText().toString());
                subvalue = String.format("%03d",value);
                getQuestion(subvalue);
                correctAns=null;
                explanation=null;
                rb_opA.setChecked(false);
                rb_opB.setChecked(false);
                rb_opC.setChecked(false);
                rb_opD.setChecked(false);
                btn_sub.setEnabled(false);
                btn_sub.setBackgroundResource(R.color.centercolor);
                tv_explain.setVisibility(View.GONE);

                break;
            case R.id.bt_pos2:
                tv_ques.startAnimation(animFadein);
                rg_option.startAnimation(animMove);
                checkedRadioButtonID =rg_option.getCheckedRadioButtonId();
                value = Integer.parseInt(bt_pos2.getText().toString());
                subvalue = String.format("%03d",value);
                getQuestion(subvalue);
                //getQuestion(qno_list);
                correctAns=null;
                explanation=null;
                rb_opA.setChecked(false);
                rb_opB.setChecked(false);
                rb_opC.setChecked(false);
                rb_opD.setChecked(false);
                btn_sub.setEnabled(false);
                btn_sub.setBackgroundResource(R.color.centercolor);
                tv_explain.setVisibility(View.GONE);
                break;
            case R.id.bt_pos3:
                tv_ques.startAnimation(animFadein);
                rg_option.startAnimation(animMove);
                checkedRadioButtonID =rg_option.getCheckedRadioButtonId();
                value = Integer.parseInt(bt_pos3.getText().toString());
                subvalue = String.format("%03d",value);
                getQuestion(subvalue);
                //getQuestion(qno_list);
                correctAns=null;
                explanation=null;
                rb_opA.setChecked(false);
                rb_opB.setChecked(false);
                rb_opC.setChecked(false);
                rb_opD.setChecked(false);
                btn_sub.setEnabled(false);
                btn_sub.setBackgroundResource(R.color.centercolor);
                tv_explain.setVisibility(View.GONE);
                break;
            case R.id.bt_pos4:
                tv_ques.startAnimation(animFadein);
                rg_option.startAnimation(animMove);
                checkedRadioButtonID =rg_option.getCheckedRadioButtonId();
                value = Integer.parseInt(bt_pos4.getText().toString());
                subvalue = String.format("%03d",value);
                getQuestion(subvalue);
                //getQuestion(qno_list);
                correctAns=null;
                explanation=null;
                rb_opA.setChecked(false);
                rb_opB.setChecked(false);
                rb_opC.setChecked(false);
                rb_opD.setChecked(false);
                btn_sub.setEnabled(false);
                btn_sub.setBackgroundResource(R.color.centercolor);
                tv_explain.setVisibility(View.GONE);
                break;
            case R.id.bt_pos5:
                tv_ques.startAnimation(animFadein);
                rg_option.startAnimation(animMove);
                checkedRadioButtonID =rg_option.getCheckedRadioButtonId();
                value = Integer.parseInt(bt_pos5.getText().toString());
                subvalue = String.format("%03d",value);
                getQuestion(subvalue);
                //getQuestion(qno_list);
                correctAns=null;
                explanation=null;
                rb_opA.setChecked(false);
                rb_opB.setChecked(false);
                rb_opC.setChecked(false);
                rb_opD.setChecked(false);
                btn_sub.setEnabled(false);
                btn_sub.setBackgroundResource(R.color.centercolor);
                tv_explain.setVisibility(View.GONE);
                break;
            case R.id.bt_pos6:
                tv_ques.startAnimation(animFadein);
                rg_option.startAnimation(animMove);
                checkedRadioButtonID =rg_option.getCheckedRadioButtonId();
                value = Integer.parseInt(bt_pos6.getText().toString());
                subvalue = String.format("%03d",value);
                getQuestion(subvalue);
                //getQuestion(qno_list);
                correctAns=null;
                explanation=null;
                rb_opA.setChecked(false);
                rb_opB.setChecked(false);
                rb_opC.setChecked(false);
                rb_opD.setChecked(false);
                btn_sub.setEnabled(false);
                btn_sub.setBackgroundResource(R.color.centercolor);
                tv_explain.setVisibility(View.GONE);
                break;
            case R.id.bt_pos7:
                tv_ques.startAnimation(animFadein);
                rg_option.startAnimation(animMove);
                checkedRadioButtonID =rg_option.getCheckedRadioButtonId();
                value = Integer.parseInt(bt_pos7.getText().toString());
                subvalue = String.format("%03d",value);
                getQuestion(subvalue);
                //getQuestion(qno_list);
                correctAns=null;
                explanation=null;
                rb_opA.setChecked(false);
                rb_opB.setChecked(false);
                rb_opC.setChecked(false);
                rb_opD.setChecked(false);
                btn_sub.setEnabled(false);
                btn_sub.setBackgroundResource(R.color.centercolor);
                tv_explain.setVisibility(View.GONE);
                break;
            case R.id.bt_pos8:
                tv_ques.startAnimation(animFadein);
                rg_option.startAnimation(animMove);
                checkedRadioButtonID =rg_option.getCheckedRadioButtonId();
                value = Integer.parseInt(bt_pos8.getText().toString());
                subvalue = String.format("%03d",value);
                getQuestion(subvalue);
                //getQuestion(qno_list);
                correctAns=null;
                explanation=null;
                rb_opA.setChecked(false);
                rb_opB.setChecked(false);
                rb_opC.setChecked(false);
                rb_opD.setChecked(false);
                btn_sub.setEnabled(false);
                btn_sub.setBackgroundResource(R.color.centercolor);
                tv_explain.setVisibility(View.GONE);
                break;
            case R.id.bt_pos9:
                tv_ques.startAnimation(animFadein);
                rg_option.startAnimation(animMove);
                checkedRadioButtonID =rg_option.getCheckedRadioButtonId();
                value = Integer.parseInt(bt_pos9.getText().toString());
                subvalue = String.format("%03d",value);
                getQuestion(subvalue);
                //getQuestion(qno_list);
                correctAns=null;
                explanation=null;
                rb_opA.setChecked(false);
                rb_opB.setChecked(false);
                rb_opC.setChecked(false);
                rb_opD.setChecked(false);
                btn_sub.setEnabled(false);
                btn_sub.setBackgroundResource(R.color.centercolor);
                tv_explain.setVisibility(View.GONE);
                break;
            case R.id.bt_pos10:
                tv_ques.startAnimation(animFadein);
                rg_option.startAnimation(animMove);
                checkedRadioButtonID =rg_option.getCheckedRadioButtonId();
                value = Integer.parseInt(bt_pos10.getText().toString());
                int nextbtn = value+1;
                String nextbtn_sub = String.format("%03d",nextbtn);
                subvalue = String.format("%03d",value);
                getQuestion(subvalue);

                getButton(nextbtn_sub);
                //getQuestion(qno_list);
                correctAns=null;
                rb_opA.setChecked(false);
                rb_opB.setChecked(false);
                rb_opC.setChecked(false);
                rb_opD.setChecked(false);
                btn_sub.setEnabled(false);
                btn_sub.setBackgroundResource(R.color.centercolor);
                tv_explain.setVisibility(View.GONE);
                break;
        }



    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        btn_sub.setEnabled(true);
        btn_sub.setBackgroundResource(R.drawable.ripple_effect);
        tv_explain.setVisibility(View.VISIBLE);

        rbselect = (RadioButton)group.findViewById(checkedId);
        rbcorrect= (RadioButton)group.findViewById(checkedId);


        switch (checkedId) {
            case R.id.rb_opA:
                selectedAns = rb_opA.getText().toString();
                break;
            case R.id.rb_opB:
                selectedAns = rb_opB.getText().toString();
                break;
            case R.id.rb_opC:
                selectedAns = rb_opC.getText().toString();
                break;
            case R.id.rb_opD:
                selectedAns = rb_opD.getText().toString();
                break;

        }

        if (selectedAns.equals(correctAns)){

            rbselect.setTextColor(ContextCompat.getColor(QuestionActivity.this, R.color.green));
            rbcorrect=rbselect;
            rb_opA.setClickable(false);
            rb_opB.setClickable(false);
            rb_opC.setClickable(false);
            rb_opD.setClickable(false);
            if (explanation.equals("undefined")){
                tv_explain.setText("");

            }else{
                tv_explain.setText(explanation);

            }


//            db_ref.child("myquiz").child(tv_quesno.getText().toString()).setValue(rbselect.getText());



        }else {
            rbselect.setTextColor(ContextCompat.getColor(QuestionActivity.this, R.color.red));
            if(rb_opA.getText().toString().equals(correctAns)){
                rb_opA.setTextColor(ContextCompat.getColor(QuestionActivity.this, R.color.green));
                rbcorrect=rb_opA;
            }else  if(rb_opB.getText().toString().equals(correctAns)){
                rb_opB.setTextColor(ContextCompat.getColor(QuestionActivity.this, R.color.green));
                rbcorrect=rb_opB;
            }else if(rb_opC.getText().toString().equals(correctAns)){
                rb_opC.setTextColor(ContextCompat.getColor(QuestionActivity.this, R.color.green));
                rbcorrect=rb_opC;
            }else if(rb_opD.getText().toString().equals(correctAns)){
                rb_opD.setTextColor(ContextCompat.getColor(QuestionActivity.this, R.color.green));
                rbcorrect=rb_opD;
            }
            rb_opA.setClickable(false);
            rb_opB.setClickable(false);
            rb_opC.setClickable(false);
            rb_opD.setClickable(false);
            if (explanation.equals("undefined")){
                tv_explain.setText("");

            }else{
                tv_explain.setText(explanation);

            }

        }



    }
}