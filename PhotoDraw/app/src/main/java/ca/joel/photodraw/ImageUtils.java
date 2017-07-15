package ca.joel.photodraw;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

//Java class with helper methods for image transformations
public class ImageUtils {

    //Create an empty file for a Photo
    public static File createPhotoEmptyFile(Context context) {

        //Create a unique name based on timestamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CANADA).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;

        try {
            image = File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    //Generate an URI for a photo file
    public static Uri getUri(Context context, File photoFile) {
        return FileProvider.getUriForFile(context,
                "ca.joel.photodraw.fileprovider", photoFile);
    }

    //Grant permissions for an URI - required for using camera and external files
    public static void grantUriPermissions(Context context, Intent intent, Uri photoURI) {
        List<ResolveInfo> resolvedIntentActivities = context.getPackageManager().
                queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        for (ResolveInfo resolvedIntentInfo : resolvedIntentActivities) {
            String packageName = resolvedIntentInfo.activityInfo.packageName;
            context.grantUriPermission(packageName, photoURI,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
                            Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
    }

    //Retrieve the actual photo saved by the camera
    public static Bitmap retrieveImageFromFile(String photoPath, int targetW, int targetH) {

        //Customize some options
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(photoPath, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        //Decode the file into a Bitmap
        return BitmapFactory.decodeFile(photoPath, bmOptions);
    }

    //Rotate the image, as cameras always take picture in landscape
    public static Bitmap rotateImage(String photoPath, Bitmap photo) {

        ExifInterface ei;
        int orientation = 0;

        //Check the phone's orientation
        try {
            ei = new ExifInterface(photoPath);
            orientation = ei.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Evaluate the orientation and rotate accordingly
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

    //Rotate the Bitmap using matrix
    private static Bitmap doRotate(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth() - 100, source.getHeight(),
                matrix, true);
    }

}
