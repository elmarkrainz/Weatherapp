package at.fhj.mad.weatherapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Demonstration of a Service,
 *
 * This services asks the weatherAPI in
 *
 * @author EKrainz
 */
public class WeatherService extends Service{

    private int counter=0;
    private String sUrl;
    private String weatherStr;

    private int minutesToWait=1;


    @Override
    public IBinder onBind(Intent intent) {
        return null;

    }

    public void onCreate() {
        super.onCreate();

        // ---------log
        Log.i("WEATHER SERVICE", "Service created ");


        // get Data from user Settings & creat query String

        SharedPreferences prefs = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        sUrl = IConstansts.API_URL + "q=" ;
        sUrl= sUrl + prefs.getString("hometown","vienna");
        sUrl = sUrl + "&appid="+ IConstansts.API_KEY;

        checkWeather();

    }

    /**
     * this method calls the API in the service
     */
    private void checkWeather() {
        Log.i("WEATHER SERVICE", "Service do something ");
    //    while (counter<5){


            new CountDownTimer(minutesToWait*60000, 60000) {

                public void onTick(long millisUntilFinished) {
                    Log.i("WEATHER SERVICE", "Service CountDownTimer Tick ");
                }

                public void onFinish() {
                    Log.i("WEATHER SERVICE", "Service CountDownTimer done ");

                    // call httphelper and stuff
                    HttpHelper helper = new HttpHelper();

                    // set the callback with inner class
                    helper.setCallback(new ICallback() {
                        @Override
                        public void handleJSonString(String jsonString) {
                            try {
                                JSONObject jsonObject = new JSONObject(jsonString);
                                JSONArray ja = jsonObject.getJSONArray("weather");
                                weatherStr = ja.getJSONObject(0).getString("main") + ", " + ja.getJSONObject(0).getString("description");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Toast.makeText(WeatherService.this, "check Weather: " + weatherStr, Toast.LENGTH_LONG)
                                    .show();

                        }
                    });



                    // check the weather 3 times
                    counter++;

                    if (counter == 3){
                        stopSelf();
                    }
                    else{
                        checkWeather();
                    }

                }
            }.start();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.i("WEATHER SERVICE", "Service destroyed ");
    }
}
