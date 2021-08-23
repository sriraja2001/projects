package com.example.memorableplaces;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    static ListView listView;
    static ArrayList<String> places=new ArrayList<String>();
    static ArrayAdapter arrayAdapter;
    static  ArrayList<LatLng> locations=new ArrayList<LatLng>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView=(ListView)findViewById(R.id.listView);
        ArrayList<String> latitudes=new ArrayList<String>();
        ArrayList<String> longitudes=new ArrayList<String>();
        places.clear();
        locations.clear();
        latitudes.clear();
        longitudes.clear();

        SharedPreferences sharedPreferences=this.getSharedPreferences("com.example.memorableplaces", Context.MODE_PRIVATE);

        try{

            places=(ArrayList<String>)ObjectSerializer.deserialize(sharedPreferences.getString("places", ObjectSerializer.serialize(new ArrayList<String>())));
            latitudes=(ArrayList<String>)ObjectSerializer.deserialize(sharedPreferences.getString("lats", ObjectSerializer.serialize(new ArrayList<String>())));
            longitudes=(ArrayList<String>)ObjectSerializer.deserialize(sharedPreferences.getString("longs", ObjectSerializer.serialize(new ArrayList<String>())));

        }catch (Exception e){
            e.printStackTrace();
        }
        if(places.size()>0&&latitudes.size()>0&&longitudes.size()>0) {
            if (places.size() == latitudes.size() && places.size() == longitudes.size()) {
                for (int i = 0; i < latitudes.size(); i++) {
                    locations.add(new LatLng(Double.parseDouble(latitudes.get(i)),Double.parseDouble(longitudes.get(i))));
                }
            }
        }else{
            int ct=0;
            for(int i=0;i<places.size();i++){
                if(places.get(i).equalsIgnoreCase("Add a new place.....")==true){
                    ct=1;
                    break;
                }
            }
            if(ct==0) {
                places.add("Add a new place.....");
                locations.add(new LatLng(0, 0));
            }

        }

        arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,places);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent newIntent=new Intent(MainActivity.this,MapsActivity.class);
                    newIntent.putExtra("place number",position);
                    startActivity(newIntent);


            }
        });

    }
}
