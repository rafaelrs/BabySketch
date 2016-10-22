package ru.rafaelrs.babysketch;

import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;

import java.util.concurrent.TimeUnit;

/**
 * Created with Android Studio
 * User: rafaelrs
 * Date: 22.10.16
 * To change this template use File | Settings | File Templates.
 */

public abstract class VeryLongClickListener implements View.OnTouchListener {

    private long longClickDuration = TimeUnit.NANOSECONDS.convert(5, TimeUnit.SECONDS);
    private volatile boolean isLongPress = false;
    private Thread waitingThread = null;

    public abstract void doAction();

    @Override
    public boolean onTouch(final View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            v.setPressed(true);
            isLongPress = true;
            final Handler handler = new Handler();
            waitingThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    long startTime = System.nanoTime();
                    ToneGenerator toneGen = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                    while (System.nanoTime() - startTime < longClickDuration && isLongPress) {
                        try {
                            Thread.sleep(500);
                        } catch (Exception e) {
                        }

                        toneGen.startTone(ToneGenerator.TONE_CDMA_PIP, 100);
                    }
                    toneGen.release();

                    if (isLongPress) {
                        Vibrator vibrator = (Vibrator) v.getContext().getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(100);

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                doAction();
                            }
                        });
                    }
                }
            });
            waitingThread.start();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (waitingThread != null && waitingThread.isAlive()) {
                waitingThread.interrupt();
            }
            waitingThread = null;

            v.setPressed(false);
            isLongPress = false;
        }
        return true;
    }
}
