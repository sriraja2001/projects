package com.example.uberclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.util.Strings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ViewRequestsActivity extends AppCompatActivity {
    ListView selectRequestListView;
    ArrayList<String> requests=new ArrayList<String>();
    LocationManager locationManager;
    LocationListener locationListener;
    ArrayList<Double> requestLatitude=new ArrayList<>();
    ArrayList<Double> requestLongitude=new ArrayList<>();
    ArrayList<String> usernames=new ArrayList<>();

    ArrayAdapter arrayAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=this.getMenuInflater();
        menuInflater.inflate(R.menu.driver_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.logoutMenuItem){
            ParseUser.logOut();
            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);

            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateListView(Location location) {
        if (location != null) {



            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Request");
            final ParseGeoPoint geoPointLocation = new ParseGeoPoint(location.getLatitude(), location.getLongitude());

            query.whereNear("location",geoPointLocation);

            query.setLimit(10);

            query.whereDoesNotExist("driverUsername");


            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {

                    if(e==null&&objects.size()>0){

                        requests.clear();
                        requestLatitude.clear();
                        requestLongitude.clear();

                        for(ParseObject object:objects){

                            ParseGeoPoint requestLocation=object.getParseGeoPoint("location");

                            if(requestLocation!=null) {

                                Double distanceInKms = geoPointLocation.distanceInKilometersTo(requestLocation);

                                Double distanceOneDp = (double) Math.round(distanceInKms * 10) / 10;

                                requests.add(distanceOneDp + " Km");
                                requestLatitude.add(requestLocation.getLatitude());
                                requestLongitude.add(requestLocation.getLongitude());
                                usernames.add(object.getString("username"));




                            }
                        }
                        arrayAdapter.notifyDataSetChanged();
                    }else{
                        requests.clear();
                        requests.add("No nearby requests");
                        arrayAdapter.notifyDataSetChanged();
                    }
                }
            });

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED&&grantResults.length>0) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, locationListener);
                }
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_requests);



        setTitle("Nearby Requests");

        selectRequestListView=findViewById(R.id.requestsListView);


        arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,requests);
        selectRequestListView.setAdapter(arrayAdapter);

        locationManager=(LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        locationListener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
               updateListView(location);
               if(ParseUser.getCurrentUser()!=null) {
                   Log.i("Location", "Changed");
                   ParseUser.getCurrentUser().put("Location", new ParseGeoPoint(location.getLatitude(), location.getLongitude()));
                   ParseUser.getCurrentUser().saveInBackground();
               }else{
                   Log.i("Unsolvable"," Error");
               }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        selectRequestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (ContextCompat.checkSelfPermission(ViewRequestsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) ==PackageManager.PERMISSION_GRANTED) {

                    Location location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
                    if (requestLatitude.size() > 0 && requestLongitude.size() > 0) {
                        Intent intent = new Intent(getApplicationContext(), DriverLocationActivity.class);
                        intent.putExtra("requestLatitude",requestLatitude.get(position));
                        intent.putExtra("requestLongitude",requestLongitude.get(position));
                        intent.putExtra("driverLatitude",location.getLatitude());
                        intent.putExtra("driverLongitude",location.getLongitude());
                        intent.putExtra("username",usernames.get(position));
                        startActivity(intent);
                    }
                }
            }
        });


        if(Build.VERSION.SDK_INT<23){
            Toast.makeText(this, "This version of android does not support these services", Toast.LENGTH_SHORT).show();
        }else{
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);


            }else{
                locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER,5000,0,locationListener);
                Location location=locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
                updateListView(location);
            }
        }


    }


}


