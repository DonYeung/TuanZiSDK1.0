package com.loanhome.lib.activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.loanhome.lib.R;
import com.loanhome.lib.bean.IDCardFailInfo;
import com.loanhome.lib.bean.IDCardInfo;
import com.loanhome.lib.bean.IDCardResult;
import com.loanhome.lib.http.RetrofitUtils4test;
import com.loanhome.lib.http.StatisticsController;
import com.loanhome.lib.listener.ExitConfirmDialogDismissListener;
import com.loanhome.lib.listener.IDCardResultDialogDismissListener;
import com.loanhome.lib.listener.IDCardVerifyFailDialogDismissListener;
import com.loanhome.lib.listener.LivenessDialogDismissListener;
import com.loanhome.lib.listener.VerifyResultCallback;
import com.loanhome.lib.statistics.IStatisticsConsts;
import com.loanhome.lib.util.CameraHandlerThread;
import com.loanhome.lib.util.ICamera;
import com.loanhome.lib.util.Machine;
import com.loanhome.lib.util.RotaterUtil;
import com.loanhome.lib.util.Util;
import com.loanhome.lib.view.ConfirmDialog;
import com.loanhome.lib.view.Dialog_FilpTip;
import com.loanhome.lib.view.ExitConfirmDialog;
import com.loanhome.lib.view.IDCardGuideH;
import com.loanhome.lib.view.IDCardVerifyFailDialog;
import com.loanhome.lib.view.LivenessResultDialog;
import com.megvii.idcardquality.IDCardQualityAssessment;
import com.megvii.idcardquality.IDCardQualityResult;
import com.megvii.idcardquality.bean.IDCardAttr;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static android.os.Build.VERSION_CODES.M;


public class IDCardDetectActivity extends Activity implements TextureView.SurfaceTextureListener, Camera.PreviewCallback, View.OnClickListener {
    private static final String TAG = "IDCardDetectActivity";
    private TextureView mTextureView;
    private RelativeLayout mTitleBarH;
    private TextView mTvMegviiTipsH;
    private RelativeLayout mRelativeLayout;
    private IDCardGuideH mIdcardGuideH;
    private int mCardType = 0;
    private IDCardAttr.IDCardSide mSide;
    private ICamera mICamera;
    private BlockingQueue<byte[]> mFrameDataQueue;
    private boolean mHasSurface = false;
    private DecodeThread mDecoder = null;
    private IDCardQualityAssessment mIdCardQualityAssessment = null;
    private Rect mRoi;
    //    private ImageView mImageView;
    private boolean isCanDetected = false;//是否可以检测，5s超时后停止检测 TODO
    private boolean isDetectFinished = false;

    private IDCardQualityResult mQualityResult;

    private ImageView mIbAnimalBreathView;
    private ImageView mIbAnimalOneView;
    private ImageView mIvPeopleIconlightView;
    private ImageView mIvChinaIconLightView;
    private ImageView iv_close;
    private RectF mRectScreen;
    private TextView mTvTipText;
    private ObjectAnimator mIDcardAlphaAnimation;
    private ObjectAnimator mPeopleIconAlphaAnimation;
    private ObjectAnimator mChinaIconAlphaAnimation;


//    private TextView mToastTitle;
//    private ImageView mToastTip;
//    private RelativeLayout mLayoutToast;

    private int FIRST_RECT = 1;
    private int SENCOND_RECT = 2;
    private int rectType = FIRST_RECT;//第几面

    private byte[] mImgData = null;
    private int mImageWidth;
    private int mImageHeight;
    private long mBeginTime;

    private boolean mIsVertical = false;
    private Vibrator vibrator;
    //点击关闭时，是否需要返回数据
    private boolean isNeedCallBackFront = false;
    private boolean isNeedCallBackBack = false;
    //埋点相关
    //埋点时记录正反面信息
    private String index;
    private String functionId;
    private String contentId;
    private String api_id;
    private String pPosition;
    private String param1;
    private String param2;

    //将数据返回
//    private IDCardVerifyEvent event = new IDCardVerifyEvent();
    //是否已完成正面
    private boolean isFrontComplete;
    //是否已完成反面
    private boolean isBackComplete;
    //是否在识别中
    private boolean isIdentifying = false;

    //正面开始预处理标志
    private boolean mIsFrontPre = false;
    //反面开始预处理标志
    private boolean mIsBackPre = false;
    //正面预处理成功标志
    private boolean mIsFrontShotDone = false;
    //反面预处理成功标志
    private boolean mIsBackShotDone = false;


    private IDCardInfo idCardInfo = new IDCardInfo();
    private static VerifyResultCallback mVerifyResultCallback;

    private ThreadPoolExecutor mThreadPool;
    private static final int KEEP_ALIVE_TIME = 10;
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    private BlockingQueue<Runnable> workQueue;

