package com.sage.ws.util;

import com.sage.task.SageTask;

import com.google.common.io.Files;

import com.sage.ws.service.SageServletContextListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.bind.DatatypeConverter;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;

/**
 * Created by Nick Hale on 2/21/16.
 * @author  Nick Hale, Nick Costanzo
 *          NJohnHale@gmail.com
 *
 */
public class JavaToDexTranslator {

    private static final Logger logger = LogManager.getLogger(JavaToDexTranslator.class);

    private String buildzone;

    /**
     * Default constructor for JavaToDexTranslator
     */
    public JavaToDexTranslator() throws Exception {
        buildzone = SageServletContextListener.config.buildzoneDir;
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
        // parse each groupin
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
        File root = new File(buildzone + this.hashCode());
        File config = new File("/opt/sage/config/");

        // get the location of the SageTask interface
        URL location = SageTask.class.getProtectionDomain().getCodeSource().getLocation();
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
                config.getAbsolutePath() + "/java2dex.sh",
                root.getAbsolutePath() + "/", fqn);
        pb.redirectErrorStream(true);

        //Start the process
        logger.debug("Running java2dex.sh ...");
        Process p = pb.start();

        BufferedReader reader = new BufferedReader (new InputStreamReader(p.getInputStream()));
        while ((reader.readLine ()) != null) {
            logger.error("stdout: " + reader.readLine());
        }
        //Wait for it to finish
        int status = p.waitFor();
        logger.debug("Run of java2dex.sh completed with status code: " + status);

        URL taskUrl = getJarDir(SageTask.class);

        // load and instantiate compiled class.
        URLClassLoader classLoader = URLClassLoader.newInstance(
                new URL[] { config.toURI().toURL() , root.toURI().toURL() });

        Class<?> cls = Class.forName(fqn, false, classLoader);
        // make sure the compiled class can be cast to type SageTask
        logger.debug("loaded class instance");
        //SageTask task = (SageTask) cls.newInstance();
        logger.debug("SageTask URI: " + getJarDir(SageTask.class).toURI().toString());
        Object task = cls.newInstance();
        // read the dex file into a String
        File dexFile = new File(root.getAbsolutePath() + "/" + fqn + ".dex");
        String dex = Files.toString(dexFile, StandardCharsets.UTF_8);
        // encode the dex
        String encodedDex = DatatypeConverter.printBase64Binary(fqn.getBytes())
                + "." + DatatypeConverter.printBase64Binary(dex.getBytes());

        // delete the temporary directory
//        if (!recDelete(root)) {
//            logger.error("An error occurred while attempting to delete the temporary directory.");
//        }

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

    /**
     * Taken from: http://stackoverflow.com/questions/15359702/get-location-of-jar-file
     * Compute the absolute file path to the jar file.
     * The framework is based on http://stackoverflow.com/a/12733172/1614775
     * But that gets it right for only one of the four cases.
     *
     * @param aclass A class residing in the required jar.
     *
     * @return A File object for the directory in which the jar file resides.
     * During testing with NetBeans, the result is ./build/classes/,
     * which is the directory containing what will be in the jar.
     */
    public static URL getJarDir(Class aclass) {
        URL url;
        String extURL;      //  url.toExternalForm();

        // get an url
        try {
            url = aclass.getProtectionDomain().getCodeSource().getLocation();
            // url is in one of two forms
            //        ./build/classes/   NetBeans test
            //        jardir/JarName.jar  froma jar
        } catch (SecurityException ex) {
            url = aclass.getResource(aclass.getSimpleName() + ".class");
            // url is in one of two forms, both ending "/com/physpics/tools/ui/PropNode.class"
            //          file:/U:/Fred/java/Tools/UI/build/classes
            //          jar:file:/U:/Fred/java/Tools/UI/dist/UI.jar!
        }

        // convert to external form
        extURL = url.toExternalForm();

        // prune for various cases
        if (extURL.endsWith(".jar"))   // from getCodeSource
            extURL = extURL.substring(0, extURL.lastIndexOf("/"));
        else {  // from getResource
            String suffix = "/"+(aclass.getName()).replace(".", "/")+".class";
            extURL = extURL.replace(suffix, "");
            if (extURL.startsWith("jar:") && extURL.endsWith(".jar!"))
                extURL = extURL.substring(4, extURL.lastIndexOf("/"));
        }

        // convert back to url
        try {
            url = new URL(extURL);
        } catch (MalformedURLException mux) {
            // leave url unchanged; probably does not happen
        }

        return url;
    }
}