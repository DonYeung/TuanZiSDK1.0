package com.loanhome.lib.view;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.loanhome.lib.R;
import com.loanhome.lib.listener.LivenessDialogDismissListener;

public class LivenessResultDialog extends DialogFragment {

    private TextView tvReson;
    private TextView tvNextOperation;
    private ImageView imgClose;
    private TextView tvTitle;

    private String mReason;
    private String mTitle;

    private LivenessDialogDismissListener livenessDialogDismissListener;




    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final String tag = getTag();

        final View view = LayoutInflater.from(getActivity().getBaseContext()).inflate(R.layout.dialog_liveness_result, null);

        final Dialog dialog = new Dialog(getActivity(), R.style.MyDialogStyle);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setWindowAnimations(R.style.myAnimStyle);
        }
        dialog.setContentView(view);
        tvReson = view.findViewById(R.id.tv_liveness_reason);
        tvNextOperation = view.findViewById(R.id.tv_liveness_next_operation);
        imgClose = view.findViewById(R.id.img_close);
        tvTitle = view.findViewById(R.id.liveness_result_title);

        tvNextOperation.setText("再试一次");

        tvNextOperation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (livenessDialogDismissListener != null){
                    livenessDialogDismissListener.onDismiss(true);
                }
                dismiss();
            }
        });

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (livenessDialogDismissListener != null){
                    livenessDialogDismissListener.onDismiss(false);
                }
                dismiss();
            }
        });
        setTvReson(mReason);
        setTvTitle(mTitle);
        return dialog;
    }

    public void setReson(String reason){
        this.mReason = reason;
        setTvReson(reason);
    }

    public void setTvReson(String reason){
        if (this.tvReson != null){
            tvReson.setText(reason);
        }

    }

    public void setTitle(String title){
        this.mTitle = title;
        setTvTitle(title);
    }

    public void setTvTitle(String title){
        if (this.tvTitle != null){
            tvTitle.setText(title);
        }

    }
    public void setDismissButton(boolean isDismiss){
        if (this.tvNextOperation != null) {
            if (isDismiss) {
                tvNextOperation.setVisibility(View.GONE);
            } else {
                tvNextOperation.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setLivenessDialogDismissListener(LivenessDialogDismissListener livenessDialogDismissListener) {
        this.livenessDialogDismissListener = livenessDialogDismissListener;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            Log.d("ABSDIALOGFRAG", "Exception", e);
        }
    }

    
}
