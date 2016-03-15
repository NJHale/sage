package com.sage.ws.util;

import com.google.common.io.Files;
import com.sage.ws.resources.JobOrdersResource;
import com.sun.jersey.core.util.Base64;
import junit.framework.TestCase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * Created by root on 3/5/16.
 */
public class JavaToDexTranslatorTest extends TestCase {

    private static final Logger logger = LogManager.getLogger(JavaToDexTranslatorTest.class);

    public void testEncodedJavaToDex() throws Exception {
        // fetch the java file
        String name = "Hello";
        File javaFile = new File("/home/nhale/Documents/Spring2016/CS491/dexProof/" + name + ".java");
        // slurp the file into a String
        String java = Files.toString(javaFile, StandardCharsets.UTF_8);
        logger.debug("Java before sending: " + java);
        // encode the slurpped java
        String encodedJava = DatatypeConverter.printBase64Binary(name.getBytes())
                            + "." + DatatypeConverter.printBase64Binary(java.getBytes());
        System.out.println("encodedJava: \n" + encodedJava);
        // get the encodedDex
        JavaToDexTranslator jdTrans = new JavaToDexTranslator();
        String encodedDex = jdTrans.encodedJavaToDex(encodedJava);
        logger.debug("encodedDex: " + encodedDex);

    }
}