package com.example.pictureinpicture;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    Button pipButton;

    public void goPip(View v){
        enterPictureInPictureMode();
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);



        if (isInPictureInPictureMode==true){//Going into picture in picture mode
            pipButton.setVisibility(View.INVISIBLE);
            getSupportActionBar().hide();
            textView.setText("$10,454.57");
        }else{//Going out of picture in picture mode
            getSupportActionBar().show();
            pipButton.setVisibility(View.VISIBLE);
            textView.setText("Go Picture in Picture mode!!");
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView=findViewById(R.id.textView);
        pipButton=findViewById(R.id.pipButton);
    }
}
