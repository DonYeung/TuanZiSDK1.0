package com.loanhome.demo;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.loanhome.lib.LoanHomeLib;
import com.loanhome.lib.activity.IDCardDetectActivity;
import com.loanhome.lib.listener.VerifyResultCallback;
import com.loanhome.lib.util.IDCarcdDetectUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String pheadjson = getJson(MainActivity.this, "provsData.json");//获取assets目录下的json文件数据


        Log.i(TAG, "onCreate: "+pheadjson.toString());
        LoanHomeLib loanHomeLib = new LoanHomeLib.LoanHomeBuilder(this)
                .setpheadjson(pheadjson)
                .setappKey("3BBmJIV1n+NwgKSl0THpQw==")
                .setmoxieKey("4ee0820a005441f583325be04cc270ab")
                .setuuid("020000000000")
                .setisTestVersion(true)
                .setisDebug(true)
                .setenableLog(true)
                .build();

        Button button1 =findViewById(R.id.btn1);
        Button button2 =findViewById(R.id.btn2);
        Button button3 =findViewById(R.id.btn3);
        Button button4 =findViewById(R.id.btn4);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);

    }


    public String getJson(Context context, String fileName) {

        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn1){
            setIDCardOCR(0);
        }
        else if (v.getId() == R.id.btn2){
            setIDCardOCR(1);
        }
        else if (v.getId() == R.id.btn3){
            setLiveNess();
        }else if (v.getId() == R.id.btn4){
            setMoxie();
        }
    }


    private void setIDCardOCR(int side){
        IDCarcdDetectUtil idCarcdDetectUtil =new IDCarcdDetectUtil();
        idCarcdDetectUtil.setSide(side);
        idCarcdDetectUtil.gotoIDCardDetect(MainActivity.this, new VerifyResultCallback() {
            @Override
            public void onVerifySuccess(String result) {
                Log.i(TAG, "onVerifySuccess: "+result.toString());
            }

            @Override
            public void onVerifyWaitConfirm() {

            }

            @Override
            public void onVerifyFail(String type) {
                Log.i(TAG, "onVerifyFail: ");
            }

            @Override
            public void onVerifyCancel() {
                Log.i(TAG, "onVerifyCancel: ");
            }

            @Override
            public void onVerifyStart() {

            }
        });
    }

//    private void setBankCardOCR(){ }

    private void setLiveNess(){

    }
    private void setMoxie(){

    }
}
