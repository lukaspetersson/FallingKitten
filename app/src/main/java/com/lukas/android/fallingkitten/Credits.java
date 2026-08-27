package com.lukas.android.fallingkitten;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.lukas.android.fallingkitten.R;

public class Credits extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
    }

    public void openThirdPartyNotices(View view) {
        startActivity(new Intent(this, ThirdPartyNoticesActivity.class));
    }

    public void Back (View view){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}