    //HandlerThread
    private CameraHandlerThread cameraHandlerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect);
        initView();

        int corePoolSize = Runtime.getRuntime().availableProcessors();
        int maximumPoolSize = corePoolSize * 2;
        workQueue = new LinkedBlockingQueue<>();
        mThreadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, KEEP_ALIVE_TIME, TIME_UNIT, workQueue);



    }

    private void initView() {
        mSide = getIntent().getIntExtra("side", 0) == 0 ? IDCardAttr.IDCardSide.IDCARD_SIDE_FRONT
                : IDCardAttr.IDCardSide.IDCARD_SIDE_BACK;
        mCardType = mSide == IDCardAttr.IDCardSide.IDCARD_SIDE_FRONT ? 1 : 2;//1代表人像面，2代表国徽面检测
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        index = mSide == IDCardAttr.IDCardSide.IDCARD_SIDE_FRONT ? "front" : "back";
        mIsVertical = getIntent().getBooleanExtra("isvertical", false);
        isNeedCallBackFront = getIntent().getBooleanExtra("isNeedCallBackFront", true);
        isNeedCallBackBack = getIntent().getBooleanExtra("isNeedCallBackBack", true);
        functionId = getIntent().getStringExtra("functionId");
        contentId = getIntent().getStringExtra("contentId");
        api_id = getIntent().getStringExtra("api_id");
        pPosition = getIntent().getStringExtra("pPosition");
        param1 = getIntent().getStringExtra("param1");
        param2 = getIntent().getStringExtra("param2");

        if (isNeedCallBackFront) {
            isFrontComplete = false;
        } else {
            isFrontComplete = true;
        }
        if (isNeedCallBackBack) {
            isBackComplete = false;
        } else {
            isBackComplete = true;
        }
        mICamera = new ICamera(mIsVertical);


        mRelativeLayout = (RelativeLayout) findViewById(R.id.rl_idcard_cn_root_view);
        mTextureView = (TextureView) findViewById(R.id.idcardscan_cn_layout_surface);
        mTitleBarH = (RelativeLayout) findViewById(R.id.rl_megvii_idcard_cn_title_bar_h);
        mTvMegviiTipsH = (TextView) findViewById(R.id.tv_megvii_idcard_cn_tips_h);

        mIvPeopleIconlightView = (ImageView) findViewById(R.id.iv_people_light_icon);
        mIvChinaIconLightView = (ImageView) findViewById(R.id.iv_china_light_icon);

        mIbAnimalBreathView = (ImageView) findViewById(R.id.ib_animal_breath_view);
        mIbAnimalOneView = (ImageView) findViewById(R.id.ib_animal_one_view);

//        mToastTitle = (TextView) findViewById(R.id.toast_tv);
//        mToastTip = (ImageView) findViewById(R.id.iv_auth_toast_tip);
//        mLayoutToast = (RelativeLayout) findViewById(R.id.layout_toast);

        mTvTipText = (TextView) findViewById(R.id.tv_megvii_idcard_cn_h_tips);
        mIdcardGuideH = (IDCardGuideH) findViewById(R.id.idcardscan_cn_layout_guide_h);
        iv_close = (ImageView) findViewById(R.id.iv_close);

        iv_close.setOnClickListener(this);
        mIdcardGuideH.setOnClickListener(this);
        mTextureView.setOnClickListener(this);
        mRelativeLayout.setOnClickListener(this);
        mTextureView.setSurfaceTextureListener(this);

        mIbAnimalBreathView.setBackgroundResource(R.drawable.bg_sfz_light);
        mIbAnimalOneView.setVisibility(View.VISIBLE);
//        mTvTipText.setText("请保证身份证边缘与线框对齐");

        if (mCardType == 1) {
            mSide = IDCardAttr.IDCardSide.IDCARD_SIDE_FRONT;
            mIbAnimalOneView.setBackgroundResource(R.drawable.sfz_front);
            mTvMegviiTipsH.setText("人像面拍摄");
        } else {
            mSide = IDCardAttr.IDCardSide.IDCARD_SIDE_BACK;
            mIbAnimalOneView.setBackgroundResource(R.drawable.sfz_back);
            mTvMegviiTipsH.setText("国徽面拍摄");
        }


        //横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mRelativeLayout.setBackgroundColor(Color.BLACK);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mIdcardGuideH.setVisibility(View.VISIBLE);
        mTitleBarH.setVisibility(View.VISIBLE);
        mIdcardGuideH.setCardSide(mSide);

        if (Build.VERSION.SDK_INT >= M) {
            if (checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "没有摄像机权限", Toast.LENGTH_SHORT).show();
                doFinish();
            }
        }

        initSdk();
    }

    private void initSdk() {
        mICamera = new ICamera(mIsVertical);

        mFrameDataQueue = new LinkedBlockingDeque<byte[]>(1);

        //首先判断SDK版本是否大于等于5.0,实际部分厂商实现了Camera2.0的不同程度，
        // 其支持程度为FULL （1）> LIMITED（0） > LEGACY（2）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            try {
                String[] cameraIds = manager.getCameraIdList();
                if (cameraIds != null && cameraIds.length > 0) {
                    //后置摄像头存在
                    if (cameraIds[0] != null) {
                        CameraCharacteristics characteristics
                                = manager.getCameraCharacteristics(cameraIds[0]);
                        int level = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
                        Log.i(TAG, "initSdk: Camera2.0 api支持程度为"+ level);
                        if (level != CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL){
                            //Camera2.0 支持程度在 LEGACY / LIMITED 设置初始化clear值为0.4
                            //初始化
                            mIdCardQualityAssessment = new IDCardQualityAssessment.Builder()
                                    .setClear((float)0.4)
                                    .setIsIgnoreShadow(false)// 不忽略阴影
                                    .setIsIgnoreHighlight(false)//不忽略光斑
                                    .build();
                        }else{
                            //Camera2.0 支持程度在 FULL 设置初始化clear值为0.5
                            //初始化
                            mIdCardQualityAssessment = new IDCardQualityAssessment.Builder()
                                    .setClear((float)0.5)
                                    .setIsIgnoreShadow(false)// 不忽略阴影
                                    .setIsIgnoreHighlight(false)//不忽略光斑
                                    .build();
                        }
                    }
                }
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }

        }else {
            //SDK版本小于5.0，不支持Camera2.0 直接设置初始化clear值为0.4
            //初始化
            mIdCardQualityAssessment = new IDCardQualityAssessment.Builder()
                    .setClear((float) 0.4)
                    .setIsIgnoreShadow(false)// 不忽略阴影
                    .setIsIgnoreHighlight(false)//不忽略光斑
                    .build();
        }


        //必须先做授权判断

        //加载模型，加载该方法之前，一定要先调用授权！！否则会crash！！！注意
        boolean initSuccess = mIdCardQualityAssessment.init(this, Util.readModel_New(this));
        if (!initSuccess) {
            Toast.makeText(this, "检测器初始化失败", Toast.LENGTH_SHORT).show();
            doFinish();
        }

    }


    private void doPreview() {
        if (!mHasSurface) {
            return;
        } else {
            mICamera.startPreview(mTextureView.getSurfaceTexture());
        }
    }

    private void callBackData() {
        if (mVerifyResultCallback == null) {
            return;
        }
        if (isNeedCallBackFront && isNeedCallBackBack && isFrontComplete && isBackComplete) {
            idCardInfo.setSide(2);
            String jsoncallback = "{\"side\": \" " + idCardInfo.getSide() + "\",\"idCardName\":\""+idCardInfo.getIdCardName() + "\",\"idCardNumber\":\""+idCardInfo.getIdCardNumber() +"\",\"address\":\""+idCardInfo.getAddress() + "\",\"issueBy\":\""+idCardInfo.getIssuedBy() + "\",\"validDate\":\""+idCardInfo.getValidDate() +"\"}";
            mVerifyResultCallback.onVerifySuccess(jsoncallback);

        } else if (isNeedCallBackFront && isFrontComplete) {
            idCardInfo.setSide(0);
            String jsoncallback = "{\"side\": \" " + idCardInfo.getSide() + "\",\"idCardName\":\""+idCardInfo.getIdCardName() + "\",\"idCardNumber\":\""+idCardInfo.getIdCardNumber() +"\",\"address\":\""+idCardInfo.getAddress()+"\"}";
            Log.i(TAG, "onDismiss: "+jsoncallback);
            mVerifyResultCallback.onVerifySuccess(jsoncallback);

        } else if (isNeedCallBackBack && isBackComplete) {
            idCardInfo.setSide(1);
            String jsoncallback = "{\"side\": \" " + idCardInfo.getSide() + "\",\"issueBy\":\""+idCardInfo.getIssuedBy() + "\",\"validDate\":\""+idCardInfo.getValidDate() +"\"}";
            Log.i(TAG, "onDismiss: "+jsoncallback);
            mVerifyResultCallback.onVerifySuccess(jsoncallback);
        } else {
            //取消身份证认证
            idCardInfo.setSide(3);
            mVerifyResultCallback.onVerifyCancel();
        }
        finish();
    }

    /**
     * 加载完毕后取得身份证的坐标，计算动画的位置
     */
    private void initIdCardRect() {
        int imageWidth = mICamera.cameraWidth;
        int imageHeight = mICamera.cameraHeight;
        if (mIsVertical) {
            imageWidth = mICamera.cameraHeight;
            imageHeight = mICamera.cameraWidth;
        }
        RectF rectF;

        rectF = mIdcardGuideH.getPosition(rectType);

        mRoi = new Rect();
        mRoi.left = (int) (rectF.left * imageWidth);
        mRoi.top = (int) (rectF.top * imageHeight);
        mRoi.right = (int) (rectF.right * imageWidth);
        mRoi.bottom = (int) (rectF.bottom * imageHeight);

        if (!isEven01(mRoi.left))
            mRoi.left = mRoi.left + 1;
        if (!isEven01(mRoi.top))
            mRoi.top = mRoi.top + 1;
        if (!isEven01(mRoi.right))
            mRoi.right = mRoi.right - 1;
        if (!isEven01(mRoi.bottom))
            mRoi.bottom = mRoi.bottom - 1;

        mRectScreen = mIdcardGuideH.getScreenPosition(rectType);


        int width = (int) (mRectScreen.right - mRectScreen.left);
        int height = (int) (mRectScreen.bottom - mRectScreen.top);
        Log.i(TAG, "initIdCardRect width: "+width);
        Log.i(TAG, "initIdCardRect height: "+height);


        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(mIbAnimalOneView.getLayoutParams());
        int offset = (int) getResources().getDimension(R.dimen.dp_16);

        layoutParams.width = width + offset;
        layoutParams.height = height + offset;
        layoutParams.topMargin = (int) mRectScreen.top - offset / 2;

        Log.i(TAG, "layoutParams width: "+layoutParams.width);
        Log.i(TAG, "layoutParams height: "+layoutParams.height);
        Log.i(TAG, "layoutParams topMargin: "+layoutParams.topMargin);


        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mIbAnimalBreathView.setLayoutParams(layoutParams);
        mIbAnimalOneView.setLayoutParams(layoutParams);

        mIvChinaIconLightView.setLayoutParams(layoutParams);
        mIvPeopleIconlightView.setLayoutParams(layoutParams);

        //--tip textview
        RelativeLayout.LayoutParams textlayoutParams = new RelativeLayout.LayoutParams(mTvTipText.getLayoutParams());
        textlayoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
        textlayoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        textlayoutParams.topMargin = (int) mRectScreen.bottom + offset;
        textlayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mTvTipText.setLayoutParams(textlayoutParams);

//        toast相对居中，减去框的高度/2， 再减去自身的高度/2
//        RelativeLayout.LayoutParams myToastParams = new RelativeLayout.LayoutParams(mLayoutToast.getLayoutParams());
//        myToastParams.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
//        myToastParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
//        myToastParams.topMargin = layoutParams.topMargin + layoutParams.height / 2 - (int) getResources().getDimension(R.dimen.dimen_10dp);
//        myToastParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
//        mLayoutToast.setLayoutParams(myToastParams);


    }

    public synchronized void ThreadPoolPost(final byte[] frameData) {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                // 编码
                Log.i(TAG, Thread.currentThread().getName()+ "_onPreviewFrame");
                mFrameDataQueue.offer(frameData);
            }
        });
    }

    @Override
    public void onPreviewFrame(final byte[] data, Camera camera) {
        if (isCanDetected) {
            ThreadPoolPost(data);
//            mFrameDataQueue.offer(data);
//            Log.i(TAG, Thread.currentThread().getName()+ "_onPreviewFrame");
            Log.i(TAG,"after setting, previewformate is " + camera.getParameters().getPreviewFormat());
            Log.i(TAG,"initCamera  after setting, previewframetate is " + camera.getParameters().getPreviewFrameRate());
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
//        Camera camera =mICamera.openCamera(this);

        cameraHandlerThread = new CameraHandlerThread("CameraMythread",this);
        synchronized (cameraHandlerThread) {
            mICamera = cameraHandlerThread.openCamera();
        }


        if (mICamera != null) {
            initIdcardGuide();

            mHasSurface = true;

            doPreview();
            mICamera.actionDetect(this);

            lastFaileType = IDCardQualityResult.IDCardResultType.IDCARD_QUALITY_FAILED_UNKNOWN;
            mBeginTime = System.currentTimeMillis();
            isCanDetected = true;


            mTextureView.post(new Runnable() {
                @Override
                public void run() {
                    initIdCardRect();
                }
            });

            if (mDecoder == null) {
                mDecoder = new DecodeThread();
            }
            if (mDecoder != null && !mDecoder.isAlive()) {
                long starttime  =System.currentTimeMillis();
                Log.i(TAG, "开始预处理："+starttime);
                mDecoder.start();
            }

            startBreatheAlphaAnimation(mIbAnimalBreathView);
            startBreatheAlphaAnimation(mTvTipText);

            //新OCR统计-调用相机
            if (mSide == IDCardAttr.IDCardSide.IDCARD_SIDE_FRONT) {
                StatisticsController.getInstance().newOCRRequestStatics(IStatisticsConsts.UmengEventId.Page.PAGE_OCR_SHOT,
                        IStatisticsConsts.UmengEventId.LogType.LOG_TYPE_VIEW,
                        IStatisticsConsts.UmengEventId.CkModule.CK_MODULE_OCR_ASKCAMERA,index
                        , functionId, contentId,api_id, pPosition, "0", param2);
            } else {
                StatisticsController.getInstance().newOCRRequestStatics(IStatisticsConsts.UmengEventId.Page.PAGE_OCR_SHOT,
                        IStatisticsConsts.UmengEventId.LogType.LOG_TYPE_VIEW,
                        IStatisticsConsts.UmengEventId.CkModule.CK_MODULE_OCR_ASKCAMERA,
                        index, functionId, contentId,api_id, pPosition, "1", param2);
            }

        } else {
            Toast.makeText(this, "打开摄像头失败", Toast.LENGTH_SHORT).show();
            doFinish();
            return;
        }
    }

    private void initIdcardGuide() {
        RelativeLayout.LayoutParams layout_params = mICamera.getLayoutParam(this);

        layout_params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mIdcardGuideH.setLayoutParams(layout_params);

        mTextureView.setLayoutParams(layout_params);

        layout_params.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        mTitleBarH.setLayoutParams(layout_params);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isDetectFinished) {
            doFinish();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        mICamera.closeCamera();
        mHasSurface = false;
        if (cameraHandlerThread!=null){
            cameraHandlerThread.quit();
        }
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }

    private class DecodeThread extends Thread {
        boolean mHasFinished = false;

        public void setmHasSuccess(boolean mHasFinished) {
            this.mHasFinished = mHasFinished;
        }

        @Override
        public void run() {

            //开始预处理埋点
            if (!mIsFrontPre && mSide == IDCardAttr.IDCardSide.IDCARD_SIDE_FRONT){
                //新OCR统计-开始抓拍/正面
                StatisticsController.getInstance().newOCRRequestStatics(IStatisticsConsts.UmengEventId.Page.PAGE_OCR_SHOT,
                        IStatisticsConsts.UmengEventId.LogType.LOG_TYPE_VIEW,
                        IStatisticsConsts.UmengEventId.CkModule.CK_MODULE_OCR_SHOT,index
                        , functionId, contentId,api_id, pPosition, "0", param2);

                mIsFrontPre = true;

            } else if (!mIsBackPre && mSide == IDCardAttr.IDCardSide.IDCARD_SIDE_BACK){

                //新OCR统计-开始抓拍/反面
                StatisticsController.getInstance().newOCRRequestStatics(IStatisticsConsts.UmengEventId.Page.PAGE_OCR_SHOT,
                        IStatisticsConsts.UmengEventId.LogType.LOG_TYPE_VIEW,
                        IStatisticsConsts.UmengEventId.CkModule.CK_MODULE_OCR_SHOT,
                        index, functionId, contentId,api_id, pPosition, "1", param2);
                mIsBackPre = true;
            }

            try {
                byte[] image = null;
                while ((image = mFrameDataQueue.take()) != null) {
                    if (mHasFinished) {//两面全部检测通过
                        return;
                    }
                    if (!isCanDetected) {
                        continue;
                    }
                    mImageWidth = mICamera.cameraWidth;
                    mImageHeight = mICamera.cameraHeight;

                    mImgData = RotaterUtil.rotate(image, mImageWidth, mImageHeight,
                            mICamera.getCameraAngle(IDCardDetectActivity.this));
                    if (mIsVertical) {
                        mImageWidth = mICamera.cameraHeight;
                        mImageHeight = mICamera.cameraWidth;
                    }
                    mQualityResult = mIdCardQualityAssessment.getQuality(mImgData, mImageWidth,
                            mImageHeight, mSide, mRoi);

                    final boolean isSuccess = mQualityResult.isValid();

                    mDecoder.setmHasSuccess(isSuccess);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isSuccess) {
                                long midtime  =System.currentTimeMillis();
                                Log.i(TAG, "识别为关键帧："+midtime);
                                Log.i(TAG, "run: 成功");
                                vibrator.vibrate(new long[]{0, 50, 50, 100, 50}, -1);           //重复两次上面的pattern 如果只想震动一次，index设为-1

                                handleSuccessResult();
                                String debugResult = "clear: " + new BigDecimal(mQualityResult.attr.lowQuality).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue() + "\n"
                                        + "in_bound: " + new BigDecimal(mQualityResult.attr.inBound).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue() + "\n"
                                        + "is_idcard: " + new BigDecimal(mQualityResult.attr.isIdcard).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue() + "\n"
                                        + "flare: " + mQualityResult.attr.specularHightlightCount + "\n"
                                        + "shadow: " + mQualityResult.attr.shadowCount + "\n";
                                Log.i("Don", "run: success result---" + debugResult);
                            } else {
//                                Log.i(TAG, "run: 失败");
                                IDCardQualityResult.IDCardResultType resultType = mQualityResult.idCardResultType;

                                //以下几种情况隐藏身份证人像面或者国徽面图标icon
                                if (resultType == IDCardQualityResult.IDCardResultType.IDCARD_QUALITY_FAILED_NONE
                                        || resultType == IDCardQualityResult.IDCardResultType.IDCARD_QUALITY_FAILED_NOTINBOUND
                                        || resultType == IDCardQualityResult.IDCardResultType.IDCARD_QUALITY_FAILED_NOTCLEAR
                                        || resultType == IDCardQualityResult.IDCardResultType.IDCARD_QUALITY_FAILED_HAVEHIGHLIGHT
                                        || resultType == IDCardQualityResult.IDCardResultType.IDCARD_QUALITY_FAILED_HAVESHADOW
                                        || resultType == IDCardQualityResult.IDCardResultType.IDCARD_QUALITY_FAILED_CONVERT) {
                                    dismissIcon();
                                } else {
                                    showIcon();
                                }
                                showFaileToast(resultType);

                                String debugResult = "clear: " + new BigDecimal(mQualityResult.attr.lowQuality).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue() + "\n"
                                        + "in_bound: " + new BigDecimal(mQualityResult.attr.inBound).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue() + "\n"
                                        + "is_idcard: " + new BigDecimal(mQualityResult.attr.isIdcard).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue() + "\n"
                                        + "flare: " + mQualityResult.attr.specularHightlightCount + "\n"
                                        + "shadow: " + mQualityResult.attr.shadowCount + "\n";
                                Log.i("Don", "run: failed result---" + debugResult);
                            }
                        }
                    });
                }
            } catch (
                    Throwable e) {
                e.printStackTrace();
            }
        }

    }

    private Bitmap iDCardImg = null;
    private Bitmap portraitImg = null;
    private void handleSuccessResult() {
        cancelMyToast();
        setBlueLine();
        Log.i(TAG, "handleSuccessResult: 成功");
        mIbAnimalBreathView.setVisibility(View.GONE);
        iDCardImg = mQualityResult.croppedImageOfIDCard();

        long endtime =System.currentTimeMillis();
        Log.i(TAG, "handleSuccessResult: 成功裁剪出图片："+endtime);
        if (mQualityResult.attr.side == IDCardAttr.IDCardSide.IDCARD_SIDE_FRONT) {
            portraitImg = mQualityResult.croppedImageOfPortrait();
        }

        gotoVerify(mSide == IDCardAttr.IDCardSide.IDCARD_SIDE_FRONT ? 0 : 1,
                Util.bmp2byteArr(iDCardImg),Util.bmp2byteArr(portraitImg));
        if (mSide == IDCardAttr.IDCardSide.IDCARD_SIDE_FRONT) {
            isFrontComplete = true;
            //假如反面已经完成，则回调信息给页端
            if (isBackComplete){
                // 下一模块
                callBackData();
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                         //新OCR统计- 翻转提示弹窗- 正面
                        StatisticsController.getInstance().newOCRRequestStatics(IStatisticsConsts.UmengEventId.Page.PAGE_OCR_FILP,
                                IStatisticsConsts.UmengEventId.LogType.LOG_TYPE_VIEW,
                                IStatisticsConsts.UmengEventId.CkModule.CK_MODULE_OCR_FILP,index
                                , functionId, contentId,api_id, pPosition, "0", param2);

                        Dialog_FilpTip dialogFilpTip = new Dialog_FilpTip();
                        dialogFilpTip.show(getFragmentManager(), "");
                        dialogFilpTip.setSide("0");
                        dialogFilpTip.setDialogDismissListener(new Dialog_FilpTip.DialogDismissListener() {
                            @Override
                            public void onDismiss(boolean isTryAgain) {
                                switchSide(IDCardAttr.IDCardSide.IDCARD_SIDE_BACK);

                                //新OCR统计- 点击翻转提示弹窗- 正面
                                StatisticsController.getInstance().newOCRRequestStatics(IStatisticsConsts.UmengEventId.Page.PAGE_OCR_FILP,
                                        IStatisticsConsts.UmengEventId.LogType.LOG_TYPE_CLICK,
                                        IStatisticsConsts.UmengEventId.CkModule.CK_MODULE_OCR_FILP_OK,index
                                        , functionId, contentId,api_id, pPosition, "0", param2);
                            }
                        });
                    }
                });

                //假如反面没有完成，则开始反面ocr
                switchSide(IDCardAttr.IDCardSide.IDCARD_SIDE_BACK);

            }
        }else{
            isBackComplete = true;
            //假如正面已完成，则回调信息给页端
            if (isFrontComplete){
                //下一模块
                callBackData();
            } else {
                //假如正面没有完成，则开始正面的ocr
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //新OCR统计- 翻转提示弹窗- 反面
                        StatisticsController.getInstance().newOCRRequestStatics(IStatisticsConsts.UmengEventId.Page.PAGE_OCR_FILP,
                                IStatisticsConsts.UmengEventId.LogType.LOG_TYPE_VIEW,
                                IStatisticsConsts.UmengEventId.CkModule.CK_MODULE_OCR_FILP,index
                                , functionId, contentId,api_id, pPosition, "1", param2);

                        Dialog_FilpTip dialogFilpTip = new Dialog_FilpTip();
                        dialogFilpTip.show(getFragmentManager(), "");
                        dialogFilpTip.setSide("1");
                        dialogFilpTip.setDialogDismissListener(new Dialog_FilpTip.DialogDismissListener() {
                            @Override
                            public void onDismiss(boolean isTryAgain) {
                                switchSide(IDCardAttr.IDCardSide.IDCARD_SIDE_BACK);

                                //新OCR统计- 点击翻转提示弹窗 - 反面
                                StatisticsController.getInstance().newOCRRequestStatics(IStatisticsConsts.UmengEventId.Page.PAGE_OCR_FILP,
                                        IStatisticsConsts.UmengEventId.LogType.LOG_TYPE_CLICK,
                                        IStatisticsConsts.UmengEventId.CkModule.CK_MODULE_OCR_FILP_OK,index
                                        , functionId, contentId,api_id, pPosition, "1", param2);
                            }
                        });
                    }
                });

            }
        }

    }

    private long toastTime = 0;
    private IDCardQualityResult.IDCardResultType lastFaileType;
    private long lastTime = 0;
    private long firstTime = 0;
    private boolean isFirstToast = true;
    private boolean canShowAnimal = true;
    private boolean isTheSameError = true;

    private void showFaileToast(IDCardQualityResult.IDCardResultType faileType) {

        try {
            //检测5s后开始弹toast
            if (System.currentTimeMillis() - mBeginTime <= 5000) {
                return;
            }

            if (System.currentTimeMillis() - toastTime < 500) {
                return;
            }

            //计算同一个错误出现的时间
            if (faileType == lastFaileType) {
                isTheSameError = true;
                if (isFirstToast) {
                    firstTime = System.currentTimeMillis();
                    lastTime = firstTime;
                    isFirstToast = false;
                }
                lastTime += (System.currentTimeMillis() - lastTime);
            } else {
                isTheSameError = false;
                firstTime = System.currentTimeMillis();
                lastTime = firstTime;

                //两个toast，间隔500毫秒
                if (isMyToastShown()) {
                    lastFaileType = faileType;
                    cancelMyToast();
                    toastTime = System.currentTimeMillis();
                    return;
                }
            }

            long continueTime = lastTime - firstTime;
            //超过2秒后，弹出动画
            if (continueTime > 2000) {
                if (canShowAnimal) {//闪烁动画只出现一次
                    canShowAnimal = false;
                    startToastTipAnimal();
                    if (faileType == IDCardQualityResult.IDCardResultType.IDCARD_QUALITY_FAILED_NEEDFRONT) {//需要人像面
                        startPeopleIconAlphaAnimation();
                    } else if (faileType == IDCardQualityResult.IDCardResultType.IDCARD_QUALITY_FAILED_NEEDBACK) {
                        startChinaIconAlphaAnimation();
                    }
                    autoFocus();
                }
            } else {
                canShowAnimal = true;
//                cancelToastTipAnimal();
                cancelPeopleIconAlphaAnimation();
                cancelChinaIconAlphaAnimation();
            }


            lastFaileType = faileType;
            String toastStr = "";
            if (faileType == IDCardQualityResult.IDCardResultType.IDCARD_QUALITY_FAILED_NOTIDCARD) {
                //没有检测到身份证
                toastStr = getResources().getString(R.string.remind_idcard_quality_failed_1);
            } else if (faileType == IDCardQualityResult.IDCardResultType.IDCARD_QUALITY_FAILED_NOTINBOUND) {
                //身份证不在引导框内
                toastStr = getResources().getString(R.string.remind_idcard_quality_failed_2);
            } else if (faileType == IDCardQualityResult.IDCardResultType.IDCARD_QUALITY_FAILED_NOTCLEAR) {
                //身份证清晰度太低
                toastStr = getResources().getString(R.string.remind_idcard_quality_failed_3);
            } else if (faileType == IDCardQualityResult.IDCardResultType.IDCARD_QUALITY_FAILED_HAVEHIGHLIGHT) {
                //存在光斑
                toastStr = getResources().getString(R.string.remind_idcard_quality_failed_4);
            } else if (faileType == IDCardQualityResult.IDCardResultType.IDCARD_QUALITY_FAILED_HAVESHADOW) {
                //存在阴影
                toastStr = getResources().getString(R.string.remind_idcard_quality_failed_5);
            } else if (faileType == IDCardQualityResult.IDCardResultType.IDCARD_QUALITY_FAILED_NEEDFRONT) {
                //请翻到人像面进行识别
                toastStr = getResources().getString(R.string.remind_idcard_quality_failed_6);
            } else if (faileType == IDCardQualityResult.IDCardResultType.IDCARD_QUALITY_FAILED_NEEDBACK) {
                //请翻到国徽面进行识别
                toastStr = getResources().getString(R.string.remind_idcard_quality_failed_7);
            } else if (faileType == IDCardQualityResult.IDCardResultType.IDCARD_QUALITY_FAILED_NEEDBACK) {
                //需要检测身份证国徽面
                toastStr = getResources().getString(R.string.remind_idcard_quality_failed_7);
            } else if (faileType == IDCardQualityResult.IDCardResultType.IDCARD_QUALITY_FAILED_CONVERT) {
                //请摆正身份证进行识别
                toastStr = getResources().getString(R.string.remind_idcard_quality_failed_8);
            }else if(faileType == IDCardQualityResult.IDCardResultType.IDCARD_QUALITY_FAILED_ERRORARGUMENT){
                //识别出了些小问题
                toastStr = getResources().getString(R.string.remind_idcard_quality_failed_9);
            }else if(faileType == IDCardQualityResult.IDCardResultType.IDCARD_QUALITY_FAILED_UNKNOWN){
                //识别出了些小问题
                toastStr = getResources().getString(R.string.remind_idcard_quality_failed_9);
            }

            if (!"".equals(toastStr)) {
                final String finalToastStr = toastStr;
                if (isTheSameError) {
                    showMyToast(finalToastStr, false);
                } else {
                    showMyToast(finalToastStr, true);
                }
                toastTime = System.currentTimeMillis();
            }


        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void showIcon() {
        mIbAnimalOneView.setVisibility(View.VISIBLE);
    }

    private void dismissIcon() {
        mIbAnimalOneView.setVisibility(View.GONE);
    }

    public void autoFocus() {
        try {
            if (null != mICamera) {
                mICamera.autoFocus();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    /**
     * 展示toast前面的tip动画
     */
    ObjectAnimator tipAnimation;

    public void startToastTipAnimal() {
        mTvTipText.setVisibility(View.VISIBLE);
        tipAnimation = ObjectAnimator.ofFloat(mTvTipText, "alpha", 0.3f, 0.8f, 0.3f);
        tipAnimation.setDuration(500);
        tipAnimation.setRepeatCount(300);
        tipAnimation.setInterpolator(new LinearInterpolator());
        tipAnimation.setRepeatMode(ObjectAnimator.REVERSE);
        tipAnimation.start();
    }

    /**
     * 展示自定义toast
     *
     * @param msg
     */
    private void showMyToast(final String msg, final boolean useCustomTime) {
        if (!isMyToastShown()) {
            mTvTipText.setVisibility(View.VISIBLE);
            mTvTipText.setText(msg);
            if (useCustomTime) {
                final Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                cancelMyToast();
                            }
                        });
                    }
                }, 1000);
            }
        }
    }


    /**
     * 展示自定义toast
     */
    private void cancelMyToast() {
        mTvTipText.setVisibility(View.GONE);
//        cancelToastTipAnimal();
    }

    /**
     * 自定义toast是否展示
     */
    private boolean isMyToastShown() {
        if (View.VISIBLE == mTvTipText.getVisibility()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 隐藏toast前面的tip动画
     */
    public void cancelToastTipAnimal() {
        if (tipAnimation != null) {
            tipAnimation.cancel();
            tipAnimation.end();
            tipAnimation.removeAllListeners();
            tipAnimation.removeAllUpdateListeners();
            tipAnimation = null;
        }
        mTvTipText.setVisibility(View.GONE);
    }

    /**
     * 取消人像面icon呼吸渐变动画
     */
    private void cancelPeopleIconAlphaAnimation() {
        if (null != mPeopleIconAlphaAnimation) {
            mPeopleIconAlphaAnimation.cancel();
            mPeopleIconAlphaAnimation.end();
            mPeopleIconAlphaAnimation = null;
        }
        mIvPeopleIconlightView.setVisibility(View.GONE);
    }

    /**
     * 取消人像面icon呼吸渐变动画
     */
    private void cancelChinaIconAlphaAnimation() {
        if (null != mChinaIconAlphaAnimation) {
            mChinaIconAlphaAnimation.cancel();
            mChinaIconAlphaAnimation.end();
            mChinaIconAlphaAnimation = null;
        }
        mIvChinaIconLightView.setVisibility(View.GONE);
    }

    /**
     * 国徽面icon呼吸渐变动画
     */
    private void startChinaIconAlphaAnimation() {
        mIvChinaIconLightView.setVisibility(View.VISIBLE);
        mChinaIconAlphaAnimation = ObjectAnimator.ofFloat(mIvChinaIconLightView, "alpha", 0.2f, 0.8f, 0.2f);
        mChinaIconAlphaAnimation.setDuration(500);
        mChinaIconAlphaAnimation.setRepeatCount(100);
        mChinaIconAlphaAnimation.setInterpolator(new LinearInterpolator());
        mChinaIconAlphaAnimation.setRepeatMode(ObjectAnimator.REVERSE);
        mChinaIconAlphaAnimation.start();
    }

    /**
     * 人像面icon呼吸渐变动画
     */
    private void startPeopleIconAlphaAnimation() {
        mIvPeopleIconlightView.setVisibility(View.VISIBLE);
        mPeopleIconAlphaAnimation = ObjectAnimator.ofFloat(mIvPeopleIconlightView, "alpha", 0.2f, 0.8f, 0.2f);
        mPeopleIconAlphaAnimation.setDuration(500);
        mPeopleIconAlphaAnimation.setRepeatCount(100);
        mPeopleIconAlphaAnimation.setInterpolator(new LinearInterpolator());
        mPeopleIconAlphaAnimation.setRepeatMode(ObjectAnimator.REVERSE);
        mPeopleIconAlphaAnimation.start();
    }


    private void removeAllAnimation() {
        if (mChinaIconAlphaAnimation != null) {
            mChinaIconAlphaAnimation.cancel();
            mChinaIconAlphaAnimation.end();
            mChinaIconAlphaAnimation.removeAllListeners();
            mChinaIconAlphaAnimation.removeAllUpdateListeners();
            mChinaIconAlphaAnimation = null;
        }
        if (mPeopleIconAlphaAnimation != null) {
            mPeopleIconAlphaAnimation.cancel();
            mPeopleIconAlphaAnimation.end();
            mPeopleIconAlphaAnimation.removeAllListeners();
            mPeopleIconAlphaAnimation.removeAllUpdateListeners();
            mPeopleIconAlphaAnimation = null;
        }
        if (mIDcardAlphaAnimation != null) {
            mIDcardAlphaAnimation.cancel();
            mIDcardAlphaAnimation.end();
            mIDcardAlphaAnimation.removeAllListeners();
            mIDcardAlphaAnimation.removeAllUpdateListeners();
            mIDcardAlphaAnimation = null;
        }

        cancelChinaIconAlphaAnimation();
        cancelPeopleIconAlphaAnimation();

    }

    /**
     * 画蓝色的线
     */
    private void setBlueLine() {
//        mIdcardGuideH.setDrawLine(false);
    }

    /**
     * 常驻呼吸框渐变动画
     *
     * @param view
     */
    private void startBreatheAlphaAnimation(final View view) {
        view.setVisibility(View.VISIBLE);
        mTvTipText.setVisibility(View.VISIBLE);
        if (view == mTvTipText) {
            mIDcardAlphaAnimation = ObjectAnimator.ofFloat(view, "alpha", 1f, 0.3f, 1f);
        } else {
            mIDcardAlphaAnimation = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f, 1f);
        }
        mIDcardAlphaAnimation.setDuration(1500);
        mIDcardAlphaAnimation.setRepeatCount(ObjectAnimator.INFINITE);
        mIDcardAlphaAnimation.setRepeatMode(ObjectAnimator.REVERSE);
        mIDcardAlphaAnimation.start();
    }


    // 用取余运算
    public boolean isEven01(int num) {
        if (num % 2 == 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onClick(View v) {
//        if ( v.getId() == R.id.rl_megvii_idcard_cn_goback_h) {
//            doFinish();
//        } else
        if(v.getId() == R.id.iv_close){
            onBackPressed();
        }
        if (v.getId() == R.id.idcardscan_cn_layout_guide_h
                || v.getId() == R.id.idcardscan_cn_layout_surface) {
            if (mICamera != null) {
                mICamera.autoFocus();
            }
        }
    }


    @Override
    public void onBackPressed() {
//        if (isIdentifying){
            Log.i(TAG, "onBackPressed: "+isIdentifying);
            //确认是否离开
            ExitConfirmDialog exitDialog = new ExitConfirmDialog();
            exitDialog.show(getFragmentManager(), "exit");
            exitDialog.setExitConfirmDialogDismissListener(new ExitConfirmDialogDismissListener() {
                @Override
                public void onDismiss(boolean isWait) {
                    if (!isWait){

                        //新OCR统计-点击关闭按钮
                        if (mSide == IDCardAttr.IDCardSide.IDCARD_SIDE_FRONT) {
                            StatisticsController.getInstance().newOCRRequestStatics(IStatisticsConsts.UmengEventId.Page.PAGE_OCR_SHOT,
                                    IStatisticsConsts.UmengEventId.LogType.LOG_TYPE_CLICK,
                                    IStatisticsConsts.UmengEventId.CkModule.CK_MODULE_OCR_CLOSE,index
                                    , functionId, contentId,api_id, pPosition, "0", param2);
                        } else {
                            StatisticsController.getInstance().newOCRRequestStatics(IStatisticsConsts.UmengEventId.Page.PAGE_OCR_SHOT,
                                    IStatisticsConsts.UmengEventId.LogType.LOG_TYPE_CLICK,
                                    IStatisticsConsts.UmengEventId.CkModule.CK_MODULE_OCR_CLOSE,
                                    index, functionId, contentId,api_id, pPosition, "1", param2);
                        }

                        callBackData();
                        mVerifyResultCallback.onVerifyCancel();
                        doFinish();

                    }
                }
            });
//        } else {
//            Log.i(TAG, "onBackPressed2: "+isIdentifying);
//
//            //新OCR统计-点击关闭按钮
//            if (mSide == IDCardAttr.IDCardSide.IDCARD_SIDE_FRONT) {
//                StatisticsController.getInstance().newOCRRequestStatics(IStatisticsConsts.UmengEventId.Page.PAGE_OCR_SHOT,
//                        IStatisticsConsts.UmengEventId.LogType.LOG_TYPE_CLICK,
//                        IStatisticsConsts.UmengEventId.CkModule.CK_MODULE_OCR_CLOSE,index
//                        , functionId, contentId,api_id, pPosition, "0", param2);
//            } else {
//                StatisticsController.getInstance().newOCRRequestStatics(IStatisticsConsts.UmengEventId.Page.PAGE_OCR_SHOT,
//                        IStatisticsConsts.UmengEventId.LogType.LOG_TYPE_CLICK,
//                        IStatisticsConsts.UmengEventId.CkModule.CK_MODULE_OCR_CLOSE,
//                        index, functionId, contentId,api_id, pPosition, "1", param2);
//            }
//
//            callBackData();
//            doFinish();
//
//        }
    }

    private void doFinish() {
        if (cameraHandlerThread!=null){
            cameraHandlerThread.quit();
        }
        try {
            if (mDecoder != null) {
                mDecoder.setmHasSuccess(true);
                mDecoder.interrupt();
                mDecoder.join();
                mDecoder = null;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        if (mFrameDataQueue != null) {
            mFrameDataQueue.clear();
        }
        if (mIdCardQualityAssessment!=null) {
            mIdCardQualityAssessment.release();
        }
        removeAllAnimation();

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void gotoVerify(final int mIdSide, final byte[] bytes,final byte[] bytes_ref) {
        Log.i(TAG, "gotoVerify: 识别");


        isIdentifying = true;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTvMegviiTipsH.setText("正在识别，请稍后");
            }
        });
        if (mIdSide == 0) {
            Log.i(TAG, "gotoVerify: 正面请求认证");

            RetrofitUtils4test.getInstance().getOCRResultmain(bytes, bytes_ref,mIdSide, new RetrofitUtils4test.ResponseListener<IDCardResult>() {

                @Override
                public void onResponse(IDCardResult idCardResult) {

                    isIdentifying = false;
                    Log.i(TAG, "onResponse: "+idCardResult.toString());

                    boolean flag = idCardResult.isFlag();
                    if (!flag){
//                        int errorType = idCardResult.get("error_type");

                        // TODO: 2019/2/28 正面失败是区分原因 埋点
                        final String msg = idCardResult.getErrorMsg();
                        final int type = idCardResult.getIconType();
                        List<IDCardResult.IconListBean> iconList = idCardResult.getIconList();
                        if (iconList == null ){
                            if (msg != null && !TextUtils.isEmpty(msg)){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        LivenessResultDialog livenessDialog = new LivenessResultDialog();
                                        livenessDialog.show(getFragmentManager(), "");
                                        livenessDialog.setTitle("认证失败");
                                        livenessDialog.setReson(msg);
                                        livenessDialog.setLivenessDialogDismissListener(new LivenessDialogDismissListener() {
                                            @Override
                                            public void onDismiss(boolean isTryAgain) {
                                                switchSide(IDCardAttr.IDCardSide.IDCARD_SIDE_FRONT);
                                            }
                                        });
                                    }
                                });
                            }
                            return;
                        }
                        final List<IDCardFailInfo> failInfos = getFailInfo(iconList);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                IDCardVerifyFailDialog failDialog = new IDCardVerifyFailDialog();
                                if (!IDCardDetectActivity.this.isDestroyed()){
                                    failDialog.show(getFragmentManager(), String.valueOf(type));
                                }
                                failDialog.setFaidReason(msg);
                                if (failInfos != null){
                                    for (int i = 0 ; i < failInfos.size() ; i++){
                                        failDialog.setTvFailTip(failInfos.get(i).getMsg(), type, i);
                                        failDialog.setImgPhoto(failInfos.get(i).getIcon(), type, i);
                                    }
                                }
                                failDialog.setIDCardVerifyFailDialogDismissListener(new IDCardVerifyFailDialogDismissListener() {
                                    @Override
                                    public void onDismiss() {

                                        switchSide(IDCardAttr.IDCardSide.IDCARD_SIDE_FRONT);

                                    }
                                });
                            }
                        });
                        return;
                    }
                    /**识别成功*/

                    String name = "";
                    String number = "";
                    String address = "";
                    IDCardResult.IdCardMessageBean idCardMessage = idCardResult.getIdCardMessage();
                    if (idCardMessage!=null ) {
                         name = idCardMessage.getIdCardName();
                         number = idCardMessage.getIdCardNumber();
                         address = idCardMessage.getAddress();
                    }

//                    event.setIdCardName(name);
//                    event.setIdCardNumber(number);
//                    event.setAddress(address);
//                    event.setFrontImages(Base64.encodeToString(bytes, Base64.DEFAULT));
                    idCardInfo.setIdCardName(name);
                    idCardInfo.setIdCardNumber(number);
                    idCardInfo.setAddress(address);
                    idCardInfo.setFrontImages(Base64.encodeToString(bytes, Base64.DEFAULT));

                    isNeedCallBackFront = true;
                    //弹窗提醒
                    final String finalName = name;
                    final String finalNumber = number;
                    final String finalAddress = address;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ConfirmDialog dialog = new ConfirmDialog();
                            if (!IDCardDetectActivity.this.isDestroyed()){
                                dialog.show(getFragmentManager(), ConfirmDialog.FRONT_CONFIRM);
                            }
                            //新OCR统计-打开弹窗/正面
                            StatisticsController.getInstance().newOCRRequestStatics(IStatisticsConsts.UmengEventId.Page.PAGE_OCR_POPUP,
                                    IStatisticsConsts.UmengEventId.LogType.LOG_TYPE_VIEW,
                                    IStatisticsConsts.UmengEventId.CkModule.CK_MODULE_OCR_POPUP,index
                                    , functionId, contentId,api_id, pPosition, "0", param2);

                            dialog.setIdName(finalName);
                            dialog.setIdNumber(finalNumber);
                            dialog.setIdAddress(finalAddress);
                            dialog.setIDCardResultDialogDismissListener(new IDCardResultDialogDismissListener() {
                                @Override
                                public void onDismiss(boolean isconfirm) {
                                    if (isconfirm){


                                        //新OCR统计-确认无误按钮/正面
                                        StatisticsController.getInstance().newOCRRequestStatics(IStatisticsConsts.UmengEventId.Page.PAGE_OCR_CONFIRM,
                                                IStatisticsConsts.UmengEventId.LogType.LOG_TYPE_CLICK,
                                                IStatisticsConsts.UmengEventId.CkModule.CK_MODULE_OCR_CONFIRM,index
                                                , functionId, contentId,api_id, pPosition, "0", param2);

                                        isFrontComplete = true;
                                        //假如反面已经完成，则回调信息给页端
                                        if (isBackComplete){
                                            // 下一模块

                                            callBackData();
                                        } else {
                                            //假如反面没有完成，则开始反面ocr
                                            switchSide(IDCardAttr.IDCardSide.IDCARD_SIDE_BACK);

                                        }
                                    } else {

                                        //新OCR统计-重试按钮/正面
                                        StatisticsController.getInstance().newOCRRequestStatics(IStatisticsConsts.UmengEventId.Page.PAGE_OCR_RETRY,
                                                IStatisticsConsts.UmengEventId.LogType.LOG_TYPE_CLICK,
                                                IStatisticsConsts.UmengEventId.CkModule.CK_MODULE_OCR_RETRY,index
                                                , functionId, contentId,api_id, pPosition, "0", param2);

                                        //用户不确认信息，重新开始正面的ocr
                                        switchSide(IDCardAttr.IDCardSide.IDCARD_SIDE_FRONT);
                                        isNeedCallBackFront = false;
                                        isFrontComplete = false;
                                    }
                                }
                            });
                        }
                    });
                }

                @Override
                public void onErrorResponse(int errorcode, String msg) {

                    isIdentifying = false;
                    if (!Machine.isNetworkOK(IDCardDetectActivity.this)){
                        Toast.makeText(IDCardDetectActivity.this, "未能连接到互联网，请检查网络设置",
                                Toast.LENGTH_SHORT).show();
                    }
                    Log.i(TAG, "onErrorResponse: "+msg);
                    switchSide(IDCardAttr.IDCardSide.IDCARD_SIDE_FRONT);
                }
            });

        } else {//背面认证
            Log.i(TAG, "gotoVerify: 背面请求认证");

            RetrofitUtils4test.getInstance().getOCRResultmain(bytes,bytes_ref, mIdSide, new RetrofitUtils4test.ResponseListener<IDCardResult>() {

                @Override
                public void onResponse(IDCardResult idCardResult) {

                    isIdentifying = false;
                    Log.i(TAG, "onResponse: "+idCardResult.toString());
                    boolean flag = idCardResult.isFlag();
                    if (!flag) {



                        final String errorMsg = idCardResult.getErrorMsg();
                        final int type = idCardResult.getIconType();
                        List<IDCardResult.IconListBean> iconList = idCardResult.getIconList();
                        if (iconList == null){
                            if (errorMsg != null && !TextUtils.isEmpty(errorMsg)){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        LivenessResultDialog livenessDialog = new LivenessResultDialog();
                                        livenessDialog.show(getFragmentManager(), "");
                                        livenessDialog.setTitle("认证失败");
                                        livenessDialog.setReson(errorMsg);
                                        livenessDialog.setLivenessDialogDismissListener(new LivenessDialogDismissListener() {
                                            @Override
                                            public void onDismiss(boolean isTryAgain) {
                                                switchSide(IDCardAttr.IDCardSide.IDCARD_SIDE_BACK);
                                            }
                                        });
                                    }
                                });
                            }
                        }
                        final List<IDCardFailInfo> failInfos = getFailInfo(iconList);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                IDCardVerifyFailDialog failDialog = new IDCardVerifyFailDialog();
                                if (!IDCardDetectActivity.this.isDestroyed()){
                                    failDialog.show(getFragmentManager(), String.valueOf(type));
                                }
                                failDialog.setFaidReason(errorMsg);
                                if (failInfos != null){
                                    for (int i = 0 ; i < failInfos.size() ; i++){
                                        failDialog.setTvFailTip(failInfos.get(i).getMsg(), type, i);
                                        failDialog.setImgPhoto(failInfos.get(i).getIcon(), type, i);
                                    }
                                }
                                failDialog.setIDCardVerifyFailDialogDismissListener(new IDCardVerifyFailDialogDismissListener() {
                                    @Override
                                    public void onDismiss() {

                                        switchSide(IDCardAttr.IDCardSide.IDCARD_SIDE_BACK);

                                    }
                                });
                            }
                        });
                        return;
                    }


                    IDCardResult.IdCardMessageBean idCardMessage = idCardResult.getIdCardMessage();
                    String validDate ="";
                    String issueBy ="";
                    if (idCardMessage!= null ) {
                        validDate = idCardMessage.getValidDate();
                        issueBy = idCardMessage.getIssuedBy();
                    }
                    idCardInfo.setIssuedBy(validDate);
                    idCardInfo.setValidDate(issueBy);
                    idCardInfo.setBackImages(Base64.encodeToString(bytes, Base64.DEFAULT));

