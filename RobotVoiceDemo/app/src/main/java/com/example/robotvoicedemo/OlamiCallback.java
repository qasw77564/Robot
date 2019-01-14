package com.example.robotvoicedemo;

import ai.olami.nli.NLIResult;

public interface OlamiCallback {
    public void onSuccess(String[] resp);
    public void onSuccess(NLIResult[] resp);
    public void onError(String msg, Exception e);

}
