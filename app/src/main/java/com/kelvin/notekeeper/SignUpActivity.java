package com.kelvin.notekeeper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {
    TextInputEditText mEmail, mPassword, mPassword2;
    TextView mSignin;
    Button mSigup;

    ProgressDialog pd;
    //Declare an instance of FirebaseAuth;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Actionbar and its title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Create Account");

        //Enable back button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_sign_up);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mPassword2 = findViewById(R.id.Repassword);
        mSignin = findViewById(R.id.signUpTv);
        mSigup = findViewById(R.id.signInBtn);

        //init firebase auth
        mAuth = FirebaseAuth.getInstance();

        pd = new ProgressDialog(this);
        pd.setMessage("Registering User...");

        mSigup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                //validate
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    //set error and focuss
                    mEmail.setText("Invalid Email");
                    mEmail.setFocusable(true);

                }else if(password.length()<6){

                    //set error and focuss
                    mPassword.setText("Password length at least 6 characters");
                    mPassword.setFocusable(true);
                }else{
                    registerUser(email,password);
                }
            }
        });

        mSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signin = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(signin);
            }
        });
    }

    private void registerUser(String email, final String password) {


        pd.show();
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            pd.dismiss();

                            FirebaseUser user = mAuth.getCurrentUser();

                            //Get user email and uid from auth
                            String email = user.getEmail();
                            String uid = user.getUid();

                            //Store user info in to database
                            HashMap<Object, String> hashMap = new HashMap<>();
                            //put info in hashmap
                            hashMap.put("email", email);
                            hashMap.put("userId", uid);
                            hashMap.put("name", "");
                            hashMap.put("onlineStatus", "online");
                            hashMap.put("isAdmin", "false");
                            hashMap.put("phone", "");
                            hashMap.put("image", "");
                            hashMap.put("cover", "");
                            //firebase database instance
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            //paath to store user data name "users"
                            DatabaseReference reference = database.getReference("Users");
                            //puth datawithin hashmap
                            reference.child(uid).setValue(hashMap);

                            Toast.makeText(SignUpActivity.this, "Registered....\n"+user.getEmail(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                            finish();
                        }else{
                            Toast.makeText(SignUpActivity.this, "Authenitaction failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(SignUpActivity.this, "" +e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();//go privous activitry
        return super.onSupportNavigateUp();
    }
}
