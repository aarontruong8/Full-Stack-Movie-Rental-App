package edu.uci.ics.aaront8.service.movies.models;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.ArrayList;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PeopleSearchResponseModel {


    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;

    @JsonProperty(value = "message", required = true)
    private String message;

    @JsonProperty(value = "people", required = true)
    private ArrayList<PersonSearchModel> people;

    @JsonCreator
    public PeopleSearchResponseModel(@JsonProperty(value = "resultCode", required = true) int resultCode,
                                     @JsonProperty(value = "message", required = true) String message,
                                     @JsonProperty(value = "people", required = true) ArrayList<PersonSearchModel> people){


        this.resultCode = resultCode;
        this.message = message;
        this.people = people;



    }

    @JsonProperty("resultCode")
    public int getResultCode() {
        return resultCode;
    }
    @JsonProperty("message")
    public String getMessage() {
        return message;
    }
    @JsonProperty("people")
    public ArrayList<PersonSearchModel> getPeople() {
        return people;
    }
}
