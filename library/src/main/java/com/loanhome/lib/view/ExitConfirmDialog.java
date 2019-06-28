package com.loanhome.lib.view;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import com.loanhome.lib.R;
import com.loanhome.lib.listener.ExitConfirmDialogDismissListener;

public class ExitConfirmDialog extends DialogFragment {

    private TextView tvExitConfirm;
    private TextView tvWaitMoreTime;

    private ExitConfirmDialogDismissListener exitConfirmDialogDismissListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity().getBaseContext()).inflate(R.layout.dialog_exit_confirm, null);

        Dialog dialog = new Dialog(getActivity(), R.style.MyDialogStyle);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setWindowAnimations(R.style.myAnimStyle);
        }
        dialog.setContentView(view);
        tvExitConfirm = view.findViewById(R.id.tv_exit_confirm);
        tvWaitMoreTime = view.findViewById(R.id.rv_wait_more_time);
        initListener();

        return dialog;
    }

    private void initListener() {

        tvExitConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (exitConfirmDialogDismissListener != null){
                    exitConfirmDialogDismissListener.onDismiss(false);
                }
                dismiss();

            }
        });

        tvWaitMoreTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (exitConfirmDialogDismissListener != null){
                    exitConfirmDialogDismissListener.onDismiss(true);
                }
                dismiss();

            }
        });
    }


    public void setExitConfirmDialogDismissListener(ExitConfirmDialogDismissListener exitConfirmDialogDismissListener) {
        this.exitConfirmDialogDismissListener = exitConfirmDialogDismissListener;
    }
}
