package edu.uci.ics.aaront8.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetMovieId_MovieModel {

    @JsonProperty(value = "movie_id", required = true)
    private String movie_id;

    @JsonProperty(value = "title", required = true)
    private String title;

    @JsonProperty(value = "year", required = true)
    private int year;

    @JsonProperty(value = "director", required = true)
    private String director;

    @JsonProperty(value = "rating", required = true)
    private float rating;

    @JsonProperty(value = "num_votes", required = true)
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
    private Boolean hidden;

    @JsonProperty(value = "genres", required = true)
    private ArrayList<GenreModel> genres;

    @JsonProperty(value = "people", required = true)
    private ArrayList<PersonModel> people;

    @JsonCreator
    public GetMovieId_MovieModel(@JsonProperty(value = "movie_id", required = true) String movie_id,
                      @JsonProperty(value = "title", required = true) String title,
                      @JsonProperty(value = "year", required = true) int year,
                      @JsonProperty(value = "director", required = true) String director,
                      @JsonProperty(value = "rating", required = true) float rating,
                      @JsonProperty(value = "num_votes", required = true) int num_votes,
                      @JsonProperty(value = "budget") String budget,
                      @JsonProperty(value = "revenue") String revenue,
                      @JsonProperty(value = "overview") String overview,
                      @JsonProperty(value = "backdrop_path") String backdrop_path,
                      @JsonProperty(value = "poster_path") String poster_path,
                      @JsonProperty(value = "hidden") Boolean hidden,
                      @JsonProperty(value = "genres", required = true) ArrayList<GenreModel> genres,
                      @JsonProperty(value = "people", required = true) ArrayList<PersonModel> people)
    {

        this.movie_id = movie_id;
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
        this.genres = genres;
        this.people = people;
    }

    @JsonProperty("movie_id")
    public String getMovie_id() {
        return movie_id;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("year")
    public int getYear() {
        return year;
    }

    @JsonProperty("director")
    public String getDirector() {
        return director;
    }

    @JsonProperty("rating")
    public float getRating() {
        return rating;
    }

    @JsonProperty("backdrop_path")
    public String getBackdrop_path() {
        return backdrop_path;
    }

    @JsonProperty("poster_path")
    public String getPoster_path() {
        return poster_path;
    }

    @JsonProperty("hidden")
    public Boolean getHidden() {
        return hidden;
    }

    @JsonProperty("num_votes")
    public int getNum_votes() {
        return num_votes;
    }
    @JsonProperty("budget")
    public String getBudget() {
        return budget;
    }
    @JsonProperty("revenue")
    public String getRevenue() {
        return revenue;
    }

    @JsonProperty("overview")
    public String getOverview() {
        return overview;
    }

    @JsonProperty("genres")
    public ArrayList<GenreModel> getGenres() {
        return genres;
    }
    @JsonProperty("people")
    public ArrayList<PersonModel> getPeople() {
        return people;
    }

    @JsonProperty("genres")
    public void setGenres(ArrayList<GenreModel> genres) {
        this.genres = genres;
    }

    @JsonProperty("people")
    public void setPeople(ArrayList<PersonModel> people) {
        this.people = people;
    }
}
