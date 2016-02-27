package com.sage.models;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by root on 2/21/16.
 */
public class AndroidNode {


    // Unique android node Id
    private String nodeId;

    private int ownerId;

    private String info;

    public AndroidNode(){
        // set and increment computeId
        //computeId = latestComputeId++;
    }

    @XmlElement(name = "nodeId")
    public String getNodeId(){
        return nodeId;
    }

    public void setNodeId(String nodeId){
        this.nodeId = nodeId;
    }

    @XmlElement(name = "ownerId")
    public int getOwnerId(){
        return ownerId;
    }

    public void setOwnerId(int ownerId){
        this.ownerId = ownerId;
    }

    @XmlElement(name = "info")
    public String getInfo(){
        return info;
    }

    public void setInfo(String info){
        this.info = info;
    }

}
