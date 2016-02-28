package com.sage.dao;

import org.hibernate.criterion.SimpleExpression;

import java.util.List;

/**
 * Created by root on 2/27/16.
 */
public class UserDao<User> extends Dao<User> {

    public UserDao() throws Exception {
        super();
    }


    @Override
    public User get(int id) throws Exception {
        return null;
    }

    @Override
    public void add(User user) throws Exception {

    }

    @Override
    public void upsert(User user) throws Exception {

    }

    @Override
    public List<User> get(List<SimpleExpression> expressions) throws Exception {
        return null;
    }
}
