package com.tistory.black_jin0427.myandroidarchitecture.api.model;

import android.arch.persistence.room.Embedded;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Location implements Serializable {
    @Embedded
    @SerializedName("street") public Street street;

    @SerializedName("city") public String city;

    @SerializedName("state") public String state;

    @SerializedName("postcode") public String postcode;

}
