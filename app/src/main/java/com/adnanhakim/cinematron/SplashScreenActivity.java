package com.adnanhakim.cinematron;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        Launcher launcher = new Launcher();
        launcher.start();
    }

    private class Launcher extends Thread {
        public void run() {
            try {
                sleep(2000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

            startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
            SplashScreenActivity.this.finish();
        }
    }
}
