package com.example.snapchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.common.util.Strings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChooseUserActivity extends AppCompatActivity {

    ListView chooseUsersListView;
    ArrayList<String> emails=new ArrayList<>();
    ArrayList<String> keys=new ArrayList<>();

    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_user);

        setTitle("Send to ");

        Intent get=getIntent();
        final String imageName=get.getStringExtra("imageName");
        final String imageURL=get.getStringExtra("imageURL");
        final String message=get.getStringExtra("message");

        Log.i("URL 2",imageURL);

        database=FirebaseDatabase.getInstance();
        chooseUsersListView=findViewById(R.id.chooseUsersListView);

        final ArrayAdapter arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,emails);
        chooseUsersListView.setAdapter(arrayAdapter);

        database.getReference().child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                String email=dataSnapshot.child("email").getValue().toString();
                emails.add(email);
                keys.add(dataSnapshot.getKey());
                arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        chooseUsersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String,String>  snapMap=new HashMap<>();
                snapMap.put("from", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                snapMap.put("imageName",imageName);
                snapMap.put("imageURL",imageURL);
                snapMap.put("message",message);

                database.getReference().child("users").child(keys.get(position)).child("snaps").push().setValue(snapMap);
                Intent intent=new Intent(ChooseUserActivity.this,SnapsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);


            }
        });





    }
}
