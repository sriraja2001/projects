package com.example.snapchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.UUID;

public class CreateSnapActivity extends AppCompatActivity {

    ImageView createSnapImageView;
    Button chooseImageButton;
    Button nextButton;
    EditText messageEditText;
    FirebaseStorage storage ;
    String imageName;
    Uri selectedImage;

    public void getPhoto(){
        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==1){
            if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                getPhoto();

            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if(requestCode==1&&resultCode==RESULT_OK&&data!=null) {
                selectedImage = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                Log.i("Infographic","Image selected successfully");
                Toast.makeText(this, "Image selected successfully", Toast.LENGTH_SHORT).show();
                createSnapImageView.setImageBitmap(bitmap);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void chooseImageClicked(View view){

        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            getPhoto();
        }else{
            getPhoto();
        }

    }

    public void nextClicked(View view){

        // Get the data from an ImageView as bytes
        //uploading an image using byte array
        /*createSnapImageView.setDrawingCacheEnabled(true);
        createSnapImageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) createSnapImageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference image=storage.getReference().child("images").child(imageName);

        UploadTask uploadTask = image.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(CreateSnapActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                Log.i("Infographic","Upload Failed");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                Log.i("Infographic","Uploaded !");
            }
        });
        */

        try {
            Uri file = selectedImage;
            final StorageReference Ref = storage.getReference().child("images").child(imageName);
            UploadTask uploadTask = Ref.putFile(file);


            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Toast.makeText(CreateSnapActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                    Log.i("Infographic", "Upload Failed");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    Log.i("Infographic", "Uploaded !");
                    Toast.makeText(CreateSnapActivity.this, "Image Uploaded !", Toast.LENGTH_SHORT).show();

                    String imageURL;

                    storage.getReference().child("images").child(imageName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.i("URL", "Success");
                            String url = uri.toString();

                            Log.i("URL", url);

                            Intent intent = new Intent(CreateSnapActivity.this, ChooseUserActivity.class);
                            intent.putExtra("imageURL", url);
                            intent.putExtra("imageName", imageName);
                            intent.putExtra("message", messageEditText.getText().toString());

                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.i("URL", "Fail");
                        }
                    });


                }
            });
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "Please upload a picture to continue", Toast.LENGTH_SHORT).show();
        }




    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_snap);

        setTitle("Send a Snap");

        createSnapImageView=findViewById(R.id.createSnapImageView);
        chooseImageButton=findViewById(R.id.chooseImageButton);
        nextButton=findViewById(R.id.nextButton);
        messageEditText=findViewById(R.id.messageEditText);

        storage=FirebaseStorage.getInstance();

        imageName=UUID.randomUUID().toString()+".jpeg";


    }
}
