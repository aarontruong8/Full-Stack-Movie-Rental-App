package edu.uci.ics.aaront8.service.movies.models;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.lang.reflect.Array;
import java.util.ArrayList;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MovieResponseModel {

    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;

    @JsonProperty(value = "message", required = true)
    private String message;

    @JsonProperty(value = "movies", required = true)
    private ArrayList<MovieModel> movies;

    @JsonCreator

    public MovieResponseModel(@JsonProperty(value = "resultCode", required = true) int resultCode,
                              @JsonProperty(value = "message", required = true) String message,
                              @JsonProperty(value = "movies", required = true) ArrayList<MovieModel> movies){

   //     public MovieResponseModel(int resultCode, String message, ArrayList<MovieModel> movies){
        this.resultCode = resultCode;
        this.message = message;
        this.movies = movies;

    }

    @JsonProperty("resultCode")
    public int getResultCode() {
        return resultCode;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("movies")
    public ArrayList<MovieModel> getMovies() {
        return movies;
    }
}
