package com.jotamarti.golocal.Utils;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.jotamarti.golocal.R;

public class CustomToast {

    public enum mode {
        LONGER,
        SHORTER
    }

    public static void showToast(Context parentContext, String message, Enum modeReceived){
        Toast toast;
        if (modeReceived.equals(mode.SHORTER)){
            toast = Toast.makeText(parentContext, message, Toast.LENGTH_SHORT);
        } else {
            toast = Toast.makeText(parentContext, message, Toast.LENGTH_LONG);
        }
        View view = toast.getView();
        view.getBackground().setColorFilter(parentContext.getColor(R.color.background_local_dark), PorterDuff.Mode.SRC_IN);
        TextView text = (TextView) view.findViewById(android.R.id.message);
        text.setTextColor(parentContext.getColor(R.color.indian_red_700));
        toast.show();
    }
}
