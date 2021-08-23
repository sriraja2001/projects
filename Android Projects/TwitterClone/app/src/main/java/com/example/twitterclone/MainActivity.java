package com.example.twitterclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class MainActivity extends AppCompatActivity {
    EditText usernameEditText,passwordEditText;
    Button loginButton;

    public void redirectUser(){
        if(ParseUser.getCurrentUser()!=null){
            Intent intent=new Intent(MainActivity.this,UsersActivity.class);
            startActivity(intent);
        }
    }


    public void signupLogin(View view){
        final String username=usernameEditText.getText().toString();
        final String password=passwordEditText.getText().toString();

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(user!=null&&e==null){
                    Log.i("Login",user.getUsername()+"was logged in successfully");
                    redirectUser();

                }else{
                    ParseUser parseUser=new ParseUser();//creating new user
                    parseUser.setUsername(username);
                    parseUser.setPassword(password);

                    parseUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e==null) {
                                Log.i("Login Failed", "sign up successful");
                                redirectUser();

                            }else{
                                Toast.makeText(MainActivity.this, e.getMessage().substring(e.getMessage().indexOf(" ")), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Twitter Login");

        usernameEditText=findViewById(R.id.userNameEditText);
        passwordEditText=findViewById(R.id.passwordEditText);
        loginButton=findViewById(R.id.loginButton);



        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                // if defined
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build()
        );
        redirectUser();

    }
}
