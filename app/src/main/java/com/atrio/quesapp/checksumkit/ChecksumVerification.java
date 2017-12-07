package com.atrio.quesapp.checksumkit;

import android.util.Log;

import com.paytm.pg.merchant.CheckSumServiceHelper;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Arpita Patel on 11/15/2017.
 */

public class ChecksumVerification {
    private static String MercahntKey = "KAia1ujBt&2_sFUe";


    public void chcksumverrifymethod(){

        String paytmChecksum = "";

        Map<String, String> mapData = new TreeMap<String,String>();

        TreeMap<String, String> paytmParams = new  TreeMap<String,String>();

        for (Map.Entry<String, String> entry : mapData.entrySet())
        {
            if(entry.getKey().equals("CHECKSUMHASH")){
                paytmChecksum = entry.getKey();
            }else{
                paytmParams.put(entry.getKey(), entry.getValue());
            }
        }


        boolean isValideChecksum = false;
        try{

            isValideChecksum = CheckSumServiceHelper.getCheckSumServiceHelper().verifycheckSum(MercahntKey, paytmParams, paytmChecksum);

            System.out.println(isValideChecksum);
            Log.i("Checklog45",""+isValideChecksum);

            // if checksum is validated Kindly verify the amount and status
            // if transaction is successful
            // kindly call Paytm Transaction Status API and verify the transaction amount and status.
            // If everything is fine then mark that transaction as successful into your DB.


        }catch(Exception e){
            e.printStackTrace();
            Log.i("e4Checklog45",""+e);

        }
    }
}
