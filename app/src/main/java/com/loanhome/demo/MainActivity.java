package com.loanhome.demo;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.loanhome.lib.LoanHomeLib;
import com.loanhome.lib.activity.IDCardDetectActivity;
import com.loanhome.lib.bean.HttpResult;
import com.loanhome.lib.bean.TaskInfo;
import com.loanhome.lib.bean.VerifyInfo;
import com.loanhome.lib.listener.MoxieResultCallback;
import com.loanhome.lib.listener.TongDunResultCallback;
import com.loanhome.lib.listener.VerifyResultCallback;
import com.loanhome.lib.util.IDCarcdDetectUtil;
import com.loanhome.lib.util.LivenessUtil;
import com.loanhome.lib.util.MoXieUtil;
import com.loanhome.lib.util.OCRBankCardUtil;
import com.loanhome.lib.util.TongDunUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private TextView tv_content;
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
        Button button5 =findViewById(R.id.btn5);
        Button button6 =findViewById(R.id.btn6);
        tv_content =findViewById(R.id.tv_content);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        button6.setOnClickListener(this);

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
        }else if (v.getId() == R.id.btn5){
            setTongDunSDK();
        }else if(v.getId() == R.id.btn6){
            setOCRBankCard();
//            setNotification();
//            setTopdialog();
        }
    }

    /**
     * 调起身份证OCR
     * @param side
     */
    private void setIDCardOCR(int side){
        IDCarcdDetectUtil idCarcdDetectUtil =new IDCarcdDetectUtil();
        VerifyInfo verifyInfo =new VerifyInfo();
        verifyInfo.setCameraType(side);
        verifyInfo.setNeedCallBackBack(true);
        verifyInfo.setNeedCallBackFront(true);
        verifyInfo.setApi_id("430");
        verifyInfo.setContentId("90");
        idCarcdDetectUtil.setInfo(verifyInfo);
        idCarcdDetectUtil.gotoIDCardDetect(MainActivity.this, new VerifyResultCallback() {
            @Override
            public void onVerifySuccess(String result) {
                Log.i(TAG, "onVerifySuccess: "+result.toString());
                tv_content.setText(result);
            }

            @Override
            public void onVerifyWaitConfirm() {

            }

            @Override
            public void onVerifyFail(String type) {
                tv_content.setText(type);
                Log.i(TAG, "onVerifyFail: ");
            }

            @Override
            public void onVerifyCancel() {
                Toast.makeText(MainActivity.this, "onVerifyCancel", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "onVerifyCancel: ");
            }

            @Override
            public void onVerifyStart() {

            }
        });
    }

    /**
     * 调起活体人脸认证
     */
    private void setLiveNess(){

        LivenessUtil livenessUtil =new LivenessUtil();

        VerifyInfo verifyInfo = new VerifyInfo();
        verifyInfo.setIdCardName("杨振东");
        verifyInfo.setIdCardNumber("440509199411291218");
        verifyInfo.setApi_id("430");
        verifyInfo.setContentId("90");
        livenessUtil.setInfo(verifyInfo);
        livenessUtil.getBizToken(this, new VerifyResultCallback() {
            @Override
            public void onVerifySuccess(String result) {
                Log.i(TAG, "onVerifySuccess: "+result);
                tv_content.setText(result);
            }

            @Override
            public void onVerifyWaitConfirm() {

            }

            @Override
            public void onVerifyFail(String type) {
                Log.i(TAG, "onVerifyFail: "+type);

            }

            @Override
            public void onVerifyCancel() {
                Toast.makeText(MainActivity.this, "onVerifyCancel", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "onVerifyCancel: ");
            }

            @Override
            public void onVerifyStart() {
                Log.i(TAG, "onVerifyStart: ");
            }
        });
    }

    /**
     * 调起魔蝎SDK-刷新账单
     */
    private void setMoxie(){

        MoXieUtil moXieUtil =new MoXieUtil();
        String task = "bank"; //声明类型

        TaskInfo taskInfo =new TaskInfo();
        taskInfo.setLoginCode("CMB");
        taskInfo.setLoginTarget("CREDITCARD");
        taskInfo.setLoginType("IDCARD");
        taskInfo.setAccount("362324199111124228");
        taskInfo.setPassword("PBqEBh9Zd/AB0rIfeUptxg==\n");

        moXieUtil.gotoMoXie(this, task, taskInfo, new MoxieResultCallback() {

            @Override
            public void onSuccess(String action) {
                tv_content.setText("Moxie成功回调:"+action);
                Log.i(TAG, "onSuccess: "+action);
            }

            @Override
            public void onFailed() {
                Toast.makeText(MainActivity.this, "Moxie onFailed", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "onFailed: ");
            }

            @Override
            public void onProgress() {
                Log.i(TAG, "onProgress: ");
            }
        });
    }

    /**
     * 调起同盾SDK
     */
    private void setTongDunSDK(){

        TongDunUtil tongDunUtil =new TongDunUtil();
        tongDunUtil.intitTongDunSDK(MainActivity.this, new TongDunResultCallback() {

            @Override
            public void onSuccess(String data) {
                Log.i(TAG, "onSuccess: "+data);
                tv_content.setText("Moxie成功回调:"+data);
            }

            @Override
            public void onFailed() {
                Toast.makeText(MainActivity.this, "TongDunSDK onFailed", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "onFailed: ");
            }

            @Override
            public void onProgress() {

            }
        });
    }

    /**
     * 调起银行卡OCR
     */
    private void setOCRBankCard(){
        OCRBankCardUtil ocrBankCardUtil = new OCRBankCardUtil();
        VerifyInfo verifyInfo = new VerifyInfo();
        verifyInfo.setIdCardName("杨振东");
        verifyInfo.setIdCardNumber("");
        ocrBankCardUtil.setInfo(verifyInfo);
        ocrBankCardUtil.gotoBankCardDetect(this, new VerifyResultCallback() {
            @Override
            public void onVerifySuccess(String result) {
                Log.i(TAG, "onSuccess: "+result);
                tv_content.setText("OCRBankCard成功回调:"+result);
            }

            @Override
            public void onVerifyWaitConfirm() {

            }

            @Override
            public void onVerifyFail(String type) {
                Toast.makeText(MainActivity.this, "OCRBankCard onFailed", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "onFailed: ");
            }

            @Override
            public void onVerifyCancel() {
                Toast.makeText(MainActivity.this, "OCRBankCard onCancel", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "onCancel: ");
            }

            @Override
            public void onVerifyStart() {

            }
        });

    }

    /**
     * 调起通知
     */
    private void setNotification(){
        Log.i(TAG, "setNotification: ");
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(this);

        //使用广播启动Activity
//        Intent intent = new Intent(mContext, GeneralReceiver.class);
//        intent.setAction(ActionNames.BASE_STARTAPP_FROM_NOTY);
//        PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // 通知渠道的id
            String id = "duodian_channel_01";
            // 用户可以看到的通知渠道的名字.
//			CharSequence name = mContext.getString(R.string.channel_name);
            CharSequence name = "消息通知";
            // 用户可以看到的通知渠道的描述
//			String description = mContext.getString(R.string.channel_description);
            String description = "暂无";
            int importance = NotificationManager.IMPORTANCE_MAX;
            //注意Name和description不能为null或者""
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            // 配置通知渠道的属性
            mChannel.setDescription(description);
            // 设置通知出现时的闪灯（如果 android 设备支持的话）
            mChannel.enableLights(false);
            // 设置通知出现时的震动（如果 android 设备支持的话）
            mChannel.enableVibration(false);
            // 显示通知圆点
            mChannel.setShowBadge(false);
            //最后在notificationmanager中创建该通知渠道
            manager.createNotificationChannel(mChannel);
            builder.setChannelId(id);
        }
        //5.0以上必需要用此方法建造通知栏，图标才会正常显示
//        Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(R.mipmap.ic_launcher_round)).getBitmap();
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.icon_idverifie_tips);
        if(Build.VERSION.SDK_INT>= 16) {//	4.1


//            builder.setContentTitle("哈哈哈")
//                    .setContentText("内容")
//                    .setSmallIcon(R.drawable.icon_idverifie_tips)
//                    .setLargeIcon(bitmap)
////                .setContentIntent(pi)
//                    .build();
//            manager.notify(1, builder.build());

            builder.setContentTitle("哈哈哈")
                    .setContentText("内容")
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setSmallIcon(R.drawable.icon_idverifie_tips)
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                            R.drawable.icon_idverifie_tips))
                    .build();
            manager.notify(1, builder.build());
        }
    }
}
