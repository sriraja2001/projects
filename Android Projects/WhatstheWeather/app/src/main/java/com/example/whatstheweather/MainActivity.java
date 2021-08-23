package com.example.whatstheweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText cityName;
    TextView resultTextView;

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection httpURLConnection = null;
            try {
                url = new URL(urls[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }


        }

        @Override
        protected void onPostExecute(String s) {
            if (s == null) {
                resultTextView.setText("");
                Toast.makeText(MainActivity.this, "Could not find Weather (Check the Input/Internet)", Toast.LENGTH_SHORT).show();
            } else {


                super.onPostExecute(s);
                Log.i("JSON", s);
                try {
                    JSONObject main = new JSONObject(s);
                    JSONArray weather = main.getJSONArray("weather");
                    String message = "";
                    for (int i = 0; i < weather.length(); i++) {
                        String mainWeather = weather.getJSONObject(i).getString("main");
                        String weatherDescription = weather.getJSONObject(i).getString("description");
                        message += mainWeather + " : " + weatherDescription + "\n\r" + "\n\r";
                    }
                    JSONObject temperature = main.getJSONObject("main");
                    String temp = temperature.getString("temp");
                    message += "Temperature :" + temp+ "°C";

                    if(message!="") {
                        resultTextView.setText(message);
                    }else{
                        Toast.makeText(MainActivity.this, "Could not find Weather (Check the Input/Internet)", Toast.LENGTH_SHORT).show();
                    }




                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

        public void getWeather(View v) {
            String city = cityName.getText().toString();
            try {
                String encodedCity = URLEncoder.encode(city, "UTF-8");
                DownloadTask downloadTask = new DownloadTask();
                downloadTask.execute("api.openweathermap.org/data/2.5/weather?q="+city+"&appid=3a3e9fce290073fbaab25b957dc7c616");
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(cityName.getWindowToken(), 0);
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Could not find Weather (Check the Input/Internet)", Toast.LENGTH_SHORT).show();
            }

        }


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            cityName = (EditText) findViewById(R.id.cityEditText);
            resultTextView = (TextView) findViewById(R.id.resultTextView);


        }
    }

