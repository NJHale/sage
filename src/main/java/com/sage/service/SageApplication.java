package com.sage.service;

import com.sage.models.JobOrder;
import com.sage.resources.GoatsResource;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.*;

import javax.ws.rs.core.Application;

/**
 * Created by root on 2/18/16.
 */
@ApplicationPath("/0.1/")
public class SageApplication extends Application {


    public static List<Object> everything = new LinkedList<Object>();

    @Override
    public Set<Class<?>> getClasses() {
        // get all resource classes from the com.sage.resources directory
        ResourceConfig config = new PackagesResourceConfig("com.sage.resources");
        Set<Class<?>> classes = config.getClasses();
        return classes;
    }

}