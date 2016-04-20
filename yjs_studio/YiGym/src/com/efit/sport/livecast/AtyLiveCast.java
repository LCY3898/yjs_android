package com.efit.sport.livecast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.cy.widgetlibrary.WidgetUtils;
import com.cy.widgetlibrary.base.BaseFragmentActivity;
import com.cy.widgetlibrary.base.BindView;
import com.cy.widgetlibrary.content.DlgTextMsg;
import com.cy.widgetlibrary.view.AppMsg;
import com.cy.yigym.Task.AppTaskManager;
import com.cy.yigym.ble.BleConnect;
import com.cy.yigym.fragment.live.FragVideoBase;
import com.cy.yigym.fragment.live.FragVideoBase.PlayInfoRetriver;
import com.cy.yigym.net.YJSNet;
import com.cy.yigym.net.req.ReqReviewJoin;
import com.cy.yigym.net.req.ReqSaveWatchProgress;
import com.cy.yigym.net.rsp.RspReviewJoin;
import com.cy.yigym.net.rsp.RspSaveWatchProgress;
import com.cy.yigym.utils.DataStorageUtils;
import com.cy.yigym.utils.PlayerCode;
import com.cy.yigym.view.content.live.HistoryInfoView;
import com.cy.yigym.view.content.live.HistoryRankView;
import com.cy.yigym.view.content.live.HistorySportDatas;
import com.efit.sport.R;
import com.efit.sport.persist.bean.VideoHistDao;
import com.efit.sport.persist.bean.VideoHistory;
import com.hhtech.utils.NetUtils;
import com.hhtech.utils.UITimer;
import com.umeng.analytics.MobclickAgent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import tv.danmaku.ijk.media.player.IMediaPlayer;

