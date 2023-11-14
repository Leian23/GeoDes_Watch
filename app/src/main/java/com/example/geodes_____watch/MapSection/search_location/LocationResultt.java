package com.example.geodes_____watch.MapSection.search_location;

import android.os.Parcel;
import android.os.Parcelable;

public class LocationResultt implements Parcelable {
    private String name;
    private double latitude;
    private double longitude;

    public LocationResultt(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    // Parcelable implementation
    protected LocationResultt(Parcel in) {
        name = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LocationResultt> CREATOR = new Creator<LocationResultt>() {
        @Override
        public LocationResultt createFromParcel(Parcel in) {
            return new LocationResultt(in);
        }

        @Override
        public LocationResultt[] newArray(int size) {
            return new LocationResultt[size];
        }
    };
}
