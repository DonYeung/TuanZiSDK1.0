package com.loanhome.lib.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;


import com.loanhome.lib.R;
import com.megvii.idcardquality.bean.IDCardAttr;


public class IDCardGuideH extends View {
    private Bitmap mCircleBmp;
    private Canvas mCanvas;
    private float IDCARD_RATIO = 856.0f / 540.0f;
    private int mViewWidth = 0;
    private int mViewHeight = 0;
    private float reduceRatio = 1.0f;

    private Rect srcRect;
    private Rect bitmapRect;
    private RectF roundRec;
    private RectF disRectF;
    private Paint mRectPaint;
    private Paint mCirclePaint;
    private Paint duffXmodePaint;
    private PorterDuffXfermode duffXmode;

    private int rectType = 1;//1小框80% 2 大框88%
    private float roundRectLeft1;
    private float roundRectTop1;
    private float roundRectRight1;
    private float roundRectBottom1;

    private float roundRectLeft;
    private float roundRectTop;
    private float roundRectRight;
    private float roundRectBottom;

    private float roundRectLeft2;
    private float roundRectTop2;
    private float roundRectRight2;
    private float roundRectBottom2;
    private Bitmap lineBitmap;

    private boolean isDrawImage = true;//是否画框

    public static final float BIG_RECT_PERCENT = 0.95f;//大框的比例
    public static final float SMALL_RECT_PERCENT = 0.65f;//小框的比例


    public IDCardGuideH(Context context) {
        this(context, null);
    }

