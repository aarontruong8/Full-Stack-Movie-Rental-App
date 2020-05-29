package edu.uci.ics.aaront8.service.movies.models;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ThumbnailResponseModel {


    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;

    @JsonProperty(value = "message", required = true)
    private String message;

    @JsonProperty(value = "thumbnails", required = true)
    private ArrayList<ThumbnailModel> thumbnails;

    @JsonCreator
    public ThumbnailResponseModel(@JsonProperty(value = "resultCode", required = true) int resultCode,
                                  @JsonProperty(value = "message", required = true) String message,
                                  @JsonProperty(value = "thumbnails", required = true) ArrayList<ThumbnailModel> thumbnails){
    //@JsonCreator
  //  public ThumbnailResponseModel(int resultCode, String message, ArrayList<ThumbnailModel> thumbnails){
        this.resultCode = resultCode;
        this.message = message;
        this.thumbnails = thumbnails;



    }
    @JsonProperty("resultCode")
    public int getResultCode() {
        return resultCode;
    }
    @JsonProperty("message")
    public String getMessage() {
        return message;
    }
    @JsonProperty("thumbnails")
    public ArrayList<ThumbnailModel> getThumbnails() {
        return thumbnails;
    }
}
