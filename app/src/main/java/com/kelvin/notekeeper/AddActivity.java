package com.kelvin.notekeeper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import static android.Manifest.permission_group.CAMERA;

public class AddActivity extends AppCompatActivity {

    ActionBar actionBar;

    private static final int CAMERA_REQUEST_CODE =100;
    private static final int STORAGE_REQUEST_CODE =200;

    private static final int IMAGE_PICK_CAMERA_CODE =300;
    private static final int IMAGE_PICK_GALLERY_CODE =400;

    String cameraPermissions[];
    String storagePermissions[];

    FirebaseAuth firebaseAuth;
    DatabaseReference userDb;



    EditText titleEt, descEt;
    ImageView imageIv;
    Button uploadBtn;
    String name, email, uid ,dp;


    Uri image_rui = null;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Add New Post");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        actionBar.setSubtitle(email);

        //init array of permissions
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE};


        firebaseAuth =FirebaseAuth.getInstance();

        pd = new ProgressDialog(this);

        userDb = FirebaseDatabase.getInstance().getReference("Users");
        Query query = userDb.orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    name = ""+ds.child("name").getValue();
                    email = ""+ds.child("email").getValue();
                    dp = ""+ds.child("image").getValue();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        titleEt = findViewById(R.id.pTitleEt);
        descEt = findViewById(R.id.pDescriptionEt);
        imageIv = findViewById(R.id.pImage);
        uploadBtn = findViewById(R.id.pUploadbtn);

        imageIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });


        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEt.getText().toString().trim();
                String desc = descEt.getText().toString().trim();

                if(TextUtils.isEmpty(title)){
                    Toast.makeText(AddActivity.this, "Enter title....", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(desc)){
                    Toast.makeText(AddActivity.this, "Enter description....", Toast.LENGTH_SHORT).show();
                    return;
                }

                if( image_rui == null){

                    uploadData(title, desc,"noImage");

                }else{
                    uploadData(title, desc,String.valueOf(image_rui));

                }
            }
        });

        checkUserStatus();
    }

    private void uploadData(final String title, final String desc, String uri) {


        pd.setMessage("Publishing post...");


        final String timestamp = String.valueOf(System.currentTimeMillis());

        String filePathAndName = "Posts/" + "post_" +timestamp;

        if(!uri.equals("noImage")){
            StorageReference reference = FirebaseStorage.getInstance().getReference().child(filePathAndName);
            reference.putFile(Uri.parse(uri))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while ((!uriTask.isSuccessful()));
                            String downloadUri = uriTask.getResult().toString();

                            if(uriTask.isSuccessful()){

                                HashMap<Object, String>hashMap = new HashMap<>();
                                hashMap.put("uid", uid);
                                hashMap.put("uName", name);
                                hashMap.put("uEmail",email );
                                hashMap.put("uDp", dp);
                                hashMap.put("pId", timestamp);
                                hashMap.put("pTitle",title );
                                hashMap.put("pDescr", desc);
                                hashMap.put("pImage", downloadUri );
                                hashMap.put("pTime", timestamp );


                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                                ref.child(timestamp).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                pd.dismiss();
                                                Toast.makeText(AddActivity.this, "Post published", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                                pd.dismiss();
                                                Toast.makeText(AddActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                                            }
                                        });
                            }
                            else {
                                HashMap<Object, String>hashMap = new HashMap<>();
                                hashMap.put("uid", uid);
                                hashMap.put("uName", name);
                                hashMap.put("uEmail",email );
                                hashMap.put("uDp", dp);
                                hashMap.put("pId", timestamp);
                                hashMap.put("pTitle",title );
                                hashMap.put("pDescr", desc);
                                hashMap.put("pImage", downloadUri );
                                hashMap.put("pTime", timestamp );


                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                                ref.child(timestamp).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                pd.dismiss();
                                                Toast.makeText(AddActivity.this, "Post published", Toast.LENGTH_SHORT).show();

                                                titleEt.setText("");
                                                descEt.setText("");
                                                imageIv.setImageURI(null);
                                                image_rui = null;
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                                pd.dismiss();
                                                Toast.makeText(AddActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            pd.dismiss();
                            Toast.makeText(AddActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        }
        else {
            HashMap<Object, String>hashMap = new HashMap<>();
            hashMap.put("uid", uid);
            hashMap.put("uName", name);
            hashMap.put("uEmail",email );
            hashMap.put("uDp", dp);
            hashMap.put("pId", timestamp);
            hashMap.put("pTitle",title );
            hashMap.put("pDescr", desc);
            hashMap.put("pImage", "onImage");
            hashMap.put("pTime", timestamp );


            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
            ref.child(timestamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            pd.dismiss();
                            Toast.makeText(AddActivity.this, "Post published", Toast.LENGTH_SHORT).show();

                            titleEt.setText("");
                            descEt.setText("");
                            imageIv.setImageURI(null);
                            image_rui = null;
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            pd.dismiss();
                            Toast.makeText(AddActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        }


    }

    private void showImagePickDialog() {


        String[] options = {"Camera", "Gallery"};

        //alert dialog
        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //set title
        builder.setTitle("Choose Image from");

        //set items to dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(which == 0){

                    //Camera clicked
                    if (!checkCameraPermission()){
                        requestCameraPermission();
                    }else{
                        pickFromCamera();
                    }


                } if(which == 1){
                    //Gallery clicked
                    if (!checkStoragePermission()){
                        requestStoragePermission();
                    }else {
                        pickFromGallery();
                    }


                }

            }
        });
        //create and show dialog
        builder.create().show();

    }

    private void pickFromGallery() {
//        //pick
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,IMAGE_PICK_GALLERY_CODE);

    }

    private void pickFromCamera() {

        //pick

        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE, "Temp Pick");
        cv.put(MediaStore.Images.Media.DESCRIPTION, "Temp Descr");
        image_rui = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);

        Intent cameraIntent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_rui);
        startActivityForResult(cameraIntent,IMAGE_PICK_CAMERA_CODE);
    }




    private boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return  result;
    }

    private  void requestStoragePermission(){
        ActivityCompat.requestPermissions(this,storagePermissions,STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        boolean result = ContextCompat.checkSelfPermission(this, CAMERA)
                == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return  result && result1;
    }

    private  void requestCameraPermission(){
        ActivityCompat.requestPermissions(this,cameraPermissions,CAMERA_REQUEST_CODE);
    }


    @Override
    protected void onStart() {

        super.onStart();
        checkUserStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUserStatus();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);


        menu.findItem(R.id.action_add_post).setVisible(true);
        menu.findItem(R.id.action_search).setVisible(true);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_logout){
            firebaseAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }
    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {

            email = user.getEmail();
            uid = user.getUid();

        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch(requestCode){
            case CAMERA_REQUEST_CODE:{

                if(grantResults.length >0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (cameraAccepted && storageAccepted){
                        pickFromCamera();
                    }else{

                        Toast.makeText(this , "Please enable camera & storage permission", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
            case STORAGE_REQUEST_CODE:{

                if(grantResults.length >0){
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (writeStorageAccepted){
                        pickFromGallery();
                    }else{

                        Toast.makeText(this, "Please enable storage permission", Toast.LENGTH_SHORT).show();
                    }
                }


            }
            break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RESULT_OK){
            if (requestCode == IMAGE_PICK_GALLERY_CODE){
                image_rui = data.getData();

                imageIv.setImageURI(image_rui);



            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE){
                imageIv.setImageURI(image_rui);

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
