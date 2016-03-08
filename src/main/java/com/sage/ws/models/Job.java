package com.sage.ws.models;

import org.hibernate.annotations.Table;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Date;

/**
 * Created by Nick Hale on 2/21/16.
 * @author Nick Hale
 *         NJohnHale@gmail.com
 *
 */
@XmlRootElement
@Entity
@Table(appliesTo = "job")
public class Job {

    /**
     * Hibernate Annotations for ORM persistence
     */
    @Id
    @GeneratedValue
    @Column(name = "job_id")
    private int jobId;

    @Column(name = "orderer_id")
    private int ordererId;

    @Column(name = "nodeid")
    private int nodeId;

    @Column(name = "bounty")
    private int bounty;

    @Column(name = "status")
    private JobStatus status;

    @Column(name = "timeout")
    private long timeOut;

    @Column(name = "encoded_dex")
    private String encodedDex;

    @Column(name = "encoded_java")
    private String encodedJava;

    @Column(name = "data")
    private byte[] data;

    @Column(name = "result")
    private byte[] result;

    @Column(name = "completion")
    private Date completion;

    /**
     * Default constructor
     */
    public Job() { }

    /**
     * Jersey JAXB Annotations
     */
    @XmlElement(name = "jobId")
    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) { this.jobId = jobId; }

    @XmlElement(name = "ordererId")
    public int getOrdererId() { return ordererId; }

    public void setOrdererId(int ordererId) { this.ordererId = ordererId; }

    @XmlElement(name = "nodeId")
    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    @XmlTransient
    public int getBounty() { return bounty; }

    public void setBounty(int bounty) { this.bounty = bounty; }

    @XmlElement(name = "status")
    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    @XmlElement(name = "timeout")
    public long getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(long timeOut) {
        this.timeOut = timeOut;
    }

    @XmlElement(name = "encodedDex")
    public String getEncodedDex() { return encodedDex; }

    public void setEncodedDex(String encodedDex) { this.encodedDex = encodedDex; }

    @XmlTransient
    public String getEncodedJava() { return encodedJava; }

    public void setEncodedJava(String encodedJava) { this.encodedJava = encodedJava; }


    @XmlElement(name = "data")
    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @XmlElement(name = "result")
    public byte[] getResult() {
        return result;
    }

    public void setResult(byte[] result) {
        this.result = result;
    }

    @XmlElement(name = "completion")
    public Date getCompletion() { return completion; }

    public void setCompletion(Date completion) { this.completion = completion; }

}
