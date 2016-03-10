package com.sage.ws.util;

import com.sage.task.SageTask;

import com.google.common.io.Files;
import com.sage.ws.resources.JobOrdersResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    public JavaToDexTranslator() { }

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
        // get the fully qualified object name
        String fqn = new String(DatatypeConverter.parseBase64Binary(grps[idx++]));
        // get the java file
        String src = new String(DatatypeConverter.parseBase64Binary(grps[idx++]));

        logger.debug("fqn: " + fqn);
        logger.debug("src: " + src);

        // create a unique temp directory to store the java, class, and dex files
        File root = new File("buildzone/" + this.hashCode());
        logger.debug("Making temp directory for compiled java and dex...");
        if (!root.mkdirs() && !root.isDirectory()) {
            // In this case the temp directory could not be created
            throw new IOException("Could not make the temporary directories required for dex compilation.");
        }
        logger.debug("Temp directory made!");

        //save the src temporarily to .java
        File sourceFile = new File(root, fqn + ".java");
        Files.write(src, sourceFile, StandardCharsets.UTF_8);

        // convert the class file to a dex file using the dx command
        // for now hardcode unix command
        //Create a ProcessBuilder using the java2dex.sh resource as its command.
        //java2dex.sh takes 2 arguments, where $1 is the path to the file's directory,
        //and $2 is the file name without the extension
        ProcessBuilder pb = new ProcessBuilder( "/bin/bash",
                "src/main/resources/config/java2dex.sh",
                root.getAbsolutePath()+"/", fqn);
        pb.redirectErrorStream(true);

        //Start the process
        Process p = pb.start();
        BufferedReader reader = new BufferedReader (new InputStreamReader(p.getInputStream()));
        while ((reader.readLine ()) != null) {
            logger.error("Stdout: " + reader.readLine());
        }
        //Wait for it to finish
        p.waitFor();


        // load and instantiate compiled class.
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { root.toURI().toURL() });
        Class<?> cls = Class.forName(fqn, true, classLoader);
        // make sure the compiled class can be cast to type SageTask
        SageTask task = (SageTask) cls.newInstance();

        // read the dex file into a String
        File dexFile = new File(root.getAbsolutePath() + "/" + fqn + ".dex");
        String dex = Files.toString(dexFile, StandardCharsets.UTF_8);
        // encode the dex
        String encodedDex = DatatypeConverter.printBase64Binary(fqn.getBytes())
                + "." + DatatypeConverter.printBase64Binary(dex.getBytes());

        // delete the temporary directory
        if (!recDelete(root)) {
            logger.error("An error occurred while attempting to delete the temporary directory.");
        }

        return encodedDex;
    }

    /**
     * Recursively deletes a directory including all subdirectories and files
     *
     * @param file Directory to delete
     * @return true if the directory has been successfully deleted; false otherwise
     */
    private boolean recDelete(File file) {
        boolean deleted = true;
        if (file.isDirectory()) {
            for (File f : file.listFiles())
                deleted &= recDelete(f);
        }
        deleted &= file.delete();
        return deleted;
    }
}