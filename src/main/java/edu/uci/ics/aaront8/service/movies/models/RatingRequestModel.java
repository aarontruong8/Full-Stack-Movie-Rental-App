package edu.uci.ics.aaront8.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RatingRequestModel {

    @JsonProperty(value = "movie_id", required = true)
    private String movie_id;

    @JsonProperty(value = "rating", required = true)
    private float rating;

    @JsonCreator
    public RatingRequestModel(@JsonProperty(value = "movie_id", required = true) String movie_id,
                              @JsonProperty(value = "rating", required = true) float rating){


        this.movie_id = movie_id;
        this.rating = rating;

    }

    @JsonProperty(value = "movie_id")
    public String getMovie_id() {
        return movie_id;
    }

    @JsonProperty(value = "rating")
    public float getRating() {
        return rating;
    }
}
