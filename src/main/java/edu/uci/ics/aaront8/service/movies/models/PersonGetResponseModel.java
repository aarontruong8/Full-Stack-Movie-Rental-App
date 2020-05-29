package edu.uci.ics.aaront8.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonGetResponseModel {


    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;

    @JsonProperty(value = "message", required = true)
    private String message;

    @JsonProperty(value = "person", required = true)
    private PersonGetModel person;


    @JsonCreator
    public PersonGetResponseModel(@JsonProperty(value = "resultCode", required = true) int resultCode,
                                     @JsonProperty(value = "message", required = true) String message,
                                     @JsonProperty(value = "person", required = true) PersonGetModel person){


        this.resultCode = resultCode;
        this.message = message;
        this.person = person;



    }


    @JsonProperty("resultCode")
    public int getResultCode() {
        return resultCode;
    }
    @JsonProperty("message")
    public String getMessage() {
        return message;
    }
    @JsonProperty("person")
    public PersonGetModel getPerson() {
        return person;
    }


}
