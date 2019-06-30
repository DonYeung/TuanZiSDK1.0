package com.loanhome.lib.view;


import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.loanhome.lib.R;
import com.loanhome.lib.listener.IDCardVerifyFailDialogDismissListener;

public class IDCardVerifyFailDialog extends DialogFragment {

    private ImageView imgColse;
    private TextView tvFailReson;
    private TextView tvFailTip_1;
    private ImageView imgPhoto_1;
    private TextView tvFailTip_2_1;
    private ImageView imgPhoto_2_1;
    private TextView tvFailTip_2_2;
    private ImageView imgPhoto_2_2;
    private TextView tvFailTip_3_1;
    private ImageView imgPhoto_3_1;
    private TextView tvFailTip_3_2;
    private ImageView imgPhoto_3_2;
    private TextView tvFailTip_3_3;
    private ImageView imgPhoto_3_3;
    private TextView tvTryAgain;
    private IDCardVerifyFailDialogDismissListener listener;
    private LinearLayout mLlType1;
    private LinearLayout mLlType2;
    private LinearLayout mLlType3;
    private boolean isInited = false;

    public static final String FAIL_TYPE_1 = "1";
    public static final String FAIL_TYPE_2 = "2";
    public static final String FAIL_TYPE_3 = "3";

    public static final int MESSAGE_SET_TIP_LATER = 100;
    public static final int MESSAGE_SET_REASON_LATER = 200;
    public static final int MESSAGE_SET_IMAGE_LATER = 300;

