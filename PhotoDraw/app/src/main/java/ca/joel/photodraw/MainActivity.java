package ca.joel.photodraw;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_TAKE_GALLERY = 2;

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

    private void setupButtons() {
        final FloatingActionMenu fabMenu = (FloatingActionMenu) findViewById(R.id.fabMenu);

        FloatingActionButton fabCamera = (FloatingActionButton) findViewById(R.id.fabCamera);
        fabCamera.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openCamera();
                    fabMenu.close(true);
                }
            }
        );

        FloatingActionButton fabGallery = (FloatingActionButton) findViewById(R.id.fabGallery);
        fabGallery.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openGallery();
                        fabMenu.close(true);
                    }
                }
        );
    }

    private void setupPaint() {
        drawingArea = (RelativeLayout) findViewById(R.id.drawingArea);
        drawingView = new DrawingView(this);
        drawingArea.addView(drawingView);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(getPackageManager()) == null)
            return;

        File photoFile = createPhotoEmptyFile();
        photoFilePath = photoFile.getAbsolutePath();

        Uri photoURI = FileProvider.getUriForFile(this,
                "ca.joel.photodraw.fileprovider", photoFile);

        grantUriPermissions(intent, photoURI);

        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoURI);

        startActivityForResult(intent, REQUEST_TAKE_PHOTO);
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        startActivityForResult(gallery, REQUEST_TAKE_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:
                Bitmap photo = retrieveImageFromFile(photoFilePath);
                photo = rotateImage(photoFilePath, photo);
                drawingView.setCanvasImage(photo);
                break;
            case REQUEST_TAKE_GALLERY:
                Toast.makeText(this, "galeria a a a a...", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }

    private void grantUriPermissions(Intent intent, Uri photoURI) {
        List<ResolveInfo> resolvedIntentActivities = this.getPackageManager().
                queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        for (ResolveInfo resolvedIntentInfo : resolvedIntentActivities) {
            String packageName = resolvedIntentInfo.activityInfo.packageName;
            this.grantUriPermission(packageName, photoURI,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
                            Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
    }

    private File createPhotoEmptyFile() {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CANADA).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;

        try {
            image = File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    private Bitmap retrieveImageFromFile(String photoPath) {
        int targetW = drawingArea.getWidth();
        int targetH = drawingArea.getHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(photoPath, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        return BitmapFactory.decodeFile(photoPath, bmOptions);
    }

    private Bitmap rotateImage(String photoPath, Bitmap photo) {

        ExifInterface ei;
        int orientation = 0;

        try {
            ei = new ExifInterface(photoPath);
            orientation = ei.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
        } catch (IOException e) {
            e.printStackTrace();
        }

        switch(orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                photo = doRotate(photo, 90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                photo = doRotate(photo, 180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                photo = doRotate(photo, 270);
                break;

            case ExifInterface.ORIENTATION_NORMAL:

            default:
                break;
        }
        return photo;
    }

    public static Bitmap doRotate(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth() - 100, source.getHeight(),
                matrix, true);
    }

}
