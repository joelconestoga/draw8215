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

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_SHOW_DRAWINGS = 3;

    RelativeLayout drawingArea;
    DrawingView drawingView;

    String photoFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupButtons();
        setupPaint();
    }

    private void setupPaint() {
        drawingArea = (RelativeLayout) findViewById(R.id.drawingArea);
        drawingView = new DrawingView(this);
        drawingArea.addView(drawingView);
    }

    private void setupButtons() {
        final FloatingActionMenu fabMenu = (FloatingActionMenu) findViewById(R.id.fabMenu);

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

        FloatingActionButton fabSave = (FloatingActionButton) findViewById(R.id.fabSave);
        fabSave.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fabMenu.close(true);
                        drawingView.saveCanvasImage(photoFilePath);
                    }
                }
        );

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
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(getPackageManager()) == null)
            return;

        File photoFile = ImageUtils.createPhotoEmptyFile(this);
        photoFilePath = photoFile.getAbsolutePath();

        Uri photoURI = ImageUtils.getUri(this, photoFile);

        ImageUtils.grantUriPermissions(this, intent, photoURI);

        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoURI);

        startActivityForResult(intent, REQUEST_TAKE_PHOTO);
    }

    private void openDrawings() {
        Intent intent = new Intent(MainActivity.this, SliderActivity.class);
        startActivityForResult(intent, REQUEST_SHOW_DRAWINGS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:
                Bitmap photo = ImageUtils.retrieveImageFromFile(photoFilePath,
                        drawingArea.getWidth(), drawingArea.getHeight());
                photo = ImageUtils.rotateImage(photoFilePath, photo);
                drawingView.setCanvasImage(photo);
                break;
            case REQUEST_SHOW_DRAWINGS:
                break;
            default:
                break;
        }
    }

}
