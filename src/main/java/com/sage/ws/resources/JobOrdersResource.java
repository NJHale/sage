package com.sage.ws.resources;

import com.sage.ws.dao.Dao;
import com.sage.ws.dao.JobDao;
import com.sage.ws.models.Job;
import com.sage.ws.models.JobStatus;
import com.sage.ws.models.User;
import com.sage.ws.util.JavaToDexTranslator;
import com.sage.ws.util.UserAuth;
import com.sage.ws.models.JobOrder;
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

    private static final ExecutorService pool =
            Executors.newFixedThreadPool(4 * Runtime.getRuntime().availableProcessors());

    protected class JobCompilationCallable<Void> implements Callable {

        private Job job;

        public JobCompilationCallable(Job job) {
            this.job = job;
        }

        public Void call() throws Exception {
            // create a new JobDAO
            Dao<Job> jobDao = new JobDao();
            try {
                JavaToDexTranslator jdTrans = new JavaToDexTranslator();
                String encodedDex = jdTrans.encodedJavaToDex(job.getEncodedJava());
                // translation succeeded, update the job with encodedDex and ready status
                job.setEncodedDex(encodedDex);
                job.setStatus(JobStatus.READY);
                // update the job
                jobDao.upsert(job);

            } catch (Exception e) {
                // translation failed, switch the job status to error and update
                job.setStatus(JobStatus.ERROR);
                // update the job
                jobDao.upsert(job);
                logger.error("An error occurred when attempting to translate encodedJava asynchronously");
                logger.debug("Error: ", e);
            }
            // return null to satisfy Void return type
            return null;
        }
    }

    public static byte[] int2byte(int[]src) {
        int srcLength = src.length;
        byte[]dst = new byte[srcLength << 2];

        for (int i=0; i<srcLength; i++) {
            int x = src[i];
            int j = i << 2;
            dst[j++] = (byte) ((x >>> 0) & 0xff);
            dst[j++] = (byte) ((x >>> 8) & 0xff);
            dst[j++] = (byte) ((x >>> 16) & 0xff);
            dst[j++] = (byte) ((x >>> 24) & 0xff);
        }
        return dst;
    }

    public static int[] byte2int(byte[]src) {
        int dstLength = src.length >>> 2;
        int[]dst = new int[dstLength];

        for (int i=0; i<dstLength; i++) {
            int j = i << 2;
            int x = 0;
            x += (src[j++] & 0xff) << 0;
            x += (src[j++] & 0xff) << 8;
            x += (src[j++] & 0xff) << 16;
            x += (src[j++] & 0xff) << 24;
            dst[i] = x;
        }
        return dst;
    }


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



            JobDao jobDao = new JobDao();


            // attempt to find precompiled encodedDex for the given encodedJava
            // create the criterion to filter by
            List<Criterion> crits = new ArrayList<Criterion>();
            crits.add(Restrictions.eq("encodedJava", order.getEncodedJava()));
            crits.add(Restrictions.ne("encodedDex", ""));
            // create the ordering
            Order ord = Order.desc("status");
            // make sure to only get the first result - setSize = 1
            List<Job> jobs = jobDao.get(crits, ord, 1);

            logger.debug("jobs.size(): " + jobs.size());

            if (jobs.size() > 0) {
                //
                // set reclaimedDex
                job.setEncodedDex(jobs.get(0).getEncodedDex());
                // add the new job to the database
                job.setStatus(JobStatus.READY);
                // set the jobId
                jobId = jobDao.add(job);
            } else {
                // set the job status to pending and add the job
                job.setStatus(JobStatus.PENDING);
                jobId = jobDao.add(job);
                // stuff the jobId back into the job
                job.setJobId(jobId);
                // enqueue a job compilation callable to finish job compilation
                Callable<Void> jcc = new JobCompilationCallable<Void>(job);
                pool.submit(jcc);
            }

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
