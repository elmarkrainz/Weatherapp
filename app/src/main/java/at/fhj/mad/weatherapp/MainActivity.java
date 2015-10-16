package at.fhj.mad.weatherapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Main Screen:
 * <p/>
 * simple UI, Request the weather of a city
 * <p/>
 * * @author EKrainz
 */
public class MainActivity extends Activity implements ICallback {

    final private String WEATHERAPP = "Settings";
    final private String HOME = "hometown";

    final private String API_URL = "http://api.openweathermap.org/data/2.5/weather?";
    final private String API_KEY = "&appid=1c108b58fb003a8e3c60132638252ad5";

    final private String FILENAME = "lastresult.json";


    private TextView output;
    private EditText inputCity;

    // to save the city
    private SharedPreferences prefs;

    //location
    private LocationManager locationManager;
    private LocationListener locList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get the objects form the xml definition
        inputCity = (EditText) findViewById(R.id.txtCity);
        output = (TextView) findViewById(R.id.txtResponse);


        //load the last city form your shared preferenes (xml file in App)
        prefs = getSharedPreferences(WEATHERAPP, 0);
        inputCity.setText(prefs.getString(HOME, "graz"));


        // load last JSON response from File
        try {
            FileInputStream fis = openFileInput(FILENAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            isr.close();
            fis.close();

            // output Json in Toast
            Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();
            // TODO show last resulet in Textview

        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();

        //maybe we should not ask for GPS any more
        if (locationManager != null) {
            locationManager.removeUpdates(locList);
        }
    }

    /**
     * this method is called wenn clickeing the getwether button (see xml layout)
     *
     * @param v view is the calling UI component
     */
    public void getWeather(View v) {

        // create a Query string e.g.

        // by city
        // http://api.openweathermap.org/data/2.5/weather?q=kapfenberg

        String sUrl = API_URL + "q=" + inputCity.getText().toString();


        // by lat/long
        // api.openweathermap.org/data/2.5/weather?lat=35&lon=139


        // add API Key
        sUrl = sUrl + API_KEY;

        // for Testing: show url for testing in the output textview
        //output.setText(url);


        // create obj of Httphelper -> is asnc task
        HttpHelper helper = new HttpHelper();

        // set the callback
        helper.setCallback(this);


        helper.execute(sUrl);  // the url is sent to the do-in-background method  use params[0]


        // save city in preferences for the next time
        SharedPreferences.Editor edit = prefs.edit();  //editor to update the values
        edit.putString(HOME, inputCity.getText().toString());
        edit.commit();


    }


    public void getPosition(View v) {

        //get Location Manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // add location listener

        locList = new LocList();
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 1, locList);

        // remove location listern  but NOT too early
        //locationManager.removeUpdates(locList);

        //DONT forget the PERMISSION!!!!!
    }


    // Location Listener with Nested class

    /**
     * inner class for location listening
     */
    private class LocList implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {

            Log.i("LOCATION", location.getLatitude() + "," + location.getLongitude());

            EditText position = (EditText) findViewById(R.id.txtposition);

            position.setText(location.getLatitude() + ", " + location.getLongitude());

        }


        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        ;
    }





    @Override
    public void handleJSonString(String jsonString) {

        // e.g. parse Json  & update UI
        //
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray ja = jsonObject.getJSONArray("weather");
            String weatherStr = ja.getJSONObject(0).getString("main") + ", " + ja.getJSONObject(0).getString("description");
            this.output.setText(weatherStr);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
