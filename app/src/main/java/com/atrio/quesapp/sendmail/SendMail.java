package com.atrio.quesapp.sendmail;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by Arpita Patel on 03-04-2017.
 */

public class SendMail extends AsyncTask<Void,Void,Void> {

private Context context;
private Session session;

private String email;
private String mail_subject;
private String message;

private ProgressDialog progressDialog;

  public SendMail(Context context, String email, String mail_subject, String message){
        this.context = context;
        this.email = email;
        this.mail_subject = mail_subject;
        this.message = message;
        }



    @Override
protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(context,"Sending message","Please wait...",false,false);
        }

@Override
protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        progressDialog.dismiss();
        Toast.makeText(context,"Message Sent",Toast.LENGTH_LONG).show();
        }

@Override
protected Void doInBackground(Void... params) {
        Properties props = new Properties();

    props.put("mail.smtp.host", "smtp.gmail.com");
    props.put("mail.smtp.socketFactory.port", "465");
    props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.port", "465");
        session = Session.getDefaultInstance(props,new javax.mail.Authenticator() {
//Authenticating the password
protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(Config.EMAIL, Config.PASSWORD);
        }
        });

       try {
           MimeMessage mm = new MimeMessage(session);
        mm.setFrom(new InternetAddress(Config.EMAIL));
        mm.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
        mm.setSubject(mail_subject);
        mm.setText(message);

        Transport.send(mm);
           Log.i("message_mm2",""+mm);


       } catch (MessagingException e) {
        e.printStackTrace();
        }

        return null;
        }
        }
