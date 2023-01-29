package com.avispamientos.avispamientosandroid.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Sighting {

    private String id;
    private String creator;
    private String information;
    private double latitude;
    private double longitude;
    private long timestamp;
    private long lastTimestamp;
    private String lastContributor;
    private int confirmationCount;

    public Sighting(JSONObject jsonObject) {
        fromJson(jsonObject);
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getCreator() { return creator; }

    public void setCreator(String creator) { this.creator = creator; }

    public String getInformation() { return information; }

    public void setInformation(String information) { this.information = information; }

    public double getLatitude() { return latitude; }

    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }

    public void setLongitude(double longitude) { this.longitude = longitude; }

    public long getTimestamp() { return timestamp; }

    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public int getConfirmationCount() { return confirmationCount; }

    public void setConfirmationCount(int confirmationCount) { this.confirmationCount = confirmationCount; }

    public long getLastTimestamp() { return lastTimestamp; }

    public void setLastTimestamp(long lastTimestamp) { this.lastTimestamp = lastTimestamp; }

    public String getLastContributor() { return lastContributor; }

    public void setLastContributor(String lastContributor) { this.lastContributor = lastContributor; }

    public void fromJson(JSONObject json) {
        try {
            this.id = json.getString("id");
            this.creator = json.getString("creator");
            this.information = json.getString("information");
            this.latitude = json.getDouble("latitude");
            this.longitude = json.getDouble("longitude");
            this.timestamp = json.getLong("timestamp");
            this.lastTimestamp = json.optLong("lastTimestamp", -1);
            this.lastContributor = json.optString("lastContributor", "");
            this.confirmationCount = json.getInt("confirmationCount");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
