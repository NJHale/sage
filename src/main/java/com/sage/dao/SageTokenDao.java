package com.sage.dao;

import org.hibernate.criterion.SimpleExpression;

import java.util.List;
import java.util.Map;

/**
 * @author Nick Hale
 *         NJohnHale@gmail.com
 *         NJIT - Computer Science & Applied Math
 */
public class SageTokenDao<SageToken> extends Dao<SageToken> {

    /**
     * Default SageToken constructor
     * @throws Exception if something goes wrong
     * while calling super()
     */
    public SageTokenDao() throws Exception {
        super();
    }

    @Override
    public SageToken get(int id) throws Exception {
        return null;
    }

    @Override
    public List<SageToken> get(List<SimpleExpression> expressions) throws Exception {
        return null;
    }

    @Override
    public void add(SageToken token) throws Exception {

    }

    @Override
    public void upsert(SageToken token) throws Exception {

    }
}
