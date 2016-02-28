package com.sage.dao;

import com.sage.models.AndroidNode;
import org.hibernate.criterion.SimpleExpression;

import java.util.List;

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
    public List<AndroidNode> get(List<SimpleExpression> expressions) throws Exception {
        return null;
    }

    @Override
    public void add(AndroidNode node) throws Exception {

    }

    @Override
    public void upsert(AndroidNode node) throws Exception {

    }

}
