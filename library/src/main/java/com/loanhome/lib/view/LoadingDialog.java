package com.loanhome.lib.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import com.loanhome.lib.R;

/**
 * @author liaopeijian
 * @Date 2018/7/8
 */
public class LoadingDialog extends DialogFragment {
    private OnDismissListener mListener;

    public OnDismissListener getListener() {
        return mListener;
    }

    public void setListener(OnDismissListener mListener) {
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getActivity() == null){
            return null;
        }
        View view = getActivity().getLayoutInflater().inflate(R.layout.loading_dialog_layout,null);
        TextView view1 = view.findViewById(R.id.tv_loading);
        iniTextData(view1);
        Dialog dialog = new Dialog(getActivity(),R.style.LoadingDialogStyle);
        dialog.setContentView(view);
        return dialog;
    }

    private void iniTextData(TextView view){
        String tag = getTag();
//        if (tag != null &&!tag.isEmpty()){
//            view.setText(tag);
//        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (mListener != null){
            mListener.onDismiss();
        }
        super.onDismiss(dialog);
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
    }

     public interface OnDismissListener{
        void onDismiss();
    }
}


