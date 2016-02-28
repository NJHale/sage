
package com.sage.resources;

import com.sage.dao.AndroidNodeDao;
import com.sage.dao.Dao;
import com.sage.dao.JobDao;
import com.sage.models.AndroidNode;
import com.sage.models.Job;
import com.sage.models.JobOrder;
import com.sage.models.User;
import com.sage.service.SageApplication;
import com.sage.service.UserAuth;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by root on 2/18/16.
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/jobs")
public class JobsResource {

    private static final Logger logger = LogManager.getLogger(JobsResource.class);

    /**
     * Gets Job associated with the given jobId
     * @param googleTokenStr
     * @param sageTokenStr
     * @param jobId
     * @return Gets Job associated with the given jobId
     */
    @GET
    @Path("/{jobId}")
    public Job getJob(
            @HeaderParam("GoogleToken") String googleTokenStr,
            @HeaderParam("SageToken") String sageTokenStr,
            @PathParam("jobId") int jobId ) {

        // create job reference
        Job job = null;
        try {
            // get the acting user
            User user = null;
            UserAuth auth = new UserAuth();
            // verify token(s)
            if (googleTokenStr != null && !googleTokenStr.equals("") ) {
                user = auth.verifyGoogleToken(googleTokenStr);
            } else if (sageTokenStr != null && !googleTokenStr.equals("")) {
                //TODO: Change to verifySageToken()
                user = auth.verifyGoogleToken(googleTokenStr);
            }

            if (user == null) {
                // The user is unauthorized
                throw new WebApplicationException(Response.status(401).build());// unauthorized
            }

            // get the job by its id
            Dao<Job> jobDao = new JobDao();
            job = jobDao.get(jobId);

            // check to make sure the user is authorized to get the job
            if (job != null) {
                // get the related android node
                Dao<AndroidNode> nodeDao = new AndroidNodeDao();
                AndroidNode node = nodeDao.get(job.getNodeId());
                if (job.getOrdererId() != user.getUserId() &&
                        node.getOwnerId() != user.getUserId()) {
                    // the user is neither the orderer or the node owner
                    throw new WebApplicationException(Response.status(403).build());// forbidden
                }
            }

        } catch (WebApplicationException e) {
            logger.error("Something went wrong while attempting to get the Job");
            logger.error(e.getMessage());
            logger.debug(e.getStackTrace().toString());
            logger.debug("rethrowing web exception");
            // rethrow given web exception
            throw e;// unavailable
        } catch (Exception e) {
            logger.error("Something went wrong while attempting to get the Job");
            logger.error(e.getMessage());
            logger.debug(e.getStackTrace().toString());
            logger.debug("rethrowing web exception");
            // rethrow as web exception
            throw new WebApplicationException(Response.status(503).build());
        }
        // return the job
        return job;
    }

    /**
     * Gets Job associated with the given jobId
     * POST should not have a body
     * @param googleTokenStr
     * @param sageTokenStr
     * @param nodeId
     * @return Gets Job associated with the given jobId
     */
    @POST
    @Path("/nextReady/{nodeId}")
    public Job getNextReadyJob(
            @HeaderParam("googleToken") String googleTokenStr,
            @HeaderParam("sageToken") String sageTokenStr,
            @PathParam("nodeId") int nodeId) {

        // create job reference
        Job job = null;
        try {
            // get the acting user
            User user = null;
            UserAuth auth = new UserAuth();
            // verify token(s)
            if (googleTokenStr != null && !googleTokenStr.equals("") ) {
                user = auth.verifyGoogleToken(googleTokenStr);
            } else if (sageTokenStr != null && !googleTokenStr.equals("")) {
                //TODO: Change to verifySageToken()
                user = auth.verifyGoogleToken(googleTokenStr);
            }

            if (user == null) {
                // The user is unauthorized
                logger.debug("User Unauthorized!");
                throw new WebApplicationException(Response.status(401).build());// unauthorized
            }

            logger.debug("Attempting to dig up the given AndroidNode ...");

            // make sure the user is the owner of the given nodeId
            Dao<AndroidNode> nodeDao = new AndroidNodeDao();
            AndroidNode node = nodeDao.get(nodeId);

            logger.debug("Given AndroidNode found!");
            logger.debug("Verifying whether it belongs to the User...");

            if (node == null || node.getOwnerId() != user.getUserId()) {
                logger.debug("User doesn't own the node they are attempting to represent or the node does not exist.");
                throw new WebApplicationException(Response.status(403).build());// forbidden
            }

            logger.debug("AndroidNode ownership verified!");

            // at this point we know the user is acting on behalf of a node they own
            // get a list of ready jobs descending by bounty
            //TODO: work on getting job and updating job status to JobStatus.RUNNING

        } catch (WebApplicationException e) {
            logger.error("Something went wrong while attempting to get the next ready Job");
            logger.error(e.getMessage());
            logger.debug(e.getStackTrace().toString());
            logger.debug("rethrowing web exception");
            // rethrow as web exception
            throw e;
        } catch (Exception e) {
            logger.error("Something went wrong while attempting to get the next ready Job");
            logger.error(e.getMessage());
            logger.debug(e.getStackTrace().toString());
            logger.error("Silently withering...");
        }
        // return the job
        return job;
    }




}
