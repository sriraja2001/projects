package com.example.uberclone;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class DriverLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LatLng driverLocation;
    LatLng requestLocation;

    Intent get;

    public void acceptRequest(View view){

        ParseQuery<ParseObject> query=ParseQuery.getQuery("Request");

        query.whereEqualTo("username",get.getStringExtra("username"));
        query.whereDoesNotExist("driverUsername");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null&&objects.size()>0){
                    for(ParseObject object:objects){
                        object.put("driverUsername", ParseUser.getCurrentUser().getUsername());
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e==null){
                                    Intent directionIntent = new Intent(android.content.Intent.ACTION_VIEW,
                                            Uri.parse("http://maps.google.com/maps?saddr="+driverLocation.latitude+","+driverLocation.longitude+ "&daddr="+requestLocation.latitude+","+requestLocation.longitude));
                                    startActivity(directionIntent);
                                }
                            }
                        });
                    }
                }
            }
        });
    }
    //creating custom map icon
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResourceID){
        Drawable vectorDrawable= ContextCompat.getDrawable(context,vectorResourceID);
        vectorDrawable.setBounds(0,0,vectorDrawable.getIntrinsicWidth(),vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap=Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),vectorDrawable.getIntrinsicHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        ArrayList<Marker> markers=new ArrayList<>();

        get=getIntent();

        driverLocation = new LatLng(get.getDoubleExtra("driverLatitude",0), get.getDoubleExtra("driverLongitude",0));
        requestLocation = new LatLng(get.getDoubleExtra("requestLatitude",0), get.getDoubleExtra("requestLongitude",0));
        markers.add(mMap.addMarker(new MarkerOptions().position(driverLocation).title("Driver Location").icon(bitmapDescriptorFromVector(getApplicationContext(),R.drawable.ic_directions_car_black_24dp))));
        markers.add(mMap.addMarker(new MarkerOptions().position(requestLocation).title("Request Location").icon(bitmapDescriptorFromVector(getApplicationContext(),R.drawable.ic_person_black_24dp))));


        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for(Marker marker:markers) {
            builder.include(marker.getPosition());

        }

        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.30); // offset from edges of the map 10% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        mMap.animateCamera(cu);

    }
}
