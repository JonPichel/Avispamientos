package com.avispamientos.avispamientosandroid.screens.login;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.avispamientos.avispamientosandroid.MainActivity;
import com.avispamientos.avispamientosandroid.R;
import com.avispamientos.avispamientosandroid.databinding.FragmentLoginBinding;
import com.avispamientos.avispamientosandroid.databinding.FragmentSightingsBinding;

import java.util.HashMap;
import java.util.Map;

public class SightingsFragment extends Fragment {
    private FragmentSightingsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSightingsBinding.inflate(inflater, container, false);

        // Actions
        final GridView gridView = binding.grid;


        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
