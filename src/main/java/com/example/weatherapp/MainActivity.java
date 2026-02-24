package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    EditText cityInput;
    Button btnGetWeather;
    TextView weatherResult;

    String API_KEY = "6d08ebf61d21c3de8cb08769e44f1b13"; // Replace with your OpenWeatherMap API key

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityInput = findViewById(R.id.cityInput);
        btnGetWeather = findViewById(R.id.btnGetWeather);
        weatherResult = findViewById(R.id.weatherResult);

        btnGetWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = cityInput.getText().toString().trim();
                if (!city.isEmpty()) {
                    new GetWeatherTask().execute(city);
                } else {
                    weatherResult.setText("Please enter a city name.");
                }
            }
        });
    }

    private class GetWeatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String city = params[0];
            String urlString = "https://api.openweathermap.org/data/2.5/weather?q="
                    + city + "&appid=" + API_KEY + "&units=metric";

            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();
                return result.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String json) {
            if (json != null) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    JSONObject main = jsonObject.getJSONObject("main");
                    double temp = main.getDouble("temp");
                    int humidity = main.getInt("humidity");

                    String description = jsonObject.getJSONArray("weather")
                            .getJSONObject(0).getString("description");

                    String output = "Temperature: " + temp + "°C\n"
                            + "Humidity: " + humidity + "%\n"
                            + "Condition: " + description;

                    weatherResult.setText(output);

                } catch (Exception e) {
                    weatherResult.setText("Error parsing weather data.");
                }
            } else {
                weatherResult.setText("Failed to fetch weather data.");
            }
        }
    }
}
