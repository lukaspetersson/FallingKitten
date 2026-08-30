package com.lukas.android.fallingkitten;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.lukas.android.fallingkitten.R;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import pl.droidsonroids.gif.GifDrawable;


public class Cat {

    private static final String TAG = "Cat";

    interface OutcomeListener {
        void onLanding(boolean upright);
    }

    private final ImageView image;
    private final OutcomeListener outcomeListener;
    private final Context applicationContext;

    int position = 0;
    boolean deployed = false;
    boolean inEnd = false;
    float distance = 0;
    ObjectAnimator animation;
    ObjectAnimator end_animation;
    int fall_distance;
    int fall_time;
    private float screenHeight;


    Cat(ImageView image, OutcomeListener outcomeListener) {
        this.image = image;
        this.outcomeListener = outcomeListener;
        this.applicationContext = image.getContext().getApplicationContext();
    }

    public void spawn (float screenHeight, int configuredFallTime, int configuredDistance){
        this.screenHeight = screenHeight;
        distance = 0;

        //set random rotation
        position = (int) Math.floor(Math.random() * 4);
        image.setRotation(90*position);

        int random = (int)Math.floor(Math.random() * 12);
        //set random image
        if(random == 0){
            image.setImageResource(R.drawable.blue1);
        }else if(random == 1){
            image.setImageResource(R.drawable.blue2);
        }else if(random == 2){
            image.setImageResource(R.drawable.green1);
        }else if(random == 3){
            image.setImageResource(R.drawable.green2);
        }else if(random == 4){
            image.setImageResource(R.drawable.red1);
        }else if(random == 5){
            image.setImageResource(R.drawable.red2);
        }else if(random == 6){
            image.setImageResource(R.drawable.yellow1);
        }else if(random == 7){
            image.setImageResource(R.drawable.yellow2);
        }else if(random == 8){
            image.setImageResource(R.drawable.sit1);
        }else if(random == 9){
            image.setImageResource(R.drawable.sit2);
        }else if(random == 10){
            image.setImageResource(R.drawable.stand1);
        }else{
            image.setImageResource(R.drawable.stand2);
        }

        //set random X coordinate
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) image.getLayoutParams();
        params.horizontalBias = (float) Math.random();
        image.setLayoutParams(params);

        //make image placed at top of screen
        image.setY((float) -0.2 * screenHeight);

        animate(configuredFallTime, configuredDistance);
    }

    public void animate (int t, int d){
        fall_distance=d;
        fall_time=t;

        //animation for exiting screen
        end_animation = ObjectAnimator.ofFloat(image, "translationY", fall_distance*15);
        end_animation.setDuration((int)(fall_time*0.21));
        end_animation.setInterpolator(new LinearInterpolator());
        end_animation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                inEnd = false;
            }
        });

        //animation for fall
        animation = ObjectAnimator.ofFloat(image, "translationY", fall_distance*11);
        animation.setDuration(fall_time);
        animation.setInterpolator(new LinearInterpolator());
        animation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                boolean upright = position % 4 == 0;
                if (upright) {
                    outcomeListener.onLanding(true);
                    end_animation.start();
                    inEnd = true;
                } else {
                    image.setRotation(0);
                    try {
                        image.setImageDrawable(new GifDrawable(
                                applicationContext.getResources(), R.drawable.boom));
                    } catch (IOException exception) {
                        Log.w(TAG, "Unable to load explosion animation", exception);
                    }
                    new Thread(() -> {
                        try {
                            Thread.sleep(1100);
                        } catch (InterruptedException exception) {
                            Thread.currentThread().interrupt();
                        }
                        image.setY((float) -0.2 * screenHeight);
                    }).start();
                    outcomeListener.onLanding(false);
                    int randomMeow = (int) Math.floor(Math.random() * 2);
                    playMeow(randomMeow == 0 ? R.raw.meow2 : R.raw.meow3);
                }
                deployed = false;
            }
        });
        animation.start();
        deployed = true;
    }

    private void playMeow(int soundResource) {
        final MediaPlayer player = MediaPlayer.create(
                applicationContext, soundResource);
        if (player == null) {
            return;
        }

        final AtomicBoolean released = new AtomicBoolean(false);
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                releaseOnce(mediaPlayer, released);
            }
        });
        player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
                releaseOnce(mediaPlayer, released);
                return true;
            }
        });
        try {
            player.start();
        } catch (RuntimeException exception) {
            releaseOnce(player, released);
            Log.w(TAG, "Unable to start meow playback", exception);
        }
    }

    private static void releaseOnce(MediaPlayer player, AtomicBoolean released) {
        if (released.compareAndSet(false, true)) {
            player.release();
        }
    }

}
