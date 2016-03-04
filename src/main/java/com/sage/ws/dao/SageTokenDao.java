package com.sage.ws.dao;

import org.hibernate.criterion.Criterion;

import java.util.List;

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
    public List<SageToken> get(List<Criterion> expressions) throws Exception {
        return null;
    }

    @Override
    public void add(SageToken token) throws Exception {

    }

    @Override
    public void upsert(SageToken token) throws Exception {

    }
}
