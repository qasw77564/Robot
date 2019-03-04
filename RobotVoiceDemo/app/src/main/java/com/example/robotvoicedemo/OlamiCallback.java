package com.example.robotvoicedemo;

import ai.olami.cloudService.APIResponseData;
import ai.olami.nli.NLIResult;

public interface OlamiCallback {
    public void onSuccess(APIResponseData apiResponseData);
    public void onError(String msg, Exception e);

}
