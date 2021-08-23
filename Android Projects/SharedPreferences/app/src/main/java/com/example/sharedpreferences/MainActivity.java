package com.example.sharedpreferences;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences=this.getSharedPreferences("com.example.sharedpreferences", Context.MODE_PRIVATE);
/*
        ArrayList<String> names=new ArrayList<String>();//Array list is a collection of objects of a particular data type
        names.add("Sri");
        names.add("Mia");
        names.add("Dani");
        names.add("Allie");

        try {
            sharedPreferences.edit().putString("names", ObjectSerializer.serialize(names)).apply();
            Log.i("friends", ObjectSerializer.serialize(names));

        }catch(Exception e){
            e.printStackTrace();
        }
        */
        ArrayList<String> newFriends=new ArrayList<String>();
        try {
            newFriends = (ArrayList<String>)ObjectSerializer.deserialize(sharedPreferences.getString("names", ObjectSerializer.serialize(new ArrayList<String>())));
            Log.i("new friends", newFriends.toString());
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
