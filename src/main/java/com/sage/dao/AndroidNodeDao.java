package com.sage.dao;

import com.sage.models.AndroidNode;

import java.util.List;
import java.util.Map;

/**
 * Created by root on 2/27/16.
 */
public class AndroidNodeDao extends Dao<AndroidNode> {

    /**
     * Default constructor for AndroidNodeDao
     * @throws Exception if something goes wrong when
     * calling super()
     */
    public AndroidNodeDao() throws Exception {
        super();
    }



    @Override
    public AndroidNode get(int id) throws Exception {
        return null;
    }

    @Override
    public List<AndroidNode> get(Map filter) throws Exception {
        return null;
    }

    @Override
    public void add(AndroidNode node) throws Exception {

    }

    @Override
    public void upsert(AndroidNode node) throws Exception {

    }

}
