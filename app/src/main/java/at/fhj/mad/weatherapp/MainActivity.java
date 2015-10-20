package at.fhj.mad.weatherapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

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

    }


    @Override
    protected void onResume() {
        super.onResume();

        // load Settings and Saved Data

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
          //  Toast.makeText(this, parseWeather(sb.toString()), Toast.LENGTH_LONG).show();
            // TODO show last result in Textview
            this.output.setText(parseWeather(sb.toString()));

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

        // remove location listern  but NOT too early, see onPause()

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


            // get Address
            Geocoder gc = new Geocoder(MainActivity.this);
            Address address = null;
            List<Address> addresses = null;
            try {
                addresses = gc.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses.size() > 0) {
                address = addresses.get(0);
            }
            // works with 4digit zipcode
            inputCity.setText(address.getAddressLine(1).substring(5));

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

        //---Store JSON result in a File
        FileOutputStream fos = null;

        try {
            fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(jsonString.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Update UI
        this.output.setText(parseWeather(jsonString));


    }


    /**
     * Handles the Json From weather api
     *
     * @param jsonString
     * @return String output from weather
     */
    private String parseWeather(String jsonString) {
        String weatherStr = null;

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray ja = jsonObject.getJSONArray("weather");
            weatherStr = ja.getJSONObject(0).getString("main") + ", " + ja.getJSONObject(0).getString("description");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return weatherStr;
    }


    //----- menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
