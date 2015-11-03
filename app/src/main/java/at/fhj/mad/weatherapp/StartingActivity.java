package at.fhj.mad.weatherapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Starting activity - Splash screen with image an button
 *
 * @author EKrainz
 */
public class StartingActivity extends Activity {

    private Button btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);


        btn = (Button) findViewById(R.id.buttonFoo);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StartingActivity.this, MainActivity.class);

                // start activity with intent
                startActivity(i);

            }
        });

    }


    // open 2nd activity
    public void openActivity(View v) {

        // create intent

        Intent i = new Intent(this, MainActivity.class);

        // start activity with intent
        startActivity(i);


    }

    public void startService(View v) {

        // Start the service
        //  --  test service
        //Intent i = new Intent(this, MyService.class);


        Intent i = new Intent(this, WeatherService.class);
        startService(i);

    }

    public void stopService(View v) {

        // stop the service
        Intent i = new Intent(this, MyService.class);
        stopService(i);

    }


}
