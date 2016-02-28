package com.sage.dao;

import com.sage.models.Job;
import org.hibernate.criterion.SimpleExpression;

import java.util.List;
import java.util.Map;

/**
 * Created by Nick Hale on 2/21/16.
 * @Author Nick Hale
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
        return null;
    }

    @Override
    public List<Job> get(List<SimpleExpression> expressions) {
        return null;
    }

    @Override
    public void add(Job job) {

    }

    @Override
    public void upsert(Job job) {

    }


}
