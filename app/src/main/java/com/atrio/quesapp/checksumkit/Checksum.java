package com.atrio.quesapp.checksumkit;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Arpita Patel on 1/19/2018.
 */

public class Checksum {
    @SerializedName("CHECKSUMHASH")
    private String checksumHash;

    @SerializedName("ORDER_ID")
    private String orderId;

    @SerializedName("payt_STATUS")
    private String paytStatus;

    public Checksum(String checksumHash, String orderId, String paytStatus) {
        this.checksumHash = checksumHash;
        this.orderId = orderId;
        this.paytStatus = paytStatus;
    }

    public String getChecksumHash() {
        return checksumHash;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getPaytStatus() {
        return paytStatus;
    }
}
