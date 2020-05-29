package edu.uci.ics.aaront8.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MovieAddRequestModel {


    @JsonProperty(value = "title", required = true)
    private String title;

    @JsonProperty(value = "year", required = true)
    private int year;

    @JsonProperty(value = "director", required = true)
    private String director;

    @JsonProperty(value = "rating")
    private float rating;

    @JsonProperty(value = "num_votes")
    private int num_votes;

    @JsonProperty(value = "budget")
    private String budget;

    @JsonProperty(value = "revenue")
    private String revenue;

    @JsonProperty(value = "overview")
    private String overview;

    @JsonProperty(value = "backdrop_path")
    private String backdrop_path;

    @JsonProperty(value = "poster_path")
    private String poster_path;

    @JsonProperty(value = "hidden")
    private boolean hidden;

    @JsonCreator
    public MovieAddRequestModel(@JsonProperty(value = "title", required = true) String title,
                                @JsonProperty(value = "year", required = true) int year,
                                @JsonProperty(value = "director", required = true) String director,
                                @JsonProperty(value = "rating") float rating,
                                @JsonProperty(value = "num_votes") int num_votes,
                                @JsonProperty(value = "budget") String budget,
                                @JsonProperty(value = "revenue") String revenue,
                                @JsonProperty(value = "overview") String overview,
                                @JsonProperty(value = "backdrop_path") String backdrop_path,
                                @JsonProperty(value = "poster_path") String poster_path,
                                @JsonProperty(value = "hidden") boolean hidden){

        this.title = title;
        this.year = year;
        this.director = director;
        this.rating = rating;
        this.num_votes = num_votes;
        this.budget = budget;
        this.revenue = revenue;
        this.overview = overview;
        this.backdrop_path = backdrop_path;
        this.poster_path = poster_path;
        this.hidden = hidden;
    }


    @JsonProperty(value = "title")
    public String getTitle() {
        return title;
    }
    @JsonProperty(value = "year")
    public int getYear() {
        return year;
    }
    @JsonProperty(value = "director")
    public String getDirector() {
        return director;
    }
    @JsonProperty(value = "rating")
    public float getRating() {
        return rating;
    }
    @JsonProperty(value = "num_votes")
    public int getNum_votes() {
        return num_votes;
    }
    @JsonProperty(value = "budget")
    public String getBudget() {
        return budget;
    }
    @JsonProperty(value = "revenue")
    public String getRevenue() {
        return revenue;
    }
    @JsonProperty(value = "overview")
    public String getOverview() {
        return overview;
    }
    @JsonProperty(value = "backdrop_path")
    public String getBackdrop_path() {
        return backdrop_path;
    }
    @JsonProperty(value = "poster_path")
    public String getPoster_path() {
        return poster_path;
    }
    @JsonProperty(value = "hidden")
    public boolean isHidden() {
        return hidden;
    }
}
