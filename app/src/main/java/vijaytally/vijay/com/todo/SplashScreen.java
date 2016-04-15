package vijaytally.vijay.com.todo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Thread thread = new Thread(){
            public void run(){
                try {
                    sleep(3*1000);
                    startActivity(new Intent(SplashScreen.this,Todo.class));
                    finish();
                } catch (Exception e) {}
            }
        };
        thread.start();
    }

}
