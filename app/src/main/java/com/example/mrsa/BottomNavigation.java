package com.example.mrsa;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.os.Bundle;

import com.example.mrsa.databinding.ActivityBottomNavigationBinding;

public class BottomNavigation extends AppCompatActivity {

    ActivityBottomNavigationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBottomNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new CommandFragment());
        EdgeToEdge.enable(this);



        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.commandScreen) {
                replaceFragment(new CommandFragment());
            } else if (item.getItemId() == R.id.statusScreen) {
                replaceFragment(new StatusFragment());
            } else if (item.getItemId() == R.id.questionScreen) {
                replaceFragment(new QuestionFragment());
            } else if (item.getItemId() == R.id.accountScreen)
                replaceFragment(new AccountFragment());
            return true;
        });

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}