package com.sage.ws.resources;

import com.sage.ws.dao.AndroidNodeDao;
import com.sage.ws.dao.Dao;
import com.sage.ws.dao.JobDao;
import com.sage.ws.models.AndroidNode;
import com.sage.ws.models.Goat;
import com.sage.ws.models.Job;
import com.sage.ws.models.User;
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
import java.util.LinkedList;
import java.util.List;

/**
 /**
 * Created by Nick Hale on 2/21/16.
 * @author Nick Hale
 *         NJohnHale@gmail.com
 *
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/androidNodes")
public class AndroidNodesResource {

    private static final Logger logger = LogManager.getLogger(AndroidNodesResource.class);

    @POST
    public int addNode(@HeaderParam("SageToken") String sageTokenStr,
                       AndroidNode node) {
        int nodeId = -1;
        try {
            // get the acting user
            User user = null;
            UserAuth auth = new UserAuth();
            // verify given sageTokenStr
            if (sageTokenStr != null && !sageTokenStr.equals("")) {
                user = auth.verifySageToken(sageTokenStr);
            }

            if (user == null) {
                // The user is unauthorized
                throw new WebApplicationException(Response.status(401).build());// unauthorized
            }

            // null out the given nodeId
            node.setNodeId(0);
            // assume the endpoint caller is the node owner
            node.setOwnerId(user.getUserId());

            // save the node to the datastore
            Dao nodeDao = new AndroidNodeDao();
            nodeId = nodeDao.add(node);

        } catch (WebApplicationException e) {
            logger.error("Something went wrong while attempting to add a Node");
            logger.error(e.getMessage());
            logger.debug(e.getStackTrace().toString());
            logger.debug("rethrowing web exception");
            // rethrow given web exception
            throw e;// unavailable
        } catch (Exception e) {
            logger.error("Something went wrong while attempting to add a Node");
            logger.error(e.getMessage());
            logger.debug(e.getStackTrace().toString());
            logger.debug("rethrowing web exception");
            // rethrow as web exception
            throw new WebApplicationException(Response.status(503).build());
        }
        return nodeId;
    }

    @GET
    public List<AndroidNode> getNodes(
            @HeaderParam("SageToken") String sageTokenStr,
            @QueryParam("count") int count,
            @QueryParam("dir") String dir,
            @QueryParam("orderBy") String orderBy,
            @QueryParam("androidId") String androidId,
            @QueryParam("ownerId") int ownerId) {
        // create job reference
        List<AndroidNode> nodes = new ArrayList<AndroidNode>();
        try {
            // get the acting user
            User user = null;
            UserAuth auth = new UserAuth();
            // verify given sageTokenStr
            if (sageTokenStr != null && !sageTokenStr.equals("")) {
                user = auth.verifySageToken(sageTokenStr);
            }

            if (user == null) {
                // The user is unauthorized
                throw new WebApplicationException(Response.status(401).build());// unauthorized
            }

            // get the job by the given query parameters
            Dao nodeDao = new AndroidNodeDao();
            Order order = null;
            // create the criterion to filter by
            List<Criterion> crits = new ArrayList<Criterion>();
            // apply direction and orderBy
            if ( dir != null ) {
                if (orderBy != null) {
                    order = dir.equals("asc") ? Order.asc(orderBy) : Order.desc(orderBy);
                } else {
                    order = dir.equals("asc") ? Order.asc("nodeId") : Order.desc("nodeId");
                }
            } else {
                if (orderBy != null) {
                    order = Order.desc(orderBy);// always descending
                } else {
                    order = Order.desc("nodeId");
                }
            }


            if (androidId != null) crits.add(Restrictions.eq("androidId", androidId));
            if ( ownerId > 0 ) crits.add(Restrictions.eq("ownerId", ownerId));

            logger.debug("crits size: "  + crits.size());

            // retrieve jobs from the datastore
            nodes = nodeDao.get(crits, order, count);

        } catch (WebApplicationException e) {
            logger.error("Something went wrong while attempting to get Nodes");
            logger.error(e.getMessage());
            logger.debug(e.getStackTrace().toString());
            logger.debug("rethrowing web exception");
            // rethrow given web exception
            throw e;// unavailable
        } catch (Exception e) {
            logger.error("Something went wrong while attempting to get Nodes");
            logger.error(e.getMessage());
            logger.debug(e.getStackTrace().toString());
            logger.debug("rethrowing web exception");
            // rethrow as web exception
            throw new WebApplicationException(Response.status(503).build());
        }
        // return the job
        return nodes;
    }


}
