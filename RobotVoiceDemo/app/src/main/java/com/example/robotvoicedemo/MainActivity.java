package com.example.robotvoicedemo;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.robot.motion.RobotMotion;
import android.robot.speech.SpeechManager;
import android.robot.speech.SpeechManager.AsrListener;
import android.robot.speech.SpeechManager.NluListener;
import android.robot.speech.SpeechManager.TtsListener;
import android.robot.speech.SpeechService;
import android.robot.hw.RobotDevices.Units;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.robotvoicedemo.util.Tools;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ai.olami.android.RecorderSpeechRecognizer;
import ai.olami.android.hotwordDetection.HotwordDetect;
import ai.olami.cloudService.APIResponse;
import ai.olami.cloudService.APIResponseData;
import ai.olami.cloudService.TextRecognizer;
import ai.olami.ids.IDSResult;
import ai.olami.ids.WeatherData;
import ai.olami.nli.NLIResult;


public class MainActivity extends Activity implements View.OnClickListener {

    //可從此下面去搜尋有哪些 ai.olami.ids Data
    private String TAG = MainActivity.class.getSimpleName();
    private ImageView mBtnBack;
    private TextView mAsrStatus;
    private TextView mAsrVolume;
    private TextView mAsrContent;
    private Button mBtnASREnabled;
    private Button mBtnASRDisabled;
    private EditText mTTSContent;
    private TextView mTTSStatus;
    private Button mBtnTTSStart;
    private EditText mNLUContent;
    private TextView mNLUStatus;
    private Button mBtnNLUStart;
    private InputMethodManager mInputMethodManager;
    private SpeechManager mSpeechManager;

