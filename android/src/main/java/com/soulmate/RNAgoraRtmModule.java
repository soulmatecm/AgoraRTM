
package com.soulmate;

import android.support.annotation.Nullable;
import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmClient;
import io.agora.rtm.RtmClientListener;
import io.agora.rtm.RtmMessage;

import static com.facebook.react.bridge.UiThreadUtil.runOnUiThread;


public class RNAgoraRtmModule extends ReactContextBaseJavaModule {
    private final String TAG = "AgoraRtm";
    private final ReactApplicationContext reactContext;
    private RtmClient mRtmClient;

    public RNAgoraRtmModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNAgoraRtm";
    }


    @ReactMethod
    private void sendEvent(ReactContext reactContext,
                           String eventName,
                           @Nullable WritableMap params) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }


    public RtmClient getRtmClient() {
        return mRtmClient;
    }

    @ReactMethod
    private void init(String appid) {

        try {
            mRtmClient = RtmClient.createInstance(reactContext, appid, new RtmClientListener() {
                @Override
                public void onConnectionStateChanged(int state, int reason) {

                }

                // 接收到消息
                @Override
                public void onMessageReceived(RtmMessage rtmMessage, String peerId) {
                    String msg = rtmMessage.getText();

                }

                @Override
                public void onTokenExpired() {

                }
            });

            if (BuildConfig.DEBUG) {
                mRtmClient.setParameters("{\"rtm.log_filter\": 65535}");
            }
        } catch (Exception e) {

            throw new RuntimeException("NEED TO check rtm sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    @ReactMethod
    private void login(String userId, final Promise promise) {
        mRtmClient.login(null, userId, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
                Log.i(TAG, "login success");
                promise.resolve(responseInfo.toString());
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                Log.i(TAG, "login failed: " + errorInfo.getErrorCode());
                promise.reject("-1", errorInfo.toString());
            }
        });
    }
}
