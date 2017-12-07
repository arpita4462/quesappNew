package com.atrio.quesapp.custom;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.atrio.quesapp.MultipleChoiceActivity;
import com.atrio.quesapp.QuestionAnswerActivity;
import com.atrio.quesapp.R;

/**
 * Created by Admin on 07-11-2017.
 */

public class CustomFabDialog extends Dialog{
    Context mycontext;
    Button btn_cancel,btn_go;
    EditText et_quesno;
    TextInputLayout inputLayoutName;
    private ProgressBar progressBar;
    String  ques_no,tittle,lang;
    long total_ques;

    public CustomFabDialog(Context context, String tittle, String lang, long total_ques) {
        super(context);
        mycontext=context;
        this.tittle =tittle;
        this.lang = lang;
        this.total_ques=total_ques;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView();
        setContentView(R.layout.custom_fab_dialog);

        et_quesno = (EditText) findViewById(R.id.et_quesno);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_go = (Button) findViewById(R.id.btn_go);
       // progressBar = (ProgressBar) findViewById(R.id.progressBar);
        inputLayoutName = (TextInputLayout)findViewById(R.id.input_error);

        btn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quesno = et_quesno.getText().toString().trim();

                int ques_int;
//                ques_int = Integer.parseInt(quesno);
                if (TextUtils.isEmpty(quesno) || quesno==null) {
                    inputLayoutName.setError(getContext().getString(R.string.enterquesnofield));
                    return;
                }else if ( Integer.parseInt(quesno)>total_ques){
                    Log.i("totaloques34",""+total_ques);
                    Log.i("totaloques324",""+ Integer.parseInt(quesno));

                    inputLayoutName.setError(getContext().getString(R.string.errormsginput)+""+total_ques);
                }
                else{

                    if ( tittle.equals("മൾട്ടിപ്പിൾ ചോയ്സ് ചോദ്യോത്തരങ്ങൾ") || tittle.equals("MultipleChoiceQuestion")){

                       // Log.i("print99",""+"if");

                        ques_no = et_quesno.getText().toString();
                        Intent intent = new Intent(mycontext,MultipleChoiceActivity.class);
                        intent.putExtra("ques_no",ques_no);
                        intent.putExtra("Sub",tittle);
                        intent.putExtra("lang",lang);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        mycontext.startActivity(intent);
                        dismiss();

                    }else{
                        Log.i("print99",""+"else");
                        ques_no = et_quesno.getText().toString();
                        Intent intent = new Intent(mycontext,QuestionAnswerActivity.class);
                        intent.putExtra("ques_no",ques_no);
                        intent.putExtra("Sub",tittle);
                        intent.putExtra("lang",lang);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        mycontext.startActivity(intent);
                        dismiss();
                    }

                }



            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

            }
        });


    }

}
