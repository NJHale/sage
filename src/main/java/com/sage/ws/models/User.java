package com.sage.ws.models;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by root on 2/21/16.
 */
public class User {

    /**
     * Instance variables annotated with Hibernate mappings
     */

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private int userId;

    @Column(name = "user_email")
    private String userEmail;

    /**
     * Default User constructor
     */
    public User() { }

    /**
     * Getters and Setters annotated with Jersey mappings
     */

    @XmlElement(name = "userId")
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @XmlElement(name = "userEmail")
    public String getUserEmail() { return userEmail; }

    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
}
