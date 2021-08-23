package com.example.snapchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    EditText emailEditText;
    EditText passwordEditText;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;

    public void loginUser(){
        //move to next Activity
        Intent intent=new Intent(MainActivity.this,SnapsActivity.class);
        startActivity(intent);
    }

    public void goClicked(View view){
        //login the user if new user,then sign up the user
        final String email=emailEditText.getText().toString();
        final String password=passwordEditText.getText().toString();


        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Sign in the user
                            Log.i("Infograph", "Sign in was successful");
                            loginUser();
                            Toast.makeText(MainActivity.this, "Signed in successfully !", Toast.LENGTH_SHORT).show();
                        } else {
                            //Sign up the user
                            Log.i("Infograph", "Sign in was not successful, Attempting SignUp");
                            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        //Add to database
                                        DatabaseReference reference=database.getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("email");

                                        reference.setValue(email);

                                        Log.i("Infograph","Sign up was successful");
                                        loginUser();
                                        Toast.makeText(MainActivity.this, "Signed up successfully !", Toast.LENGTH_SHORT).show();


                                    }else{
                                        Log.i("Infograph","Sign up was not successful");
                                        Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

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

        emailEditText=findViewById(R.id.emailEditText);
        passwordEditText=findViewById(R.id.passwordEditText);
        mAuth = FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();

        if(mAuth.getCurrentUser()!=null){
            loginUser();
        }
    }
}
