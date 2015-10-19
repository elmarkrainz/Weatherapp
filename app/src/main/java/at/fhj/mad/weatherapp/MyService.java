package at.fhj.mad.weatherapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by krajn on 19/10/15.
 */
public class MyService extends Service {


    private boolean isrunning;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        // Service Starts
        isrunning = true;

        Thread t = new Thread(new  Runnable(){
            @Override
            public void run() {
                while (isrunning) {
                    Log.i("SERVICE", "service started");

                    // do some stuff

                }
            }
        });
        t.start();

        //stop itself

        // stopSelf();
    }


    @Override
    public void onDestroy() {
        isrunning=false;
        super.onDestroy();


        // Service stops
        Log.i("SERVICE", "service stopped");
    }
}
