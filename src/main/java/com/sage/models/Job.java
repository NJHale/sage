package com.sage.models;

import com.sage.service.Task;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.File;
import java.util.Date;

/**
 * Created by root on 2/21/16.
 */
@XmlRootElement
public class Job {

    // Hibernate Annotations

    private int jobId;

    private int ordererId;

    private int nodeId;

    private JobStatus status;

    private long timeOut;

    private File dexFile;

    private byte[] data;

    private byte[] result;

    private Date completion;

    private Task task;

    // Default Constructor

    public Job() {

    }

    // Jersey JAXB Annotations
    @XmlTransient
    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) { this.jobId = jobId; }

    @XmlTransient
    public int getOrdererId() { return ordererId; }

    public void setOrdererId(int ordererId) { this.ordererId = ordererId; }

    @XmlElement(name = "nodeId")
    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

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

    @XmlElement(name = "dexFile")
    public File getDexFile() {
        return dexFile;
    }

    public void setDexFile(File dexFile) {
        this.dexFile = dexFile;
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
    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
