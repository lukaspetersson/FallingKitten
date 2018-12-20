package com.lukas.android.fallingkitten;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.lukas.android.fallingkitten.R;

public class MainActivity extends AppCompatActivity {

    float width;
    ImageView movingCat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        movingCat = findViewById(R.id.moving_cat);

        //move cat outside screen
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;

        MoveCat();
    }

    private void MoveCat(){
        int random = (int)Math.floor(Math.random() * 12);
        //set random image
        if(random == 0){
            movingCat.setImageResource(R.drawable.blue1);
        }else if(random == 1){
            movingCat.setImageResource(R.drawable.blue2);
        }else if(random == 2){
            movingCat.setImageResource(R.drawable.green1);
        }else if(random == 3){
            movingCat.setImageResource(R.drawable.green2);
        }else if(random == 4){
            movingCat.setImageResource(R.drawable.red1);
        }else if(random == 5){
            movingCat.setImageResource(R.drawable.red2);
        }else if(random == 6){
            movingCat.setImageResource(R.drawable.yellow1);
        }else if(random == 7){
            movingCat.setImageResource(R.drawable.yellow2);
        }else if(random == 8){
            movingCat.setImageResource(R.drawable.sit1);
        }else if(random == 9){
            movingCat.setImageResource(R.drawable.sit2);
        }else if(random == 10){
            movingCat.setImageResource(R.drawable.stand1);
        }else{
            movingCat.setImageResource(R.drawable.stand2);
        }

        movingCat.setX(width);

        movingCat.animate()
                .x((float)-1.2*width)
                .setDuration(18000)
                .setInterpolator(new LinearInterpolator())
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        MoveCat();
                    }
                })
                .start();
    }

    public void Credits (View view){
        Intent i = new Intent(this, Credits.class);
        startActivity(i);
    }
    public void Play (View view){
        Intent i = new Intent(this, Play.class);
        startActivity(i);
    }
}
