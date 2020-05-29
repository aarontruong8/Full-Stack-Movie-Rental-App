package edu.uci.ics.aaront8.service.movies.models;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class MovieId_MovieResponseModel {


    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;

    @JsonProperty(value = "message", required = true)
    private String message;

    @JsonProperty(value = "movie", required = true)
    private GetMovieId_MovieModel movie;

    @JsonCreator
    public MovieId_MovieResponseModel(@JsonProperty(value = "resultCode", required = true) int resultCode,
                              @JsonProperty(value = "message", required = true) String message,
                              @JsonProperty(value = "movie", required = true) GetMovieId_MovieModel movie){

        this.resultCode = resultCode;
        this.message = message;
        this.movie = movie;

    }
    @JsonProperty("resultCode")
    public int getResultCode() {
        return resultCode;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("movie")
    public GetMovieId_MovieModel getMovies() {
        return movie;
    }
}
