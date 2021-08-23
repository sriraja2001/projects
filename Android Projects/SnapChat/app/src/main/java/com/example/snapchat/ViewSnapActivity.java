package com.example.snapchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ViewSnapActivity extends AppCompatActivity {

    TextView messageTextView;
    ImageView snapImageView;
    String snapId;
    String imageName;

    public class DownloadImage extends AsyncTask<String,Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {

            URL url;
            HttpURLConnection httpURLConnection=null;
            try{
                url=new URL(urls[0]);
                httpURLConnection=(HttpURLConnection) url.openConnection();
                InputStream in=httpURLConnection.getInputStream();
                Bitmap bitmap= BitmapFactory.decodeStream(in);

                return bitmap;

            }catch (Exception e){
                e.printStackTrace();
                return null;
            }

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("snaps").child(snapId).removeValue();
        FirebaseStorage.getInstance().getReference().child("images").child(imageName).delete();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_snap);

        messageTextView=findViewById(R.id.messageTextView);
        snapImageView=findViewById(R.id.snapImageView);

        Intent get=getIntent();
        messageTextView.setText(get.getStringExtra("message"));
        String imageURL=get.getStringExtra("imageURL");
        snapId=get.getStringExtra("snapKey");
        imageName=get.getStringExtra("imageName");
        String name=get.getStringExtra("name");

        setTitle(name+"'s Snap");

        DownloadImage downloadImage=new DownloadImage();
        try {
            Bitmap image = downloadImage.execute(imageURL).get();
            snapImageView.setImageBitmap(image);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
