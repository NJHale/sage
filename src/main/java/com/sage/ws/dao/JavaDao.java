package com.sage.ws.dao;

import com.sage.ws.models.Java;
import com.sage.ws.models.Job;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick Hale on 4/10/16.
 * @author Nick Hale
 *         NJohnHale@gmail.com
 *
 */
public class JavaDao extends Dao<Java> {

    /**
     * Default constructor for JavaDao
     * @throws Exception if something goes wrong
     * when calling super()
     */
    public JavaDao() throws Exception {
        // call super
        super();
        logger.trace("JavaDao - Super called!");
    }

    @Override
    public Java get(int id) {
        // create a Java reference
        Java java = null;
        // get a session
        Session session = sessionFactory.openSession();
        logger.debug("Getting java by id...");
        try {
            //session.beginTransaction();
            java = session.get(Java.class, id);
//            java.getOrdererId();// beat up lazy-load
            logger.debug("Transaction complete!");
        } catch (HibernateException e) {
            logger.error("An error has occurred while attempting to retrieve the Java");
            logger.debug("Error : ", e);
        } finally {
            session.close();
        }

        return java;
    }

    @Override
    public List<Java> get(List<Criterion> crits, Order order, int setSize) {
        // create List<Java> reference
        List<Java> javas = new ArrayList<Java>();
        // open a new session
        Session session = sessionFactory.openSession();

        try {
            Criteria cr = session.createCriteria(Java.class);
            // add each of the given criterion
            for (Criterion crit : crits)
                cr.add(crit);
            // add the order
            if (order == null) order = Order.desc("javaId");
            cr.addOrder(order);
            // trim the resilt set
            if (setSize >= 0) cr.setMaxResults(setSize);
            // retrieve the javas
            javas = cr.list();
        } catch (HibernateException e) {
            logger.error("Something went wrong when attempting to get javas from the datastore");
            logger.debug("Error: ", e);
        } finally {
            session.close();
        }

        return javas;
    }

    @Override
    public int add(Java java) {
        // open a new session
        Session session = sessionFactory.openSession();
        // instantiate the javaId (-1 default)
        int javaId = -1;

        try {
            session.beginTransaction();
            javaId = (Integer) session.save(java);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            logger.error("Something went wrong when attempting to add java to the datastore");
            logger.debug("Rolling back changes...");
            session.getTransaction().rollback();
            logger.debug(e.getMessage());
            logger.debug(e.getStackTrace());
        } finally {
            session.close();
        }

        return javaId;
    }

    @Override
    public void upsert(Java java) {
        // open a new session
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.saveOrUpdate(java);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            logger.error("Something went wrong when attempting to upsert java in the datastore");
            logger.debug("Rolling back changes...");
            session.getTransaction().rollback();
            logger.debug(e.getMessage());
            logger.debug(e.getStackTrace());
        } finally {
            session.close();
        }
    }

    /**
     * Removes the given java and all associated jobs from the datastore
     * @param java Java to remove from the datastore
     */
    public void delete(Java java) {
        // open a new session
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            // remove all associated jobs from the datastore
            Query query = session.createQuery("DELETE FROM Job WHERE javaId = :javaId");
            query.setParameter("javaId", java.getJavaId());
            query.executeUpdate();
            // remove the given java from the datastore
            session.delete(java);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            logger.error("Something went wrong when attempting to remove java from the datastore");
            logger.debug("Rolling back changes...");
            session.getTransaction().rollback();
            logger.debug(e.getMessage());
            logger.debug(e.getStackTrace());
        } finally {
            session.close();
        }
    }
}
