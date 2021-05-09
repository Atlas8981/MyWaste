package com.example.mywaste.David.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.mywaste.David.fragments.AddFragment;
import com.example.mywaste.David.fragments.HomeFragment;
import com.example.mywaste.Lyhour.Aboutme_Fragment;
import com.example.mywaste.R;
import com.example.mywaste.Viseth.RegisterActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity implements HomeFragment.OnCategorySelectedListener {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        bottomNavigationView = findViewById(R.id.bottomNavigationBar);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavMethod);
        getSupportFragmentManager().beginTransaction().replace(R.id.choicesContainer, new HomeFragment()).commit();

    }


    //******************
    // BottomNavigationBar's item action
    //******************

    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavMethod = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            Fragment fragment = null; // use to store the id of fragment from the switch

            switch (menuItem.getItemId()){
                case R.id.home:
                    fragment = new HomeFragment();
                    break;
                case R.id.add:
                    // Check whether user is login or not
                    if (FirebaseAuth.getInstance().getUid()==null){
                        Intent intent = new Intent();
                        intent.setClass(getApplicationContext(), RegisterActivity.class);
                        startActivity(intent);

                    }else {
                        fragment = new AddFragment();
                    }
                    break;
                case R.id.aboutme:
                    if (FirebaseAuth.getInstance().getUid()==null){
                        Intent intent = new Intent();
                        intent.setClass(getApplicationContext(), RegisterActivity.class);
                        startActivity(intent);

                    }else {
                        fragment = new Aboutme_Fragment();
                    }

                    break;
            }


            if (fragment!=null){
                getSupportFragmentManager().beginTransaction().replace(R.id.choicesContainer, fragment).commit();
            }

            return true;
        }
    };

    //******************
    // Method use for responding to user click in HomeFragment
    //******************
    @Override
    public void onCategorySelected(Class destinationActivity,String categorySelected) {
        Intent intent = new Intent();
        intent.setClass(this, destinationActivity);
        intent.putExtra("categorySelected", categorySelected);
        startActivity(intent);
    }
}