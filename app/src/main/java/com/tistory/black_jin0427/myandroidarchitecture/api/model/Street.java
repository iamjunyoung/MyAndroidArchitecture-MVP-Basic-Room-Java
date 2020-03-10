package com.tistory.black_jin0427.myandroidarchitecture.api.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Street implements Serializable {
    @SerializedName("number") public int number;
    @SerializedName("name") public String name;

}
