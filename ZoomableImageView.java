package com.example.shawara.chat.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

import static android.view.MotionEvent.INVALID_POINTER_ID;

/**
 * Created by shawara on 8/27/2016.
 */

public class ZoomableImageView extends ImageView {
    Context mContext;
    GestureDetector gestureDetector;
    ScaleGestureDetector scaleGestureDetector;
    boolean longPress = false;
    float scaleX = 1.0f, scaleY = 1.0f, scaleXP = 0.f, scaleYP = 0.f;
    float translateX = 0.0f, translateY = 0.0f;
    float mScaleFactor = 1.f;
    float mPreviousScale = 1.0f;
    private int mActivePointerId = INVALID_POINTER_ID;
    float mLastTouchX;
    float mLastTouchY;

    @Override
    protected void onDraw(Canvas canvas) {


        canvas.translate(translateX, translateY);
        canvas.scale(scaleX, scaleY, scaleXP, scaleYP);


        super.onDraw(canvas);

    }


    public ZoomableImageView(Context context) {
        super(context);
        mContext = context;
        gestureDetector = new GestureDetector(context, new GestureListener());
        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureListener());

    }

    public ZoomableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        gestureDetector = new GestureDetector(context, new GestureListener());
        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureListener());

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //
        detectDragging(event);
        scaleGestureDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    // Double tap
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            float x = e.getX();
            float y = e.getY();
            // Toast.makeText(mContext, "Double Tap: Tapped at (" + x + "," + y + ")", Toast.LENGTH_SHORT).show();
            if (scaleX == 1.f)
               mScaleFactor= scaleX = scaleY = 3.0f;
            else
              mScaleFactor=  scaleX = scaleY = 1.0f;

            scaleXP = x;
            scaleYP = y;
            translateX = translateY = 0f;
            invalidate();
            return true;
        }

    }


    // dragging
    private void detectDragging(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                final int pointerIndex = MotionEventCompat.getActionIndex(ev);
                final float x = MotionEventCompat.getX(ev, pointerIndex);
                final float y = MotionEventCompat.getY(ev, pointerIndex);

                // Remember where we started (for dragging)
                mLastTouchX = x;
                mLastTouchY = y;
                // Save the ID of this pointer (for dragging)
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                // Find the index of the active pointer and fetch its position
                final int pointerIndex =
                        MotionEventCompat.findPointerIndex(ev, mActivePointerId);

                final float x = MotionEventCompat.getX(ev, pointerIndex);
                final float y = MotionEventCompat.getY(ev, pointerIndex);

                // Calculate the distance moved
                final float dx = x - mLastTouchX;
                final float dy = y - mLastTouchY;

                translateX += dx;
                translateY += dy;

                invalidate();

                // Remember this touch position for the next move event
                mLastTouchX = x;
                mLastTouchY = y;

                break;
            }

            case MotionEvent.ACTION_UP: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }
            case MotionEvent.ACTION_POINTER_UP: {

                final int pointerIndex = MotionEventCompat.getActionIndex(ev);
                final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);

                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchX = MotionEventCompat.getX(ev, newPointerIndex);
                    mLastTouchY = MotionEventCompat.getY(ev, newPointerIndex);
                    mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
                }
                break;
            }
        }
    }


    // scaling
    private class ScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            float currentScale = detector.getScaleFactor();
            //  Log.d("hhh", "onScale: " + mScaleFactor + " scalefactor " + detector.getScaleFactor());


            mScaleFactor += (currentScale - mPreviousScale);


            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(.7f, Math.min(mScaleFactor, 3.0f));

            scaleX = mScaleFactor;
            scaleY = mScaleFactor;


            scaleXP = getWidth() / 2;
            scaleYP = getHeight() / 2;

            mPreviousScale = currentScale;
            invalidate();


            return super.onScale(detector);
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {

            mPreviousScale = 1.f;
            super.onScaleEnd(detector);
        }
    }


}
