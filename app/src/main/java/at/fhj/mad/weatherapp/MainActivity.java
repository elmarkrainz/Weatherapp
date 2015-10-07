package at.fhj.mad.weatherapp;

import android.app.Activity;
import android.content.SharedPreferences;
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

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Main Screen:
 *
 * simple UI, Request the weather of a city
 *
 * * @author EKrainz
 */
public class MainActivity extends Activity {

    final private String WEATHERAPP="Settings";
    final private String HOME="hometown";

    private TextView output;
    private EditText inputCity;

    // to save the city
    private SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get the objects form the xml definition
        inputCity = (EditText) findViewById(R.id.txtCity);
        output = (TextView) findViewById(R.id.txtResponse);


        //load the last city form your shared preferenes (xml file in App)
        prefs = getSharedPreferences(WEATHERAPP,0);
        inputCity.setText(prefs.getString(HOME, "graz"));
    }

    /**
     * this method is called wenn clickeing the getwether button (see xml layout)
     * @param v view is the calling UI component
     */
    public void getWeather(View v) {

        // create a Query string e.g.         http://api.openweathermap.org/data/2.5/weather?q=kapfenberg

        String sUrl = "    http://api.openweathermap.org/data/2.5/weather?q=";
        sUrl = sUrl + inputCity.getText().toString();

        // for Testing: show url for testing in the output textview
        //output.setText(url);


        // create obj of Httphelper -> is asnc task

        HttpHelper helper = new HttpHelper();
        helper.execute(sUrl);  // the url is sent to the do-in-background method  use params[0]


        // save city in preferences for the next time
        SharedPreferences.Editor edit = prefs.edit();  //editor to update the values
        edit.putString(HOME, inputCity.getText().toString());
        edit.commit();

    }


    // http with nested class

    // asyncTask has 3 generic parameter
    // 1. the type of the execute method == input of do-in-Backgroudn
    // 2. the return value of the do-in-background = input parameter of onpostexecute
    // 3. type of the progressindicator

    /**
     * Perform internet operation in asyn Task
     *
     * HttpHelper as nested class, parent class Async Task
     * http://developer.android.com/reference/android/os/AsyncTask.html
     *
     * AsyncTask has 3 generic parameter
     * 1. the type of the execute method == input of do-in-Backgroudn
     * 2. type of the progressindicator
     * 3. the return value of the do-in-background = input parameter of onpostexecute
     *
     * @author EKrainz
     *
     */
    private class HttpHelper extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            // create Http Client & Co

            StringBuilder out = new StringBuilder();
            try {
                //for Testing: Hardcoded URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=kapfenberg");

                // get the string parameter from execute()
                URL url = new URL(params[0]);

                // creat Urlconnection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                // read inputstrem
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null) {
                    out.append(line);
                }
                Log.i("INTERNET", out.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }

            return out.toString(); // return of do in background method is input paramet od onpostexecude method
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            // handle the result of the do-in-background-method

            //For Testing: update the UI with json String
           // MainActivity.this.output.setText(s);

            // e.g. parse Json  & update UI
            //
            try {
                JSONObject jsonObject  = new JSONObject(s);
                JSONArray ja =jsonObject.getJSONArray("weather");

                String weatherStr =ja.getJSONObject(0).getString("main") + ", "+ ja.getJSONObject(0).getString("description");
                MainActivity.this.output.setText(weatherStr);

                // todo update weather infos:-)

            } catch (JSONException e){
               e.printStackTrace();
            }



        }
    }


}
