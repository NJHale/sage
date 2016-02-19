package com.sage.service;

import com.sage.resources.GoatsResource;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

/**
 * Created by root on 2/18/16.
 */
@ApplicationPath("/sage")
public class SageApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        // get all resource classes from the com.sage.resources directory
        ResourceConfig config = new PackagesResourceConfig("com.sage.resources");
        Set<Class<?>> classes = config.getClasses();
        return classes;
    }

}