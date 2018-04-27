package bing.bulb3;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;

public class welcome extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        getSupportActionBar().hide();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                startActivity(new Intent().setClass(welcome.this, MainActivity.class));
                finish();
            }
        }, 1500);
    }
}
