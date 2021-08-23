package com.example.twitterclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity {
    ArrayList<String> usernames=new ArrayList<String>();
    ArrayAdapter arrayAdapter;
    ListView usersListView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=new MenuInflater(this);
        menuInflater.inflate(R.menu.tweet_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.tweetMenuItem){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);

            builder.setTitle("Send a Tweet");
            final EditText tweetEditText=new EditText(this);

            builder.setView(tweetEditText)
            .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.i("Tweet",tweetEditText.getText().toString());

                    ParseObject tweet=new ParseObject("Tweet");
                    tweet.put("tweet",tweetEditText.getText().toString());
                    tweet.put("username",ParseUser.getCurrentUser().getUsername());

                    tweet.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e==null){
                                Toast.makeText(UsersActivity.this, "The Tweet was posted !", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(UsersActivity.this,"Tweet Failed !", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.i("Tweet","Cancelled");
                    dialog.cancel();


                }
            })
            .show();



        }else if(item.getItemId()==R.id.logoutMenuItem){
            ParseUser.logOut();
            Intent intent=new Intent(UsersActivity.this,MainActivity.class);
            startActivity(intent);
        }else if(item.getItemId()==R.id.feedMenuItem){
            Intent intent=new Intent(getApplicationContext(),FeedActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        setTitle("User List for "+ParseUser.getCurrentUser().getUsername());

        usersListView=findViewById(R.id.usersListView);


        usersListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        arrayAdapter=new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_checked,usernames);
        usersListView.setAdapter(arrayAdapter);



        ParseQuery<ParseUser> query=ParseUser.getQuery();

        query.whereNotEqualTo("username",ParseUser.getCurrentUser().getUsername());

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e==null&&objects.size()>0){
                    for(ParseUser user:objects){
                        usernames.add(user.getUsername());
                        Log.i("usernames",user.getUsername());
                    }
                    arrayAdapter.notifyDataSetChanged();

                    for(String users:usernames){
                        if(ParseUser.getCurrentUser().getList("isFollowing").contains(users)){
                            usersListView.setItemChecked(usernames.indexOf(users),true);
                        }
                    }
                }
            }
        });

        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckedTextView checkedTextView=(CheckedTextView)view;

                if(checkedTextView.isChecked()){
                    Log.i("User","Checked!");
                    ParseUser.getCurrentUser().add("isFollowing",usernames.get(position));
                }else{
                    Log.i("User","UnChecked!");
                    ParseUser.getCurrentUser().getList("isFollowing").remove(usernames.get(position));

                    List tempUsers=ParseUser.getCurrentUser().getList("isFollowing");
                    ParseUser.getCurrentUser().remove("isFollowing");
                    ParseUser.getCurrentUser().put("isFollowing",tempUsers);

                }
                ParseUser.getCurrentUser().saveInBackground();
            }
        });






    }
}
