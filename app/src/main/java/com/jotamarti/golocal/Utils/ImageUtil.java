package com.jotamarti.golocal.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

import com.jotamarti.golocal.App;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

public class ImageUtil {

    public static String UriToBase64(Uri uri){
        String imageBase64 = "";
        try {
            Bitmap image = BitmapFactory.decodeStream(App.getContext().getContentResolver().openInputStream(uri));
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            imageBase64 = "data:image/jpeg;base64," + Base64.encodeToString(byteArray, Base64.NO_WRAP);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return imageBase64;
    }
}
