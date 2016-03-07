package com.sage.ws.dao;

import com.sage.ws.models.SageToken;
import com.sage.ws.models.SageToken;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Nick Hale
 *         NJohnHale@gmail.com
 *         NJIT - Computer Science & Applied Math
 */
public class SageTokenDao extends Dao<SageToken> {

    /**
     * Default constructor for SageTokenDao
     * @throws Exception if something goes wrong
     * when calling super()
     */
    public SageTokenDao() throws Exception {
        // call super
        super();
        logger.trace("SageTokenDao - Super called!");
    }

    @Override
    public SageToken get(int id) {
        // create a SageToken reference
        SageToken token = null;
        // get a session
        Session session = sessionFactory.openSession();

        try {
            session.beginTransaction();
            token = session.load(SageToken.class, id);
        } catch (HibernateException e) {
            logger.error("An error has occurred while attempting to retrieve the SageToken");
            logger.debug(e.getMessage());
            logger.debug(e.getStackTrace());
        } finally {
            session.close();
        }

        return token;
    }

    @Override
    public List<SageToken> get(List<Criterion> crits, Order order, int setSize) {
        // create List<SageToken> reference
        List<SageToken> tokens = new ArrayList<SageToken>();
        // open a new session
        Session session = sessionFactory.openSession();

        try {
            Criteria cr = session.createCriteria(SageToken.class);
            // add each of the given criterion
            for (Criterion crit : crits)
                cr.add(crit);
            // add the order
            cr.addOrder(order);
            // trim the resilt set
            cr.setFetchSize(setSize);
            // retrieve the tokens
            tokens = cr.list();
        } catch (HibernateException e) {
            logger.error("Something went wrong when attempting to get tokens from the datastore");
            logger.debug(e.getMessage());
            logger.debug(e.getStackTrace());
        } finally {
            session.close();
        }

        return tokens;
    }

    @Override
    public int add(SageToken token) {
        // open a new session
        Session session = sessionFactory.openSession();
        // instantiate the tokenId (-1 default)
        int tokenId = -1;

        try {
            session.beginTransaction();
            tokenId = (Integer) session.save(token);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            logger.error("Something went wrong when attempting to add token to the datastore");
            logger.debug("Rolling back changes...");
            session.getTransaction().rollback();
            logger.debug(e.getMessage());
            logger.debug(e.getStackTrace());
        } finally {
            session.close();
        }

        return tokenId;
    }

    @Override
    public void upsert(SageToken token) {
        // open a new session
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.saveOrUpdate(token);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            logger.error("Something went wrong when attempting to upsert token in the datastore");
            logger.debug("Rolling back changes...");
            session.getTransaction().rollback();
            logger.debug(e.getMessage());
            logger.debug(e.getStackTrace());
        } finally {
            session.close();
        }
    }
}
