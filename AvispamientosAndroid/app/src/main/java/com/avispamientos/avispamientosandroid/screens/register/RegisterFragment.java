package com.avispamientos.avispamientosandroid.screens.register;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.avispamientos.avispamientosandroid.MainActivity;
import com.avispamientos.avispamientosandroid.databinding.FragmentLoginBinding;
import com.avispamientos.avispamientosandroid.databinding.FragmentRegisterBinding;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class RegisterFragment extends Fragment {
    private FragmentRegisterBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);

        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final EditText repeatPasswordEditText = binding.repeatPassword;
        final Button registerButton = binding.register;
        final ProgressBar loadingProgressBar = binding.loading;

        registerButton.setOnClickListener(view -> {
            if (passwordEditText.getText().toString().equals(repeatPasswordEditText.getText().toString())) {
                register(usernameEditText.getText().toString(), passwordEditText.getText().toString());
            } else {
                Toast.makeText(getContext(), "Passwords must be equal", Toast.LENGTH_LONG).show();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void register(String username, String password) {
        String url = "http://10.192.36.31:9000/android/register";
        ProgressBar loadingProgressBar = binding.loading;
        StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
            loadingProgressBar.setVisibility(View.GONE);
            Log.i("AVISPAMIENTOS", "Login response: " + response);
            if (response.startsWith("OK")) {
                ((MainActivity)getActivity()).goToSightings();
            }
            else {
                Toast.makeText(getContext(), response,
                        Toast.LENGTH_LONG).show();
            }
        }, error -> {
            Log.e("AVISPAMIENTOS", error.toString());
            Toast.makeText(getContext(), "Got error!", Toast.LENGTH_LONG).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };
        Volley.newRequestQueue(getContext()).add(request);
        loadingProgressBar.setVisibility(View.VISIBLE);
    }
}