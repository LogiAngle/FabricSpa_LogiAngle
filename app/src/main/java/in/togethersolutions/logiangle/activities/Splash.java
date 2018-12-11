package in.togethersolutions.logiangle.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import in.togethersolutions.logiangle.R;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class Splash extends AppCompatActivity {

    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash);
        imageView = (ImageView) findViewById(R.id.text);
        Thread timer = new Thread() {
            public void run() {
                Thread timer = new Thread() {
                    public void run() {
                        try {
                            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
                            imageView.startAnimation(animation);
                            sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            Intent i = new Intent(Splash.this, LoginActivity.class);
                            startActivity(i);


                        }
                    }
                };
                timer.start();
            }
        };
        timer.start();

    }
}
