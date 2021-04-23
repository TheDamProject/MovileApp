package com.jotamarti.golocal.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import com.jotamarti.golocal.App;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

public class ImageUtil {

    private static final String TAG = "ImageUtil";
    public enum IMAGE_TYPE {
        CLIENT,
        SHOP,
        POST
    }

    public static String UriToBase64(Uri uri, IMAGE_TYPE image_type){
        String imageBase64 = "";
        try {
            Bitmap image = BitmapFactory.decodeStream(App.getContext().getContentResolver().openInputStream(uri));
            Bitmap scaledImage = null;
            if(image_type == IMAGE_TYPE.CLIENT){
                scaledImage = Bitmap.createScaledBitmap(image, 768, 768, true);
            }
            if(image_type == IMAGE_TYPE.SHOP){
                scaledImage = Bitmap.createScaledBitmap(image, 768, 384, true);
            }
            if(image_type == IMAGE_TYPE.POST) {
                scaledImage = Bitmap.createScaledBitmap(image, 1536, 384, true);
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            scaledImage.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            imageBase64 = "data:image/jpeg;base64," + Base64.encodeToString(byteArray, Base64.NO_WRAP);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return imageBase64;
    }
}
