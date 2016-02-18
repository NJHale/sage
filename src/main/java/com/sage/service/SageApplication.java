package com.sage.service;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

/**
 * Created by root on 2/18/16.
 */
@ApplicationPath("/")
public class SageApplication extends Application {
    public Set<Class<?>> getClasses() {
        return new HashSet<Class<?>>();
    }
}