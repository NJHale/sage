package com.sage.ws.service;

import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.*;

/**
 * Created by root on 2/18/16.
 */
@ApplicationPath("/")
public class SageApplication extends Application {


    @Override
    public Set<Class<?>> getClasses() {
        // get all resource classes from the com.sage.resources directory
        ResourceConfig config = new PackagesResourceConfig("com.sage.resources");
        Set<Class<?>> classes = config.getClasses();
        return classes;
    }

}