    public IDCardGuideH(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IDCardGuideH(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void computeArgs() {
        mViewWidth = getWidth();
        mViewHeight = getHeight();
    }

    private void init(Context context) {
        srcRect = new Rect();
        disRectF = new RectF();
        roundRec = new RectF();
        bitmapRect = new Rect();
        mRectPaint = new Paint();
        mRectPaint.setAntiAlias(true);

        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);

        duffXmodePaint = new Paint();
        duffXmodePaint.setAntiAlias(true);
    }

    private Bitmap bitmap;

    public void setCardSide(IDCardAttr.IDCardSide cardSide) {
        int bitmapId = R.drawable.bg_sfz_empty_icon;
        bitmap = BitmapFactory.decodeResource(getContext().getResources(), bitmapId);
        isDrawImage = true;
        rectType = 1;
        roundRectColor = Color.parseColor("#FFFFFF");
        requestLayout();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

//        float roundRectHeight = height * 0.52f;
        float roundRectHeight = height * 0.68f;
        float roundRectWidth = roundRectHeight * IDCARD_RATIO;
//        float roundRectHeight = width * (4 / 3.0f);
//        float roundRectWidth =  roundRectHeight * (4 / 3.0f);


        roundRectLeft1 = (width - roundRectWidth) / 2;
        roundRectRight1 = roundRectLeft1 + roundRectWidth;
        float roundCenterY = height / 2;

        roundRectTop1 = roundCenterY - roundRectHeight / 2;
        roundRectBottom1 = roundCenterY + roundRectHeight / 2;

        roundRectHeight = height * 0.67f;
        roundRectWidth = roundRectHeight * (3 / 4.0f);
//        roundRectWidth = roundRectHeight * IDCARD_RATIO;
        roundRectLeft2 = (width - roundRectWidth) / 2;
        roundRectRight2 = roundRectLeft2 + roundRectWidth;
        roundRectTop2 = roundCenterY - roundRectHeight / 2;
        roundRectBottom2 = roundCenterY + roundRectHeight / 2;

        roundRectLeft = rectType == 1 ? roundRectLeft1 : roundRectLeft2;
        roundRectRight = rectType == 1 ? roundRectRight1 : roundRectRight2;
        roundRectTop = rectType == 1 ? roundRectTop1 : roundRectTop2;
        roundRectBottom = rectType == 1 ? roundRectBottom1 : roundRectBottom2;

//        int width = MeasureSpec.getSize(widthMeasureSpec);
//        int height = MeasureSpec.getSize(heightMeasureSpec);
//
//        float roundRectWidth = width * BIG_RECT_PERCENT;
//        float roundRectHeight = roundRectWidth / IDCARD_RATIO;
//
//        roundRectLeft1 = (width - roundRectWidth) / 2;
//        roundRectRight1 = roundRectLeft1 + roundRectWidth;
//
//        float previewHeight = width * (4 / 3.0f);
//        float roundCenterY = previewHeight * (2 / 5.0f) + getResources().getDimension(R.dimen.checktrue_title_bar_height);
//
//        roundRectTop1 = roundCenterY - roundRectHeight / 2;
//        roundRectBottom1 = roundCenterY + roundRectHeight / 2;
//
//        roundRectWidth = width * SMALL_RECT_PERCENT;
//        roundRectHeight = roundRectWidth / IDCARD_RATIO;
//        roundRectLeft2 = (width - roundRectWidth) / 2;
//        roundRectRight2 = roundRectLeft2 + roundRectWidth;
//        roundRectTop2 = roundCenterY - roundRectHeight / 2;
//        roundRectBottom2 = roundCenterY + roundRectHeight / 2;
//
//        roundRectLeft = rectType == 1 ? roundRectLeft1 : roundRectLeft2;
//        roundRectRight = rectType == 1 ? roundRectRight1 : roundRectRight2;
//        roundRectTop = rectType == 1 ? roundRectTop1 : roundRectTop2;
//        roundRectBottom = rectType == 1 ? roundRectBottom1 : roundRectBottom2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        computeArgs();
        drawRectAndEmpCicle();
        //将bitmap拷贝到canvas上并恢复原来大小
        disRectF.set(0f, 0f, getWidth(), getHeight());
        canvas.drawBitmap(mCircleBmp, srcRect, disRectF, mRectPaint);
        drawRoundRect(canvas);

    }


    /**
     * 画一个挖出圆角空间的矩形(因为效率原因在绘画时先进行缩放)
     *
     * @return
     */
    private void drawRectAndEmpCicle() {
        mRectPaint.setColor(Color.argb(125, 0, 0, 0));
        //阴影层图像
        if (mCircleBmp == null) {
            mCircleBmp = Bitmap.createBitmap((int) (mViewWidth / reduceRatio), (int) (mViewHeight / reduceRatio), Bitmap.Config.ARGB_8888);
        }
        if (mCanvas == null) {
            mCanvas = new Canvas(mCircleBmp);
        }
        srcRect.set(0, 0, (int) (mViewWidth / reduceRatio), (int) (mViewHeight / reduceRatio));
        mCanvas.drawRect(srcRect, mRectPaint);
        if (isDrawImage) {
            if (duffXmode == null) {
                duffXmode = new PorterDuffXfermode(PorterDuff.Mode.XOR);
            }
            duffXmodePaint.setXfermode(duffXmode);
            roundRec.set(roundRectLeft / reduceRatio, roundRectTop / reduceRatio, roundRectRight / reduceRatio, roundRectBottom / reduceRatio);
            mCanvas.drawRoundRect(roundRec, 20, 20, duffXmodePaint);
            duffXmodePaint.setXfermode(null);
        }
    }

    public void setDrawLine(boolean flag) {
        if (!flag) {
            roundRectColor = Color.parseColor("#3DDCFF");
        }
        invalidate();
    }

    private int roundRectColor = Color.parseColor("#FFFFFF");

    private void drawRoundRect(Canvas canvas) {
        if (isDrawImage) {
            mCirclePaint.setColor(roundRectColor);
            mCirclePaint.setStrokeWidth(getResources().getDimension(R.dimen.dp_3));
            mCirclePaint.setStyle(Paint.Style.STROKE);
            roundRec.set(roundRectLeft, roundRectTop, roundRectRight, roundRectBottom);
            canvas.drawRoundRect(roundRec, 20, 20, mCirclePaint);

            bitmapRect.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
            canvas.drawBitmap(bitmap, bitmapRect, roundRec, null);
        }

    }

    public RectF getPosition(int rectType) {
        RectF rectF = new RectF();
        if (rectType == 1) {
            rectF.left = roundRectLeft1 / (float) getWidth();
            rectF.top = roundRectTop1 / (float) getHeight();

            rectF.right = roundRectRight1 / (float) getWidth();
            rectF.bottom = roundRectBottom1 / (float) getHeight();
        } else if (rectType == 2) {
            rectF.left = roundRectLeft2 / (float) getWidth();
            rectF.top = roundRectTop2 / (float) getHeight();

            rectF.right = roundRectRight2 / (float) getWidth();
            rectF.bottom = roundRectBottom2 / (float) getHeight();
        }

        return rectF;
    }

    public RectF getScreenPosition(int rectType) {
        RectF rectF = new RectF();
        if (rectType == 1) {
            rectF.left = roundRectLeft1;
            rectF.top = roundRectTop1;
            rectF.right = roundRectRight1;
            rectF.bottom = roundRectBottom1;
        } else if (rectType == 2) {
            rectF.left = roundRectLeft2;
            rectF.top = roundRectTop2;
            rectF.right = roundRectRight2;
            rectF.bottom = roundRectBottom2;
        }
        return rectF;
    }

    public Rect getMargin() {
        Rect rect = new Rect();
        rect.left = bitmapRect.left;
        rect.top = bitmapRect.top;
        rect.right = getWidth() - bitmapRect.right;
        rect.bottom = getHeight() - bitmapRect.bottom;
        return rect;
    }
}
