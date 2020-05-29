package edu.uci.ics.aaront8.service.movies.models;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ThumbnailModel {

    @JsonProperty(value = "movie_id", required = true)
    private String movie_id;

    @JsonProperty(value = "title", required = true)
    private String title;

    @JsonProperty(value = "backdrop_path")
    private String backdrop_path;

    @JsonProperty(value = "poster_path")
    private String poster_path;

    @JsonCreator
    public ThumbnailModel(@JsonProperty(value = "movie_id", required = true) String movie_id,
                          @JsonProperty(value = "title", required = true) String title,
                          @JsonProperty(value = "backdrop_path") String backdrop_path,
                          @JsonProperty(value = "poster_path") String poster_path){

   // public ThumbnailModel(String movie_id, String title, String backdrop_path, String poster_path){

        this.movie_id = movie_id;
        this.title = title;
        this.backdrop_path = backdrop_path;
        this.poster_path = poster_path;


    }

    @JsonProperty("movie_id")
    public String getMovie_id() {
        return movie_id;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }
    @JsonProperty("backdrop_path")
    public String getBackdrop_path() {
        return backdrop_path;
    }
    @JsonProperty("poster_path")
    public String getPoster_path() {
        return poster_path;
    }
}
