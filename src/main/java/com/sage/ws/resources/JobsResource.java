
package com.sage.ws.resources;

import com.sage.ws.dao.AndroidNodeDao;
import com.sage.ws.dao.Dao;
import com.sage.ws.dao.JobDao;
import com.sage.ws.models.Job;
import com.sage.ws.models.JobStatus;
import com.sage.ws.models.User;
import com.sage.ws.util.ModelValidator;
import com.sage.ws.util.UserAuth;
import com.sage.ws.models.AndroidNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Nick Hale on 2/21/16.
 * @author Nick Hale
 *         NJohnHale@gmail.com
 *
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
    @Path("/nextReady/{nodeId}")//sage-ws.ddns.net:8080/sage/0.1/jobs/nextReady/2234
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
            //TODO: figure out how to handle clustered environment
            JobDao jobDao = new JobDao();
            // create the criterion to filter by
            List<Criterion> crits = new ArrayList<Criterion>();
            crits.add(Restrictions.eq("status", JobStatus.READY));
            // create the ordering
            Order order = Order.desc("bounty");
            // make sure to only get the first result - setSize = 1
            List<Job> jobs = jobDao.get(crits, order, 1);

            if (jobs.size() > 0) {
                // we know the job is at the first index
                job = jobs.get(0);
                logger.info("Retrieved next ready job!");
                logger.debug("Setting job status to running");
                // set the status to JobStatus.RUNNING
                job.setStatus(JobStatus.RUNNING);
                // set the android nodeId
                job.setNodeId(nodeId);
                // upsert the job
                jobDao.upsert(job);
            } else {
                logger.info("No next ready job available.");
            }


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

    /**
     * Updates the given job in the datastore
     * Only the android node running the given job is allowed to make updates
     * No changing of dex content is permitted
     * @param googleTokenStr
     * @param sageTokenStr
     * @param job
     * @return Gets Job associated with the given jobId
     */
    @POST
    public void updateJob(
            @HeaderParam("googleToken") String googleTokenStr,
            @HeaderParam("sageToken") String sageTokenStr,
            Job job) {

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

            logger.debug("Verifying node ownership ...");
            // get the android node and confirm ownership
            Dao<AndroidNode> nodeDao = new AndroidNodeDao();
            AndroidNode node = nodeDao.get(job.getNodeId());
            if (node == null || user.getUserId() != node.getOwnerId()) {
                // node is not owned by the user or does not exist
                logger.debug("User doesn't own the node they are attempting to represent or the node does not exist.");
                throw new WebApplicationException(Response.status(403).build());// forbidden
            }
            logger.debug("Node ownership verified!");

            // at this point we know the user is acting on behalf of a node they own
            // dig up the stored version of the job and check for tampering
            JobDao jobDao = new JobDao();
            Job stored = jobDao.get(job.getJobId());
            // validate the job
            ModelValidator validator = new ModelValidator();
            if (!validator.validate(stored, job)) {
                // the core job data has been tampered with
                logger.debug("Validation failed. User is attempting to tamper with core job info.");
                throw new WebApplicationException(Response.status(403).build());// forbidden
            }
            logger.debug("Validation successful!");
            // update the stored job with status change and result
            stored.setResult(job.getResult());
            stored.setStatus(job.getStatus());
            stored.setCompletion(new Date());
            // update the datastore
            jobDao.upsert(stored);

        } catch (WebApplicationException e) {
            logger.error("Something went wrong while attempting to update the Job");
            logger.error(e.getMessage());
            logger.debug(e.getStackTrace().toString());
            logger.debug("rethrowing web exception");
            // rethrow as web exception
            throw e;
        } catch (Exception e) {
            logger.error("Something went wrong while attempting to update the Job");
            logger.error(e.getMessage());
            logger.debug(e.getStackTrace().toString());
            logger.error("Silently withering...");
        }

    }

    /**
     *
     * @param googleTokenStr
     * @param sageTokenStr
     * @param count
     * @param dir
     * @param uBounty
     * @param lBounty
     * @param nodeId
     * @param ordererId
     * @return
     */
    @GET
    public List<Job> getJobs(
            @HeaderParam("GoogleToken") String googleTokenStr,
            @HeaderParam("SageToken") String sageTokenStr,
            @QueryParam("count") int count,
            @QueryParam("dir") String dir,
            @QueryParam("orderBy") String orderBy,
            @QueryParam("status") int status,
            @QueryParam("upperBounty") int uBounty,
            @QueryParam("lowerBounty") int lBounty,
            @QueryParam("nodeId") int nodeId,
            @QueryParam("ordererId") int ordererId) {

        // create job reference
        List<Job> jobs = new ArrayList<Job>();
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

            // get the job by the given query parameters
            Dao<Job> jobDao = new JobDao();
            Order order = null;
            // create the criterion to filter by
            List<Criterion> crits = new ArrayList<Criterion>();
            if ( status >= 0 ) crits.add(Restrictions.eq("status", status));// include status
            // apply direction and orderBy
            if ( dir != null ) {
                if (orderBy != null) {
                    order = dir.equals("asc") ? Order.asc(orderBy) : Order.desc(orderBy);
                } else {
                    order = dir.equals("asc") ? Order.asc("jobId") : Order.desc("jobId");
                }
            } else {
                if (orderBy != null) {
                    order = Order.desc(orderBy);// always descending
                } else {
                    order = Order.desc("jobId");
                }
            }
            if ( uBounty > 0 ) crits.add(Restrictions.lt("bounty", uBounty));
            if ( lBounty > 0 ) crits.add(Restrictions.gt("bounty", lBounty));
            if ( nodeId > 0 ) crits.add(Restrictions.eq("nodeId", nodeId));
            if ( ordererId > 0 ) crits.add(Restrictions.eq("ordererId", ordererId));
            // retrieve jobs from the datastore
            jobs = jobDao.get(crits, order, count);

        } catch (WebApplicationException e) {
            logger.error("Something went wrong while attempting to get Jobs");
            logger.error(e.getMessage());
            logger.debug(e.getStackTrace().toString());
            logger.debug("rethrowing web exception");
            // rethrow given web exception
            throw e;// unavailable
        } catch (Exception e) {
            logger.error("Something went wrong while attempting to get Jobs");
            logger.error(e.getMessage());
            logger.debug(e.getStackTrace().toString());
            logger.debug("rethrowing web exception");
            // rethrow as web exception
            throw new WebApplicationException(Response.status(503).build());
        }
        // return the job
        return jobs;
    }
}
