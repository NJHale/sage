package com.sage.resources;

import com.sage.models.JobOrder;
import com.sage.service.SageApplication;

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

    @GET
    public List<JobOrder> getjobOrders(
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
    public Response postJobOrder(JobOrder order) {
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
