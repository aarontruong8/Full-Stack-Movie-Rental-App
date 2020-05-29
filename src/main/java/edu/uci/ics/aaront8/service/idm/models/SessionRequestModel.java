package edu.uci.ics.aaront8.service.idm.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;


public class SessionRequestModel {

    @JsonProperty(value = "email", required = true)
    private String email;

    @JsonProperty(value = "session_id", required = true)
    private String session_id;

    @JsonCreator
    public SessionRequestModel(@JsonProperty(value = "email", required = true) String email,
                                @JsonProperty(value = "session_id", required = true) String session_id){

        this.email = email;
        this.session_id = session_id;

    }
    @JsonProperty("email")
    public String getEmail() {
        return email;
    }
    @JsonProperty("session_id")
    public String getSession_Id() {
        return session_id;
    }
}

