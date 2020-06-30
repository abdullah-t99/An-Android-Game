package com.example.androidgame;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

public class SoundPlayer {
    private AudioAttributes audioAttributes;
    final int SOUND_POOL_MAX = 3;

    private static SoundPool soundPool;
    private static int hitGreenSound;
    private static int hitRedSound;
    private static int hitRocketSound;

    public SoundPlayer(Context context) {

        // SoundPool is deprecated in API level 21. (Lollipop)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setMaxStreams(SOUND_POOL_MAX)
                    .build();

        } else {
            soundPool = new SoundPool(SOUND_POOL_MAX, AudioManager.STREAM_MUSIC, 0);
        }

        // loading the 3 sounds files located in raw directory
        hitGreenSound = soundPool.load(context, R.raw.green, 1);  // green.raw
        hitRedSound = soundPool.load(context, R.raw.red, 1); // red.raw
        hitRocketSound = soundPool.load(context, R.raw.rocket, 1); // rocket.raw
    }

    // customisation of the sound
    public void playHitGreenSound() {
        soundPool.play(hitGreenSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void playHitRedSound() {
        soundPool.play(hitRedSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void playHitRocketSound() {
        soundPool.play(hitRocketSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }
}
