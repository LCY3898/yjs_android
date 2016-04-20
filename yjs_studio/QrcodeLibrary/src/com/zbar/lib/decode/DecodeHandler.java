package com.zbar.lib.decode;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;


import com.zbar.lib.CaptureCodeActivity;
import com.zbar.lib.ZbarManager;

/**
 * 描述: 接受消息后解码
 */
final class DecodeHandler extends Handler {	

	CaptureCodeActivity activity = null;

	DecodeHandler(CaptureCodeActivity activity) {
		this.activity = activity;
	}

	@Override
	public void handleMessage(Message message) {
		switch (message.what) {
		case MessageIds.MSG_DECODE:
			decode((byte[]) message.obj,message.arg1, message.arg2);
			break;
		case MessageIds.MSG_QUIT:
			Looper.myLooper().quit();
			break;
		}
	}

	private void decode(byte[] data, int width, int height) {
		byte[] rotatedData = new byte[data.length];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++)
				rotatedData[x * height + height - y - 1] = data[x + y * width];
		}
		int tmp = width;// Here we are swapping, that's the difference to #11
		width = height;
		height = tmp;

		ZbarManager manager = new ZbarManager();
		String result = manager.decode(rotatedData, width, height, true,
				activity.getX(), activity.getY(), activity.getCropWidth(),
				activity.getCropHeight());

		if (result != null) {
			if(null != activity.getHandler()){
				Message msg = new Message();
				msg.obj = result;
				msg.what = MessageIds.MSG_DECODE_SUCCESS;
				activity.getHandler().sendMessage(msg);
			}
		} else {
			if (null != activity.getHandler()) {
				activity.getHandler().sendEmptyMessage(MessageIds.MSG_DECODE_FAIL);
			}
		}
	}

}