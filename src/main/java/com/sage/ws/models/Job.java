package com.sage.ws.models;



import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by Nick Hale on 2/21/16.
 * @author Nick Hale
 *         NJohnHale@gmail.com
 *
 */
@XmlRootElement
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "job")
public class Job {

    /**
     * Hibernate Annotations for ORM persistence
     */
    @Id
    @GeneratedValue()
    @Column(name = "job_id")
    private int jobId;

    @Column(name = "orderer_id")
    private int ordererId;

    @Column(name = "node_id")
    private int nodeId;

    @Column(name = "java_id")
    private int javaId;

    @Column(name = "bounty")
    private BigDecimal bounty;

    @Column(name = "status")
    private JobStatus status;

    @Column(name = "timeout")
    private long timeout;

    @Column(name = "completion")
    private Date completion;

    @Column(name = "data")
    private byte[] data;

    @Column(name = "result")
    private byte[] result;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "_ts", nullable = true)
    private Date _ts;


    /**
     * Default constructor
     */
    public Job() { }

    /**
     * Jersey JAXB Annotations
     */
    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    @XmlElement(name = "ordererId")
    public int getOrdererId() {
        return ordererId;
    }

    public void setOrdererId(int ordererId) {
        this.ordererId = ordererId;
    }

    @XmlElement(name = "nodeId")
    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    @XmlElement(name = "javaId")
    public int getJavaId() {
        return javaId;
    }

    public void setJavaId(int javaId) {
        this.javaId = javaId;
    }

    @XmlElement(name = "bounty")
    public BigDecimal getBounty() {
        return bounty;
    }

    public void setBounty(BigDecimal bounty) {
        this.bounty = bounty;
    }

    @XmlElement(name = "status")
    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    @XmlElement(name = "timeout")
    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

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

    @XmlTransient
    @JsonIgnore
    public Date get_ts() {
        return _ts;
    }

    public void set_ts(Date _ts) {
        this._ts = _ts;
    }


}
