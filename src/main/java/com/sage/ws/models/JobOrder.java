package com.sage.ws.models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Created by root on 2/24/16.
 */
@XmlRootElement
public class JobOrder {

    private int javaId;

    private BigDecimal bounty;

    private long timeout;

    private byte[] data;


    /**
     * Default JobOrder constructor
     */
    public JobOrder() {

    }

    /**
     * Getters and Setters annotated with Jersey mappings
     */

    @XmlElement(name = "javaId")
    public int getJavaId() { return javaId; }

    public void setJavaId(int javaId){ this.javaId = javaId; }

    @XmlElement(name = "bounty")
    public BigDecimal getBounty() {
        return bounty;
    }

    public void setBounty(BigDecimal bounty) {
        this.bounty = bounty;
    }

    @XmlElement(name = "timeout")
    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeOut) {
        this.timeout = timeOut;
    }

    @XmlElement(name = "data")
    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

}
