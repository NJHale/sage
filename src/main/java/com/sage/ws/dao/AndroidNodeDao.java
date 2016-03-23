package com.sage.ws.dao;

import com.sage.ws.models.AndroidNode;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
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

public class AndroidNodeDao extends Dao<AndroidNode> {

    /**
     * Default constructor for AndroidNodeDao
     * @throws Exception if something goes wrong
     * when calling super()
     */
    public AndroidNodeDao() throws Exception {
        // call super
        super();
        logger.trace("AndroidNodeDao - Super called!");
    }

    @Override
    public AndroidNode get(int id) {
        // create a AndroidNode reference
        AndroidNode node = null;
        // get a session
        Session session = sessionFactory.openSession();

        try {
            //session.beginTransaction();
            node = session.get(AndroidNode.class, id);
        } catch (HibernateException e) {
            logger.error("An error has occurred while attempting to retrieve the AndroidNode");
            logger.debug(e.getMessage());
            logger.debug(e.getStackTrace());
        } finally {
            session.close();
        }

        return node;
    }

    @Override
    public List<AndroidNode> get(List<Criterion> crits, Order order, int setSize) {
        // create List<AndroidNode> reference
        List<AndroidNode> nodes = new ArrayList<AndroidNode>();
        // open a new session
        Session session = sessionFactory.openSession();

        try {
            Criteria cr = session.createCriteria(AndroidNode.class);
            // add each of the given criterion
            for (Criterion crit : crits)
                cr.add(crit);
            // add the order
            cr.addOrder(order);
            // trim the resilt set
            if (setSize > 0) cr.setFetchSize(setSize);
            // retrieve the AndroidNodes
            nodes = cr.list();
        } catch (HibernateException e) {
            logger.error("Something went wrong when attempting to get AndroidNodes from the datastore");
            logger.debug(e.getMessage());
            logger.debug(e.getStackTrace());
        } finally {
            session.close();
        }

        return nodes;
    }

    @Override
    public int add(AndroidNode node) {
        // open a new session
        Session session = sessionFactory.openSession();
        // instantiate the AndroidNodeId (-1 default)
        int nodeId = -1;

        try {
            session.beginTransaction();
            nodeId = (Integer) session.save(node);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            logger.error("Something went wrong when attempting to add AndroidNode to the datastore");
            logger.debug("Rolling back changes...");
            session.getTransaction().rollback();
            logger.debug(e.getMessage());
            logger.debug(e.getStackTrace());
        } finally {
            session.close();
        }

        return nodeId;
    }

    @Override
    public void upsert(AndroidNode node) {
        // open a new session
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.saveOrUpdate(node);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            logger.error("Something went wrong when attempting to upsert AndroidNode in the datastore");
            logger.debug("Rolling back changes...");
            session.getTransaction().rollback();
            logger.debug(e.getMessage());
            logger.debug(e.getStackTrace());
        } finally {
            session.close();
        }
    }
}
