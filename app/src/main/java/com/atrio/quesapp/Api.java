package com.atrio.quesapp;

import android.util.Log;

import com.atrio.quesapp.checksumkit.Checksum;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Arpita Patel on 1/19/2018.
 */

public interface Api {

    String BASE_URL = "http://192.168.0.156/Paytm_App_Checksum_Kit_PHP-master/generateChecksum.php/";

    @FormUrlEncoded
    @POST("generateChecksum.php")
    Call<Checksum> getChecksum(
    @Field("MID") String mId,
            @Field("ORDER_ID") String orderId,
            @Field("CUST_ID") String custId,
            @Field("CHANNEL_ID") String channelId,
            @Field("TXN_AMOUNT") String txnAmount,
            @Field("WEBSITE") String website,
            @Field("CALLBACK_URL") String callbackUrl,
            @Field("INDUSTRY_TYPE_ID") String industryTypeId
    );
}
