package com.sage.ws.dao;

import com.sage.ws.models.Job;
import com.sage.ws.models.JobStatus;
import org.hibernate.*;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick Hale on 2/21/16.
 * @author Nick Hale
 *         NJohnHale@gmail.com
 *
 */
public class JobDao extends Dao<Job> {

    /**
     * Default constructor for JobDao
     * @throws Exception if something goes wrong
     * when calling super()
     */
    public JobDao() throws Exception {
        // call super
        super();
        logger.trace("JobDao - Super called!");
    }

    @Override
    public Job get(int id) {
        // create a Job reference
        Job job = null;
        // get a session
        Session session = sessionFactory.openSession();
        logger.debug("Getting job by id...");
        try {
            //session.beginTransaction();
            job = session.get(Job.class, id);
//            job.getOrdererId();// beat up lazy-load
            logger.debug("Transaction complete!");
        } catch (HibernateException e) {
            logger.error("An error has occurred while attempting to retrieve the Job");
            logger.debug("Error : ", e);
        } finally {
            session.close();
        }

        return job;
    }

    @Override
    public List<Job> get(List<Criterion> crits, Order order, int setSize) {
        // create List<Job> reference
        List<Job> jobs = new ArrayList<Job>();
        // open a new session
        Session session = sessionFactory.openSession();

        try {
            Criteria cr = session.createCriteria(Job.class);
            // add each of the given criterion
            for (Criterion crit : crits)
                cr.add(crit);
            // add the order
            cr.addOrder(order);
            // trim the resilt set
            if (setSize >= 0) cr.setMaxResults(setSize);
            // retrieve the jobs
            jobs = cr.list();
        } catch (HibernateException e) {
            logger.error("Something went wrong when attempting to get jobs from the datastore");
            logger.debug("Error: ", e);
        } finally {
            session.close();
        }

        return jobs;
    }

    @Override
    public int add(Job job) {
        // open a new session
        Session session = sessionFactory.openSession();
        // instantiate the jobId (-1 default)
        int jobId = -1;

        try {
            session.beginTransaction();
            jobId = (Integer) session.save(job);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            logger.error("Something went wrong when attempting to add job to the datastore");
            logger.debug("Rolling back changes...");
            session.getTransaction().rollback();
            logger.debug(e.getMessage());
            logger.debug(e.getStackTrace());
        } finally {
            session.close();
        }

        return jobId;
    }

    @Override
    public void upsert(Job job) {
        // open a new session
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.saveOrUpdate(job);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            logger.error("Something went wrong when attempting to upsert job in the datastore");
            logger.debug("Rolling back changes...");
            session.getTransaction().rollback();
            logger.debug(e.getMessage());
            logger.debug("Error: ", e);
        } finally {
            session.close();
        }
    }

    public List<Job> getTimedout() {
        // instantiate an empty list of jobs
        List<Job> jobs = new ArrayList<Job>();
        // open a new session
        Session session = sessionFactory.openSession();
        try {
            // YAY NATIVE QUERIES!!!
            jobs = session
                    .createSQLQuery("SELECT * FROM job WHERE status='RUNNING' AND _tm + timeout < CURRENT_TIMESTAMP")
                    .addEntity(Job.class)
                    .list();
        } catch (HibernateException e) {
            logger.error("Something went wrong when attempting to get timed out jobs");
            logger.debug(e.getMessage());
            logger.debug("Error: ", e);
        } finally {
            session.close();
        }

        return jobs;
    }

    /**
     * Sets all PENDING jobs to READY with the given javaId
     * @param javaId Id of the Java model to set READY for
     */
    public int setAllReadyOnJava(int javaId) {
        // instantiate a result status (-1 for failure)
        int result = -1;
        Session session = sessionFactory.openSession();
        try {
            // YAY! More native queries!!!
            Query query = session.createQuery("UPDATE job SET status = :status WHERE javaId = :javaId ");
            query.setParameter("status", JobStatus.READY);
            query.setParameter("javaId", javaId);
            result = query.executeUpdate();
        } catch (HibernateException e) {
            logger.error("An error occurred while attempting to set all jobs READY for javaId: " + javaId);
            logger.debug("Error: ", e);
        } finally {
            // close the session
            session.close();
        }
        return result;
    }


}
