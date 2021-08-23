package com.example.uberclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class MainActivity extends AppCompatActivity {
    Switch userTypeSwitch;
    Button goButton;
    EditText usernameEditText;
    EditText passwordEditText;

    public void redirectUser() {



        if (ParseUser.getCurrentUser().get("riderOrDriver").equals("rider")) {
            Intent intent = new Intent(getApplicationContext(), RiderActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getApplicationContext(), ViewRequestsActivity.class);
            startActivity(intent);
        }
    }



    public void clickGo(View view) {

        final String username = usernameEditText.getText().toString();
        final String password = passwordEditText.getText().toString();

        Log.i("Switch value", userTypeSwitch.isChecked() + "");
        String userType = "driver";
        if (userTypeSwitch.isChecked()) {
            userType = "rider";
        }


        final String finalUserType = userType;
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(user!=null&&e==null){
                    Log.i("Login",user.getUsername()+"was logged in successfully");
                    Toast.makeText(MainActivity.this, user.getUsername()+" was logged in successfully", Toast.LENGTH_SHORT).show();
                    user.put("riderOrDriver", finalUserType);
                    user.saveInBackground();
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
                                Toast.makeText(MainActivity.this, "sign up successful", Toast.LENGTH_SHORT).show();
                                ParseUser.getCurrentUser().put("riderOrDriver", finalUserType);
                                ParseUser.getCurrentUser().saveInBackground();
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

        userTypeSwitch = findViewById(R.id.userTypeSwitch);
        goButton = findViewById(R.id.goButton);
        usernameEditText=findViewById(R.id.usernameEditText);
        passwordEditText=findViewById(R.id.passwordEditText);


        getSupportActionBar().hide();



        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                // if defined
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build()
        );




        if(ParseUser.getCurrentUser()!=null){

            Log.i("Existing", "User");
            if (ParseUser.getCurrentUser().get("riderOrDriver")!=null) {

                Log.i("Redirecting as", ParseUser.getCurrentUser().getString("riderOrDriver"));

                redirectUser();
            }
        }
    }
}


