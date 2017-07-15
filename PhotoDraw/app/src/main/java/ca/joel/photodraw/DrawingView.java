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

//Java class with all logic for drawing/displaying
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

    //Constructor
    public DrawingView(Context c) {
        super(c);

        //Initialize drawing components
        setupDraw();
        setupBrush();
        setupWriter();
    }

    //Set the photo in the canvas
    public void setCanvasImage(Bitmap photo) {
        canvasImage = photo;
        canvas.drawBitmap(canvasImage, 0, 0, null);
        canvas.setBitmap(canvasImage);
    }

    //Save the photo into a file
    public void saveCanvasImage(String photoFilePath) {
        File file = new File(photoFilePath);
        try {
            canvasImage.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Set "DRAWING" mode
    public void setDrawing() {
        isDrawing = true;
        canvasText = "";
    }

    //Set "WRITING" mode
    public void setWriting() {
        isDrawing = false;
        //Request user input for text
        createAlertForUserInput();
    }

    //Setup the drawing details
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

    //Setup the round brush
    private void setupBrush() {
        roundBrush = new Paint();
        roundBrush.setAntiAlias(true);
        roundBrush.setColor(Color.BLUE);
        roundBrush.setStyle(Paint.Style.STROKE);
        roundBrush.setStrokeJoin(Paint.Join.MITER);
        roundBrush.setStrokeWidth(4f);

        roundBrushPath = new Path();
    }

    //Setup the writer for text input
    private void setupWriter() {
        canvasWriter = new Paint();
        canvasWriter.setStyle(Paint.Style.FILL);
        canvasWriter.setStrokeWidth(1);
        canvasWriter.setColor(Color.MAGENTA);
        canvasWriter.setTextSize(100);
        canvasWriter.setAntiAlias(true);
    }

    //Setup canvas on resizing
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasImage = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(canvasImage);
    }

    //Draw event, where drawing logic happens
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //Draw the background
        canvas.drawBitmap(canvasImage, 0, 0, draw);

        //Draw the path and the round brush
        if (isDrawing) {
            canvas.drawPath(drawPath, draw);
            canvas.drawPath(roundBrushPath, roundBrush);
        }
    }

    //Handling the user touch, to display live drawing
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();

        //Handle press, move and release on the screen
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

        //Write the text at the touched position
        if (!isDrawing && !"".equals(canvasText))
            canvas.drawText(canvasText, x, y, canvasWriter);

        return true;
    }

    //Handling when the touch starts
    private void touch_start(float x, float y) {
        drawPath.reset();
        drawPath.moveTo(x, y);
        this.x = x;
        this.y = y;
    }

    //Handling when the touch moves, while pressed
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

    //Handling when the touch is released
    private void touch_up() {

        if (!isDrawing)
            return;

        drawPath.lineTo(x, y);
        roundBrushPath.reset();
        canvas.drawPath(drawPath, draw);
        drawPath.reset();
    }

    //Create a Dialog for user text input
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
