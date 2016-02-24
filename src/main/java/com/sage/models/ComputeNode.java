package com.sage.models;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by root on 2/21/16.
 */
public class ComputeNode {

    private static int latestComputeId = 0;

    private int computeId;

    private int user;

    private String info;

    public ComputeNode(){
        // set and increment computeId
        computeId = latestComputeId++;
    }

    @XmlElement(name = "computeId")
    public int getComputeId(){
        return computeId;
    }

    public void setComputeId(int computeId){
        this.computeId = computeId;
    }

    @XmlElement(name = "user")
    public int getUser(){
        return user;
    }

    public void setUser(int user){
        this.user = user;
    }

    @XmlElement(name = "info")
    public String getInfo(){
        return info;
    }

    public void setInfo(String info){
        this.info = info;
    }

}
