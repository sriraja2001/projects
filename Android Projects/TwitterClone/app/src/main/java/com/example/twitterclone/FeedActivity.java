package com.example.twitterclone;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {
    ListView feedListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        setTitle(ParseUser.getCurrentUser().getUsername()+"'s Feed");

        feedListView=findViewById(R.id.feedListView);

        final List<Map<String,String>> tweetData= new ArrayList<>();
        final SimpleAdapter simpleAdapter=new SimpleAdapter(getApplicationContext(),tweetData,android.R.layout.simple_list_item_2,new String[]{"content","username"},new int[]{android.R.id.text1,android.R.id.text2});
        feedListView.setAdapter(simpleAdapter);

        ParseQuery<ParseObject> query=ParseQuery.getQuery("Tweet");
        query.whereContainedIn("username",ParseUser.getCurrentUser().getList("isFollowing"));
        query.orderByDescending("createdAt");
        query.setLimit(20);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null&&objects.size()>0) {
                    for (ParseObject parseObject : objects) {
                        Map<String,String> tweetInfo= new HashMap<>();
                        tweetInfo.put("content",parseObject.getString("tweet"));
                        tweetInfo.put("username","Posted by "+parseObject.getString("username")+" on "+parseObject.getCreatedAt().toString().substring(0,parseObject.getCreatedAt().toString().indexOf('G')));
                        tweetData.add(tweetInfo);

                    }
                    simpleAdapter.notifyDataSetChanged();

                }
            }
        });




    }
}
