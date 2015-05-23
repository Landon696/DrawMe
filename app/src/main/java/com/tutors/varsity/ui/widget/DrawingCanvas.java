package com.tutors.varsity.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import com.tutors.varsity.R;
import com.tutors.varsity.model.DrawMePath;

import java.util.LinkedList;

public class DrawingCanvas extends View implements View.OnTouchListener {


    private int mStrokeSize = 6;
    private Canvas mCanvas;
    private DrawMePath mPath;
    private Paint mPaint;
    private LinkedList<DrawMePath> mPaths = new LinkedList<>();
    private int mPencilColor = R.color.blue;

    public DrawingCanvas(Context context) {
        super(context);
        setFocusable(true);
        setFocusableInTouchMode(true);

        this.setOnTouchListener(this);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(getResources().getColor(mPencilColor));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(mStrokeSize);
        mCanvas = new Canvas();
        mCanvas.drawColor(Color.WHITE);
        mPath = new DrawMePath(mPencilColor, mStrokeSize);
        mPaths.addFirst(mPath);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (mPaths.size() > 0) {
            for (DrawMePath p : mPaths) {
                mPaint.setColor(getResources().getColor(p.getColorId()));
                mPaint.setStrokeWidth(p.getStokeSize());
                canvas.drawPath(p, mPaint);
            }
        }
        else {
            canvas.drawColor(Color.TRANSPARENT);
        }
    }

    public LinkedList<DrawMePath> getPaths() {
        return mPaths;
    }

    public void undo(){
        if (!mPaths.isEmpty()) {
            if (mPaths.get(0).isEmpty() && mPaths.size() > 1) {
                mPaths.removeFirst();
            }
            mPaths.removeFirst();
            invalidate();
        }
    }

    public void setPencilColor(int color) {

        mPencilColor = color;
        mPaint.setColor(getResources().getColor(mPencilColor));
        mPaths.get(0).setColorId(mPencilColor);
        refreshPaint();
    }

    public void setStroke(int size) {
        mStrokeSize = size;
        mPaths.get(0).setStokeSize(mStrokeSize);
        refreshPaint();
    }

    public void erase() {
        mPaths.clear();
        invalidate();
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }
    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;
        }
    }
    private void touch_up() {
        mPath.lineTo(mX, mY);
        // commit the path to our offscreen
        mCanvas.drawPath(mPath, mPaint);
        // kill this so we don't double draw
        mPath = new DrawMePath(mPencilColor, mStrokeSize);
        mPaths.addFirst(mPath);
    }



    @Override
    public boolean onTouch(View arg0, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }

    private void refreshPaint () {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(getResources().getColor(mPencilColor));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(mStrokeSize);
        mCanvas = new Canvas();
        mCanvas.drawColor(Color.WHITE);
    }
}