public class AtyLiveCast extends BaseFragmentActivity implements
        OnClickListener, PlayInfoRetriver {
    private static final String INTENT_KEY_VIDEO_URL = "videoUrl";
    // 自定义VideoView
    private FullScreenVideoView mVideo;
    private FragVideoBase fragVideo;

    // 头部View
    private View mTopView;

    // 底部View
    private View mBottomView;
    // 视频播放拖动条
    private SeekBar mSeekBar;
    private ImageView mPlay;
    private TextView mPlayTime;
    private TextView mDurationTime;
    @BindView
    private ProgressBar progressBar;
    @BindView
    private ImageView ivRePlay;
    @BindView
    private View rlPlayAgain;

    @BindView
    private TextView tvLiveTitle;

    @BindView
    private TextView tvTimeCutDown;

    @BindView
    private LinearLayout lvClickBack;

    @BindView
    private ImageView btnPlay;


    // 音频管理器
    private AudioManager mAudioManager;

    // 屏幕宽高
    private float width;
    private float height;

    // private String testVideoUrl =
    // "http://121.40.16.113:8082/yjs/mp4/f14458486450820310116001.mp4";
    private String videoUrl = "";

    // 自动隐藏顶部和底部View的时间
    private static final int HIDE_TIME = 5000;

    // 声音调节Toast
    private VolumnController volumnController;

    // 原始屏幕亮度
    private int orginalLight;
    private UITimer timerTask;

    private VideoHistory videoHistory;
    private int curPostion;

    private HistorySportDatas sportData;
    private HistoryInfoView infoView;
    private HistoryRankView rankView;

    private boolean joinSucceed = false;


    private Animation animTopHide;
    private Animation animBottomHide;

    private Animation animTopEnter;
    private Animation animBottomEnter;

    private Handler handler = new Handler();

    private Runnable checkAndShowTip = new Runnable() {

        public void run() {
            handler.removeCallbacks(checkAndShowTip);
            doCheckAndShowTip();
        }

    };


    @Override
    protected boolean isBindViewByAnnotation() {
        return true;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.aty_live_cast;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        handler.postDelayed(checkAndShowTip, 12 * 1000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        savePausePosition();
        btnPlay.setVisibility(View.VISIBLE);
        if (mVideo != null || mVideo.isPlaying()) {
            doPauseVideo();
        }
    }

    private void savePausePosition() {
        if (mVideo != null && curPostion > 0) {
            videoHistory.lastPlayPostion = curPostion;
            VideoHistDao.addOrUpdate(videoHistory);

            //uploadWatchCourseRecord(videoHistory);
        }
    }

    @Override
    protected void initView() {
        getWindow().getDecorView().setSystemUiVisibility(View.INVISIBLE);
        volumnController = new VolumnController(this);
        mVideo = (FullScreenVideoView) findViewById(R.id.videoview);
        mPlayTime = (TextView) findViewById(R.id.play_time);
        mDurationTime = (TextView) findViewById(R.id.total_time);
        mPlay = (ImageView) findViewById(R.id.play_btn);
        mSeekBar = (SeekBar) findViewById(R.id.seekbar);
        mTopView = findViewById(R.id.top_layout);

        View bottomLayout = findViewById(R.id.bottom_layout);
        bottomLayout.setVisibility(View.GONE);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        width = DensityUtil.getWidthInPx(this);
        height = DensityUtil.getHeightInPx(this);
        threshold = DensityUtil.dip2px(this, 18);
        orginalLight = LightnessController.getLightness(this);
        mPlay.setOnClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);
        mSeekBar.setVisibility(View.GONE);
        initPanelView();

        lvClickBack.setOnClickListener(this);

        mBottomView = sportData;

        btnPlay = (ImageView) findViewById(R.id.btnPlay);
        //btnPlay.setVisibility(View.GONE);
        btnPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                doStartVideo();
                btnPlay.setVisibility(View.GONE);
            }
        });
        initAnim();
    }

    private void doCheckAndShowTip() {
        BleConnect instance = BleConnect.instance();
        if (instance == null) {
            WidgetUtils.showToast("请检查蓝牙");
        }

        if (instance.isBleConnected()) {
            WidgetUtils.showToast("蓝牙已连上");
        } else {
            AppMsg.makeText(this, "蓝牙还未连接，请骑动自行车或重新扫描二维码", AppMsg.STYLE_ALERT).show();
            handler.postDelayed(checkAndShowTip, 30 * 1000);
        }
    }


    private void initAnim() {
        animTopHide = AnimationUtils.loadAnimation(this,
                R.anim.option_leave_from_top);
        animBottomHide = AnimationUtils.loadAnimation(this,
                R.anim.option_leave_from_bottom);

        animTopEnter = AnimationUtils.loadAnimation(this,
                R.anim.option_entry_from_top);
        animBottomEnter = AnimationUtils.loadAnimation(this,
                R.anim.option_entry_from_bottom);

        animTopHide.setAnimationListener(new AnimationImp() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                mTopView.setVisibility(View.GONE);
            }
        });
        animBottomHide.setAnimationListener(new AnimationImp() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                mBottomView.setVisibility(View.GONE);
            }
        });
    }

    private void initPanelView() {
        rankView = (HistoryRankView) findViewById(R.id.rankView);
        infoView = (HistoryInfoView) findViewById(R.id.infoView);
        sportData = (HistorySportDatas) findViewById(R.id.sportData);
        rankView = (HistoryRankView) findViewById(R.id.rankView);

        sportData.setLogTag(LOG_TAG);
        sportData.setPlayInfoRetriver(this);
        rankView.setPlayInfoRetriver(this);
        rankView.setInfoView(infoView, rankView, LOG_TAG);
        infoView.setRankView(rankView);
    }

    @Override
    protected void initData() {
        timerTask = new UITimer();
        LiveCastHelper.VideoInfo info = LiveCastHelper
                .getVideoInfo(getIntent());
        videoUrl = info.videoUrl;
        videoHistory = VideoHistDao.getVideoHistory(info.courseId);
        if (videoHistory == null) {
            videoHistory = new VideoHistory();
            videoHistory.pid = DataStorageUtils.getPid();
            videoHistory.coachName = info.coachName;
            videoHistory.courseName = info.courseName;
            videoHistory.duration = info.duration;
            videoHistory.videoUrl = info.videoUrl;
            videoHistory.videoFid = info.avatarFid;
            videoHistory.lastPlayTime = (int) (System.currentTimeMillis() / 1000);
            videoHistory.lastPlayPostion = 0;
            videoHistory.continueTipStatus = false;
            videoHistory.courseId = info.courseId;
        } else {
            curPostion = videoHistory.lastPlayPostion;
        }
        if (videoHistory.continueTipStatus) {
            DlgTextMsg dlg = new DlgTextMsg(mActivity,
                    new DlgTextMsg.ConfirmDialogListener() {
                        @Override
                        public void onCancel() {
                            videoHistory.lastPlayPostion = 1;
                            curPostion = videoHistory.lastPlayPostion;
                            sendJoinReview(true);
                            playVideo();
                        }

                        @Override
                        public void onOk() {
                            sendJoinReview(false);
                            playVideo();
                        }

                        @Override
                        public void onCenter() {
                        }
                    });
            dlg.showCancel(true);
            dlg.getDialog().setCancelable(false);
            dlg.setBtnString("重放", "继续");
            dlg.show("提示", "是否继续上次播放？");
        } else {
            sendJoinReview(true);
            playVideo();
        }

        tvLiveTitle.setText(videoHistory.courseName);
    }

    private void sendJoinReview(boolean freshStart) {

        sportData.initHistoryData(0, 0, 0);
        final int startTime;
        if (freshStart) {
            startTime = 0;
        } else {
            startTime = videoHistory.lastPlayPostion / 1000;
        }
        YJSNet.send(new ReqReviewJoin(videoHistory.courseId, startTime,
                        freshStart), LOG_TAG,
                new YJSNet.OnRespondCallBack<RspReviewJoin>() {
                    @Override
                    public void onSuccess(RspReviewJoin data) {
                        joinSucceed = true;
                        sportData.initHistoryData(startTime,
                                (int) data.data.distance,
                                (int) data.data.calorie);

                    }

                    @Override
                    public void onFailure(String errorMsg) {
                    }
                });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            height = DensityUtil.getWidthInPx(this);
            width = DensityUtil.getHeightInPx(this);
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            width = DensityUtil.getWidthInPx(this);
            height = DensityUtil.getHeightInPx(this);
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onPause() {
        super.onPause();
        rankView.pause();
        LightnessController.setLightness(this, orginalLight);
    }

    @Override
    public void onResume() {
        super.onResume();
        rankView.resume();
        MobclickAgent.onEvent(this, "JoinVideoPage");
    }

    private OnSeekBarChangeListener mSeekBarChangeListener = new OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mHandler.postDelayed(hideRunnable, HIDE_TIME);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mHandler.removeCallbacks(hideRunnable);
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            if (fromUser) {
                int time = progress * mVideo.getDuration() / 100;
                mVideo.seekTo(time);
            }
        }
    };

    private void backward(float delataX) {
        int current = mVideo.getCurrentPosition();
        int backwardTime = (int) (delataX / width * mVideo.getDuration());
        int currentTime = current - backwardTime;
        mVideo.seekTo(currentTime);
        mSeekBar.setProgress(currentTime * 100 / mVideo.getDuration());
        mPlayTime.setText(formatTime(currentTime));
        tvTimeCutDown.setText(formatTime(mVideo.getDuration() - currentTime));
    }

    private void forward(float delataX) {
        int current = mVideo.getCurrentPosition();
        int forwardTime = (int) (delataX / width * mVideo.getDuration());
        int currentTime = current + forwardTime;
        mVideo.seekTo(currentTime);
        mSeekBar.setProgress(currentTime * 100 / mVideo.getDuration());
        mPlayTime.setText(formatTime(currentTime));
        tvTimeCutDown.setText(formatTime(mVideo.getDuration() - currentTime));
    }

    private void volumeDown(float delatY) {
        int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int down = (int) (delatY / height * max * 3);
        int volume = Math.max(current - down, 0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
        int transformatVolume = volume * 100 / max;
        volumnController.show(transformatVolume);
    }

    private void volumeUp(float delatY) {
        int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int up = (int) ((delatY / height) * max * 3);
        int volume = Math.min(current + up, max);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
        int transformatVolume = volume * 100 / max;
        volumnController.show(transformatVolume);
    }

    private void lightDown(float delatY) {
        int down = (int) (delatY / height * 255 * 3);
        int transformatLight = LightnessController.getLightness(this) - down;
        LightnessController.setLightness(this, transformatLight);
    }

    private void lightUp(float delatY) {
        int up = (int) (delatY / height * 255 * 3);
        int transformatLight = LightnessController.getLightness(this) + up;
        LightnessController.setLightness(this, transformatLight);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sportData.fini();
        rankView.fini();
        mVideo.stopPlayback();
        if (timerTask != null) {
            timerTask.cancel();
        }
        mHandler.removeCallbacksAndMessages(null);
        savePauseTip();
    }

    private void updateTime() {
        if (mVideo.getCurrentPosition() > 0) {
            curPostion = mVideo.getCurrentPosition();
            mPlayTime.setText(formatTime(mVideo.getCurrentPosition()));
            tvTimeCutDown.setText(formatTime(mVideo.getDuration()
                    - mVideo.getCurrentPosition()));
            int duration = mVideo.getDuration();
            int progress = duration == 0 ? 0
                    : (mVideo.getCurrentPosition() * 100 / duration);
            mSeekBar.setProgress(progress);
            if (mVideo.getCurrentPosition() > mVideo.getDuration() - 100) {
                mPlayTime.setText("00:00");
                tvTimeCutDown.setText("00:00");
                mSeekBar.setProgress(0);
            }
            mSeekBar.setSecondaryProgress(mVideo.getBufferPercentage());
        } else {
            mPlayTime.setText("00:00");
            tvTimeCutDown.setText("00:00");
            mSeekBar.setProgress(0);
        }
    }

    private void playVideo() {
//        if(true) return;
        /*
		 * HttpProxyCacheServer proxy = VideoProxyFactory.getProxy();
		 * mVideo.setVideoPath(proxy.getProxyUrl(videoUrl));
		 */
        mVideo.setVideoPath(videoUrl);
        mVideo.requestFocus();
        mVideo.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                mVideo.setVideoWidth(mp.getVideoWidth());
                mVideo.setVideoHeight(mp.getVideoHeight());

                mVideo.start();
                videoHistory.duration = mVideo.getDuration();
                if (videoHistory.lastPlayPostion > 0) {
                    mVideo.seekTo(videoHistory.lastPlayPostion);
                }
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                mHandler.removeCallbacks(hideRunnable);
                mHandler.postDelayed(hideRunnable, HIDE_TIME);
                mDurationTime.setText(formatTime(mVideo.getDuration()));
                timerTask.schedule(new Runnable() {
                    @Override
                    public void run() {
                        updateTime();
                    }
                }, 1000);
            }
        });
        mVideo.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mPlay.setImageResource(R.drawable.video_btn_down);
                mPlayTime.setText("00:00");
                tvTimeCutDown.setText("00:00");
                mSeekBar.setProgress(0);
                rlPlayAgain.setVisibility(View.VISIBLE);
                TextView tv = (TextView) findViewById(R.id.text);
                tv.setText("视频播放结束");
            }
        });

        mVideo.setOnTouchListener(mTouchListener);
        mVideo.setOnErrorListener(mErrorListener);
        mVideo.setOnInfoListener(mInfoListner);


    }

    // 上传观看历史纪录
    private void uploadWatchCourseRecord(VideoHistory history) {
        YJSNet.send(new ReqSaveWatchProgress(history), LOG_TAG,
                new YJSNet.OnRespondCallBack<RspSaveWatchProgress>() {
                    @Override
                    public void onSuccess(RspSaveWatchProgress data) {
                        Log.d("tttt", "onSuccess=======" + data.msg);
                    }

                    @Override
                    public void onFailure(String errorMsg) {
                        Log.d("tttt", "errorMsg=======" + errorMsg);
                    }
                });
    }

    private Runnable hideRunnable = new Runnable() {

        @Override
        public void run() {
            showOrHide();
        }
    };

    @SuppressLint("SimpleDateFormat")
    private String formatTime(long time) {
        DateFormat formatter = new SimpleDateFormat("mm:ss");
        return formatter.format(new Date(time));
    }

    private float mLastMotionX;
    private float mLastMotionY;
    private int startX;
    private int startY;
    private int threshold;
    private boolean isClick = true;

    private long lastClickTime = 0;
    private OnTouchListener mTouchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            final float x = event.getX();
            final float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mLastMotionX = x;
                    mLastMotionY = y;
                    startX = (int) x;
                    startY = (int) y;
                    break;
                case MotionEvent.ACTION_MOVE:
                    float deltaX = x - mLastMotionX;
                    float deltaY = y - mLastMotionY;
                    float absDeltaX = Math.abs(deltaX);
                    float absDeltaY = Math.abs(deltaY);
                    // 声音调节标识
                    boolean isAdjustAudio = false;
                    if (absDeltaX > threshold && absDeltaY > threshold) {
                        if (absDeltaX < absDeltaY) {
                            isAdjustAudio = true;
                        } else {
                            isAdjustAudio = false;
                        }
                    } else if (absDeltaX < threshold && absDeltaY > threshold) {
                        isAdjustAudio = true;
                    } else if (absDeltaX > threshold && absDeltaY < threshold) {
                        isAdjustAudio = false;
                    } else {
                        return true;
                    }
                    if (isAdjustAudio) {
                        if (x < width / 2) {
                            if (deltaY > 0) {
                                lightDown(absDeltaY);
                            } else if (deltaY < 0) {
                                lightUp(absDeltaY);
                            }
                        } else {
                            if (deltaY > 0) {
                                volumeDown(absDeltaY);
                            } else if (deltaY < 0) {
                                volumeUp(absDeltaY);
                            }
                        }

                    } else {
                        // tangtaotao comment
					/*
					 * if (deltaX > 0) { forward(absDeltaX); } else if (deltaX <
					 * 0) { backward(absDeltaX); }
					 */
                    }
                    mLastMotionX = x;
                    mLastMotionY = y;
                    break;
                case MotionEvent.ACTION_UP:
                    if (Math.abs(x - startX) > threshold
                            || Math.abs(y - startY) > threshold) {
                        isClick = false;
                    }
                    mLastMotionX = 0;
                    mLastMotionY = 0;
                    startX = (int) 0;
                    if (isClick) {
                        long timestamp = System.currentTimeMillis();
                        if (timestamp - lastClickTime >= 500) {
                            showOrHide();
                            lastClickTime = timestamp;
                        }
                    }
                    isClick = true;
                    break;

                default:
                    break;
            }
            return true;
        }

    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_btn:
                if (mVideo.isPlaying()) {
                    doPauseVideo();
                } else {
                    doStartVideo();
                }
                break;
            case R.id.lvClickBack:
                finish();
                break;
            default:
                break;
        }
    }

    private void doPauseVideo() {
        mVideo.pause();
        mPlay.setImageResource(R.drawable.video_btn_down);
    }

    private void doStartVideo() {
        mVideo.start();
        mPlay.setImageResource(R.drawable.video_btn_on);
    }

    private void showOrHide() {
        if (mTopView.getVisibility() == View.VISIBLE) {
            mTopView.clearAnimation();
            mTopView.startAnimation(animTopHide);
            mBottomView.clearAnimation();
            mBottomView.startAnimation(animBottomHide);
        } else {
            mTopView.setVisibility(View.VISIBLE);
            mTopView.clearAnimation();

            mTopView.startAnimation(animTopEnter);

            mBottomView.setVisibility(View.VISIBLE);
            mBottomView.clearAnimation();

            mBottomView.startAnimation(animBottomEnter);
            mHandler.removeCallbacks(hideRunnable);
            //mHandler.postDelayed(hideRunnable, HIDE_TIME);
        }
    }

    @Override
    public int getPlayTime() {
        return curPostion / 1000;
    }

    @Override
    public int getDuration() {
        if (mVideo != null) {
            return mVideo.getDuration() / 1000;
        }
        return videoHistory.duration / 1000;
    }

    @Override
    public String getCourseId() {
        return videoHistory.courseId;
    }

    @Override
    public boolean isJoinSucceed() {
        return joinSucceed;
    }

    private class AnimationImp implements AnimationListener {

        @Override
        public void onAnimationEnd(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationStart(Animation animation) {
        }

    }

    /**
     * 启动往期课程视频播放界面
     *
     * @param context  发起启动的上下文
     * @param videoUrl 视频地址
     */
    public static void start(Context context, String videoUrl) {
        if (context == null || TextUtils.isEmpty(videoUrl))
            return;
        Intent intent = new Intent(context, AtyLiveCast.class);
        intent.putExtra(INTENT_KEY_VIDEO_URL, videoUrl);
        context.startActivity(intent);
    }

    private VideoPlayerListener playerListener = new VideoPlayerListener() {
        @Override
        public void onNetworkError(int code) {
            stopOnError();
        }

        @Override
        public void onServerError() {
            stopOnError();
        }

        @Override
        public void onUriAddrError() {
            stopOnError();
        }

        @Override
        public void onBuffering(boolean isBuffering) {
            progressBar.setVisibility(isBuffering ? View.VISIBLE : View.GONE);
        }
    };

    private void stopOnError() {
        mVideo.stopPlayback();
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        rlPlayAgain.setVisibility(View.VISIBLE);
        ivRePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetUtils.isNetConnected(mContext)) {
                    WidgetUtils.showToast("您的网络未连接，请先检查网络连接");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                rlPlayAgain.setVisibility(View.GONE);
                playVideo();
            }
        });
    }

    private MediaPlayer.OnErrorListener mErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            if (playerListener == null) {
                return false;
            }
            if (extra == PlayerCode.EXTRA_CODE_IO_ERROR) {
                playerListener.onNetworkError(extra);
            } else if (extra == PlayerCode.EXTRA_CODE_STREAM_DISCONNECTED
                    || extra == PlayerCode.EXTRA_CODE_CONNECTION_REFUSED
                    || extra == PlayerCode.EXTRA_CODE_CONNECTION_TIMEOUT) {
                playerListener.onServerError();
            } else if (extra == PlayerCode.EXTRA_CODE_INVALID_URI
                    || extra == PlayerCode.EXTRA_CODE_404_NOT_FOUND) {
                playerListener.onUriAddrError();
            } else {
                playerListener.onNetworkError(extra);
            }
            return true;
        }
    };

    private MediaPlayer.OnInfoListener mInfoListner = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            if (playerListener == null) {
                return false;
            }
            if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_START) {
                playerListener.onBuffering(true);
            } else if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_END) {
                playerListener.onBuffering(false);
            }
            return true;
        }
    };

    private void savePauseTip() {
        AppTaskManager.execute(new Runnable() {
            @Override
            public void run() {
                if (videoHistory == null || videoHistory.lastPlayPostion == 0) {
                    return;
                }
                if (videoHistory.duration == videoHistory.lastPlayPostion) {
                    videoHistory.continueTipStatus = false;
                } else {
                    videoHistory.continueTipStatus = true;
                }
                videoHistory.lastPlayTime = (int) (System.currentTimeMillis() / 1000);
                VideoHistDao.addOrUpdate(videoHistory);
                uploadWatchCourseRecord(videoHistory);
            }
        });

    }
}
