package com.sage.resources;

import com.sage.models.JobOrder;
import com.sage.models.User;
import com.sage.service.SageApplication;
import com.sage.service.UserAuth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by root on 2/18/16.
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/jobOrders")
public class JobOrdersResource {
//TODO: Add Logging!

    private static final Logger logger = LogManager.getLogger(JobOrdersResource.class);

    @GET
    @Path("/{jobOrderId}")//sage-ws.ddns.net:8080/sage-ws/0.1/jobOrders/2
    public JobOrder getJobOrder(@PathParam("jobOrderId") int jobOrderId ) {
        return new JobOrder();
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
