package com.atrio.quesapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.atrio.quesapp.model.QuestionModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

public class ReasoingActivity extends AppCompatActivity implements Animation.AnimationListener,View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    RadioGroup rg_option;
    RadioButton rb_opA,rb_opB,rb_opC,rb_opD,rbselect,rbcorrect;
    Button btn_sub,bt_done,bt_pos1,bt_pos2,bt_pos3,bt_pos4,bt_pos5,bt_pos6,bt_pos7,bt_pos8,bt_pos9,bt_pos10;
    TextView tv_sub,tv_ques,tv_quesno;
    Animation animFadein,animMove;
    ImageView img_ques;
    private DatabaseReference db_ref;
    private FirebaseDatabase db_instance;
    String tittle,correctAns,selectedAns,seriesNo,ques_noList,qno_list,selectdata;
    int qno=001,correctValue =0,checkedRadioButtonID,total_question=0;
    SpotsDialog dialog;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    File localFile;
    ArrayList<String> arrayList;
    int value ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        tv_sub=(TextView)findViewById(R.id.tv_sub);
        tv_ques=(TextView)findViewById(R.id.tv_ques);
        tv_quesno =(TextView)findViewById(R.id.tv_no);
        img_ques=(ImageView)findViewById(R.id.img_ques);
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

        dialog = new SpotsDialog(ReasoingActivity.this, R.style.Custom);

        Intent i =  getIntent();
        tittle = i.getStringExtra("tittle");
        seriesNo = i.getStringExtra("SeriesNo");
        tv_sub.setText(tittle);
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


        //firebase database
        db_instance = FirebaseDatabase.getInstance();
        db_ref = db_instance.getReference();

        qno_list = String.format("%03d",qno);
        getButton(qno_list);
        getQuestion(qno_list);

        checkedRadioButtonID = rg_option.getCheckedRadioButtonId();
        rg_option.setOnCheckedChangeListener(this);

        btn_sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_ques.startAnimation(animFadein);
                rg_option.startAnimation(animMove);

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
                int last_btn = Integer.parseInt(bt_pos10.getText().toString());
                String last_btn1 = String.format("%03d",last_btn);

                if (ques_noList.equals(last_btn1)){
                    getButton(last_btn1);
                }
            }
        });
    }

    private void getButton(String qno_list) {


        Query query_btn = db_ref.child(tittle).child(seriesNo).orderByKey().startAt("Q-"+qno_list).limitToFirst(10);
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
        bt_pos1.setVisibility(View.GONE);
        bt_pos2.setVisibility(View.GONE);
        bt_pos3.setVisibility(View.GONE);
        bt_pos4.setVisibility(View.GONE);
        bt_pos5.setVisibility(View.GONE);
        bt_pos6.setVisibility(View.GONE);
        bt_pos7.setVisibility(View.GONE);
        bt_pos8.setVisibility(View.GONE);
        bt_pos9.setVisibility(View.GONE);
        bt_pos10.setVisibility(View.GONE);

        for (int y = 0; y < arrayList.size(); y++) {
            switch (y) {
                case 0:
                    bt_pos1.setVisibility(View.VISIBLE);
                    data = arrayList.get(y);
                    sub_data = Integer.parseInt(data.substring(data.indexOf("-")+1,data.length()));
                    data_btn = String.format("%01d",sub_data);
                    bt_pos1.setText(data_btn);
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

        Query getquestion=db_ref.child(tittle).child(seriesNo).orderByKey().equalTo("Q-"+qno);
        Log.i("imgname56",""+getquestion);

        getquestion.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() !=0) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        QuestionModel qModel = child.getValue(QuestionModel.class);
                        Log.i("imgname5",""+qModel.getQuestion());

                        String questype= child.getKey();
                        int ques = Integer.parseInt(questype.substring(questype.indexOf("-")+1,questype.length()));
                        String sub_quesno = String.format("%01d",ques);

                        String quesimg= qModel.getQuestion();
                        String gettext=quesimg.substring(0,quesimg.indexOf("Img-"));
                        String getimg = quesimg.substring(quesimg.indexOf("-")+1);
                        Log.i("imgname",""+getimg);
                        showimg(getimg);
                        tv_quesno .setText(sub_quesno);
                        tv_ques.setText(gettext);
                        rb_opA.setText(qModel.getOptionA());
                        rb_opB.setText(qModel.getOptionB());
                        rb_opC.setText(qModel.getOptionC());
                        rb_opD.setText(qModel.getOptionD());
                        correctAns = qModel.getCorrect();
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
                    bt_done.setVisibility(View.VISIBLE);

                    bt_done.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                          /*  Intent intent = new Intent(QuestionActivity.this,final.class);
                            intent.putExtra("tittle",tittle);
                            startActivity(intent);
                            finish();
*/
                            tv_quesno.setVisibility(View.GONE);
                            bt_pos1.setVisibility(View.GONE);
                            bt_pos2.setVisibility(View.GONE);
                            bt_pos3.setVisibility(View.GONE);
                            bt_pos4.setVisibility(View.GONE);
                            bt_pos5.setVisibility(View.GONE);
                            bt_pos6.setVisibility(View.GONE);
                            bt_pos7.setVisibility(View.GONE);
                            bt_pos8.setVisibility(View.GONE);
                            bt_pos9.setVisibility(View.GONE);
                            bt_pos10.setVisibility(View.GONE);

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

    private void showimg(String getimg) {
/*
        storageRef.child(getimg).getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                Log.i("imagename",""+storageMetadata.getName());
            }
        });
*/
        try {
            localFile = File.createTempFile("images", "png");
            storageRef.child(getimg).getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    img_ques.setImageBitmap(bitmap);
                    Log.i("storageimg",""+bitmap);


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onAnimationStart(Animation animation) {

        rb_opA.setTextColor(ContextCompat.getColor(ReasoingActivity.this, R.color.black));
        rb_opB.setTextColor(ContextCompat.getColor(ReasoingActivity.this, R.color.black));
        rb_opC.setTextColor(ContextCompat.getColor(ReasoingActivity.this, R.color.black));
        rb_opD.setTextColor(ContextCompat.getColor(ReasoingActivity.this, R.color.black));

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
                rb_opA.setChecked(false);
                rb_opB.setChecked(false);
                rb_opC.setChecked(false);
                rb_opD.setChecked(false);
                btn_sub.setEnabled(false);
                btn_sub.setBackgroundResource(R.color.centercolor);
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
                rb_opA.setChecked(false);
                rb_opB.setChecked(false);
                rb_opC.setChecked(false);
                rb_opD.setChecked(false);
                btn_sub.setEnabled(false);
                btn_sub.setBackgroundResource(R.color.centercolor);
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
                rb_opA.setChecked(false);
                rb_opB.setChecked(false);
                rb_opC.setChecked(false);
                rb_opD.setChecked(false);
                btn_sub.setEnabled(false);
                btn_sub.setBackgroundResource(R.color.centercolor);
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
                rb_opA.setChecked(false);
                rb_opB.setChecked(false);
                rb_opC.setChecked(false);
                rb_opD.setChecked(false);
                btn_sub.setEnabled(false);
                btn_sub.setBackgroundResource(R.color.centercolor);
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
                rb_opA.setChecked(false);
                rb_opB.setChecked(false);
                rb_opC.setChecked(false);
                rb_opD.setChecked(false);
                btn_sub.setEnabled(false);
                btn_sub.setBackgroundResource(R.color.centercolor);
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
                rb_opA.setChecked(false);
                rb_opB.setChecked(false);
                rb_opC.setChecked(false);
                rb_opD.setChecked(false);
                btn_sub.setEnabled(false);
                btn_sub.setBackgroundResource(R.color.centercolor);
                break;
            case R.id.bt_pos7:
                tv_ques.startAnimation(animFadein);
                rg_option.startAnimation(animMove);
                checkedRadioButtonID =rg_option.getCheckedRadioButtonId();
                value = Integer.parseInt(bt_pos7.getText().toString());
                subvalue = String.format("%03d",value);
                subvalue = "0"+value;
                getQuestion(subvalue);
                //getQuestion(qno_list);
                correctAns=null;
                rb_opA.setChecked(false);
                rb_opB.setChecked(false);
                rb_opC.setChecked(false);
                rb_opD.setChecked(false);
                btn_sub.setEnabled(false);
                btn_sub.setBackgroundResource(R.color.centercolor);
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
                rb_opA.setChecked(false);
                rb_opB.setChecked(false);
                rb_opC.setChecked(false);
                rb_opD.setChecked(false);
                btn_sub.setEnabled(false);
                btn_sub.setBackgroundResource(R.color.centercolor);
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
                rb_opA.setChecked(false);
                rb_opB.setChecked(false);
                rb_opC.setChecked(false);
                rb_opD.setChecked(false);
                btn_sub.setEnabled(false);
                btn_sub.setBackgroundResource(R.color.centercolor);
                break;
            case R.id.bt_pos10:
                tv_ques.startAnimation(animFadein);
                rg_option.startAnimation(animMove);
                checkedRadioButtonID =rg_option.getCheckedRadioButtonId();
                value = Integer.parseInt(bt_pos10.getText().toString());
                subvalue = String.format("%03d",value);
                getQuestion(subvalue);
                getButton(subvalue);
                //getQuestion(qno_list);
                correctAns=null;
                rb_opA.setChecked(false);
                rb_opB.setChecked(false);
                rb_opC.setChecked(false);
                rb_opD.setChecked(false);
                btn_sub.setEnabled(false);
                btn_sub.setBackgroundResource(R.color.centercolor);
                break;
        }



    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        btn_sub.setEnabled(true);
        btn_sub.setBackgroundResource(R.drawable.ripple_effect);

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

            rbselect.setTextColor(ContextCompat.getColor(ReasoingActivity.this, R.color.green));
            rbcorrect=rbselect;
            rb_opA.setClickable(false);
            rb_opB.setClickable(false);
            rb_opC.setClickable(false);
            rb_opD.setClickable(false);

//            db_ref.child("myquiz").child(tv_quesno.getText().toString()).setValue(rbselect.getText());



        }else {
            rbselect.setTextColor(ContextCompat.getColor(ReasoingActivity.this, R.color.red));
            if(rb_opA.getText().toString().equals(correctAns)){
                rb_opA.setTextColor(ContextCompat.getColor(ReasoingActivity.this, R.color.green));
                rbcorrect=rb_opA;
            }else  if(rb_opB.getText().toString().equals(correctAns)){
                rb_opB.setTextColor(ContextCompat.getColor(ReasoingActivity.this, R.color.green));
                rbcorrect=rb_opB;
            }else if(rb_opC.getText().toString().equals(correctAns)){
                rb_opC.setTextColor(ContextCompat.getColor(ReasoingActivity.this, R.color.green));
                rbcorrect=rb_opC;
            }else if(rb_opD.getText().toString().equals(correctAns)){
                rb_opD.setTextColor(ContextCompat.getColor(ReasoingActivity.this, R.color.green));
                rbcorrect=rb_opD;
            }

            rb_opA.setClickable(false);
            rb_opB.setClickable(false);
            rb_opC.setClickable(false);
            rb_opD.setClickable(false);

//            db_ref.child("myquiz").child(tv_quesno.getText().toString()).setValue(rbselect.getText());

        }



    }
}