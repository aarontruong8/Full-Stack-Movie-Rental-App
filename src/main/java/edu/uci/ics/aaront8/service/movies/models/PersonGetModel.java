package edu.uci.ics.aaront8.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

//@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonGetModel {

    @JsonProperty(value = "person_id", required = true)
    private int person_id;

    @JsonProperty(value = "name", required = true)
    private String name;

    @JsonProperty(value = "gender")
    private String gender;

    @JsonProperty(value = "birthday")
    private String birthday;

    @JsonProperty(value = "deathday")
    private String deathday;

    @JsonProperty(value = "biography")
    private String biography;

    @JsonProperty(value = "birthplace")
    private String birthplace;

    @JsonProperty(value = "popularity")
    private float popularity;

    @JsonProperty(value = "profile_path")
    private String profile_path;

    @JsonCreator
    public PersonGetModel(@JsonProperty(value = "person_id", required = true) int person_id,
                          @JsonProperty(value = "name", required = true) String name,
                          @JsonProperty(value = "gender") String gender,
                          @JsonProperty(value = "birthday") String birthday,
                          @JsonProperty(value = "deathday") String deathday,
                          @JsonProperty(value = "biography") String biography,
                          @JsonProperty(value = "birthplace") String birthplace,
                          @JsonProperty(value = "popularity") float popularity,
                          @JsonProperty(value = "profile_path") String profile_path){

        this.person_id = person_id;
        this.name = name;
        this.gender = gender;
        this.birthday = birthday;
        this.deathday = deathday;
        this.biography = biography;
        this.birthplace = birthplace;
        this.popularity = popularity;
        this.profile_path = profile_path;


    }

    @JsonProperty("person_id")
    public int getPerson_id() {
        return person_id;
    }
    @JsonProperty("name")
    public String getName() {
        return name;
    }
    @JsonProperty("gender")
    public String getGender() {
        return gender;
    }
    @JsonProperty("birthday")
    public String getBirthday() {
        return birthday;
    }
    @JsonProperty("deathday")
    public String getDeathday() {
        return deathday;
    }
    @JsonProperty("biography")
    public String getBiography() {
        return biography;
    }
    @JsonProperty("birthplace")
    public String getBirthplace() {
        return birthplace;
    }
    @JsonProperty("popularity")
    public float getPopularity() {
        return popularity;
    }
    @JsonProperty("profile_path")
    public String getProfile_path() {
        return profile_path;
    }
}
