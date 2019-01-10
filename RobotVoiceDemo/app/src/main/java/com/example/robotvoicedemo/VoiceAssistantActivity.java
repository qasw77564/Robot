package com.example.robotvoicedemo;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioRecord;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import ai.olami.android.KeepRecordingSpeechRecognizer;
import ai.olami.android.hotwordDetection.HotwordDetect;
import ai.olami.android.tts.TtsPlayer;

public class VoiceAssistantActivity extends AppCompatActivity {
    public final static String TAG = "OlamiVoiceKit";

    private static final int REQUEST_EXTERNAL_PERMISSION = 1;
    private static final int REQUEST_MICROPHONE = 3;

    Context mContext = null;

    KeepRecordingSpeechRecognizer mRecognizer = null;
    HotwordDetect mHotwordDetect = null;

    MicArrayControlVT6751 mMicArrayControlVT6751 = null;
    MicArrayLEDControlHelper mMicArrayLEDControlHelper = null;
    private String mSerialPortDevice = "/dev/ttyAMA0";
    private int mSerialPortBaudrate = 115200;

    private TextView mSTTText;
    private TextView mTTSValue;
    private TextView mWhatCanOLAMIDo;
    private TextView mVolume;

    ImageView mOlamiLogo = null;
    AnimationDrawable mOlamiLogoAnimation;

    private final int mActionClickBetweenTime = 300;
    private int mActionClickCount = 0;
    private long mActionBeforeClickTime = 0;
    private Handler mActionClickHandler = new Handler();
    private boolean mActionClickFlag = false;
    private SettingDialogFragment mSettingDialog;

    private boolean mIsPlayTTS = false;
    private boolean mIsSleepMode = false;

    TtsPlayerListener mTtsListener = null;
    TtsPlayer mTtsPlayer = null;
    AudioRecord mAudioRecord = null;

    private KeepRecordingSpeechRecognizer.RecognizeState mRecognizeState;

    public enum OlamiLogoAnimationState {
        BOOTING,
        WAITING,
        LISTENING,
        LISTENED,
        LOADING,
        WAKEUP_START,
        WAKEUP_WAITING_TO_TALK,
        WAKEUP_FINISH
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


}
