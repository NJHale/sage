package com.sage.ws.models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Nick Hale on 4/14/16.
 * @author Nick Hale
 *         NJohnHale@gmail.com
 *
 */
@XmlRootElement()
public class UserCredential {

    private String googleIdStr;

    public UserCredential() { }

    @XmlElement(name = "googleIdStr")
    public String getGoogleIdToken() {
        return googleIdStr;
    }

    public void setGoogleIdStr(String googleIdStr) {
        this.googleIdStr = googleIdStr;
    }

}
