package com.sage.models;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by root on 2/21/16.
 */
public class User {

    private static int latestUserId = 0;

    private int userId;

    private String userName;

    private String userEmail;

    public User() {
        // set and increment userId
        userId = latestUserId++;
    }

    @XmlElement(name = "userId")
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @XmlElement(name = "userName")
    public String getUserName() { return userName; }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @XmlElement(name = "userEmail")
    public String getUserEmail() { return userEmail; }

    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
}
