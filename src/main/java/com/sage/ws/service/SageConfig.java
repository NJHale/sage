package com.sage.ws.service;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by Nick Hale on 3/13/16.
 * @author  Nick Hale
 *          NJohnHale@gmail.com
 *
 */
public class SageConfig {

    @JsonProperty("googleClientId")
    public String googleClientId;

    @JsonProperty("googleClientSecret")
    public String googleClientSecret;

    @JsonProperty("buildzoneDir")
    public String buildzoneDir;


    public SageConfig() { }

    public String getGoogleClientId() {
        return googleClientId;
    }

    public void setGoogleClientId(String googleClientId) {
        this.googleClientId = googleClientId;
    }

    public String getGoogleClientSecret() {
        return googleClientSecret;
    }

    public void setGoogleClientSecret(String googleClientSecret) {
        this.googleClientSecret = googleClientSecret;
    }

    public String getBuildzoneDir() {
        return buildzoneDir;
    }

    public void setBuildzoneDir(String buildzoneDir) {
        this.buildzoneDir = buildzoneDir;
    }
}
