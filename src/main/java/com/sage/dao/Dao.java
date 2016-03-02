package com.sage.dao;

import com.sage.service.SageServletContextListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.SimpleExpression;

import javax.servlet.ServletContextListener;
import javax.ws.rs.core.Context;
import java.util.List;
import java.util.Map;

/**
 * Created by root on 2/27/16.
 */
public abstract class Dao <T> {

    // inject the servlet context
    @Context
    private ServletContextListener context;

    // logging
    protected static final Logger logger = LogManager.getLogger(Dao.class);

    // Reference to Dao shared Hibernate SessionFactory
    protected SessionFactory sessionFactory;

    /**
     * Default Dao constructor called by subclasses
     * Configures and builds the singleton Hibernate SessionFactory if null
     * @throws Exception if something goes wrong while attempting to configure and
     * build the Hibernate SessionFactory
     */
    protected Dao() throws Exception {
        if (sessionFactory == null) {
            sessionFactory = SageServletContextListener.sessionFactory;
            logger.info("SessionFactory successfully referenced");
        }
    }

    /**
     * Gets a single model of Type T by its id
     * @param id Id of the model to retrieve
     * @return Model of type T with the given id
     */
    public abstract T get(int id) throws Exception;

    /**
     * Gets a List<T> based on the filter param given
     * @param crits List of SimpleExpression objects
     *                    to act as search criteria
     * @return List<T> based on the filter param given
     */
    public abstract List<T> get(List<Criterion> crits, Order order, int setSize) throws Exception;

    /**
     * Add a completely new model of type T to
     * the data layer
     * @param model New Model to attempt to add
     * @returns Id of the newly stored Job
     */
    public abstract int add(T model) throws Exception;

    /**
     * Updates the given model of type T
     * if it exists in the data layer
     * @param model Model to be updated
     */
    public abstract void upsert(T model) throws Exception;

}
