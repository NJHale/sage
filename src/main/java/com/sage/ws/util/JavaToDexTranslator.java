package com.sage.ws.util;

import com.google.common.io.Files;
import com.sage.ws.resources.JobOrdersResource;
import com.sage.ws.service.SageTask;
import com.sun.jersey.core.util.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import javax.xml.bind.DatatypeConverter;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;

/**
 * Created by root on 3/2/16.
 */
public class JavaToDexTranslator {

    private static final Logger logger = LogManager.getLogger(JobOrdersResource.class);

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
        if (encodedJava == null || !encodedJava.contains(".") || encodedJava.split("\\.").length > 2
                || encodedJava.substring(encodedJava.indexOf(".")).length() <= 1) {
            // There should be a delimiting '.' between the file name and the encoded content
            // if it exists there should be at least one character before it
            throw new IllegalArgumentException("Encoded java - " + encodedJava + " malformed!");
        }
        // parse each grouping
        logger.debug("encodedJava: " + encodedJava);
        String[] grps = encodedJava.split("\\.");
        // instantiate an index for stepping through groups
        int idx = 0;
        String fqn = new String(DatatypeConverter.parseBase64Binary(grps[idx++]));// get the fully qualified object name
        String src = new String(DatatypeConverter.parseBase64Binary(grps[idx++]));// get the java file

        logger.debug("fqn: " + fqn);
        logger.debug("src: " + src);

        // create a unique temp directory to store the java, class, and dex files
        File root = new File("~/java/" + this.hashCode());
        logger.debug("Making temp directory for compiled java and dex...");
        if (!root.mkdirs() && !root.isDirectory()) {
            // In this case the temp directory could not be created
            throw new IOException("Could not make the temporary directories required for dex compilation.");
        }
        logger.debug("Temp directory made!");

        // compile java to a class and then save the src temporarily to .java
        File sourceFile = new File(root, fqn + ".java");
        Files.write(src, sourceFile, StandardCharsets.UTF_8);

        // Compile src
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        compiler.run(null, null, null, sourceFile.getPath());

        // load and instantiate compiled class.
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { root.toURI().toURL() });
        Class<?> cls = Class.forName(fqn, true, classLoader);
        // make sure the compiled class can be cast to type SageTask
        SageTask task = (SageTask) cls.newInstance();
        // convert the class file to a dex file using the dx command
        // for now hardcode unix command TODO: Either execute bash or bat script based on deploy architecture
        StringBuilder cmd = new StringBuilder("dx --dex --output ");
        cmd.append(root.getAbsolutePath() + "/" + fqn + ".java ");
        cmd.append(root.getAbsolutePath() + "/" + fqn + ".dex ");
        logger.debug("cmd: " + cmd.toString());
        Process proc = Runtime.getRuntime().exec(cmd.toString());

        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(proc.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(proc.getErrorStream()));

        // read the output from the command
        String s;
        while ((s = stdInput.readLine()) != null) {
            logger.debug(s);
        }

        // read any errors from the attempted command
        while ((s = stdError.readLine()) != null) {
            logger.error(s);
        }
        //TODO: Delete temp directory and files after dex is slurped up into a String
        // read the dex file into a String
        File dexFile = new File(root.getAbsolutePath() + "/" + fqn + ".dex");
        String dex = Files.toString(dexFile, StandardCharsets.UTF_8);
        // encode the dex
        String encodedDex = DatatypeConverter.printBase64Binary(fqn.getBytes())
                + "." + DatatypeConverter.printBase64Binary(dex.getBytes());

        return encodedDex;
    }
}
