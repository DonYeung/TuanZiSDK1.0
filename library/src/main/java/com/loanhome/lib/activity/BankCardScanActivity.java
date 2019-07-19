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
import android.os.Vibrator;
import android.text.TextUtils;
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
import com.loanhome.lib.bean.BankCardResult;
import com.loanhome.lib.bean.IDCardFailInfo;
import com.loanhome.lib.bean.IDCardInfo;
import com.loanhome.lib.bean.IDCardResult;
import com.loanhome.lib.http.RetrofitUtils4test;
import com.loanhome.lib.http.StatisticsController;
import com.loanhome.lib.listener.ExitConfirmDialogDismissListener;
import com.loanhome.lib.listener.IDCardResultDialogDismissListener;
import com.loanhome.lib.listener.LivenessDialogDismissListener;
import com.loanhome.lib.listener.VerifyResultCallback;
import com.loanhome.lib.statistics.IStatisticsConsts;
import com.loanhome.lib.util.CameraHandlerThread;
import com.loanhome.lib.util.ICamera;
import com.loanhome.lib.util.Machine;
import com.loanhome.lib.util.RotaterUtil;
import com.loanhome.lib.util.Util;
import com.loanhome.lib.view.BankCardTipsDialog;
import com.loanhome.lib.view.ConfirmDialog;
import com.loanhome.lib.view.Dialog_FilpTip;
import com.loanhome.lib.view.ExitConfirmDialog;
import com.loanhome.lib.view.IDCardGuideH;
import com.loanhome.lib.view.LivenessResultDialog;
import com.megvii.idcardquality.IDCardQualityAssessment;
import com.megvii.idcardquality.IDCardQualityResult;
import com.megvii.idcardquality.bean.IDCardAttr;

import org.json.JSONObject;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.os.Build.VERSION_CODES.M;


/**
 * 银行卡OCR
 * Created by Don on 2019.05.08
 */
public class BankCardScanActivity extends Activity implements TextureView.SurfaceTextureListener, Camera.PreviewCallback, View.OnClickListener {
    private static final String TAG = "BankCardScanActivity";
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
    private BankCardScanActivity.DecodeThread mDecoder = null;
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
    private ImageView iv_back;
    private ImageView iv_tips;
    private ImageView iv_flashlight;
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

    private String pPosition;
    private String param1;
    private String param2;
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

    private static VerifyResultCallback mVerifyResultCallback;

    private ThreadPoolExecutor mThreadPool;
    private static final int KEEP_ALIVE_TIME = 10;
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    private BlockingQueue<Runnable> workQueue;
    //HandlerThread
    private CameraHandlerThread cameraHandlerThread;

