
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

    @GET
    @Path("/{jobId}")
    public Job getJob(
            @HeaderParam("googleToken") String googleTokenStr,
            @HeaderParam("sageToken") String sageTokenStr,
            @PathParam("jobId") int jobId ) {
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

        } catch (Exception e) {
            logger.error("Something went wrong while attempting to get the Job");
            logger.error(e.getMessage());
            logger.debug(e.getStackTrace().toString());
            logger.debug("rethrowing web exception with ");
            // rethrow as web exception
             throw new WebApplicationException(Response.status(503).build());// unavailable
        }
        // return the job
        return job;
    }

    @GET
    public List<JobOrder> getjobOrders(// sage-ws.ddns.net:8080/sage-ws/0.1/jobOrders/?age=1000&weight=150
                                       @QueryParam("age") int age,
                                       @QueryParam("aggression") int aggression,
                                       @QueryParam("weight") double weight ) {
        List<JobOrder> jobOrders = new LinkedList<JobOrder>();


        return jobOrders;
    }

    @PUT
    public Response putJobOrder(JobOrder order) {
        SageApplication.everything.add(order);
        Response resp = Response.ok().build();
        return resp;
    }

    @POST
    public Response postJobOrder(@HeaderParam("IdToken") String idTokenStr, JobOrder order) {
        Response resp;
        logger.debug("idTokenStr: " + idTokenStr);
        try {
            UserAuth auth = new UserAuth();
            logger.debug("UserAuth Created!");
            logger.debug("Validating IdToken...");
            User user = auth.verifyToken(idTokenStr);
            if (user == null) {
                throw new Exception ("An exposion of the shittiest kind!");
            }
            logger.debug("IdToken validated!");
            //resp = Response.ok().entity(user).build();
            resp = Response.ok().build();
            return resp;
        } catch (UserAuth.InvalidIdTokenException e) {
            logger.debug(e.getMessage());
        } catch (Exception e) {
            logger.debug("SHIT HAPPENS!");
            logger.debug(e.getMessage());
        }

        return putJobOrder(order);
    }

    @POST
    @Path("/generate")
    public Response generateJobOrder() {
        JobOrder order = new JobOrder();
        order.setBounty(10);
        order.setTimeOut(10);
        //order.setJavaFile();
        return putJobOrder(order);
    }



}
