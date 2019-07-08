package com.loanhome.lib.view;


import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.loanhome.lib.R;

/**
 * @Description Created by Don on 2019/7/4
 */
public class Dialog_FilpTip extends DialogFragment {
    private TextView tv_filp;
    private TextView tv_confirm;

    private ImageView iv_filp;

    private String mSide;
    private DialogDismissListener dialogDismissListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View view = LayoutInflater.from(getActivity().getBaseContext()).inflate(R.layout.dialog_flip_tip, null);

        final Dialog dialog = new Dialog(getActivity(), R.style.MyDialogStyle);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setWindowAnimations(R.style.myAnimStyle);
        }
        dialog.setContentView(view);
        tv_filp = view.findViewById(R.id.tv_filp);
        iv_filp = view.findViewById(R.id.iv_filp);
        tv_confirm = view.findViewById(R.id.tv_confirm);
        tv_filp.setText("sadsad");
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialogDismissListener != null) {
                    dialogDismissListener.onDismiss(true);
                }
                dismiss();
            }
        });
        setTvTitle(mSide);

        return dialog;
    }


    public void setSide(String side) {
        this.mSide = side;
        setTvTitle(side);
    }

    public void setTvTitle(String side) {
        if (this.tv_filp!=null) {
            if (side.equals("0")) {
                tv_filp.setText("请将身份证翻转至国徽面");
                Glide.with(this).load(R.drawable.filp_pos).into(iv_filp);
            } else if (side.equals("1")){
                tv_filp.setText("请将身份证翻转至人像面");
                Glide.with(this).load(R.drawable.filp_neg).into(iv_filp);
            }
        }
    }
    public void setDialogDismissListener(DialogDismissListener listener) {
        this.dialogDismissListener = listener;
    }

    public interface DialogDismissListener {

        void onDismiss(boolean isTryAgain);
    }
}
