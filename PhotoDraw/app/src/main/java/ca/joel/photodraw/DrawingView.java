package ca.joel.photodraw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;

import java.io.IOException;

public class DrawingView extends View {

    private static final float TOUCH_TOLERANCE = 4;

    private float mX, mY;

    private Canvas canvas;

    private Bitmap canvasImage;

    private Paint draw;
    private Paint roundBrush;

    private Path drawPath;
    private Path roundBrushPath;

    public DrawingView(Context c) {
        super(c);

        setupDraw();
        setupBrush();
    }

    public void setCanvasImage(Bitmap photo) {
        canvasImage = photo;
        canvas.drawBitmap(canvasImage, 0, 0, null);
        canvas.setBitmap(canvasImage);
    }

    private void setupDraw() {
        draw = new Paint();
        draw.setAntiAlias(true);
        draw.setDither(true);
        draw.setColor(Color.GREEN);
        draw.setStyle(Paint.Style.STROKE);
        draw.setStrokeJoin(Paint.Join.ROUND);
        draw.setStrokeCap(Paint.Cap.ROUND);
        draw.setStrokeWidth(12);

        drawPath = new Path();
    }

    private void setupBrush() {
        roundBrush = new Paint();
        roundBrush.setAntiAlias(true);
        roundBrush.setColor(Color.BLUE);
        roundBrush.setStyle(Paint.Style.STROKE);
        roundBrush.setStrokeJoin(Paint.Join.MITER);
        roundBrush.setStrokeWidth(4f);

        roundBrushPath = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        canvasImage = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(canvasImage);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(canvasImage, 0, 0, draw);
        canvas.drawPath(drawPath, draw);
        canvas.drawPath(roundBrushPath, roundBrush);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
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

    private void touch_start(float x, float y) {
        drawPath.reset();
        drawPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            drawPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;

            roundBrushPath.reset();
            roundBrushPath.addCircle(mX, mY, 30, Path.Direction.CW);
        }
    }

    private void touch_up() {
        drawPath.lineTo(mX, mY);
        roundBrushPath.reset();
        // commit the path to our offscreen
        canvas.drawPath(drawPath, draw);
        // kill this so we don't double draw
        drawPath.reset();
    }

}
