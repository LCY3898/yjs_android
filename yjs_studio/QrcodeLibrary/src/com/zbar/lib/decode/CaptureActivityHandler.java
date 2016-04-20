package com.zbar.lib.decode;

import android.os.Handler;
import android.os.Message;


import com.zbar.lib.CaptureCodeActivity;
import com.zbar.lib.camera.CameraManager;

/**
 * 作者: 陈涛(1076559197@qq.com)
 * 
 * 时间: 2014年5月9日 下午12:23:32
 *
 * 版本: V_1.0.0
 *
 * 描述: 扫描消息转发
 */
public final class CaptureActivityHandler extends Handler {

	DecodeThread decodeThread = null;
	CaptureCodeActivity activity = null;
	private State state;

	private enum State {
		PREVIEW, SUCCESS, DONE
	}

	public CaptureActivityHandler(CaptureCodeActivity activity) {
		this.activity = activity;
		decodeThread = new DecodeThread(activity);
		decodeThread.start();
		state = State.SUCCESS;
		CameraManager.get().startPreview();
		restartPreviewAndDecode();
	}

	@Override
	public void handleMessage(Message message) {

		switch (message.what) {
		case MessageIds.MSG_AUTO_FOCUS:
			if (state == State.PREVIEW) {
				CameraManager.get().requestAutoFocus(this, MessageIds.MSG_AUTO_FOCUS);
			}
			break;
		case MessageIds.MSG_RESTART_VIEW:
			restartPreviewAndDecode();
			break;
		case MessageIds.MSG_DECODE_SUCCESS:
			state = State.SUCCESS;
			activity.handleDecode((String) message.obj);// 解析成功，回调
			break;

		case MessageIds.MSG_DECODE_FAIL:
			state = State.PREVIEW;
			CameraManager.get().requestPreviewFrame(decodeThread.getHandler(),
					MessageIds.MSG_DECODE);
			break;
		}

	}

	public void quitSynchronously() {
		state = State.DONE;
		CameraManager.get().stopPreview();
		removeMessages(MessageIds.MSG_DECODE_SUCCESS);
		removeMessages(MessageIds.MSG_DECODE_FAIL);
		removeMessages(MessageIds.MSG_DECODE);
		removeMessages(MessageIds.MSG_AUTO_FOCUS);
	}

	private void restartPreviewAndDecode() {
		if (state == State.SUCCESS) {
			state = State.PREVIEW;
			CameraManager.get().requestPreviewFrame(decodeThread.getHandler(),
					MessageIds.MSG_DECODE);
			CameraManager.get().requestAutoFocus(this, MessageIds.MSG_AUTO_FOCUS);
		}
	}

}
