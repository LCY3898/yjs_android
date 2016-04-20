package com.efit.sport.p2p;

import com.yuntongxun.ecsdk.ECVoIPCallManager;
import com.yuntongxun.ecsdk.VideoRatio;

public class VoipCallback implements ECVoIPCallManager.OnVoIPListener {

    public interface VoipCbk {
        void onVideoRatioChanged(int width, int height);

        void onCallRelease(String callId);

        void onCallAnswer(String callId);

        void onCallFail(String callId,int reason);

        void onCallProcessing(String callId);
    }

    public VoipCallback(VoipCbk cbk) {
        listener = cbk;
    }

    private VoipCbk listener;


    @Override
    public final void onCallEvents(ECVoIPCallManager.VoIPCall voipCall) {
        // 接收VoIP呼叫事件回调
        if (voipCall == null) {
            return;
        }

        if (listener == null) {
            return;
        }
        String callId = voipCall.callId;
        switch (voipCall.callState) {
            case ECCALL_PROCEEDING:
                listener.onCallProcessing(callId);
                break;
            case ECCALL_ALERTING:
                break;
            case ECCALL_ANSWERED:
                listener.onCallAnswer(callId);
                break;
            case ECCALL_FAILED:
                listener.onCallFail(callId,voipCall.reason);
                break;

            case ECCALL_RELEASED:
                listener.onCallRelease(callId);
                break;
            default:
                break;
        }
    }


    @Override
    public void onDtmfReceived(String arg0, char arg1) {

    }

    @Override
    public void onSwitchCallMediaTypeRequest(String arg0, ECVoIPCallManager.CallType arg1) {

    }

    @Override
    public void onSwitchCallMediaTypeResponse(String arg0, ECVoIPCallManager.CallType arg1) {

    }


    @Override
    public void onVideoRatioChanged(VideoRatio videoRatio) {
        if (listener != null) {
            if(videoRatio == null) {
                return;
            }
            listener.onVideoRatioChanged(videoRatio.getWidth(), videoRatio.getHeight());
        }
    }
}