package com.example.uberclone;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class RiderActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    Button callUberButton;
    boolean requestActive=false;
    TextView infoTextView;
    boolean driverActive=false;

    Handler handler=new Handler();


    public void checkForUpdates() {
        if (ParseUser.getCurrentUser() != null) {
            final ParseQuery<ParseObject> query = ParseQuery.getQuery("Request");

            query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());

            query.whereExists("driverUsername");

            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null && objects.size() > 0) {

                        driverActive = true;


                        ParseQuery<ParseUser> parseUserParseQuery = ParseUser.getQuery();

                        parseUserParseQuery.whereEqualTo("username", objects.get(0).getString("driverUsername"));

                        parseUserParseQuery.findInBackground(new FindCallback<ParseUser>() {
                            @Override
                            public void done(List<ParseUser> objects, ParseException e) {
                                if (e == null && objects.size() > 0) {

                                    ParseGeoPoint driverLocation = objects.get(0).getParseGeoPoint("Location");

                                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                                        Location lastLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);

                                        if (lastLocation != null) {

                                            ParseGeoPoint userLocation = new ParseGeoPoint(lastLocation.getLatitude(), lastLocation.getLongitude());

                                            Double distanceInKms = userLocation.distanceInKilometersTo(driverLocation);

                                            if (distanceInKms < 0.1) {

                                                infoTextView.setText("Your Driver is here!");

                                                ParseQuery<ParseObject> queryParse = ParseQuery.getQuery("Request");

                                                query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());

                                                query.whereExists("driverUsername");

                                                queryParse.findInBackground(new FindCallback<ParseObject>() {
                                                    @Override
                                                    public void done(List<ParseObject> objectsdel, ParseException e) {
                                                        if (e == null) {
                                                            for (ParseObject object : objectsdel) {
                                                                object.deleteInBackground();
                                                            }
                                                        }
                                                    }
                                                });


                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        callUberButton.setEnabled(true);
                                                        callUberButton.setText("Call an Uber");
                                                        requestActive = false;
                                                        driverActive = false;

                                                        infoTextView.setText("");


                                                    }
                                                }, 5000);


                                            } else {

                                                Double distanceOneDp = (double) Math.round(distanceInKms * 10) / 10;

                                                infoTextView.setText("Your driver is " + distanceOneDp + " km away");

                                                ArrayList<Marker> markers = new ArrayList<>();


                                                LatLng driver = new LatLng(driverLocation.getLatitude(), driverLocation.getLongitude());
                                                LatLng rider = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());

                                                

                                                markers.add(mMap.addMarker(new MarkerOptions().position(driver).title("Driver Location").icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_directions_car_black_24dp))));
                                                markers.add(mMap.addMarker(new MarkerOptions().position(rider).title("Your Location").icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_person_black_24dp))));


                                                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                                                for (Marker marker : markers) {
                                                    builder.include(marker.getPosition());

                                                }

                                                LatLngBounds bounds = builder.build();

                                                int width = getResources().getDisplayMetrics().widthPixels;
                                                int height = getResources().getDisplayMetrics().heightPixels;
                                                int padding = (int) (width * 0.30); // offset from edges of the map 10% of screen

                                                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

                                                mMap.animateCamera(cu);

                                                callUberButton.setEnabled(false);

                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        checkForUpdates();

                                                    }
                                                }, 1000);


                                            }
                                        }


                                    }
                                }
                            }
                        });


                    }
                }
            });
        }

                else {
                        Log.i("Unsolvable", "Error");
                    }
                }

                public void clickLogOut(View view) {
                    ParseUser.logOut();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }

                public void callUber(View v) {
                    Log.i("Uber", "Called");
                    if (requestActive) {

                        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Request");

                        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());

                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if (e == null) {
                                    if (objects.size() > 0) {
                                        requestActive = false;
                                        callUberButton.setText("Call Uber");
                                        for (ParseObject object : objects) {
                                            object.deleteInBackground();
                                        }
                                    }
                                }
                            }
                        });

                    } else {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 5000, 0, locationListener);
                            Location location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);


                            if (location != null) {


                                final ParseObject request = new ParseObject("Request");

                                request.put("username", ParseUser.getCurrentUser().getUsername());

                                ParseGeoPoint parseGeoPoint = new ParseGeoPoint(location.getLatitude(), location.getLongitude());

                                request.put("location", parseGeoPoint);

                                request.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            callUberButton.setText("Cancel Uber");
                                            requestActive = true;

                                            checkForUpdates();
                                        } else {
                                            e.printStackTrace();
                                            Log.i("Uber", "Not called");
                                        }
                                    }
                                });


                            } else {
                                Toast.makeText(this, "Could not find location,try later", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                }


                public void centreMapOnLocation(Location location, String title) {
                    if (driverActive == false) {
                        LatLng locationX = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(locationX).title(title).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationX, 18f));
                    }
                }

                @Override
                public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                    if (requestCode == 1) {
                        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.length > 0) {
                            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 5000, 0, locationListener);
                            }
                        }
                    }
                }

                //creating custom map icon
                private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResourceID) {
                    Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResourceID);
                    vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
                    Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    vectorDrawable.draw(canvas);
                    return BitmapDescriptorFactory.fromBitmap(bitmap);
                }


                @Override
                protected void onCreate(Bundle savedInstanceState) {
                    super.onCreate(savedInstanceState);
                    setContentView(R.layout.activity_rider);
                    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(this);

                    callUberButton = findViewById(R.id.callUberButton);

                    infoTextView = findViewById(R.id.infoTextView);

                    ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Request");

                    query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());

                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            if (e == null) {
                                if (objects.size() > 0) {
                                    requestActive = true;
                                    callUberButton.setText("Cancel Uber");

                                    checkForUpdates();
                                }
                            }
                        }
                    });
                }

                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;




                    locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                    locationListener = new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            Log.i("Location", location.toString());
                            mMap.clear();
                            LatLng riderLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(riderLocation).title("Your Location").icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_person_black_24dp)));


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


                    if (Build.VERSION.SDK_INT < 23) {
                        Toast.makeText(this, "This version of android does not support these services", Toast.LENGTH_SHORT).show();
                    } else {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);


                        } else {
                            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 2000, 0, locationListener);
                            Location location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
                            centreMapOnLocation(location, "Your Location");
                        }
                    }
                }

            }