    public static final String BANKCARD_FAIL = "bankcard_fail";

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MESSAGE_SET_TIP_LATER:
                    Bundle bundle = (Bundle) msg.obj;
                    if (bundle != null){
                        String tip = bundle.getString("tip");
                        int type = bundle.getInt("type");
                        int position = bundle.getInt("position");
                        setTvFailTip(tip, type, position);
                    }
                    break;
                case MESSAGE_SET_REASON_LATER:
                    String reason = (String) msg.obj;
                    setFaidReason(reason);
                    break;
                case MESSAGE_SET_IMAGE_LATER:
                    Bundle imgBundle = (Bundle) msg.obj;
                    if (imgBundle != null){
                        String tip = imgBundle.getString("imgUrl");
                        int type = imgBundle.getInt("type");
                        int position = imgBundle.getInt("position");
                        setImgPhoto(tip, type, position);
                    }
                    break;
            }
        }
    };

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity().getBaseContext()).inflate(R.layout.dialog_idcard_verify_fail, null);


        Dialog dialog = new Dialog(getActivity(), R.style.MyDialogStyle);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setWindowAnimations(R.style.myAnimStyle);
        }
        dialog.setContentView(view);
        imgColse = view.findViewById(R.id.img_close);
        tvFailReson = view.findViewById(R.id.tv_idcard_fail_reason);
        //图片以及副文案
        tvFailTip_1= view.findViewById(R.id.tv_idcard_fail_tip_1);
        tvFailTip_2_1= view.findViewById(R.id.tv_idcard_fail_tip_2_1);
        tvFailTip_2_2= view.findViewById(R.id.tv_idcard_fail_tip_2_1);
        tvFailTip_3_1= view.findViewById(R.id.tv_idcard_fail_tip_2_1);
        tvFailTip_3_2= view.findViewById(R.id.tv_idcard_fail_tip_2_1);
        tvFailTip_3_3= view.findViewById(R.id.tv_idcard_fail_tip_2_1);

        imgPhoto_1 = view.findViewById(R.id.img_photo_1);
        imgPhoto_2_1 = view.findViewById(R.id.img_photo_2_1);
        imgPhoto_2_2 = view.findViewById(R.id.img_photo_2_2);
        imgPhoto_3_1 = view.findViewById(R.id.img_photo_3_1);
        imgPhoto_3_2 = view.findViewById(R.id.img_photo_3_2);
        imgPhoto_3_3 = view.findViewById(R.id.img_photo_3_3);

        mLlType1 = view.findViewById(R.id.idcard_fail_type_1);
        mLlType2 = view.findViewById(R.id.idcard_fail_type_2);
        mLlType3 = view.findViewById(R.id.idcard_fail_type_3);

        tvTryAgain = view.findViewById(R.id.tv_idcard_fail_try_again);
        intView();
        isInited = true;
        return dialog;
    }

    public void setFaidReason(String reason){
        if (!isInited){

            Message message = Message.obtain();
            message.what = MESSAGE_SET_REASON_LATER;
            message.obj = reason;
            handler.sendMessageDelayed(message, 500);
            return;
        }
        tvFailReson.setText(reason);
    }

    public void setTvFailTip(String tip, int type, int position){
        if (!isInited){
            Message message = Message.obtain();
            message.what = MESSAGE_SET_TIP_LATER;
            Bundle bundle = new Bundle();
            bundle.putString("tip", tip);
            bundle.putInt("type", type);
            bundle.putInt("position", position);
            message.obj = bundle;
            handler.sendMessageDelayed(message, 500);
            return;
        }
        switch (type){
            case 1:
                tvFailTip_1.setText(tip);
                break;
            case 2:
                if (position == 0){
                    tvFailTip_2_1.setText(tip);
                } else {
                    tvFailTip_2_2.setText(tip);
                }
                break;
            case 3:
                if (position == 0){
                    tvFailTip_3_1.setText(tip);
                } else if (position == 1){
                    tvFailTip_3_2.setText(tip);
                } else {
                    tvFailTip_3_3.setText(tip);
                }
                break;
        }
    }


    public void setImgPhoto(String imgUrl, int type, int position){
        if (!isInited){
            Message message = Message.obtain();
            message.what = MESSAGE_SET_IMAGE_LATER;
            Bundle bundle = new Bundle();
            bundle.putString("imgUrl", imgUrl);
            bundle.putInt("type", type);
            bundle.putInt("position", position);
            message.obj = bundle;
            handler.sendMessageDelayed(message, 500);
            return;
        }
        switch (type){
            case 1:
                RequestOptions options = new RequestOptions();
                options.placeholder(R.drawable.fault_tips_1);
                Glide.with(this).load(imgUrl).apply(options).into(imgPhoto_1);
                break;
            case 2:
                if (position == 0){
                    RequestOptions options1 = new RequestOptions();
                    options1.placeholder(R.drawable.fault_tips_2);
                    Glide.with(this).load(imgUrl).apply(options1).into(imgPhoto_2_1);
                } else {
                    RequestOptions options2 = new RequestOptions();
                    options2.placeholder(R.drawable.fault_tips_3);
                    Glide.with(this).load(imgUrl).into(imgPhoto_2_2);
                }
                break;
            case 3:
                if (position == 0){
                    RequestOptions options3 = new RequestOptions();
                    options3.placeholder(R.drawable.fault_tips_4);
                    Glide.with(this).load(imgUrl).apply(options3).into(imgPhoto_3_1);
                } else if (position == 1){
                    RequestOptions options4 = new RequestOptions();
                    options4.placeholder(R.drawable.fault_tips_5);
                    Glide.with(this).load(imgUrl).apply(options4).into(imgPhoto_3_2);
                } else {
                    RequestOptions options5 = new RequestOptions();
                    options5.placeholder(R.drawable.fault_tips_6);
                    Glide.with(this).load(imgUrl).apply(options5).into(imgPhoto_3_3);
                }
                break;
        }
    }


    private void intView() {

        String tag = getTag();
        if (tag.equals(FAIL_TYPE_1)){
            mLlType1.setVisibility(View.VISIBLE);
            mLlType2.setVisibility(View.GONE);
            mLlType3.setVisibility(View.GONE);
        } else if (tag.equals(FAIL_TYPE_2)){
            mLlType1.setVisibility(View.GONE);
            mLlType2.setVisibility(View.VISIBLE);
            mLlType3.setVisibility(View.GONE);
        }  else if(tag.equals(BANKCARD_FAIL)){
            mLlType1.setVisibility(View.GONE);
            mLlType2.setVisibility(View.GONE);
            mLlType3.setVisibility(View.GONE);
        }
        else {
            mLlType1.setVisibility(View.GONE);
            mLlType2.setVisibility(View.GONE);
            mLlType3.setVisibility(View.VISIBLE);
        }

        imgColse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null){
                    listener.onDismiss();
                }
                dismiss();
            }
        });

        tvTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null){
                    listener.onDismiss();
                }
                dismiss();
            }
        });
    }

    public void setIDCardVerifyFailDialogDismissListener(IDCardVerifyFailDialogDismissListener listener) {
        this.listener = listener;
    }
}
