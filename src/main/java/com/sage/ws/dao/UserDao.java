package com.sage.ws.dao;

import com.sage.ws.models.User;
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
public class UserDao extends Dao<User> {

    /**
     * Default constructor for UserDao
     * @throws Exception if something goes wrong
     * when calling super()
     */
    public UserDao() throws Exception {
        // call super
        super();
        logger.trace("UserDao - Super called!");
    }

    @Override
    public User get(int id) {
        // create a User reference
        User user = null;
        // get a session
        Session session = sessionFactory.openSession();

        try {
            //session.beginTransaction();
            user = session.get(User.class, id);
        } catch (HibernateException e) {
            logger.error("An error has occurred while attempting to retrieve the User");
            logger.debug(e.getMessage());
            logger.debug(e.getStackTrace());
        } finally {
            session.close();
        }

        return user;
    }

    @Override
    public List<User> get(List<Criterion> crits, Order order, int setSize) {
        // create List<User> reference
        List<User> users = new ArrayList<User>();
        // open a new session
        Session session = sessionFactory.openSession();

        try {
            Criteria cr = session.createCriteria(User.class);
            // add each of the given criterion
            for (Criterion crit : crits)
                cr.add(crit);
            // add the order
            if (order != null) cr.addOrder(order);
            // trim the resilt set
            if (setSize > 0) cr.setMaxResults(setSize);
            // retrieve the Users
            users = cr.list();
        } catch (HibernateException e) {
            logger.error("Something went wrong when attempting to get Users from the datastore");
            logger.debug(e.getMessage());
            logger.debug(e.getStackTrace());
        } finally {
            session.close();
        }

        return users;
    }

    @Override
    public int add(User user) {
        // open a new session
        Session session = sessionFactory.openSession();
        // instantiate the UserId (-1 default)
        int userId = -1;

        try {
            session.beginTransaction();
            userId = (Integer) session.save(user);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            logger.error("Something went wrong when attempting to add User to the datastore");
            logger.debug("Rolling back changes...");
            session.getTransaction().rollback();
            logger.debug(e.getMessage());
            logger.debug(e.getStackTrace());
        } finally {
            session.close();
        }

        return userId;
    }

    @Override
    public void upsert(User user) {
        // open a new session
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.saveOrUpdate(user);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            logger.error("Something went wrong when attempting to upsert User in the datastore");
            logger.debug("Rolling back changes...");
            session.getTransaction().rollback();
            logger.debug(e.getMessage());
            logger.debug(e.getStackTrace());
        } finally {
            session.close();
        }
    }

}
