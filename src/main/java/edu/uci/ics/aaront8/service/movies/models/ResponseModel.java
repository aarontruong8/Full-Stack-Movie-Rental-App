package edu.uci.ics.aaront8.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseModel {

    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;

    @JsonProperty(value = "message", required = true)
    private String message;

    @JsonCreator
    public ResponseModel(@JsonProperty(value = "resultCode", required = true) int resultCode,
                         @JsonProperty(value = "message", required = true) String message){

        this.resultCode = resultCode;
        this.message = message;
    }

    @JsonProperty("resultCode")
    public int getResultCode() {
        return resultCode;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }
}
