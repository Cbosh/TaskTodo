package com.mrbreak.todo.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.mrbreak.todo.R;
import com.wang.avi.AVLoadingIndicatorView;

public class LoaderActivity extends AppCompatActivity {

    private AVLoadingIndicatorView avi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loader);
        setStatusBarColor();
        avi = findViewById(R.id.avi);
        startAnim();
        stopAnim();
    }

    private void stopAnim() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                avi.hide();
                dismissLoader();

            }
        }, 2000);
    }

    void startAnim() {
        avi.smoothToShow();
    }

    private void dismissLoader() {
        Intent Intent = new Intent(this, MainActivity.class);
        startActivity(Intent);
    }

    private void setStatusBarColor() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.teal));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
}
