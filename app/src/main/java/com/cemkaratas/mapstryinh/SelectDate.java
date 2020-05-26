package com.cemkaratas.mapstryinh;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SelectDate extends AppCompatActivity {
    Spinner daySpinner;
    Spinner monthSpinner;
    Spinner yearSpinner;
    ArrayList<Integer> yearList = new ArrayList<>();
    ArrayList<Integer> monthList = new ArrayList<>();
    ArrayList<Integer> dayList = new ArrayList<>();
    SharedPreferences sharedPreferences;
    TextView countryText;
    String selectedCountry;
    ArrayAdapter<Integer> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_date);
        daySpinner = findViewById(R.id.daySpinner);
        monthSpinner = findViewById(R.id.monthSpinner);
        yearSpinner = findViewById(R.id.yearSpinner);
        countryText = findViewById(R.id.countryText);


        SQLiteDatabase sqLiteDatabase = this.openOrCreateDatabase("LocationDatabase",MODE_PRIVATE,null);
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT country FROM countries ORDER BY id DESC LIMIT 1",null);
        Integer countryIndex = cursor.getColumnIndex("country");
        while (cursor.moveToNext()){
            selectedCountry = cursor.getString(countryIndex);
            countryText.setText(selectedCountry.toUpperCase());
        }

        yearList.add(2020);

        for (Integer day = 1 ; day <=31 ; day++){
            dayList.add(day);
        }

        for (Integer month = 1; month <= 12 ; month++){
            monthList.add(month);
        }

        setAdapter(dayList,daySpinner,"Day");
        setAdapter(monthList,monthSpinner,"Month");
        setAdapter(yearList,yearSpinner,"Year");

    }

    public void showResults(View view) {
        Intent intenttoMain = new Intent(SelectDate.this, MainActivity.class);
        startActivity(intenttoMain);
    }

    public void setAdapter(ArrayList arrayName, Spinner spinnerName, final String putStringToSharedPrefernces){
        sharedPreferences = this.getSharedPreferences("MySharedPref", MODE_PRIVATE);
        arrayAdapter = new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_dropdown_item,arrayName);
        spinnerName.setAdapter(arrayAdapter);
        spinnerName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedValue = parent.getItemAtPosition(position).toString();
                sharedPreferences.edit().putString(putStringToSharedPrefernces,selectedValue).apply();
                System.out.println(selectedValue);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
