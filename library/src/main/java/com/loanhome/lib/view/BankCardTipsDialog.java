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

/**
 * 银行卡OCR 提示弹窗类
 * Created by Don on 2019.05.15
 */
public class BankCardTipsDialog extends DialogFragment {
    private ImageView imgColse;
    private TextView tvFailReson;

    private TextView tvTryAgain;
    private DialogDismissListener dialogDismissListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity().getBaseContext()).inflate(R.layout.dialog_bankcard_verify_tips, null);


        Dialog dialog = new Dialog(getActivity(), R.style.MyDialogStyle);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setWindowAnimations(R.style.myAnimStyle);
        }
        dialog.setContentView(view);
        imgColse = view.findViewById(R.id.img_close);
        tvFailReson = view.findViewById(R.id.tv_idcard_fail_reason);
        tvFailReson.setText(getString(R.string.dialog_bankcard_verify_tips));
        tvTryAgain = view.findViewById(R.id.tv_idcard_fail_try_again);
        intView();
        return dialog;
    }
    private void intView() {

        imgColse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialogDismissListener != null){
                    dialogDismissListener.onDismiss();
                }
                dismiss();
            }
        });

        tvTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialogDismissListener != null){
                    dialogDismissListener.onDismiss();
                }
                dismiss();
            }
        });
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

    public void setDialogDismissListener(DialogDismissListener listener) {
        this.dialogDismissListener = listener;
    }

    public interface DialogDismissListener {

        void onDismiss();
    }
}