    private MyService.LocalBinder mMyServiceBinder = null;
    private MyService mMyService = null;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMyService = ((MyService.LocalBinder)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub

        }
    };

    private Button testIntent;

    private AsrListener mAsrCommonListener = new AsrListener() {
        @Override
        public void onBegin() {
            mAsrStatus.setText(R.string.robot_listening);
        }

        @Override
        public void onEnd() {
            mAsrStatus.setText(R.string.robot_processing);
        }

        @Override
        public void onError(int error) {
            // state.setText("error:" + error);
        }

        @Override
        public boolean onResult(String text) {
            mAsrContent.setText(text);
            mSpeechManager.startSpeaking(text);
//            mTTSContent.setText(text);
//            String a = mAsrContent.getText().toString();

            Set<String> lines = new HashSet<String>(Arrays.<String>asList("打开来","打开赖"));
            Set<String> kkboxs = new HashSet<String>(Arrays.<String>asList("打开来","打开赖"));
            Set<String> youTubes = new HashSet<String>(Arrays.<String>asList("打开来","打开赖"));
            Set<String> wheather = new HashSet<String>(Arrays.asList("天气","今天天气","新竹天气","台北天气"));

            if (lines.contains(text)){
                goToLine();
            }else if (kkboxs.contains(text)){
                goToKKBOX();
            }else if (youTubes.contains(text)){
                goToYouTube();
            } else {
                // API 識別語音
                OlamiAPIConfig.getInstance().postRecoginzer("", new OlamiCallback() {
                    @Override
                    public void onSuccess(APIResponseData data) {
                        // resp
//                        data.getNLIResults()[0].getDescObject().getName().
                        List<NLIResult> nliResults = Lists.newArrayList(data.getNLIResults());
                        if(!nliResults.isEmpty()){
//                            nliResults.get(0).getDataObjects().get(0)
                        }
                    }

                    @Override
                    public void onError(String msg, Exception e) {

                    }
                });

            }
            return false;
        }

        @Override
        public void onVolumeChanged(float volume) {
            mAsrVolume.setText(String.valueOf(volume));
        }
    };

    /**
     * this AsrListener will block the result's deliver <br>
     * then you can do something for this result
     */
    private AsrListener mAsrBlockListener = new AsrListener() {
        @Override
        public void onBegin() {
            mAsrStatus.setText(R.string.robot_listening);
        }

        @Override
        public void onEnd() {
            mAsrStatus.setText(R.string.robot_processing);
        }

        @Override
        public void onError(int error) {
            // state.setText("error:" + error);
        }

        @Override
        public boolean onResult(String text) {
            mAsrContent.setText(text);
            return true;
        }

        @Override
        public void onVolumeChanged(float volume) {
            mAsrVolume.setText(String.valueOf(volume));
        }
    };

    private TtsListener mTtsListener = new TtsListener() {
        @Override
        public void onBegin(int requestId) {
            mTTSStatus.setText(getString(R.string.tts_start_speaking)
                    + requestId);
        }

        @Override
        public void onEnd(int requestId) {
            mTTSStatus.setText(getString(R.string.tts_stop_speaking)
                    + requestId);
        }

        @Override
        public void onError(int error) {
            // state.setText("error:" + error);
        }
    };

    private NluListener mNluListener = new NluListener() {

        @Override
        public void onBegin(int requestId) {
            mNLUStatus.setText(getString(R.string.nlu_start_understanding)
                    + requestId);
        }

        @Override
        public void onEnd(int requestId) {
            mNLUStatus.setText(getString(R.string.nlu_stop_understanding)
                    + requestId);
        }

        @Override
        public void onError(int requestId) {

        }

        @Override
        public boolean onResult(int requestId, String text) {
            mNLUStatus.setText(text);
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActionBar() != null) {
            getActionBar().hide();
        }
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, MyService.class);
        this.startService(intent);
        initData();
        initView();
        initListener();
        switchASRStatus();
    }

    private void initData() {
        mSpeechManager = (SpeechManager) getSystemService(SpeechService.SERVICE_NAME);
        mInputMethodManager = (InputMethodManager) this.getApplicationContext()
                                                       .getSystemService(Context
                                                               .INPUT_METHOD_SERVICE);
    }

    private void initView() {
        mBtnBack = (ImageView) findViewById(R.id.common_title_back);
        mAsrStatus = (TextView) findViewById(R.id.asr_status);
        mAsrVolume = (TextView) findViewById(R.id.asr_volume);
        mAsrContent = (TextView) findViewById(R.id.asr_content);
        mBtnASREnabled = (Button) findViewById(R.id.asr_enable);
        mBtnASRDisabled = (Button) findViewById(R.id.asr_disable);
        mTTSContent = (EditText) findViewById(R.id.tts_content);
        mTTSStatus = (TextView) findViewById(R.id.tts_status);
        mBtnTTSStart = (Button) findViewById(R.id.tts_start);
        mNLUContent = (EditText) findViewById(R.id.nlu_content);
        mNLUStatus = (TextView) findViewById(R.id.nlu_status);
        mBtnNLUStart = (Button) findViewById(R.id.nlu_start);
        findViewById(R.id.block_asr).setOnClickListener(this);
        findViewById(R.id.deliver_asr).setOnClickListener(this);
        mBtnBack.setOnClickListener(this);
        mBtnASREnabled.setOnClickListener(this);
        mBtnASRDisabled.setOnClickListener(this);
        mBtnTTSStart.setOnClickListener(this);
        mBtnNLUStart.setOnClickListener(this);
        testIntent = (Button) findViewById(R.id.testIntent);
        testIntent.setOnClickListener(this);
    }

    private void initListener() {
        mSpeechManager.setAsrListener(mAsrCommonListener);
        mSpeechManager.setTtsListener(mTtsListener);
        mSpeechManager.setNluListener(mNluListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.common_title_back:
                finish();
                break;
            case R.id.asr_enable:
                mSpeechManager.setAsrEnable(true);
                switchASRStatus();
                break;
            case R.id.asr_disable:
                mSpeechManager.setAsrEnable(false);
                switchASRStatus();
                break;
            case R.id.tts_start:
                mInputMethodManager.hideSoftInputFromWindow(
                        mTTSContent.getWindowToken(), 0);
                String tts = mTTSContent.getText().toString();
                mAsrContent.setText(tts);
                if (!TextUtils.isEmpty(tts)) {
//                    if(tts.equals("hello")){
//                        String ts = "很好啊";
//                        mSpeechManager.startSpeaking(ts);
//                    } else {
                        mSpeechManager.startSpeaking(tts);
//                    }
                } else {
                    String ts = "請輸入文字";
                    mSpeechManager.startSpeaking(ts);
                }
                break;
            case R.id.nlu_start:
                mInputMethodManager.hideSoftInputFromWindow(
                        mNLUContent.getWindowToken(), 0);
                String nlu = mNLUContent.getText().toString();
                if (!TextUtils.isEmpty(nlu)) {
                    mSpeechManager.startUnderstanding(nlu);
                } else {
                    String ts = "請輸入中文字";
                    mSpeechManager.startSpeaking(ts);
                }
                break;
            case R.id.deliver_asr:
                mSpeechManager.setAsrListener(mAsrCommonListener);
                Toast.makeText(this,"set common AsrListener",Toast.LENGTH_SHORT).show();
                break;
            case R.id.block_asr:
                mSpeechManager.setAsrListener(mAsrBlockListener);
                Toast.makeText(this,"set block AsrListener",Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    private void goToLine(){
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            ComponentName cmp = new ComponentName("jp.naver.line.android","jp.naver.line.android.activity.SplashActivity");
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cmp);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // TODO: handle exception
//            getBaseActivity().showToastLong("检查到您手机没有安装LINE，请安装后使用该功能");
        }
    }

    private void goToKKBOX(){
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            ComponentName cmp = new ComponentName("com.skysoft.kkbox.android","com.skysoft.kkbox.android.HomeActivity");
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cmp);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // TODO: handle exception
//            getBaseActivity().showToastLong("检查到您手机没有安装KKBOX，请安装后使用该功能");
        }
    }

    private void goToYouTube(){
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            ComponentName cmp = new ComponentName("com.google.android.gms","com.google.android.gms.app.settings.GoogleSettingsActivity");
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cmp);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // TODO: handle exception
//            getBaseActivity().showToastLong("检查到您手机没有安装YouTube，请安装后使用该功能");
        }
    }

    public void callPhone(String phoneNum){
        try {
            Intent intent = new Intent();
            intent.setAction("Android.intent.action.CALL");
            Uri data = Uri.parse("tel:" + phoneNum);
            intent.setData(data);
            startActivity(intent);
        }catch (ActivityNotFoundException e){

        }
    }

    private void switchASRStatus() {
        if (mSpeechManager.getAsrEnable()) {
            mBtnASREnabled.setEnabled(false);
            mBtnASRDisabled.setEnabled(true);
            mAsrStatus.setText(R.string.robot_stauts_ready);
        } else {
            mBtnASREnabled.setEnabled(true);
            mBtnASRDisabled.setEnabled(false);
            mAsrStatus.setText(R.string.disable_asr);
        }
    }


        @Override
    protected void onDestroy() {
        super.onDestroy();
        mSpeechManager.setAsrListener(null);
        mSpeechManager.setTtsListener(null);
        mSpeechManager.setNluListener(null);
    }

}
