package com.android.lukaspetersson.fallingkitten;



import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.media.AudioManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.SharedPreferences.Editor;
import android.os.*;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.LeaderboardsClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Timer;
import java.util.TimerTask;


public class Play extends AppCompatActivity {

    private static ConstraintLayout mainLayout;
    private ConstraintLayout pauseLayout;
    private static ConstraintLayout gameOverLayout;
    public static TextView scoreLable;
    public static ImageView health1;
    public static ImageView health2;
    public static ImageView health3;
    private static TextView highScore;
    private static TextView finalScore;
    private ImageView tutorial;
    private ImageView soundSwitchView;
    private boolean SoundOn;
    static Timer tm;
    private static boolean isTimerRunning;
    private static Cat[] cats;
    public static int score;
    public static int health;
    private static int current_cat;
    public static int fall_time;
    private long spawn_time;
    public static float distance;
    public static float screen_height;
    long last_spawn;
    long last_acc;
    long pause_time;
    long resume_time_spawn;
    long resume_time_acc;
    long delay_acc;
    long delay_spawn;
    private static SharedPreferences pref;
    private static AudioManager AudioMgr;
    private boolean first_task;
    private TimerTask accelerator;
    private TimerTask spawner;

    private static final int RC_LEADERBOARD_UI = 1005;
    private static final int RC_SIGN_IN = 2005;
    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;
    private static LeaderboardsClient mLeaderboardsClient;
    private static GoogleSignInAccount account;

    private static final String LOG_TAG = Play.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        ResetValues();

        scoreLable = findViewById(R.id.score);
        health1 = findViewById(R.id.hp1);
        health2 = findViewById(R.id.hp2);
        health3 = findViewById(R.id.hp3);
        pauseLayout = (ConstraintLayout) findViewById(R.id.pause_screen);
        gameOverLayout = (ConstraintLayout) findViewById(R.id.game_over_screen);
        mainLayout = (ConstraintLayout) findViewById(R.id.main_screen);
        highScore = (TextView) findViewById(R.id.high_score_lable);
        finalScore = (TextView) findViewById(R.id.final_score_count);
        tutorial = findViewById(R.id.tutorial);
        soundSwitchView = findViewById(R.id.sound_switch);

        pref = getApplicationContext().getSharedPreferences("scorePref", MODE_PRIVATE);
        AudioMgr = (AudioManager)getSystemService(Context.AUDIO_SERVICE);


        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(Games.SCOPE_GAMES_LITE)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //get info if sound was on or off last time
        SoundOn = pref.getBoolean("sound", true);
        //set sound accordingly
        setSound();

        //populate screen with images
        health1.setImageResource(R.drawable.heart_active);
        health2.setImageResource(R.drawable.heart_active);
        health3.setImageResource(R.drawable.heart_active);
        setBackground();

        //initialize cat object aray
        cats = new Cat[10];
        for (int i = 0; i < 10; i++){
            cats[i] = new Cat();
            //link cat object to a image view
            cats[i].image = findViewById(getResources().getIdentifier("cat" + i, "id", getPackageName()));
        }

        //get screen height
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screen_height = size.y;