//                    event.setBackImages(Base64.encodeToString(bytes, Base64.DEFAULT));
//                    event.setValidDate(validDate);
//                    event.setIssueBy(issueBy);
                    isNeedCallBackBack = true;
                    //弹窗提醒
                    final String finalIssueBy = issueBy;
                    final String finalValidDate = validDate;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ConfirmDialog dialog = new ConfirmDialog();
                            if (!IDCardDetectActivity.this.isDestroyed()){
                                dialog.show(getFragmentManager(), ConfirmDialog.BACK_CONFIRM);
                            }

                            //新OCR统计-打开弹窗/反面
                            StatisticsController.getInstance().newOCRRequestStatics(IStatisticsConsts.UmengEventId.Page.PAGE_OCR_POPUP,
                                    IStatisticsConsts.UmengEventId.LogType.LOG_TYPE_VIEW,
                                    IStatisticsConsts.UmengEventId.CkModule.CK_MODULE_OCR_POPUP,index
                                    , functionId, contentId, api_id,pPosition, "1", param2);

                            dialog.setIdIssueBy(finalIssueBy);
                            dialog.setIdValidDate(finalValidDate);
                            dialog.setIDCardResultDialogDismissListener(new IDCardResultDialogDismissListener() {
                                @Override
                                public void onDismiss(boolean isconfirm) {
                                    if (isconfirm){

                                        //新OCR统计-确认无误按钮/反面
                                        StatisticsController.getInstance().newOCRRequestStatics(IStatisticsConsts.UmengEventId.Page.PAGE_OCR_CONFIRM,
                                                IStatisticsConsts.UmengEventId.LogType.LOG_TYPE_CLICK,
                                                IStatisticsConsts.UmengEventId.CkModule.CK_MODULE_OCR_CONFIRM,index
                                                , functionId, contentId, api_id,pPosition, "1", param2);

                                        isBackComplete = true;
                                        //假如正面已完成，则回调信息给页端
                                        if (isFrontComplete){
                                            //下一模块
                                            callBackData();
                                        } else {
                                            //假如正面没有完成，则开始正面的ocr
                                            switchSide(IDCardAttr.IDCardSide.IDCARD_SIDE_FRONT);

                                        }
                                    } else {


                                        //新OCR统计-重试按钮/反面
                                        StatisticsController.getInstance().newOCRRequestStatics(IStatisticsConsts.UmengEventId.Page.PAGE_OCR_RETRY,
                                                IStatisticsConsts.UmengEventId.LogType.LOG_TYPE_CLICK,
                                                IStatisticsConsts.UmengEventId.CkModule.CK_MODULE_OCR_RETRY,index
                                                , functionId, contentId, api_id,pPosition, "1", param2);

                                        //不确认信息，则重新开始反面的ocr
                                        switchSide(IDCardAttr.IDCardSide.IDCARD_SIDE_BACK);
                                        isNeedCallBackBack = false;
                                        isBackComplete = false;
                                    }
                                }
                            });
                        }
                    });

                }

                @Override
                public void onErrorResponse(int errorcode, String msg) {
                    isIdentifying = false;
                    if (!Machine.isNetworkOK(IDCardDetectActivity.this)){
                        Toast.makeText(IDCardDetectActivity.this, "未能连接到互联网，请检查网络设置",
                                Toast.LENGTH_SHORT).show();
                    }
                    Log.i(TAG, "onErrorResponse: "+msg);
                    switchSide(IDCardAttr.IDCardSide.IDCARD_SIDE_BACK);

                }
            });

            }
    }


    public void switchSide(IDCardAttr.IDCardSide side) {
        Log.i("verify", "takePic");

        mSide = side;
        index = side == IDCardAttr.IDCardSide.IDCARD_SIDE_FRONT ? "front" : "back";
        mIdcardGuideH.setCardSide(mSide);
        if (mDecoder != null) {
            mDecoder.interrupt();
            mDecoder = null;
        }
        //解决回调页端调两次的bug
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mFrameDataQueue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        mDecoder = new DecodeThread();
        mFrameDataQueue.clear();
        if (!mDecoder.isAlive()) {
            mDecoder.start();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if ( mSide == IDCardAttr.IDCardSide.IDCARD_SIDE_FRONT) {

                        mIbAnimalOneView.setBackgroundResource(R.drawable.sfz_front);
                        mTvMegviiTipsH.setText("人像面拍摄");
                    }else{
                        mIbAnimalOneView.setBackgroundResource(R.drawable.sfz_back);
                        mTvMegviiTipsH.setText("国徽面拍摄");
                    }
                }
            });
        }
    }

    private List<IDCardFailInfo> getFailInfo(List<IDCardResult.IconListBean> list) {
        List<IDCardFailInfo> infos = new ArrayList<>();
        for (int i = 0 ; i < list.size() ; i++){
            IDCardFailInfo info = new IDCardFailInfo();
            info.setIcon(list.get(i).getIcon());
            info.setMsg(list.get(i).getIconMsg());
            infos.add(info);
        }
        return infos;
    }

    public static void setVerifyResultCallback(VerifyResultCallback verifyResultCallback) {
        mVerifyResultCallback = verifyResultCallback;
    }
}