    private Bitmap bitmap;
    private Boolean isVerifity = false;
    private Boolean isNeedData = false;//关闭时是否需要返回数据
    private String bankCardNumber;
    private String idCardName;
    //是否打开闪光灯
    private boolean openOrClose = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bankcardscan_layout);
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

        String idName  = getIntent().getStringExtra("idName");
        if (idName!=null&&!idName.equals("")) {
            idCardName=idName;
        }else{
            idCardName="";
        }

        //埋点相关
        index = getIntent().getStringExtra("index");
        if (TextUtils.isEmpty(index)) {
            index = "-1";
        }

        mIsVertical = getIntent().getBooleanExtra("isvertical", false);
        isNeedCallBackFront = getIntent().getBooleanExtra("isNeedCallBackFront", true);
        isNeedCallBackBack = getIntent().getBooleanExtra("isNeedCallBackBack", true);
        functionId = getIntent().getStringExtra("functionId");
        contentId = getIntent().getStringExtra("contentId");
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
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_tips = (ImageView) findViewById(R.id.iv_tips);
        iv_flashlight = (ImageView) findViewById(R.id.iv_flashlight);

        iv_back.setOnClickListener(this);
        iv_tips.setOnClickListener(this);
        iv_flashlight.setOnClickListener(this);
        mIdcardGuideH.setOnClickListener(this);
        mTextureView.setOnClickListener(this);
        mRelativeLayout.setOnClickListener(this);
        mTextureView.setSurfaceTextureListener(this);

        mIbAnimalBreathView.setBackgroundResource(R.drawable.bg_sfz_light);
        mIbAnimalOneView.setVisibility(View.VISIBLE);



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
                        Log.i(TAG, "initSdk: Camera2.0 api支持程度为" + level);
                        if (level != CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL) {
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
        if (isNeedData){
            String jsoncallback = "{\"bankCardNumber\": \" " + bankCardNumber +"\"}";
            mVerifyResultCallback.onVerifySuccess(jsoncallback);
        } else {
            //取消
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
                if(frameData!=null) {
                    mFrameDataQueue.offer(frameData);
                    Log.i(TAG, Thread.currentThread().getName() + "_ThreadPoolPost");
                }
            }
        });
    }

    @Override
    public void onPreviewFrame(final byte[] data, Camera camera) {
        if (isCanDetected) {
            ThreadPoolPost(data);
            Log.i(TAG, Thread.currentThread().getName()+ "_onPreviewFrame");
            Log.i(TAG,"after setting, previewformate is " + camera.getParameters().getPreviewFormat());
            Log.i(TAG,"initCamera  after setting, previewframetate is " + camera.getParameters().getPreviewFrameRate());
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
//        Camera camera = mICamera.openCamera(this);
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

            //统计-访问添卡OCR页面
            StatisticsController.getInstance().newRequestStatics(IStatisticsConsts.UmengEventId.Page.PAGE_CARD_PAGE,
                    IStatisticsConsts.UmengEventId.LogType.LOG_TYPE_VIEW,
                    IStatisticsConsts.UmengEventId.CkModule.CK_MODULE_VIEW_OCR_CARD_PAGE,
                    index, functionId, contentId, pPosition, param1, param2);


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

        long startTime = System.currentTimeMillis();

        @Override
        public void run() {
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
                            mICamera.getCameraAngle(BankCardScanActivity.this));
                    if (mIsVertical) {
                        mImageWidth = mICamera.cameraHeight;
                        mImageHeight = mICamera.cameraWidth;
                    }

                    try {
                        mQualityResult = mIdCardQualityAssessment.getQuality(mImgData, mImageWidth,
                                mImageHeight, IDCardAttr.IDCardSide.IDCARD_SIDE_FRONT, mRoi);
                    } catch (Exception e) {
                        continue;
                    }
                    final boolean isSuccess = mQualityResult.isValid();

                    mDecoder.setmHasSuccess(isSuccess);

                    //临时copy的变量，用于关键帧的预处理
                    final byte[] finalImgData = mImgData;


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTvMegviiTipsH.setText(getString(R.string.show_bankcard_name, idCardName));
                            mTvTipText.setText(getString(R.string.bankcardscan_dialog_horizontalTips));

                            //获取关键帧
                            if (mQualityResult.attr != null) {

                                if (System.currentTimeMillis() - startTime > 5000) {
                                    mHasFinished = true;
                                    bitmap = mICamera.getBitMap(finalImgData, mICamera, false);
                                    byte[] bytes = Util.bmp2byteArr(bitmap);
//                                                Log.i("Don", "run2: base64:"+ Base64.encodeToString(BitmapTools.bitmapToByteArray(bitmap),Base64.DEFAULT));

                                    if (!isVerifity) {
                                        gotoBankVerify(bytes);
                                        mFrameDataQueue.clear();
                                    }
                                    return;
                                }
//                                        }
                            }

                        }
                    });
