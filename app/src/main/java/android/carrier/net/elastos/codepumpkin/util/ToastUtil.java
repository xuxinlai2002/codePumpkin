package android.carrier.net.elastos.codepumpkin.util;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

    public static void showLong(final Context context,final String text){

        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context,text,Toast.LENGTH_LONG).show();
            }
        });
    }
}
