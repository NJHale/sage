package com.sage.ws.models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.File;

/**
 * Created by root on 2/24/16.
 */
@XmlRootElement
public class JobOrder {

    private int bounty;

    private long timeOut;

    private byte[] data;

    private String encodedJava;

    /**
     * Default JobOrder constructor
     */
    public JobOrder() {

    }

    /**
     * Getters and Setters annotated with Jersey mappings
     */

    @XmlElement(name = "bounty")
    public int getBounty() {
        return bounty;
    }

    public void setBounty(int bounty) {
        this.bounty = bounty;
    }

    @XmlElement(name = "timeOut")
    public long getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(long timeOut) {
        this.timeOut = timeOut;
    }

    @XmlElement(name = "data")
    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @XmlElement(name = "encodedJava")
    public String getEncodedJava() {
        return encodedJava;
    }

    public void setEncodedJava(String encodedJava) {
        this.encodedJava = encodedJava;
    }
}
