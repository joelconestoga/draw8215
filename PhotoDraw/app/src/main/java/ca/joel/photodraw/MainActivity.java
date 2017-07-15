package ca.joel.photodraw;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.io.File;

//Java class for the main activity logic
public class MainActivity extends AppCompatActivity {

    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_SHOW_DRAWINGS = 3;

    //Layout to receive the drawing area
    RelativeLayout drawingArea;
    //Java class with all logic for drawing/displaying
    DrawingView drawingView;

    String photoFilePath = "";

    //App initialization, where the execution starts
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Code auto-generated, just kept unchanged
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setup screen components
        setupButtons();
        setupPaint();
    }

    //Setup the drawing class inside the layout area
    private void setupPaint() {
        drawingArea = (RelativeLayout) findViewById(R.id.drawingArea);
        drawingView = new DrawingView(this);
        drawingArea.addView(drawingView);
    }

    //Setup all floating buttons available
    private void setupButtons() {
        final FloatingActionMenu fabMenu = (FloatingActionMenu) findViewById(R.id.fabMenu);
        final FloatingActionMenu fabTools = (FloatingActionMenu) findViewById(R.id.fabTools);

        //Open camera on click
        FloatingActionButton fabCamera = (FloatingActionButton) findViewById(R.id.fabCamera);
        fabCamera.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fabMenu.close(true);
                    openCamera();
                }
            }
        );

        //Save the image on click
        FloatingActionButton fabSave = (FloatingActionButton) findViewById(R.id.fabSave);
        fabSave.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fabMenu.close(true);
                        saveCanvasImage();
                    }
                }
        );

        //Open all my drawings on click
        FloatingActionButton fabOpen = (FloatingActionButton) findViewById(R.id.fabOpen);
        fabOpen.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fabMenu.close(false);
                        openDrawings();
                    }
                }
        );

        //Set "WRITING" feature on click
        FloatingActionButton fabText = (FloatingActionButton) findViewById(R.id.fabText);
        fabText.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fabTools.close(false);
                        drawingView.setWriting();
                    }
                }
        );

        //Set "DRAWING" feature on click
        FloatingActionButton fabBrush = (FloatingActionButton) findViewById(R.id.fabBrush);
        fabBrush.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fabTools.close(false);
                        drawingView.setDrawing();
                    }
                }
        );
    }

    //Opening the camera to take a picture
    private void openCamera() {

        //Create the camera specific intent
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //Make sure intent is all right
        if (intent.resolveActivity(getPackageManager()) == null)
            return;

        //Create a temp file to receive the photo
        File photoFile = ImageUtils.createPhotoEmptyFile(this);
        photoFilePath = photoFile.getAbsolutePath();

        //Get a URI for the file
        Uri photoURI = ImageUtils.getUri(this, photoFile);

        //Give all permissions to the intent
        ImageUtils.grantUriPermissions(this, intent, photoURI);

        //Pass the file URI to the intent so the camera can place the photo on it
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoURI);

        //Call the camera
        startActivityForResult(intent, REQUEST_TAKE_PHOTO);
    }

    //Opening all my saved drawings on another activity, the SliderActivity
    private void openDrawings() {
        Intent intent = new Intent(MainActivity.this, SliderActivity.class);
        startActivityForResult(intent, REQUEST_SHOW_DRAWINGS);
    }

    //Handling callbacks from all intents created
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Safe check
        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:

                //Retrieve the photo
                Bitmap photo = ImageUtils.retrieveImageFromFile(photoFilePath,
                        drawingArea.getWidth(), drawingArea.getHeight());

                //Rotate the photo
                photo = ImageUtils.rotateImage(photoFilePath, photo);

                //Set the photo onto the canvas
                drawingView.setCanvasImage(photo);
                break;

            case REQUEST_SHOW_DRAWINGS:
                break;
            default:
                break;
        }
    }

    //Saving the current draw
    private void saveCanvasImage() {

        //If no photo was taken, create a new file for the draw
        if ("".equals(photoFilePath)) {
            File photoFile = ImageUtils.createPhotoEmptyFile(this);
            photoFilePath = photoFile.getAbsolutePath();
        }

        //Save the canvas image into the file
        drawingView.saveCanvasImage(photoFilePath);
    }
}
