package com.sage.ws.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by root on 2/27/16.
 */
@XmlRootElement
@Entity
@Table(name = "sage_token")
public class SageToken {

    @Column(name = "token_string")
    private String sageTokenStr;

    /**
     * Default SageToken constructor
     */
    public SageToken() {

    }

    @XmlElement(name = "sageTokenStr")
    public String getSageTokenStr() { return sageTokenStr; }

    public void setSageTokenStr(String sageTokenStr) {
        this.sageTokenStr = sageTokenStr;
    }

}
