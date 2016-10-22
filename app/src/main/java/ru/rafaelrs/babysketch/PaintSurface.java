package ru.rafaelrs.babysketch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created with Android Studio
 * User: rafaelrs
 * Date: 21.10.16
 * To change this template use File | Settings | File Templates.
 */

public class PaintSurface extends SurfaceView implements SurfaceHolder.Callback {

    private enum DrawType {
        paint,
        eraser,
    }

    Context mContext;

    private float mLastPointX;
    private float mLastPointY;
    private Paint mDrawingPaint;
    private DrawType mDrawType;
    private Bitmap mDrawingAreaContent = null;
    private Bitmap mPenIcon;
    private Bitmap mEraserIcon;

    public PaintSurface(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        setFocusable(true); // make sure we get key events

        assignToBrushPaintColor(Color.RED);

        setWillNotDraw(false);

    }

    public void assignToBrushPaintColor(int color) {
        mDrawType = DrawType.paint;
        mDrawingPaint = new Paint();
        mDrawingPaint.setColor(color);
        mDrawingPaint.setStrokeCap(Paint.Cap.ROUND);
        mDrawingPaint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 1f, mContext.getResources().getDisplayMetrics()));
    }

    public void assignToBrushEraser() {
        mDrawType = DrawType.eraser;
        mDrawingPaint = new Paint();
        mDrawingPaint.setColor(Color.WHITE);
        mDrawingPaint.setStrokeCap(Paint.Cap.ROUND);
        mDrawingPaint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 4f, mContext.getResources().getDisplayMetrics()));
    }

    public void erasePaint() {
        mDrawingAreaContent = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);

        Paint drawingBackground = new Paint();
        drawingBackground.setColor(Color.WHITE);
        Canvas c = new Canvas(mDrawingAreaContent);
        c.drawRect(0, 0, getWidth(), getHeight(), drawingBackground);

        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mLastPointX = event.getX();
            mLastPointY = event.getY();

        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            Canvas c = new Canvas(mDrawingAreaContent);
            c.drawLine(mLastPointX, mLastPointY, event.getX(), event.getY(), mDrawingPaint);
            mLastPointX = event.getX();
            mLastPointY = event.getY();
            postInvalidate();

        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            Canvas c = new Canvas(mDrawingAreaContent);
            c.drawLine(mLastPointX, mLastPointY, event.getX(), event.getY(), mDrawingPaint);
            mLastPointX = -1;
            mLastPointY = -1;
            postInvalidate();
        }
        return true;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        if (mDrawingAreaContent == null) {
            if (!loadPaint(width, height)) erasePaint();
        }
        Bitmap penOriginal = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.pen);
        mPenIcon = Bitmap.createScaledBitmap(penOriginal, height / 5, height / 5, true);
        Bitmap eraserOriginal = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.eraser);
        mEraserIcon = Bitmap.createScaledBitmap(eraserOriginal, height / 5, height / 5, true);

    }

    private boolean loadPaint(int width, int height) {
        Log.i(this.getClass().getSimpleName(), "Try to restore image");
        Bitmap loadedImage = BitmapFactory.decodeFile(new File(getContext().getFilesDir(), "current_image.png").getAbsolutePath());
        if (loadedImage != null && loadedImage.getWidth() == width && loadedImage.getHeight() == height) {
            mDrawingAreaContent = loadedImage.copy(Bitmap.Config.ARGB_8888, true);;
            invalidate();

            Log.i(this.getClass().getSimpleName(), "Loaded image " + mDrawingAreaContent.getWidth() + "x" + mDrawingAreaContent.getHeight());
            return true;
        } else {
            return false;
        }
    }

    public void savePaint() {
        try {
            if (mDrawingAreaContent == null) return;

            Log.i(this.getClass().getSimpleName(), "Saving image " + mDrawingAreaContent.getWidth() + "x" + mDrawingAreaContent.getHeight());
            File outFile = new File(getContext().getFilesDir(), "current_image.png");
            FileOutputStream fOut = new FileOutputStream(outFile);

            mDrawingAreaContent.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void surfaceCreated(SurfaceHolder holder) {
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (mDrawingAreaContent != null) {
            canvas.drawBitmap(mDrawingAreaContent, 0, 0, null);
        }

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);

        if (mLastPointX != -1 && mLastPointY != -1 && !isInEditMode()) {
            switch (mDrawType) {
                case paint:
                    canvas.drawBitmap(mPenIcon, mLastPointX, mLastPointY - mPenIcon.getHeight(), null);
                    //canvas.drawCircle(mLastPointX, mLastPointY, 6f, paint);
                    break;
                case eraser:
                    canvas.drawBitmap(mEraserIcon, mLastPointX, mLastPointY - mEraserIcon.getHeight(), null);
                    //canvas.drawCircle(mLastPointX, mLastPointY, 24f, paint);
                    break;
            }
        }

        paint.setColor(Color.LTGRAY);
        paint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 0.5f, getResources().getDisplayMetrics()));
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);

        super.onDraw(canvas);
    }

    public Bitmap getBitmap() {
        return mDrawingAreaContent;
    }

}
