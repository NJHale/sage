package com.sage.dao;

import com.sage.models.Job;

import java.util.List;
import java.util.Map;

/**
 * Created by root on 2/27/16.
 */
public class JobDao<Job> extends Dao {

    public JobDao() {
        // call super
        super();
        logger.trace("JobDao - Super called!");
    }

    @Override
    public Job get(int id) {
        return null;
    }

    @Override
    public List<Job> get(Map filter) {
        return null;
    }

    @Override
    public void add(Object model) {

    }

    @Override
    public void upsert(Object model) {

    }


}
