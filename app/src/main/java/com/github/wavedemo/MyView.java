package com.github.wavedemo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * @data 2018-09-05
 * @desc
 */

public class MyView extends View {

    private Paint mPaint;
    private int mWidth;
    private int mHeight;
    private int mWaveWidth = 550;
    private int mWaveHeight = 100;
    private int mWaveStartHeight = 500;
    private Path mPath;
    private float mDx = 0;
    private Bitmap mBitmap;
    private Paint mBmPaint;
    private Region mRegion;

    public MyView(Context context) {
        this(context, null);
    }

    public MyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(Color.parseColor("#54ebfc"));
        mPath = new Path();

        mBmPaint = new Paint();
        mBmPaint.setAntiAlias(true);
        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.test);
        mBitmap = getCircleBitmap(mBitmap);
    }

    private Bitmap getCircleBitmap(Bitmap bitmap) {
        Bitmap bitmapCreate = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapCreate);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        int radius = Math.min(bitmap.getWidth(), bitmap.getHeight()) / 2;
        paint.setColor(Color.WHITE);
        canvas.drawCircle(radius, radius, 170, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF rectF = new RectF(new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()));
        canvas.drawBitmap(bitmap, rect, rectF, paint);
        return bitmapCreate;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        if (mWaveStartHeight == 0) {
            mWaveStartHeight = mHeight;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawWave(canvas);
        drawCircle(canvas);
    }

    private void drawCircle(Canvas canvas) {
        Rect bounds = mRegion.getBounds();
        if (bounds.top > 0 || bounds.right > 0) {
            if (bounds.top < mWaveStartHeight) { //从波峰滑落
                canvas.drawBitmap(mBitmap, bounds.right - mBitmap.getWidth() / 2, bounds.top - mBitmap.getHeight(), mBmPaint);
            }else{
                canvas.drawBitmap(mBitmap, bounds.right - mBitmap.getWidth() / 2, bounds.bottom - mBitmap.getHeight(), mBmPaint);
            }
        }else{
            int x = mWidth / 2 - mBitmap.getWidth() / 2;
            canvas.drawBitmap(mBitmap, x, mWaveStartHeight - mBitmap.getHeight(), mBmPaint);
        }
    }

    private void drawWave(Canvas canvas) {
        mPath.reset();
        //rQuadTo 相对于上一个点
        mPath.moveTo(-mWaveWidth + mDx, mWaveStartHeight);
        for (int i = -mWaveWidth; i < mWidth + mWaveWidth; i+=mWaveWidth) {
            mPath.rQuadTo(mWaveWidth / 4, -mWaveHeight, mWaveWidth / 2, 0);
            mPath.rQuadTo(mWaveWidth / 4, mWaveHeight, mWaveWidth / 2, 0);
        }
        mRegion = new Region();
        double l = getWidth() / 2;
        Region region = new Region((int) (l - 0.1), 0, (int) l, mHeight * 2);
        mRegion.setPath(mPath, region);

        mPath.lineTo(mWidth, mHeight);
        mPath.lineTo(0, mHeight);
        mPath.close();
        canvas.drawPath(mPath, mPaint);
    }

    public void startAnim(){
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(2000);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mDx = (float) valueAnimator.getAnimatedValue() * mWaveWidth;
                postInvalidate();
            }
        });
        valueAnimator.start();
    }
}
