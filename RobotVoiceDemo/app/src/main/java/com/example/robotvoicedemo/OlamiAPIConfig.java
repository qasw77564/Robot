package com.example.robotvoicedemo;

import android.os.Handler;

import java.io.IOException;



import ai.olami.cloudService.APIConfiguration;
import ai.olami.cloudService.APIResponse;
import ai.olami.cloudService.APIResponseData;
import ai.olami.cloudService.TextRecognizer;
import ai.olami.nli.NLIResult;

public class OlamiAPIConfig {
    private static String myAppKey = "016fb23919da4d7cb5f5c1c3e24a8361";
    private static String myAppSecret = "cae29a694d9d4175bf585edbb70a9fc1";
    private static APIConfiguration config = null;
    private static OlamiAPIConfig instance = null;

    private OlamiAPIConfig() {
        config = new APIConfiguration(myAppKey, myAppSecret, APIConfiguration.LOCALIZE_OPTION_TRADITIONAL_CHINESE);
    }

    public static OlamiAPIConfig getInstance(){
        if (instance == null ){
            instance = new OlamiAPIConfig();
        }
        return instance;
    }

    public void postRecoginzer(String text, OlamiCallback callback) {
        try{
            TextRecognizer recoginzer = new TextRecognizer(config);
            APIResponse response1 = recoginzer.requestWordSegmentation(text);
            if (response1.ok() && response1.hasData()) {
                // 取得分詞結果
                String[] ws = response1.getData().getWordSegmentation();
                APIResponseData apiResponseData = response1.getData();
                callback.onSuccess(apiResponseData);
            }else {
                callback.onError("", new RuntimeException("err_msg"));
            }

        }catch (Exception e){
            callback.onError(e.getMessage(), e);
        }

    }
}

