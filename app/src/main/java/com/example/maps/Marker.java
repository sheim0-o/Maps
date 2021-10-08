package com.example.maps;
import com.google.android.gms.maps.model.LatLng;

public class Marker {
    private LatLng location;
    private String name;

    public Marker(LatLng location, String name){
        this.location = location;
        this.name=name;
    }
    public LatLng getLocation() { return this.location; }
    public void setLocation(LatLng location) {
        this.location = location;
    }

    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