        //get screen independant pixels
        distance = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());

        //animation for totorial, start game when done
        tutorial.setX((int)((size.x/2)-1.2*distance));
        tutorial.animate().xBy(size.x).setDuration(1000).setStartDelay(1000).start();

        StartGame();
        findViewById(R.id.pause_btn).setVisibility(View.VISIBLE);

    }


    public void ResetValues(){
        tm=null;
        isTimerRunning = false;
        cats = null;
        SoundOn = true;
        score = 0;
        health = 3;
        current_cat = 0;
        fall_time = 3200;
        spawn_time = 650;
        distance = 0;
        last_spawn=0;
        last_acc=0;
        pause_time = 0;
        resume_time_spawn=0;
        resume_time_acc=0;
        delay_acc=0;
        delay_spawn=0;
        first_task = true;
    }

    private void StartGame(){

        //spawn cats on schedule
        final Handler handler = new Handler();
        spawner = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        resume_time_spawn=0;
                            //record time of spawn
                            last_spawn = System.currentTimeMillis();
                            cats[current_cat % 10].spawn();
                            current_cat++;
                    }
                });
            }
        };

        //accelerate spawn rate and speed on schedule
        accelerator = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        //every 8th score simulate pause and resume to update timer schedule
                        if(score%8 == 1){
                            pause_time = System.currentTimeMillis();
                            if (isTimerRunning) {
                                tm.cancel();
                                tm.purge();
                                tm = null;
                                isTimerRunning = false;
                            }
                            StartGame();
                        }
                        resume_time_acc=0;
                            //record time of acceleration
                            last_acc= System.currentTimeMillis();
                            if(spawn_time>8){
                                spawn_time-=8;
                            }
                            if(fall_time>29){
                                fall_time-=29;
                        }
                    }

                });
            }
        };
        startSchedule();
    }

    public void startSchedule(){
        //create timer
        tm = new Timer();
        isTimerRunning = true;

        //calculate time to next delay, timer is killed when pause is pressed
        //therefore, this value is not same every time timer is created
        if(resume_time_acc==0){
            if (last_acc==0){
                delay_acc = 800;
            }else{
                delay_acc = (pause_time-last_acc);
            }
        }else{
            delay_acc += (pause_time-resume_time_acc);
        }

        if(resume_time_spawn==0){
            if (last_spawn==0){
                delay_spawn = spawn_time;
            }else{
                delay_spawn = (pause_time-last_spawn);
            }
        }else{
            delay_spawn += (pause_time-resume_time_spawn);
        }

        if(first_task){
            delay_acc = (800-1500);
            delay_spawn = (spawn_time - 1500);
            first_task=false;
        }

        //schedule tasks
        tm.schedule(accelerator, Math.abs(800-delay_acc), 800);
        tm.schedule(spawner, Math.abs(spawn_time-delay_spawn), spawn_time);

        //record when tasks started
        resume_time_spawn = System.currentTimeMillis();
        resume_time_acc = System.currentTimeMillis();
    }

    public void Rotate(View view){
        //get index of pressed cat
        String id = getResources().getResourceEntryName(view.getId());
        int index = Integer.parseInt(id.substring(id.length() - 1));

        if(cats[index].deployed) {
            //rotate the pressed cat
            cats[index].position++;
            ObjectAnimator.ofFloat(view, "rotation", (float) cats[index].position * 90f).setDuration(100).start();
        }
    }

    public void Quit (View view){
        finish();
    }
    public void Replay (View view){
        recreate();
    }

    public void soundSwitch(View view){
        SoundOn = !SoundOn;
        setSound();
    }

    private void setSound(){
        if(!SoundOn){
            soundSwitchView.setImageResource(R.drawable.round_volume_off_white_48);

            //audio is handled different in different android versions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                AudioMgr.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0);
            } else {
                AudioMgr.setStreamMute(AudioManager.STREAM_MUSIC, true);
            }
        }else{
            soundSwitchView.setImageResource(R.drawable.round_volume_up_white_48);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                AudioMgr.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, 0);
            } else {
                AudioMgr.setStreamMute(AudioManager.STREAM_MUSIC, false);
            }
        }
    }

    public void Pause (View view){
        //record time of pause
        pause_time = System.currentTimeMillis();

        cancelMovement();

        pauseLayout.setVisibility(View.VISIBLE);
        tutorial.setVisibility(View.INVISIBLE);

        //disable clicks on main screen
        EnableMainScreen(false);
    }
    public void Resume (View view){
        //start timertasks again
        StartGame();
        //resume animation of all cats
        for(int i=0; i < 10; i++){
            if(cats[i].deployed){
                cats[i].animation.resume();
            }
            if(cats[i].inEnd){
                cats[i].end_animation.resume();
            }
        }
        //enable clicks on main screen
        EnableMainScreen(true);

        pauseLayout.setVisibility(View.GONE);
    }

    public static void GameOver(){
        cancelMovement();
        gameOverLayout.setVisibility(View.VISIBLE);
        finalScore.setText(score+"");
        handleHighscore();

        //set medal
        if(score >= 40){
            finalScore.setBackgroundResource(R.drawable.final_score_gold);
        }else if (score >= 30){
            finalScore.setBackgroundResource(R.drawable.final_score_silver);
        }else if (score >= 20){
            finalScore.setBackgroundResource(R.drawable.final_score_bronze);
        }

        //disable clicks on main screen
        EnableMainScreen(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        account = GoogleSignIn.getLastSignedInAccount(this);
    }

    @Override
    public void onStop() {
        if(pauseLayout.getVisibility() != View.VISIBLE && gameOverLayout.getVisibility() != View.VISIBLE){
            Pause(findViewById(R.id.pause_btn));
        }
        //save sound preferences
        Editor editor = pref.edit();
        editor.putBoolean("sound", SoundOn);
        editor.apply();
        super.onStop();
    }
    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onDestroy() {
        cancelMovement();
        super.onDestroy();
    }

    public static void EnableMainScreen(boolean enable){
        for(int i=0;i<mainLayout.getChildCount();i++){
            View child=mainLayout.getChildAt(i);
            child.setEnabled(enable);
        }
    }

    public static void cancelMovement(){
        if (isTimerRunning) {
            tm.cancel();
            tm.purge();
            tm = null;
            isTimerRunning = false;
        }

        //wait 1ms before canceling animations
        new Thread( new Runnable() {
            public void run()  {
                try  { Thread.sleep( 1 ); }
                catch (InterruptedException ie)  {}
                //pause animation of all cats
                for(int i=0; i < 10; i++){
                    if(cats[i].deployed){
                        cats[i].animation.pause();
                    }
                    if(cats[i].inEnd){
                        cats[i].end_animation.pause();
                    }
                }
            }
        } ).start();

    }
    private static void handleHighscore(){
        Editor editor = pref.edit();
        if ( pref.getInt("high_score", 0)< score){
            editor.putInt("high_score", score);
            editor.apply();
            highScore.setText("NEW HIGH SCORE!");
        }else{
            highScore.setText("HIGH SCORE: "+ pref.getInt("high_score", 0));
        }

        if(account != null){
            mLeaderboardsClient = Games.getLeaderboardsClient(contextRefferance.getAppContext(), account);
            mLeaderboardsClient.submitScore("CgkIoNjQkPoGEAIQAA", pref.getInt("high_score", 0));
        }
    }

    public void Leaderboard (View view){
        if(account != null){
            showLeaderboard();
        }else{
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    }

    private void showLeaderboard() {

        mLeaderboardsClient = Games.getLeaderboardsClient(this, account);

        mLeaderboardsClient.getLeaderboardIntent(getString(R.string.leaderboard_id))
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {

                        startActivityForResult(intent, RC_LEADERBOARD_UI);
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            account = completedTask.getResult(ApiException.class);

            mLeaderboardsClient = Games.getLeaderboardsClient(Play.this, account);

            showLeaderboard();

        } catch (ApiException e) {
            mLeaderboardsClient = null;

            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(LOG_TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void setBackground(){
        ImageView cloud = findViewById(R.id.cloud);
        int random = (int)Math.floor(Math.random() * 12);
        //set random image
        if(random == 0){
            mainLayout.setBackgroundResource(R.drawable.mountain_polygons);
        }else if(random == 1){
            mainLayout.setBackgroundResource(R.drawable.mountains);
        }else if(random == 2){
            mainLayout.setBackgroundResource(R.drawable.pride);
        }else if(random == 3){
            mainLayout.setBackgroundResource(R.drawable.boat_sea);
            cloud.setImageResource(R.drawable.dark_clouds);
        }else if(random == 4){
            mainLayout.setBackgroundResource(R.drawable.night);
            cloud.setImageResource(R.drawable.dark_clouds);
        }else if(random == 5){
            mainLayout.setBackgroundResource(R.drawable.forest);
        }else{
            mainLayout.setBackgroundResource(R.drawable.winter_forest);
            cloud.setImageResource(R.drawable.dark_clouds);
        }
    }

}
