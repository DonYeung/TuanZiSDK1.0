package com.loanhome.lib.view;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loanhome.lib.R;
import com.loanhome.lib.listener.IDCardResultDialogDismissListener;


public class ConfirmDialog extends DialogFragment {

    public static final String FRONT_CONFIRM = "front_confirm";
    public static final String BACK_CONFIRM = "back_confirm";
    public static final String BANKCARD_CONFIRM = "bankcard_confirm";

    private TextView tvConfirmTip;
    private TextView tvIdName,tv_name;
    private TextView tvIdNumber,tv_number;
    private TextView tvIdAddress,tv_address;
    private TextView tvIssueBy;
    private TextView tvValidDate;
    private TextView tvCancle;
    private TextView tvConfirm;
    private ImageView imgClose;
    private TextView tv_banktips,tvBankCardNumber;

    private String idName;
    private String idNumber;
    private String idAddress;
    private String idIssueBy;
    private String idValidDate;
    private String bankCardNumber;



    private RelativeLayout rlFrontResult;
    private LinearLayout llBackResult;

    private IDCardResultDialogDismissListener iDCardResultDialogDismissListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity().getBaseContext()).inflate(R.layout.confirm_dialog, null);

        Dialog dialog = new Dialog(getActivity(), R.style.MyDialogStyle);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setWindowAnimations(R.style.myAnimStyle);
        }
        dialog.setContentView(view);
        tvConfirmTip = view.findViewById(R.id.tv_confirm_tip);
        tvIdName = view.findViewById(R.id.tv_id_name);
        tv_name = view.findViewById(R.id.tv_name);
        tvIdNumber = view.findViewById(R.id.tv_id_number);
        tv_number = view.findViewById(R.id.tv_number);
        tvIdAddress = view.findViewById(R.id.tv_id_address);
        tv_address = view.findViewById(R.id.tv_address);
        tvIssueBy = view.findViewById(R.id.tv_id_issue_by);
        tvValidDate = view.findViewById(R.id.tv_id_valid_date);
        tvCancle = view.findViewById(R.id.tv_back_modify);
        tvConfirm = view.findViewById(R.id.tv_confirm);
        tvBankCardNumber = view.findViewById(R.id.tv_id_bankcard);
        tv_banktips = view.findViewById(R.id.tv_banktips);
        imgClose = view.findViewById(R.id.img_close);
        rlFrontResult = view.findViewById(R.id.ll_front_result_container);
        llBackResult = view.findViewById(R.id.ll_back_result_container);
        initClick();
        initData();
        setTvIdName(idName);
        setTvIdNumber(idNumber);
        setTvIdAddress(idAddress);
        setTvIssueBy(idIssueBy);
        setTvValidDate(idValidDate);
        setTvBankCardNumber(bankCardNumber);
        return dialog;
    }

    private void initClick() {
        tvCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if (iDCardResultDialogDismissListener != null){
                    iDCardResultDialogDismissListener.onDismiss(false);
                }

            }
        });

        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if (iDCardResultDialogDismissListener != null){
                    iDCardResultDialogDismissListener.onDismiss(true);
                }
            }
        });
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if (iDCardResultDialogDismissListener != null){
                    iDCardResultDialogDismissListener.onDismiss(false);
                }
            }
        });
    }

    private void initData() {

        String tag = getTag();
        if (tag.equals(FRONT_CONFIRM)){
            tvConfirmTip.setText("请确认人像面信息，信息有误将影响授信结果");
            rlFrontResult.setVisibility(View.VISIBLE);
            llBackResult.setVisibility(View.GONE);
            tv_banktips.setVisibility(View.GONE);
            tvBankCardNumber.setVisibility(View.GONE);
        } else if (tag.equals(BACK_CONFIRM)){
            tvConfirmTip.setText("请确认国徽面信息，信息有误将影响授信结果");
            rlFrontResult.setVisibility(View.GONE);
            llBackResult.setVisibility(View.VISIBLE);
            tv_banktips.setVisibility(View.GONE);
            tvBankCardNumber.setVisibility(View.GONE);
        } else if (tag.equals(BANKCARD_CONFIRM)){
            tvConfirmTip.setText("请确认卡片信息，信息有误将无法正常绑卡");
            rlFrontResult.setVisibility(View.VISIBLE);
//            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) tvIdName.getLayoutParams();
//            layoutParams.bottomMargin=10;
//            tvIdName.setLayoutParams(layoutParams);
            tv_banktips.setVisibility(View.VISIBLE);
            tvBankCardNumber.setVisibility(View.VISIBLE);
            tvIdName.setVisibility(View.GONE);
            tv_name.setVisibility(View.GONE);
            tv_address.setVisibility(View.GONE);
            tvIdAddress.setVisibility(View.GONE);
            tv_number.setVisibility(View.GONE);
            tvIdNumber.setVisibility(View.GONE);
            llBackResult.setVisibility(View.GONE);
        }
    }


    public void setIdName(String idName) {
        this.idName = idName;
        setTvIdName(idName);
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
        setTvIdNumber(idNumber);
    }

    public void setIdAddress(String idAddress) {
        this.idAddress = idAddress;
        setTvIdAddress(idAddress);
    }
    public void setBankCardNumber(String bankCardNumber) {
        this.bankCardNumber = bankCardNumber;
        setTvBankCardNumber(bankCardNumber);
    }

    public void setIdIssueBy(String idIssueBy) {
        this.idIssueBy = idIssueBy;
        setTvIssueBy(idIssueBy);
    }

    public void setIdValidDate(String idValidDate) {
        this.idValidDate = idValidDate;
        setTvValidDate(idValidDate);
    }



    public void setTvIdName(String idName) {
        if (this.tvIdName != null){
            this.tvIdName.setText(idName);
        }
    }

    public void setTvIdNumber(String idNumber) {
        if (this.tvIdNumber != null){
            this.tvIdNumber.setText(idNumber);
        }
    }
    public void setTvBankCardNumber(String bankCardNumber) {
        if (this.tvBankCardNumber != null){
            this.tvBankCardNumber.setText(bankCardNumber);
        }
    }

    public void setTvIdAddress(String idAddress) {
        if (this.tvIdAddress != null){
            this.tvIdAddress.setText(idAddress);
        }
    }

    public void setTvIssueBy(String issueBy) {
        if (this.tvIssueBy != null){
            this.tvIssueBy.setText(issueBy);
        }
    }

    public void setTvValidDate(String validDate) {
        if (this.tvValidDate != null){
            this.tvValidDate.setText(validDate);
        }
    }


    public void setIDCardResultDialogDismissListener(IDCardResultDialogDismissListener listener){
        this.iDCardResultDialogDismissListener = listener;
    }
}
