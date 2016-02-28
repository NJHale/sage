package com.sage.dao;

import java.util.List;
import java.util.Map;

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
    public List<User> get(Map filter) throws Exception {
        return null;
    }
}
