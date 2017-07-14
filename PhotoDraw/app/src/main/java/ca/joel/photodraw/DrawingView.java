package ca.joel.photodraw;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import java.io.File;
import java.io.FileOutputStream;

public class DrawingView extends View {

    private static final float TOUCH_TOLERANCE = 4;

    private float x, y;

    private Canvas canvas;

    private Bitmap canvasImage;
    private String canvasText = "";

    private Paint draw;
    private Paint roundBrush;
    private Paint canvasWriter;

    private Path drawPath;
    private Path roundBrushPath;

    private boolean isDrawing = true;

    public DrawingView(Context c) {
        super(c);

        setupDraw();
        setupBrush();
        setupWriter();
    }

    public void setCanvasImage(Bitmap photo) {
        canvasImage = photo;
        canvas.drawBitmap(canvasImage, 0, 0, null);
        canvas.setBitmap(canvasImage);
    }

    public void saveCanvasImage(String photoFilePath) {
        File file = new File(photoFilePath);
        try {
            canvasImage.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDrawing() {
        isDrawing = true;
        canvasText = "";
    }

    public void setWriting() {
        isDrawing = false;
        createAlertForUserInput();
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

    private void setupWriter() {
        canvasWriter = new Paint();
        canvasWriter.setStyle(Paint.Style.FILL);
        canvasWriter.setStrokeWidth(1);
        canvasWriter.setColor(Color.MAGENTA);
        canvasWriter.setTextSize(100);
        canvasWriter.setAntiAlias(true);
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

        if (isDrawing) {
            canvas.drawPath(drawPath, draw);
            canvas.drawPath(roundBrushPath, roundBrush);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(eventX, eventY);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(eventX, eventY);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }

        if (!isDrawing && !"".equals(canvasText))
            canvas.drawText(canvasText, x, y, canvasWriter);

        return true;
    }

    private void touch_start(float x, float y) {
        drawPath.reset();
        drawPath.moveTo(x, y);
        this.x = x;
        this.y = y;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - this.x);
        float dy = Math.abs(y - this.y);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            drawPath.quadTo(this.x, this.y, (x + this.x) / 2, (y + this.y) / 2);
            this.x = x;
            this.y = y;

            roundBrushPath.reset();
            roundBrushPath.addCircle(this.x, this.y, 30, Path.Direction.CW);
        }
    }

    private void touch_up() {

        if (!isDrawing)
            return;

        drawPath.lineTo(x, y);
        roundBrushPath.reset();
        // commit the path to our offscreen
        canvas.drawPath(drawPath, draw);
        // kill this so we don't double draw
        drawPath.reset();
    }

    private void createAlertForUserInput() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

        //Alert's title and label
        alert.setTitle("TEXT");
        alert.setCancelable(false);

        //Setup the Alert Edit for the user's input
        final EditText newItem = new EditText(getContext());
        alert.setView(newItem);

        //Setup the Alert Ok button
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                canvasText = newItem.getText().toString();
            }
        });

        //Setup the Alert Cancel button
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        //Show the alert
        alert.show();
    }

}
