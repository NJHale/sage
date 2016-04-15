package com.sage.ws.resources;

import com.sage.ws.dao.Dao;
import com.sage.ws.dao.JavaDao;
import com.sage.ws.dao.JobDao;
import com.sage.ws.models.Java;
import com.sage.ws.models.Job;
import com.sage.ws.models.JobStatus;
import com.sage.ws.models.User;
import com.sage.ws.util.JavaToDexTranslator;
import com.sage.ws.util.UserAuth;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Nick Hale on 2/21/16.
 * @author Nick Hale
 *         NJohnHale@gmail.com
 *
 */

@Path("/javas")
public class JavasResource {

    private static final Logger logger = LogManager.getLogger(JavasResource.class);

    private static final ExecutorService pool =
            Executors.newFixedThreadPool(4 * Runtime.getRuntime().availableProcessors());

    private static final Object lock = new Object();

    protected class JavaCompilationCallable<Void> implements Callable {

        private Java java;


        public JavaCompilationCallable(Java java) {
            this.java = java;
        }

        public Void call() throws Exception {
            // create a new JobDAO
            Dao<Java> javaDao = new JavaDao();
            try {
                // attempt to find precompiled encodedDex in case we
                // got enqueued while identical encodedJava was being compiled

                // create the criterion to filter by
                List<Criterion> crits = new ArrayList<Criterion>();
                crits.add(Restrictions.eq("encodedJava", java.getEncodedJava()));
                crits.add(Restrictions.ne("encodedDex", ""));
                // create the ordering
                Order ord = Order.desc("status");
                // make sure to only get the first result - setSize = 1
                List<Java> javas = javaDao.get(crits, ord, 1);

                logger.debug("javas.size(): " + javas.size());

                if (javas.size() == 0) {
                    // we must compile the dex
                    JavaToDexTranslator jdTrans = new JavaToDexTranslator();
                    String encodedDex = jdTrans.encodedJavaToDex(java.getEncodedJava());
                    // translation succeeded, update the java with encodedDex and ready status
                    java.setEncodedDex(encodedDex);
                    // update the java
                    javaDao.upsert(java);
                    // set any jobs tagged with this java to READY
                    JobDao jobDao = new JobDao();
                    jobDao.setAllReadyOnJava(java.getJavaId());
                }

            } catch (Exception e) {
                // translation failed, delete the java and all associated jobs from the datastore
                ((JavaDao)javaDao).delete(java);
                logger.error("An error occurred when attempting to translate encodedJava asynchronously. Java and all associated jobs removed");
                logger.debug("Error: ", e);
            }
            // return null to satisfy Void return type
            return null;
        }
    }

    @GET
    @Path("/dex/{javaId}")
    public String getJavasDex(@PathParam("javaId") int javaId) {
        // create a null encodedDex reference
        String encodedDex = null;
        // attempt to retrieve the dex from the database
        try {
            Dao<Java> javaDao = new JavaDao();
            Java java = javaDao.get(javaId);
            // pull the encodedDex out if we can s
            if (java != null) encodedDex = java.getEncodedDex();
        } catch (Exception e) {
            logger.error("Something went wrong while attempting to get encodedDex");
            logger.error(e.getMessage());
            logger.debug("Error: ", e);
            logger.debug("rethrowing web exception");
            // rethrow as web exception
            throw new WebApplicationException(Response.status(503).build());
        }
        // return the encodedDex
        return encodedDex;
    }

    @POST
    public int updateJava(
            @HeaderParam("SageToken") String sageTokenStr,
            Java java) {

        // instantiate the jobId (-1 for failure status)
        int javaId = -1;
        try {
            // get the user from the given sage token string
            UserAuth auth = new UserAuth();
            User user = auth.verifySageToken(sageTokenStr);
            if (user == null) {
                // the given sage token is invalid, throw a web app exception 401 - unauthorized
                logger.debug("The sage token given was invalid. Throwing 401 unauthorized.");
                throw new WebApplicationException(401);
            }

            // instantiate a new javaDao
            Dao<Java> javaDao = new JavaDao();
            // attempt to get the dex if it's already compiled
            List<Criterion> crits = new LinkedList<Criterion>();
            crits.add(Restrictions.eq("encodedJava", java.getEncodedJava()));
            List<Java> javas = javaDao.get(crits, null, 1);// only get the top 1
            if (javas.size() > 0) {
                // we can simply grab that job
                java = javas.get(0);
                javaId = java.getJavaId();
            } else {
                // set the creatorId
                java.setCreatorId(user.getUserId());
                // store the java in as-is
                javaId = javaDao.add(java);
                // now we need to compile
                Callable<Void> jcc = new JavaCompilationCallable<Void>(java);
                pool.submit(jcc);
            }

        } catch (WebApplicationException e) {
            // rethrow web exception
            logger.error("An error has been thrown while checking the SageToken");
            logger.debug("Error: ", e);
            throw e;
        } catch (Exception e) {
            // throw a 500 - server error
            logger.error("An error has been thrown while checking the SageToken");
            logger.debug("Error: ", e);
            throw new WebApplicationException(500);
        }
        // return the javaId
        return javaId;
    }

}
