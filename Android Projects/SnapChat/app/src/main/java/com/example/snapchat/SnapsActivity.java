package com.example.snapchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SnapsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    ListView snapsListView;
    ArrayList<String> emails=new ArrayList<String>();
    ArrayList<DataSnapshot> snaps=new ArrayList<>();
    FirebaseDatabase database;




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater=new MenuInflater(this);
        inflater.inflate(R.menu.snap_menu,menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.snapMenuItem){

            Intent intent=new Intent(getApplicationContext(),CreateSnapActivity.class);
            startActivity(intent);

        }else if(item.getItemId()==R.id.logoutMenuItem){
            mAuth.signOut();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snaps);

        setTitle("My Snaps");

        mAuth=FirebaseAuth.getInstance();
        snapsListView=findViewById(R.id.snapsListView);
        database=FirebaseDatabase.getInstance();

        final ArrayAdapter arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,emails);
        snapsListView.setAdapter(arrayAdapter);

        database.getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("snaps").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                emails.add(dataSnapshot.child("from").getValue().toString());
                snaps.add(dataSnapshot);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                int index=0;
                Log.i("Child removed",dataSnapshot.getKey());
                for(DataSnapshot snapshot:snaps){
                    Log.i("Children",snapshot.getKey());
                    if(snapshot.getKey().equals(dataSnapshot.getKey())){
                        Log.i("Position",index+"");
                        emails.remove(index);
                        snaps.remove(index);

                    }
                    index++;
                }
                arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        snapsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DataSnapshot snapshot=snaps.get(position);
                Intent intent=new Intent(SnapsActivity.this,ViewSnapActivity.class);
                intent.putExtra("imageName",snapshot.child("imageName").getValue().toString());
                intent.putExtra("imageURL",snapshot.child("imageURL").getValue().toString());
                intent.putExtra("message",snapshot.child("message").getValue().toString());
                intent.putExtra("snapKey",snapshot.getKey());
                intent.putExtra("name",mAuth.getCurrentUser().getEmail());

                startActivity(intent);


            }
        });




    }
}
