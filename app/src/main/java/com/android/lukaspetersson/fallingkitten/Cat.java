package com.android.lukaspetersson.fallingkitten;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.media.MediaPlayer;
import android.support.constraint.ConstraintLayout;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import java.io.IOException;
import pl.droidsonroids.gif.GifDrawable;


public class Cat {

    int position = 0;
    ImageView image;
    boolean deployed = false;
    boolean inEnd = false;
    float distance = 0;
    ObjectAnimator animation;
    ObjectAnimator end_animation;
    int fall_distance;
    int fall_time;

    public void spawn (){
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
        image.setY((float) -0.2*Play.screen_height);

        animate(Play.fall_time, (int)Play.distance);
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
                //if cat was correctly rotated
                if(position %4 == 0){
                    Play.score++;
                    Play.scoreLable.setText(Play.score+"");
                    end_animation.start();
                    inEnd = true;
                }else{
                    Play.health--;
                    if(Play.health == 2){
                        image.setRotation(0);
                        //set explosion gif
                        try {
                            image.setImageDrawable(new GifDrawable(contextRefferance.getAppContext().getResources(), R.drawable.boom ));
                        } catch(IOException ie) {
                            ie.printStackTrace();
                        }
                        //make gif stop
                        new Thread( new Runnable() {
                            public void run()  {
                                try  { Thread.sleep( 1100 ); }
                                catch (InterruptedException ie)  {}
                                image.setY((float) -0.2*Play.screen_height);
                            }
                        } ).start();

                        //take away a heart
                        Play.health3.setImageResource(R.drawable.heart_empty);
                        Play.health3.animate().scaleX(0f).scaleY(0f).setDuration(100).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                Play.health3.animate().scaleX(1f).scaleY(1f).setDuration(200);
                            }
                        });
                    }else if(Play.health == 1){
                        image.setRotation(0);
                        try {
                            image.setImageDrawable(new GifDrawable(contextRefferance.getAppContext().getResources(), R.drawable.boom ));
                        } catch(IOException ie) {
                            ie.printStackTrace();
                        }
                        new Thread( new Runnable() {
                            public void run()  {
                                try  { Thread.sleep( 1100 ); }
                                catch (InterruptedException ie)  {}
                                image.setY((float) -0.2*Play.screen_height);
                            }
                        } ).start();

                        Play.health2.setImageResource(R.drawable.heart_empty);
                        Play.health2.animate().scaleX(0f).scaleY(0f).setDuration(200).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                Play.health2.animate().scaleX(1f).scaleY(1f).setDuration(100);
                            }
                        });
                    }else{

                        image.setRotation(0);
                        try {
                            image.setImageDrawable(new GifDrawable(contextRefferance.getAppContext().getResources(), R.drawable.boom ));
                        } catch(IOException ie) {
                            ie.printStackTrace();
                        }
                        new Thread( new Runnable() {
                            public void run()  {
                                try  { Thread.sleep( 1100 ); }
                                catch (InterruptedException ie)  {}
                                image.setY((float) -0.2*Play.screen_height);
                            }
                        } ).start();

                        Play.health1.setImageResource(R.drawable.heart_empty);
                        Play.health1.animate().scaleX(0f).scaleY(0f).setDuration(200).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                Play.health1.animate().scaleX(1f).scaleY(1f).setDuration(100);
                            }
                        });

                        Play.GameOver();
                    }

                    //set random meuw sound
                    int randomMeow = (int)Math.floor(Math.random() * 3);
                    if(randomMeow == 0){
                        MediaPlayer.create(contextRefferance.getAppContext(), R.raw.meow1).start();
                    }else if(randomMeow == 1){
                        MediaPlayer.create(contextRefferance.getAppContext(), R.raw.meow2).start();
                    }else{
                        MediaPlayer.create(contextRefferance.getAppContext(), R.raw.meow3).start();
                    }

                }
                deployed = false;
            }
        });
        animation.start();
        deployed = true;
    }

}
