package com.sage.ws.service;

import com.google.common.io.Files;
import com.sun.javaws.exceptions.InvalidArgumentException;
import com.sun.jersey.core.util.Base64;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;

/**
 * Created by root on 3/2/16.
 */
public class JavaToDexTranslator {

    /**
     * Default constructor for JavaToDexTranslator
     */
    public JavaToDexTranslator() {

    }

    /**
     * Converts and compiles the given encoded java to
     * a dex file
     * @param encodedJava 2 base64 encoded groupings delimited
     *                    by a '.' in format - className.javaClassCodeAsString
     * @return Base64 encoded dex file representation of the given encoded java
     *         2 base64 encoded groupings delimited
     *         by a '.' in format - className.dexCodeAsString
     */
    public String encodedJavaToDex(String encodedJava) throws Exception {
        // get the name of the given encoded Java file
        if (encodedJava == null || !encodedJava.contains(".") || encodedJava.split(".").length > 2
                || encodedJava.substring(encodedJava.indexOf(".")).length() <= 1) {
            // There should be a delimiting '.' between the file name and the encoded content
            // if it exists there should be at least one character before it
            throw new IllegalArgumentException("Encoded java - " + encodedJava + " malformed!");
        }
        // parse each grouping
        String[] grps = encodedJava.split(".");
        // instantiate an index for stepping through groups
        int idx = 0;
        String fqn = Base64.base64Decode(grps[idx++]);// get the fully qualified object name
        String src = Base64.base64Decode(grps[idx++]);// get the java file

        //TODO: Convert Java file to dex

        // compile java to a class and then
        // save the src temporarily to .java
        File root = new File("~/java");
        File sourceFile = new File(root, fqn);
        sourceFile.getParentFile().mkdirs();
        Files.write(src, sourceFile, StandardCharsets.UTF_8);

        // Compile src
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        compiler.run(null, null, null, sourceFile.getPath());

        // load and instantiate compiled class.
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { root.toURI().toURL() });
        Class<?> cls = Class.forName(fqn, true, classLoader);
        // make sure the compiled class can be cast to type SageTask
        SageTask instance = (SageTask) cls.newInstance();

        //
        return null;
    }
}
