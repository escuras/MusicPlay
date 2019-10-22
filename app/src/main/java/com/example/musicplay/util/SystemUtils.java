package com.example.musicplay.util;

import android.content.Context;
import android.os.VibrationEffect;
import android.os.Vibrator;

import static android.content.Context.VIBRATOR_SERVICE;

public class SystemUtils {

    public static void vibrate(Context context, int miliSeconds){
        Vibrator vibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(VibrationEffect.createOneShot(miliSeconds, VibrationEffect.DEFAULT_AMPLITUDE));
    }
}
