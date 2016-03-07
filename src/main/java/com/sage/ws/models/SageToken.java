package com.sage.ws.models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by root on 2/27/16.
 */
@XmlRootElement
public class SageToken {

    private String tokenString;

    private int userId;

    /**
     * Default SageToken constructor
     */
    public SageToken() {

    }

    @XmlElement(name = "tokenString")
    public String getTokenString() {
        return tokenString;
    }

    public void setTokenString(String tokenString) {
        this.tokenString = tokenString;
    }

    @XmlElement(name = "user")
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
