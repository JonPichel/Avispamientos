package com.avispamientos.avispamientosandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import com.avispamientos.avispamientosandroid.screens.login.LoginFragment;
import com.avispamientos.avispamientosandroid.screens.register.RegisterFragment;
import com.avispamientos.avispamientosandroid.screens.sightings.SightingDetailsFragment;
import com.avispamientos.avispamientosandroid.screens.sightings.SightingsFragment;

public class MainActivity extends AppCompatActivity {

    private AppViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(AppViewModel.class);

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (currentFragment == null) {
            // Add the LoginFragment
            Fragment loginFragment = new LoginFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, loginFragment)
                    .commit();
        }
    }

    public void goToRegister() {
        Fragment registerFragment = new RegisterFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, registerFragment)
                .commit();
    }

    public void goToLogin() {
        Fragment loginFragment = new LoginFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, loginFragment)
                .commit();
    }

    public void goToSightings() {
        Fragment sightingsFragment = new SightingsFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, sightingsFragment)
                .commit();
    }

    public void goToSightingDetails() {
        Fragment sightingDetailsFragment = new SightingDetailsFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, sightingDetailsFragment)
                .commit();
    }
}