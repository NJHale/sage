package com.sage.ws.resources;

import com.sage.ws.dao.JobDao;
import com.sage.ws.models.Job;
import com.sage.ws.models.JobStatus;
import com.sage.ws.models.User;
import com.sage.ws.util.JavaToDexTranslator;
import com.sage.ws.util.UserAuth;
import com.sage.ws.models.JobOrder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
     * @param googleTokenStr
     * @param sageTokenStr
     * @param order
     * @return
     */
    @POST
    public int placeJobOrder(
            @HeaderParam("GoogleToken") String googleTokenStr,
            @HeaderParam("SageToken") String sageTokenStr,
            JobOrder order ) {

        // instantiate jobId to -1
        int jobId = -1;

        try {
            // get the acting user
            User user = null;
            UserAuth auth = new UserAuth();
            // verify token(s)
            if (googleTokenStr != null && !googleTokenStr.equals("")) {
                user = auth.verifyGoogleToken(googleTokenStr);
            } else if (sageTokenStr != null && !googleTokenStr.equals("")) {
                //TODO: Change to verifySageToken()
                user = auth.verifyGoogleToken(googleTokenStr);
            }

            // make sure the token is valid and that the user matches the given orderId
            if (user == null) {
                // The user is unauthorized
                throw new WebApplicationException(Response.status(401).build());// unauthorized
            }

            // create a new job with the given jobOrder
            Job job = new Job();
            job.setOrdererId(user.getUserId());
            job.setBounty(order.getBounty());
            job.setData(order.getData());
            job.setTimeOut(order.getTimeOut());
            job.setEncodedJava(order.getEncodedJava());


            //TODO: Handle JavaToDexTranslator.encodeJavaToDex() exceptions
            JavaToDexTranslator jdTrans = new JavaToDexTranslator();
            String encodedDex = jdTrans.encodedJavaToDex(job.getEncodedJava());

            // set the encoded dex string
            job.setEncodedDex(encodedDex);

            // set the status to READY
            job.setStatus(JobStatus.READY);

            // save the Job and get its id
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