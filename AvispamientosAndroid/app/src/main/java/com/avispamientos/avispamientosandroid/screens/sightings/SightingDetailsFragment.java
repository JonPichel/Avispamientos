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
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.avispamientos.avispamientosandroid.AppViewModel;
import com.avispamientos.avispamientosandroid.MainActivity;
import com.avispamientos.avispamientosandroid.R;
import com.avispamientos.avispamientosandroid.databinding.FragmentLoginBinding;
import com.avispamientos.avispamientosandroid.databinding.FragmentSightingDetailsBinding;
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

    private AppViewModel viewModel;
    private FragmentSightingDetailsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSightingDetailsBinding.inflate(inflater, container, false);

        viewModel = new ViewModelProvider(getActivity()).get(AppViewModel.class);

        Sighting sighting = viewModel.getSelectedSighting().getValue();

        binding.creator.setText(sighting.getCreator());
        binding.creator.setText(sighting.getCreator());
        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        String date = dateFormat.format(new Date(sighting.getTimestamp()));
        binding.timestamp.setText(date);
        String coordinates = String.format("%.8f N %.8f W", sighting.getLatitude(), sighting.getLongitude());
        binding.coordinates.setText(coordinates);
        binding.confirmationCount.setText(Integer.toString(sighting.getConfirmationCount()));
        binding.information.setText(sighting.getInformation());
        if (sighting.getConfirmationCount() != 0) {
            date = dateFormat.format(new Date(sighting.getLastTimestamp()));
            binding.lastConfirmation.setText(
                    "Last confirmation on: " + date + "(by " + sighting.getLastContributor() + ")");
        } else {
            binding.lastConfirmation.setText("No confirmations yet");
        }

        binding.returnButton.setOnClickListener(view -> ((MainActivity) getActivity()).goToSightings());
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
