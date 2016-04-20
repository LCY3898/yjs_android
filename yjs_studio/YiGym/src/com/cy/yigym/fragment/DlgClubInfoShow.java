package com.cy.yigym.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cy.imagelib.ImageLoaderUtils;
import com.cy.yigym.utils.DataStorageUtils;
import com.cy.yigym.view.content.EventHeadImageView;
import com.efit.sport.R;

/**
 * Created by lijianqiang.
 */
public class DlgClubInfoShow extends DialogFragment {

    public static final String TAG = "DlgClubInfoShow";

    private LinearLayout mRootView;

    private ImageView ivClose;

    private EventHeadImageView mUserHeader;

    private TextView mUserNick;

    private TextView mUserRank;

    private TextView mDistance;

    private TextView mSpeed;

    private TextView mRpm;

    private TextView mCalorie;

    private TextView mResist;


    private ExitCallBack callBack;

    private RankInfoBean value;


    /**
     * DlgClubInfoShow digShow = new DlgClubInfoShow(new DlgClubInfoShow.RankInfoBean("11", "30", "40", "50", "60", "70"));
     * digShow.setCancelable(false);
     * digShow.setCallBack(new DlgClubInfoShow.ExitCallBack() {
     *
     * @Override public void onExit() {
     * //Toast.makeText(mContext, "执行退出操作", Toast.LENGTH_SHORT).show();
     * }
     * });
     * digShow.show(getFragmentManager(), "DlgClubInfoShow");
     */
    public DlgClubInfoShow() {
        this(new RankInfoBean("0", "0", "0", "0", "0", "0"));
    }

    @SuppressLint("ValidFragment")
    public DlgClubInfoShow(RankInfoBean bean) {
        this.value = bean;
    }

    public void setCallBack(ExitCallBack callBack) {
        this.callBack = callBack;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mRootView = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.dlg_vlc_club_show,
                null);
        ivClose = (ImageView) mRootView.findViewById(R.id.ivClose);
        mUserHeader = (EventHeadImageView) mRootView.findViewById(R.id.mUserHeader);
        mUserNick = (TextView) mRootView.findViewById(R.id.mUserNick);
        mUserRank = (TextView) mRootView.findViewById(R.id.mUserRank);
        mDistance = (TextView) mRootView.findViewById(R.id.mDistance);
        mSpeed = (TextView) mRootView.findViewById(R.id.mSpeed);
        mRpm = (TextView) mRootView.findViewById(R.id.mRpm);
        mCalorie = (TextView) mRootView.findViewById(R.id.mCalorie);
        mResist = (TextView) mRootView.findViewById(R.id.mResist);

        initValue();
        initListener();

        return new AlertDialog.Builder(getActivity()).setView(mRootView).create();
    }

    private void initValue() {
        mUserNick.setText(DataStorageUtils.getUserNickName());
        ImageLoaderUtils.getInstance().loadImage(
                DataStorageUtils.getHeadDownloadUrl(DataStorageUtils.getCurUserProfileFid()),
                mUserHeader);
        if (value != null) {
            mUserRank.setText(value.mUserRank);
            mDistance.setText(value.mDistance);
            mSpeed.setText(value.mSpeed);
            mRpm.setText(value.mRpm);
            mCalorie.setText(value.mCalorie);
            mResist.setText(value.mResist);
        }

    }


    private void initListener() {
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "mConfirm");

                if (callBack != null) {
                    callBack.onExit();
                }
                dismiss();
            }
        });


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    public interface ExitCallBack {
        public void onExit();
    }

    public static class RankInfoBean {
        public String mUserRank;
        public String mDistance;
        public String mSpeed;
        public String mRpm;
        public String mCalorie;
        public String mResist;

        public RankInfoBean(String mUserRank, String mDistance, String mSpeed, String mResist, String mRpm, String mCalorie) {
            this.mUserRank = mUserRank;
            this.mDistance = mDistance;
            this.mSpeed = mSpeed;
            this.mRpm = mRpm;
            this.mCalorie = mCalorie;
            this.mResist = mResist;
        }
    }

}
