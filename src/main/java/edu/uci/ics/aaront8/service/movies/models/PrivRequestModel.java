package edu.uci.ics.aaront8.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PrivRequestModel {
    @JsonProperty(value = "email", required = true)
    private String email;

    @JsonProperty(value = "plevel", required = true)
    private int plevel;

    @JsonCreator
    public PrivRequestModel(@JsonProperty(value = "email", required = true ) String email,
                            @JsonProperty(value = "plevel", required = true) int plevel){

        this.email = email;
        this.plevel = plevel;
    }
    @JsonProperty(value = "email")
    public String getEmail() {
        return email;
    }
    @JsonProperty(value = "plevel")
    public int getPlevel() {
        return plevel;
    }
}
