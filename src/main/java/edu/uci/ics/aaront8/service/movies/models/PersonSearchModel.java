package edu.uci.ics.aaront8.service.movies.models;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonSearchModel {


    @JsonProperty(value = "person_id", required = true)
    private int person_id;

    @JsonProperty(value = "name", required = true)
    private String name;

    @JsonProperty(value = "birthday")
    private String birthday = null;

    @JsonProperty(value = "popularity")
    private float popularity;

    @JsonProperty(value = "profile_path")
    private String profile_path = null;

    @JsonCreator
    public PersonSearchModel(@JsonProperty(value = "person_id", required = true) int person_id,
                             @JsonProperty(value = "name", required = true) String name,
                             @JsonProperty(value = "birthday") String birthday,
                             @JsonProperty(value = "popularity") float popularity,
                             @JsonProperty(value = "profile_path") String profile_path){


        this.person_id = person_id;
        this.name = name;
        this.birthday = birthday;
        this.popularity = popularity;
        this.profile_path = profile_path;


    }

    public PersonSearchModel(){


    }

    @JsonProperty("person_id")
    public int getPerson_id() {
        return person_id;
    }
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("birthday")
    public String getBirthday() {
        return birthday;
    }

    @JsonProperty("popularity")
    public float getPopularity() {
        return popularity;
    }
    @JsonProperty("profile_path")
    public String getProfile_path() {
        return profile_path;
    }

    @JsonProperty("birthday")
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
    @JsonProperty("popularity")
    public void setPopularity(float popularity) {
        this.popularity = popularity;
    }
    @JsonProperty("profile_path")
    public void setProfile_path(String profile_path) {
        this.profile_path = profile_path;


    }
    @JsonProperty("person_id")
    public void setPerson_id(int person_id) {
        this.person_id = person_id;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }
}
