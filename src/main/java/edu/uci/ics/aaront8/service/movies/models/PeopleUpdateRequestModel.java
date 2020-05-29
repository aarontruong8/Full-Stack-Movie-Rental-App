package edu.uci.ics.aaront8.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Date;

public class PeopleUpdateRequestModel {

    @JsonProperty(value = "person_id", required = true)
    private int person_id;

    @JsonProperty(value = "name", required = true)
    private String name;

    @JsonProperty(value = "gender_id")
    private int gender_id;

    @JsonProperty(value = "birthday")
    private Date birthday;

    @JsonProperty(value = "deathday")
    private Date deathday;

    @JsonProperty(value = "biography")
    private String biography;

    @JsonProperty(value = "birthplace")
    private String birthplace;

    @JsonProperty(value = "popularity")
    private float popularity;

    @JsonProperty(value = "profile_path")
    private String profile_path;

    @JsonCreator
    public PeopleUpdateRequestModel(@JsonProperty(value = "person_id", required = true) int person_id,
                                    @JsonProperty(value = "name", required = true) String name,
                                    @JsonProperty(value = "gender_id") int gender_id,
                                    @JsonProperty(value = "birthday") Date birthday,
                                    @JsonProperty(value = "deathday") Date deathday,
                                    @JsonProperty(value = "biography") String biography,
                                    @JsonProperty(value = "birthplace") String birthplace,
                                    @JsonProperty(value = "popularity") float popularity,
                                    @JsonProperty(value = "profile_path") String profile_path){

        this.person_id = person_id;
        this.name = name;
        this.gender_id = gender_id;
        this.birthday = birthday;
        this.deathday = deathday;
        this.biography = biography;
        this.birthplace = birthplace;
        this.popularity = popularity;
        this.profile_path = profile_path;


    }
    @JsonProperty(value = "person_id")
    public int getPerson_id() {
        return person_id;
    }
    @JsonProperty(value = "name")
    public String getName() {
        return name;
    }
    @JsonProperty(value = "gender_id")
    public int getGender_id() {
        return gender_id;
    }
    @JsonProperty(value = "birthday")
    public Date getBirthday() {
        return birthday;
    }
    @JsonProperty(value = "deathday")
    public Date getDeathday() {
        return deathday;
    }
    @JsonProperty(value = "biography")
    public String getBiography() {
        return biography;
    }
    @JsonProperty(value = "birthplace")
    public String getBirthplace() {
        return birthplace;
    }
    @JsonProperty(value = "popularity")
    public float getPopularity() {
        return popularity;
    }
    @JsonProperty(value = "profile_path")
    public String getProfile_path() {
        return profile_path;
    }
}
