package com.avispamientos.avispamientosandroid;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.avispamientos.avispamientosandroid.models.Sighting;

import java.util.List;

public class AppViewModel extends ViewModel {
    private final MutableLiveData<List<Sighting>> sightings = new MutableLiveData<>();

    public void setSightings(List<Sighting> sightings) {
        this.sightings.setValue(sightings);
    }
    public LiveData<List<Sighting>> getSightings() {
        return sightings;
    }

    private final MutableLiveData<Sighting> selectedSighting = new MutableLiveData<>();
    public void selectSighting(Sighting sighting) {
        this.selectedSighting.setValue(sighting);
    }
    public LiveData<Sighting> getSelectedSighting() {
        return selectedSighting;
    }
}

