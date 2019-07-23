package com.sammaurya.robomuse.Views;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.sammaurya.robomuse.Layers.LaserScanRenderer;
import com.sammaurya.robomuse.Teleop;

/**
 * Custom GLSurfaceView for rendering a LaserScanLayer.
 *
 * Created by Nathaniel Stone on 3/29/16.
 */
public class LaserScanView extends GLSurfaceView {

    private static final String TAG = "LaserScanView";

    // The Renderer for this View
    private LaserScanRenderer laserScanRenderer;


    /**
     * Required Constructor.
     * @param context The parent context
     */
    public LaserScanView(Context context) {
        super(context);

        if (!isInEditMode())
            setRenderer(laserScanRenderer = new LaserScanRenderer((Teleop) getContext()));
    }

    /**
     * Standard View constructor.
     */
    public LaserScanView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (!isInEditMode())
            setRenderer(laserScanRenderer = new LaserScanRenderer((Teleop) getContext()));
    }

    /**
     * @return The renderer for this view
     */
    public LaserScanRenderer getLaserScanRenderer() {
        return laserScanRenderer;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h)
    {
        super.surfaceChanged(holder, format, w, h);

        Log.d(TAG, "surfaceChanged(" + format + ", " + w + ", " + h + ")");

        ((Teleop)getContext()).getRobotController().addLaserScanListener(laserScanRenderer);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        super.surfaceCreated(holder);

        getHolder().setFormat(PixelFormat.TRANSLUCENT);

        Log.d(TAG, "surfaceCreated(" + holder + ")");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);

        Log.d(TAG, "surfaceDestroyed(" + holder + ")");

        ((Teleop)getContext()).getRobotController().removeLaserScanListener(laserScanRenderer);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return laserScanRenderer.onTouchEvent(e);
    }
}

