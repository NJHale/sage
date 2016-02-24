package com.sage.models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.File;

/**
 * Created by root on 2/24/16.
 */
@XmlRootElement
public class JobOrder {

    // Hibernate annotations
    private int jobOrderId;

    private int bounty;

    private long timeOut;

    private byte[] data;

    private File javaFile;

    // Default constructor
    public JobOrder() {

    }

    // Jersey JAXB Annotations

    @XmlTransient
    public int getJobOrderId() {
        return jobOrderId;
    }

    public void setJobOrderId(int jobOrderId) {
        this.jobOrderId = jobOrderId;
    }

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

    @XmlElement(name = "javaFile")
    public File getJavaFile() {
        return javaFile;
    }

    public void setJavaFile(File javaFile) {
        this.javaFile = javaFile;
    }
}
