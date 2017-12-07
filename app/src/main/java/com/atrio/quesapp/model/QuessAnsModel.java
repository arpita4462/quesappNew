package com.atrio.quesapp.model;

/**
 * Created by Admin on 12-10-2017.
 */

public class QuessAnsModel {

   public String question;

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String answer;



    public QuessAnsModel(){

    }
    public QuessAnsModel(String question,String answer){
        this.question = question;
        this.answer = answer;

    }
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }



}
