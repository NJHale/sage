package com.sage.ws.service;

import com.google.gson.Gson;
import com.sage.ws.models.AndroidNode;
import com.sage.ws.models.Job;
import com.sage.ws.models.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Created by Nick Hale on 2/21/16.
 * @author Nick Hale
 *         NJohnHale@gmail.com
 *
 */
@WebListener
public class SageServletContextListener implements ServletContextListener{

    private static final Logger logger = LogManager.getLogger(SageServletContextListener.class);

    /**
     * Site-wide global variables
     */

    public static SageConfig config;

    public static SessionFactory sessionFactory;

    /**
     * Clean up service
     * @param arg0
     */
    public void contextDestroyed(ServletContextEvent arg0) {
        logger.info("ServletContextListener destroyed");
        // close the hibernate session factory
        sessionFactory.close();
    }

    /**
     * Initialize all database connections and the JobMonitor service
     * @param arg0
     */
    public void contextInitialized(ServletContextEvent arg0) {
        logger.info("ServletContextListener started");

        try {
            logger.debug("Demarshalling sage config.json...");
            // demarshall config json
            FileReader reader = new FileReader("/opt/sage/config/config.json");
            BufferedReader buffer = new BufferedReader(reader);
            // instantiate a Gson parser
            Gson gson = new Gson();
            // demarshall
            config = gson.fromJson(buffer, SageConfig.class);
            logger.debug("Sage config.json successfully demarshalled!");
            logger.debug("Building Hibernate session factory...");
            // construct the hibernate session factory
            Configuration configuration = new Configuration();
            // configure the hibernate configuration
            configuration.configure()
                    .addPackage("com.sage.models")
                    .addAnnotatedClass(User.class)
                    .addAnnotatedClass(Job.class)
                    .addAnnotatedClass(AndroidNode.class);
            //.addAnnotatedClass(SageToken.class);

            // build the service registry
            StandardServiceRegistryBuilder builder =
                    new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
            // build the factory
            sessionFactory = configuration.buildSessionFactory(builder.build());
            logger.info("Hibernate SessionFactory successfully built!");
        } catch (Exception e) {
            logger.error("An error occurred while attempting web service startup.");
            logger.debug("Error: ", e);
        }


    }
}