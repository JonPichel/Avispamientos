package com.avispamientos.avispamientosandroid.screens.sightings;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.avispamientos.avispamientosandroid.MainActivity;
import com.avispamientos.avispamientosandroid.R;
import com.avispamientos.avispamientosandroid.databinding.FragmentLoginBinding;
import com.avispamientos.avispamientosandroid.databinding.FragmentSightingsBinding;
import com.avispamientos.avispamientosandroid.databinding.LayoutSightingItemBinding;
import com.avispamientos.avispamientosandroid.models.Sighting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SightingDetailsFragment extends Fragment {

    /*
    private FragmentSightingDetailsBinding binding;
    private SightingDetailsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSightingDetailsBinding.inflate(inflater, container, false);

        ListView listView = binding.listView;

        adapter = new SightingsFragment.SightingsAdapter(getContext(), R.layout.layout_sighting_item);
        listView.setAdapter(adapter);

        // Make the request
        loadSightingDetails();

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void loadSightings() {

    }

     */
}
