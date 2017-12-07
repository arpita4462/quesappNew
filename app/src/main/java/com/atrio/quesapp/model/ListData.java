package com.atrio.quesapp.model;

import android.widget.RadioButton;

/**
 * Created by Admin on 30-06-2017.
 */

public class ListData {
    RadioButton rd;

    public RadioButton getRd() {
        return rd;
    }

    public void setRd(RadioButton rd) {
        this.rd = rd;
    }

    public String getQus_no() {
        return qus_no;
    }

    public void setQus_no(String qus_no) {
        this.qus_no = qus_no;
    }

    int attempted_data,correct_data;
            String qus_no;

    public int getAttempted_data() {
        return attempted_data;
    }

    public void setAttempted_data(int attempted_data) {

        this.attempted_data = attempted_data;
    }

    public int getCorrect_data()
    {
        return correct_data;
    }

    public void setCorrect_data(int correct_data)
    {
        this.correct_data = correct_data;
    }
}
