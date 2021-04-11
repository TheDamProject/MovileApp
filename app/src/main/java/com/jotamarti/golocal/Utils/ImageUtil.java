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
        SHOP
    }

    public static String UriToBase64(Uri uri, IMAGE_TYPE image_type){
        String imageBase64 = "";
        try {
            Bitmap image = BitmapFactory.decodeStream(App.getContext().getContentResolver().openInputStream(uri));
            Bitmap scaledImage;
            if(image_type == IMAGE_TYPE.CLIENT){
                scaledImage = Bitmap.createScaledBitmap(image, 768, 768, true);
            } else {
                scaledImage = Bitmap.createScaledBitmap(image, 768, 384, true);
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
