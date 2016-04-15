package com.sage.ws.models;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Nick Hale on 4/10/16.
 * @author Nick Hale
 *         NJohnHale@gmail.com
 *
 */
@XmlRootElement()
@Entity
@Table(name = "java")
public class Java {

    /**
     * Hibernate Annotations for ORM persistence
     */
    @Id
    @GeneratedValue
    @Column(name = "java_id")
    private int javaId;

    @Column(name = "creator_id")
    private int creatorId;

    @Column(name = "encoded_java")
    private String encodedJava;

    @Column(name = "encoded_dex")
    private String encodedDex;

    /**
     * Default constructor
     */
    public Java() { }


    /**
     * Jersey JAXB Annotations
     */

    @XmlElement(name = "javaId")
    public int getJavaId() {
        return javaId;
    }

    public void setJavaId(int javaId) {
        this.javaId = javaId;
    }

    @XmlElement(name = "creatorId")
    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    @XmlElement(name = "encodedJava")
    public String getEncodedJava() {
        return encodedJava;
    }

    public void setEncodedJava(String encodedJava) {
        this.encodedJava = encodedJava;
    }

    @XmlElement(name = "encodedDex")
    public String getEncodedDex() {
        return encodedDex;
    }

    public void setEncodedDex(String encodedDex) {
        this.encodedDex = encodedDex;
    }
}
