package com.sage.ws.resources;

import com.sage.ws.dao.Dao;
import com.sage.ws.dao.JavaDao;
import com.sage.ws.dao.JobDao;
import com.sage.ws.models.*;
import com.sage.ws.util.JavaToDexTranslator;
import com.sage.ws.util.UserAuth;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Nick Hale on 2/21/16.
 * @author  Nick Hale
 *         NJohnHale@gmail.com
 *
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/jobOrders")
public class JobOrdersResource {
    // logging
    private static final Logger logger = LogManager.getLogger(JobOrdersResource.class);

    /**
     *
     * @param sageTokenStr
     * @param order
     * @return
     */
    @POST
    public int placeJobOrder(
            @HeaderParam("SageToken") String sageTokenStr,
            JobOrder order ) {

        // instantiate jobId to -1
        int jobId = -1;

        try {
            // get the acting user
            User user = null;
            UserAuth auth = new UserAuth();
            // verify SageToken
            if (sageTokenStr != null && !sageTokenStr.equals("")) {
                user = auth.verifySageToken(sageTokenStr);
            }

            // make sure the token is valid and that the user matches the given orderId
            if (user == null) {
                // The user is unauthorized
                throw new WebApplicationException(Response.status(401).build());// unauthorized
            }

            // ensure the the javaId corresponds to an existing java compilation
            Dao<Java> javaDao = new JavaDao();
            Java java = javaDao.get(order.getJavaId());

            if (java == null) {
                // throw a 400 for malformed - we need a valid javaId
                throw new WebApplicationException(400);
            }

            // create a new job with the given jobOrder
            Job job = new Job();
            job.setOrdererId(user.getUserId()); logger.debug("UserId: " + user.getUserId());
            job.setJavaId(order.getJavaId());
            job.setBounty(order.getBounty());
            job.setData(order.getData());
            job.setTimeout(order.getTimeout());

            // decide the status based on whether the given javaId is compiled or not
            if (java.getEncodedDex() != null && !java.getEncodedDex().equals("")) {
                // the job is ready to go
                job.setStatus(JobStatus.READY);
            } else {
                // the job's java isn't finished compiling - set status pending
                job.setStatus(JobStatus.PENDING);
            }


            // add the job to the database
            JobDao jobDao = new JobDao();
            jobId = jobDao.add(job);

        } catch (WebApplicationException e) {
            logger.error("Something went wrong while attempting to place JobOrder");
            logger.error(e.getMessage());
            logger.debug(e.getStackTrace());
            logger.debug("rethrowing web exception");
            e.printStackTrace();
            // rethrow given web exception
            throw e;// unavailable
//        } catch (InvalidArgumentException e) {
//            logger.error("Something went wrong while attempting to get place the JobOrder");
//            logger.error(e.getMessage());
//            logger.debug(e.getStackTrace().toString());
//            logger.debug("rethrowing web exception");
//            // rethrow as web exception
//            throw new WebApplicationException(Response.status(400).build());
        } catch (Exception e) {
            logger.error("Something went wrong while attempting to place JobOrder");
            logger.error(e.getMessage());
            e.printStackTrace();
            logger.debug(e.getStackTrace());
            logger.debug("rethrowing web exception");
            // rethrow as web exception
            throw new WebApplicationException(Response.status(503).build());
        }
        // return the job
        return jobId;
    }


}
