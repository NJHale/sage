package com.sage.ws.service;

import com.sage.ws.dao.Dao;
import com.sage.ws.dao.JobDao;
import com.sage.ws.models.Job;
import com.sage.ws.models.JobStatus;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by root on 4/14/16.
 */
public class JobWatchdog implements Callable<Void> {

    private static final Logger logger = LogManager.getLogger(JobWatchdog.class);

    private long dt;

    /**
     * Parameterized constructor that takes some dt in milliseconds
     * to run the watchdog process on
     * @param dt Interval in milliseconds to check for timed-out jobs
     */
    public JobWatchdog(long dt) {
        this.dt = dt;
    }

    public Void call() throws Exception {
        // start watching
        watch();
        // return null to satisfy void return type
        return null;
    }

    public void watch() throws Exception {
        // instantiate a JobDao
        JobDao jobDao = new JobDao();
        // continue to loop through
        Boolean watching = true;
        while (watching) {
            // wait dt milliseconds until next check
            try {
                logger.debug("Watchdog going to sleep in thread " + Thread.currentThread().getId() +
                    "for " + dt + " ms...");
                Thread.sleep(dt);
                logger.debug("Watchdog thread awake, commencing job check.");
                // enforce the timeout
                jobDao.enforceTimeout();
                logger.debug("Job timeouts enforced.");
            } catch (InterruptedException e) {
                logger.error("Watchdog's sleep interrupted. Stopping watch...");
                logger.debug("Error: ", e);
                watching = false;
            } catch (Exception e) {
                logger.error("Some error occurred during Watchdog's watch. Silently continuing...");
                logger.debug("Error: ", e);
            }
        }
        logger.debug("Watchdog watch stopped.");
    }


}
