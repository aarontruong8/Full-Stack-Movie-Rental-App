package edu.uci.ics.aaront8.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class MovieAddResponseModel {

    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;

    @JsonProperty(value = "message", required = true)
    private String message;

    @JsonProperty(value = "movie_id", required = true)
    private String movie_id;

    @JsonCreator
    public MovieAddResponseModel(@JsonProperty(value = "resultCode", required = true) int resultCode,
                         @JsonProperty(value = "message", required = true) String message,
                         @JsonProperty(value = "movie_id", required = true) String movie_id){

        this.resultCode = resultCode;
        this.message = message;
        this.movie_id = movie_id;
    }

    @JsonProperty("resultCode")
    public int getResultCode() {
        return resultCode;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("movie_id")
    public String getMovie_id() { return movie_id;}
}
