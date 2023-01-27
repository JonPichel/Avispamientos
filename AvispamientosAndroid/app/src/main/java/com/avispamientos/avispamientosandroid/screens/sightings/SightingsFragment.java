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

public class SightingsFragment extends Fragment {

    private FragmentSightingsBinding binding;
    private SightingsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSightingsBinding.inflate(inflater, container, false);

        ListView listView = binding.listView;

        adapter = new SightingsAdapter(getContext(), R.layout.layout_sighting_item);
        listView.setAdapter(adapter);

        // Make the request
        loadSightings();

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            Sighting sighting = ((SightingsAdapter)adapterView.getAdapter()).getSighting(i);
            Toast.makeText(getContext(), "Clicked" + sighting.getId(), Toast.LENGTH_LONG).show();
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void loadSightings() {
        String url = "http://192.168.1.130:9000/android/sightings";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            Log.i("AVISPAMIENTOS", "Sightings response: " + response);
            if (response.has("error")) {
                Toast.makeText(getContext(), response.optString("error", ""), Toast.LENGTH_LONG).show();
            }
            try {
                JSONArray jsonSightings = response.getJSONArray("sightings");
                Log.e("AVISPAMIENTOS", jsonSightings.toString());
                List<Sighting> sightings = new ArrayList<>();
                for (int i = 0; i < jsonSightings.length(); i++) {
                    JSONObject jsonSighting = (JSONObject) jsonSightings.get(i);
                    Log.e("AVISPAMIENTOS", jsonSighting.toString());
                    sightings.add(new Sighting(jsonSighting));
                }
                adapter.setSightings(sightings);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            Log.e("AVISPAMIENTOS", error.toString());
            Toast.makeText(getContext(), "Got error!", Toast.LENGTH_LONG).show();
        });
        Volley.newRequestQueue(getContext()).add(request);
    }

    private class SightingsAdapter extends ArrayAdapter<Sighting> {

        private List<Sighting> items;

        public SightingsAdapter(Context context, int itemLayoutResourceId) {
            super(context, itemLayoutResourceId);
            this.items = new ArrayList<>();
        }

        public void setSightings(List<Sighting> sightings) {
            this.items = sightings;

            this.clear();
            this.addAll(sightings);
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {
            Sighting sighting = items.get(i);
            LayoutSightingItemBinding binding =
                    LayoutSightingItemBinding.inflate(LayoutInflater.from(getContext()), parent, false);

            binding.creator.setText(sighting.getCreator());
            DateFormat dateFormat = DateFormat.getDateTimeInstance();
            String date = dateFormat.format(new Date(sighting.getTimestamp()));
            binding.timestamp.setText(date);
            String coordinates = String.format("%.8f N %.8f W", sighting.getLatitude(), sighting.getLongitude());
            binding.coordinates.setText(coordinates);
            binding.confirmationCount.setText(Integer.toString(sighting.getConfirmationCount()));

            return binding.getRoot();
        }

        public Sighting getSighting(int i) {
            return items.get(i);
        }
    }
}
