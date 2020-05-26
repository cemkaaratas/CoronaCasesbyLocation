package com.cemkaratas.mapstryinh;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLOutput;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    RequestQueue requestQueue;
    JSONObject turkey;
    String cases;
    String selectedCountry;
    TextView deathsText;
    TextView confirmedText;
    TextView recoveredText;
    String selectedDay;
    String selectedMonth;
    String selectedYear;
    String selectedDate;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestQueue = Volley.newRequestQueue(this);
        deathsText = findViewById(R.id.deathsText);
        confirmedText = findViewById(R.id.confirmedText);
        recoveredText = findViewById(R.id.recoveredText);

        sharedPreferences = this.getSharedPreferences("MySharedPref", MODE_PRIVATE);

        SQLiteDatabase sqLiteDatabase = this.openOrCreateDatabase("LocationDatabase",MODE_PRIVATE,null);
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT country FROM countries ORDER BY id DESC LIMIT 1",null);
        Integer countryIndex = cursor.getColumnIndex("country");
        while (cursor.moveToNext()){
            selectedCountry = cursor.getString(countryIndex);
        }

        selectedDay = sharedPreferences.getString("Day","");
        selectedMonth = sharedPreferences.getString("Month","");
        selectedYear = sharedPreferences.getString("Year","");

        Integer selectedDayInt = Integer.parseInt(selectedDay);
        Integer selectedMonthInt = Integer.parseInt(selectedMonth);
        selectedDate = selectedYear + "-" + selectedMonth + "-" + selectedDay ;

        if (selectedDayInt < 10 ){
            this.selectedDay = "0" + selectedDay;
        }

        if (selectedMonthInt <10 ){
            this.selectedMonth = "0" +selectedMonth;
        }

        String status1 = "recovered";
        String status2 = "deaths";
        String status3 = "confirmed";

        selectedDate = selectedYear + "-" + selectedMonth + "-" + selectedDay ;

        parseJson(selectedCountry,status1,selectedDate);
        parseJson(selectedCountry,status2,selectedDate);
        parseJson(selectedCountry,status3,selectedDate);

    }

    private void parseJson(final String countryName, final String status, final String selectedDate) {
        String url = "https://api.covid19api.com/country/" + countryName + "/status/" + status + "?from=" + selectedDate + "T00:00:00Z&to=" + selectedDate + "T23:59:59Z";
        System.out.println(url);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        turkey = response.getJSONObject(i);
                        cases = turkey.getString("Cases");
                        if (status == "recovered"){
                            recoveredText.setText(cases);
                        };
                        if (status == "confirmed"){
                            confirmedText.setText(cases);
                        }
                        if(status == "deaths"){
                            deathsText.setText(cases);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

}
