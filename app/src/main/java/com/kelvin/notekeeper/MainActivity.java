package com.kelvin.notekeeper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kelvin.notekeeper.fragements.ChatsFragment;
import com.kelvin.notekeeper.fragements.HomeFragment;
import com.kelvin.notekeeper.fragements.ProfileFragment;


public class MainActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;

    ActionBar actionBar;
    String mUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Actionbar and its title
        actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");
        firebaseAuth = FirebaseAuth.getInstance();

        //bottom navigation
        BottomNavigationView navigationView = findViewById(R.id.bottom_nav);

        navigationView.setOnNavigationItemSelectedListener(selectedListener);
        actionBar.setTitle("Home");
        HomeFragment fragment1 = new HomeFragment();
        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.content, fragment1, "");
        ft1.commit();

        checkUserStatus();
    }
    @Override
    protected void onResume() {
        checkUserStatus();
        super.onResume();
    }

//    public void updateToken(String token){
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
//        Token mToken = new Token(token);
//        ref.child(mUID).setValue(mToken);
//    }
    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    //handles item clicks
                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            actionBar.setTitle("Home");
                            HomeFragment fragment1 = new HomeFragment();
                            FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                            ft1.replace(R.id.content, fragment1, "");
                            ft1.commit();
                            return true;
                        case R.id.nav_profile:
                            actionBar.setTitle("Profile");
                            ProfileFragment fragment2 = new ProfileFragment();
                            FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                            ft2.replace(R.id.content, fragment2, "");
                            ft2.commit();
                            return true;
//                        case R.id.nav_users:
//                            actionBar.setTitle("Users");
//                            UsersFragment fragment3 = new UsersFragment();
//                            FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
//                            ft3.replace(R.id.content, fragment3, "");
//                            ft3.commit();
//                            return true;

                        case R.id.nav_chats:
                            actionBar.setTitle("Chats");
                            ChatsFragment fragment4 = new ChatsFragment();
                            FragmentTransaction ft4 = getSupportFragmentManager().beginTransaction();
                            ft4.replace(R.id.content, fragment4, "");
                            ft4.commit();
                            return true;

                    }
                    return false;
                }
            };

    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {

            // mProfileTv.setText(user.getEmail());

            mUID = user.getUid();

            SharedPreferences sp = getSharedPreferences("SP_USER", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("Current_USERID", mUID);
            editor.apply();


        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }

}
