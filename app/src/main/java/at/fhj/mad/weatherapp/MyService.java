package at.fhj.mad.weatherapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Super simple Service Runs
 *
 */
public class MyService extends Service {


    private int counter;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        Thread t = new Thread(new  Runnable(){
            @Override
            public void run() {
                while (counter<1000) {
                    Log.i("SERVICE", "service started");

                    // do some stuff



                    // increment counter
                    counter ++;

                    // stop Service
                    if (counter==999){
                        MyService.this.stopSelf();
                    }

                }
            }
        });
        t.start();




    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        // Service stops
        Log.i("SERVICE", "service stopped");
    }
}