//                        }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void gotoBankVerify(final byte[] bytes) {
        isVerifity = true;
        isIdentifying = true;
        Log.i("Don", "gotoBankVerify: 请求银行卡OCR识别");
        RetrofitUtils4test.getInstance().getBankOCRResultmain(bytes, new RetrofitUtils4test.ResponseListener<BankCardResult>() {
            @Override
            public void onResponse(BankCardResult response) {
                isIdentifying = false;

                boolean flag = response.isFlag();
                Log.i("Don", "onResponse: flag:"+flag);
                if (!flag) {

                    String msg = response.getResult().getMsg();
                    Log.i("Don", "onResponse: msg:"+msg);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LivenessResultDialog livenessDialog = new LivenessResultDialog();
                            livenessDialog.show(getFragmentManager(), "");
                            livenessDialog.setTitle("没有识别成功");
                            livenessDialog.setReson("请确认卡号是否被遮挡");
                            //失败弹窗统计
                            StatisticsController.getInstance().newRequestStatics(IStatisticsConsts.UmengEventId.Page.PAGE_CARD_FAIL,
                                    IStatisticsConsts.UmengEventId.LogType.LOG_TYPE_VIEW,
                                    IStatisticsConsts.UmengEventId.CkModule.CK_MODULE_VIEW_OCR_CARD_FAIL,
                                    index, functionId, contentId, pPosition, param1, param2);

                            livenessDialog.setLivenessDialogDismissListener(new LivenessDialogDismissListener() {
                                @Override
                                public void onDismiss(boolean isTryAgain) {
                                    isVerifity = false;
                                    switchSide();

                                    //失败弹窗-再试一次点击统计
                                    //失败弹窗-再试一次点击统计
                                    StatisticsController.getInstance().newRequestStatics(IStatisticsConsts.UmengEventId.Page.PAGE_CARD_FAIL,
                                            IStatisticsConsts.UmengEventId.LogType.LOG_TYPE_CLICK,
                                            IStatisticsConsts.UmengEventId.CkModule.CK_MODULE_CLICK_CLICK_RETAKE,
                                            index, functionId, contentId, pPosition, param1, param2);
                                }
                            });
                        }
                    });
                }else{
                    /**识别成功*/
                    //暂无埋点
                    StatisticsController.getInstance().newRequestStatics(IStatisticsConsts.UmengEventId.Page.PAGE_OCR_ID_COMPARE,
                            IStatisticsConsts.UmengEventId.LogType.LOG_TYPE_VIEW,
                            IStatisticsConsts.UmengEventId.CkModule.CK_MODULE_OCR_ID_COMPARE_DONE
                    ,"front", functionId, contentId, pPosition, param1, param2);
                    final String bankcardNumber = response.getNumber();
                    if (bankcardNumber != null && !bankcardNumber.equals("")) {
                        //弹窗提醒
                        //回调bankcardNumber信息给页端,bankcardNumber去除空格
                        String finalCardnumber =null;
                        if (bankcardNumber.contains(" ")) {
                            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
                            Matcher m = p.matcher(bankcardNumber);
                            finalCardnumber = m.replaceAll("");
                            Log.i("Don", "onNext: num:" +finalCardnumber);
                        }
                        bankCardNumber = finalCardnumber;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ConfirmDialog dialog = new ConfirmDialog();
                                if (!BankCardScanActivity.this.isDestroyed()) {
                                    dialog.show(getFragmentManager(), ConfirmDialog.BANKCARD_CONFIRM);
                                    //统计-确认弹窗展示事件
                                    StatisticsController.getInstance().newRequestStatics(IStatisticsConsts.UmengEventId.Page.PAGE_CARDNUM_POP,
                                            IStatisticsConsts.UmengEventId.LogType.LOG_TYPE_VIEW,
                                            IStatisticsConsts.UmengEventId.CkModule.CK_MODULE_VIEW_CARDNUM_POP,
                                            index, functionId, contentId, pPosition, param1, param2);

                                }

                                dialog.setBankCardNumber(bankcardNumber);//设置银行卡号
                                dialog.setIDCardResultDialogDismissListener(new IDCardResultDialogDismissListener() {
                                    @Override
                                    public void onDismiss(boolean isconfirm) {
                                        if (isconfirm) {
                                            //统计-确认弹窗点击
                                            StatisticsController.getInstance().newRequestStatics(IStatisticsConsts.UmengEventId.Page.PAGE_CARDNUM_POP,
                                                    IStatisticsConsts.UmengEventId.LogType.LOG_TYPE_CLICK,
                                                    IStatisticsConsts.UmengEventId.CkModule.CK_MODULE_CLICK_CLICK_CHECK_CONFIRM,
                                                    index, functionId, contentId, pPosition, param1, param2);

                                            isFrontComplete = true;
                                            //已经完成，则回调信息给页端
                                            // 下一模块
//                                            callBackData();
                                            isNeedData = true;
                                            //验证成功,下一模块
                                            callBackData();

                                        } else {

                                            ////统计-确认弹窗重试
                                            StatisticsController.getInstance().newRequestStatics(IStatisticsConsts.UmengEventId.Page.PAGE_CARDNUM_POP,
                                                    IStatisticsConsts.UmengEventId.LogType.LOG_TYPE_CLICK,
                                                    IStatisticsConsts.UmengEventId.CkModule.CK_MODULE_CLICK_CLICK_RETAKE,
                                                    index, functionId, contentId, pPosition, param1, param2);

                                            //用户不确认信息，重新开始正面的ocr
                                            isVerifity = false;
                                            isNeedData = false;
                                            switchSide();
                                            isNeedCallBackFront = false;
                                            isFrontComplete = false;
                                        }
                                    }
                                });
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LivenessResultDialog livenessDialog = new LivenessResultDialog();
                                livenessDialog.show(getFragmentManager(), "");
                                livenessDialog.setTitle("没有识别成功");
                                livenessDialog.setReson("请确认卡号是否被遮挡");
                                //失败弹窗统计
                                StatisticsController.getInstance().newRequestStatics(IStatisticsConsts.UmengEventId.Page.PAGE_CARD_FAIL,
                                        IStatisticsConsts.UmengEventId.LogType.LOG_TYPE_VIEW,
                                        IStatisticsConsts.UmengEventId.CkModule.CK_MODULE_VIEW_OCR_CARD_FAIL,
                                        index, functionId, contentId, pPosition, param1, param2);

                                livenessDialog.setLivenessDialogDismissListener(new LivenessDialogDismissListener() {
                                    @Override
                                    public void onDismiss(boolean isTryAgain) {
                                        isVerifity = false;
                                        switchSide();

                                        //失败弹窗-再试一次点击统计
                                        StatisticsController.getInstance().newRequestStatics(IStatisticsConsts.UmengEventId.Page.PAGE_CARD_FAIL,
                                                IStatisticsConsts.UmengEventId.LogType.LOG_TYPE_CLICK,
                                                IStatisticsConsts.UmengEventId.CkModule.CK_MODULE_CLICK_CLICK_RETAKE,
                                                index, functionId, contentId, pPosition, param1, param2);
                                    }
                                });
                            }
                        });
                    }

                }
            }

            @Override
            public void onErrorResponse(int errorcode, String msg) {
                isIdentifying = false;
                Log.i("Don", "onError: "+msg.toString());
                if (!Machine.isNetworkOK(BankCardScanActivity.this)){
                    Toast.makeText(BankCardScanActivity.this, "未能连接到互联网，请检查网络设置",
                            Toast.LENGTH_SHORT).show();
                }
                switchSide();
            }
        });

    }

    private long toastTime = 0;
    private IDCardQualityResult.IDCardResultType lastFaileType;
    private long lastTime = 0;
    private long firstTime = 0;
    private boolean isFirstToast = true;
    private boolean canShowAnimal = true;
    private boolean isTheSameError = true;

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
        if (v.getId() == R.id.iv_back) {
            onBackPressed();
        }else if(v.getId() ==R.id.iv_tips){
            //统计-点击持卡人提示
            StatisticsController.getInstance().newRequestStatics(IStatisticsConsts.UmengEventId.Page.PAGE_CARD_PAGE,
                    IStatisticsConsts.UmengEventId.LogType.LOG_TYPE_CLICK,
                    IStatisticsConsts.UmengEventId.CkModule.CK_MODULE_CLICK_CLICK_TIPS,
                    index, functionId, contentId, pPosition, param1, param2);

            BankCardTipsDialog failDialog = new BankCardTipsDialog();
            if (!BankCardScanActivity.this.isDestroyed()){
                failDialog.show(getFragmentManager(), "");
            }
            failDialog.setDialogDismissListener(new BankCardTipsDialog.DialogDismissListener() {
                @Override
                public void onDismiss() {
                }
            });

        }else if(v.getId() == R.id.iv_flashlight){
            //闪光灯
            if (!openOrClose){
                openOrClose = true;
                mICamera.openFlashLight(BankCardScanActivity.this,true);
            }else{
                openOrClose = false;
                mICamera.openFlashLight(BankCardScanActivity.this,false);
            }
        }
        else if (v.getId() == R.id.idcardscan_cn_layout_guide_h
                || v.getId() == R.id.idcardscan_cn_layout_surface) {
            if (mICamera != null) {
                mICamera.autoFocus();
            }
        }
    }


    @Override
    public void onBackPressed() {
//        if (isIdentifying){
        Log.i(TAG, "onBackPressed: " + isIdentifying);
        //确认是否离开
        ExitConfirmDialog exitDialog = new ExitConfirmDialog();
        exitDialog.show(getFragmentManager(), "exit");
        exitDialog.setExitConfirmDialogDismissListener(new ExitConfirmDialogDismissListener() {
            @Override
            public void onDismiss(boolean isWait) {
                if (!isWait) {
                    callBackData();
                    mVerifyResultCallback.onVerifyCancel();
                    doFinish();

                }
            }
        });

    }

    private void doFinish() {
        //关闭闪光灯
        openOrClose = false;

        if (cameraHandlerThread != null) {
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
        if (mIdCardQualityAssessment != null) {
            mIdCardQualityAssessment.release();
        }
        removeAllAnimation();

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void switchSide() {
        Log.i("verify", "takePic");

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
                    if (mIsVertical){
                        mTvMegviiTipsH.setText("");
                    } else {
                        mTvMegviiTipsH.setText(getString(R.string.show_bankcard_name,idCardName));
                        mTvTipText.setText(getString(R.string.bankcardscan_dialog_horizontalTips));
                    }
                }
            });
        }
    }

    public static void setVerifyResultCallback(VerifyResultCallback verifyResultCallback) {
        mVerifyResultCallback = verifyResultCallback;
    }
}
