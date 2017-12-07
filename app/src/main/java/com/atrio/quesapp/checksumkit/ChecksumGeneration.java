package com.atrio.quesapp.checksumkit;

import android.util.Log;

import com.atrio.quesapp.PaymentActivity;
import com.paytm.pg.merchant.CheckSumServiceHelper;

import java.util.TreeMap;

/**
 * Created by Arpita Patel on 11/15/2017.
 */

public class ChecksumGeneration {

    private static String MID = "ATRIOD13361002889196";
    private static String MercahntKey = "KAia1ujBt&2_sFUe";
    private static String INDUSTRY_TYPE_ID = "Retail";
    private static String CHANNLE_ID = "WAP";
    private static String WEBSITE = "APP_STAGING";
    private static String CALLBACK_URL = "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";

   public ChecksumGeneration(){


   }

    public void checksumgenmethod() {


        TreeMap<String,String> paramMap = new TreeMap<String,String>();
        paramMap.put("MID" , MID);
        paramMap.put("ORDER_ID" , "ORDER00011");
        paramMap.put("CUST_ID" , "CUST00011");
        paramMap.put("INDUSTRY_TYPE_ID" , INDUSTRY_TYPE_ID);
        paramMap.put("CHANNEL_ID" , CHANNLE_ID);
        paramMap.put("TXN_AMOUNT" , "1.00");
        paramMap.put("WEBSITE" , WEBSITE);
        paramMap.put("EMAIL" , "test@gmail.com");
        paramMap.put("MOBILE_NO" , "9999999999");
        paramMap.put("CALLBACK_URL" , CALLBACK_URL);

        try{
            String checkSum =  CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum(MercahntKey, paramMap);
            paramMap.put("CHECKSUMHASH" , checkSum);
            Log.i("Checklog453",""+checkSum);

//            PaymentActivity senddata= new PaymentActivity(checkSum);


            System.out.println("Paytm Payload: "+ paramMap);
            Log.i("Checklog45",""+paramMap);

        }catch(Exception e) {

            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.i("EChecklog45",""+e);

        }
    }

}
