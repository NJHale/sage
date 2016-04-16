package com.sage.ws.util;

import com.sage.ws.models.AndroidNode;
import com.sage.ws.models.Job;
import com.sage.ws.models.JobStatus;
import com.sage.ws.models.User;

/**
 * Created by Nick Hale on 2/21/16.
 * @author Nick Hale
 *         NJohnHale@gmail.com
 *
 * Provides methods to ensure that a modified Model has not been tampered with
 * (ie. only allowed fields have been altered)
 */
public class ModelValidator {

    public ModelValidator() { }

    /**
     *
     * @param src
     * @param mod
     * @return
     */
    public boolean validate(AndroidNode src, AndroidNode mod) {
        return false;
    }

    /**
     * Checks if ordererId, encodedDex, data, or timeOut have been altered and returns the result
     * @param src The source Job to check against
     * @param mod The modified Job to check for tampering
     * @return true if ordererId, encodedDex, data, or timeOut have not been altered, false otherwise
     */
    public boolean validate(Job src, Job mod) {

        boolean valid = false;
        try {
            valid = mod.getTimeout() > 0 &&
                    mod.getStatus().ordinal() >= JobStatus.DONE.ordinal() &&
                    mod.getStatus().ordinal() <= JobStatus.TIMED_OUT.ordinal() &&
                    mod.getResult() != null &&
                    mod.getBounty() == src.getBounty() &&
                    !( mod.getOrdererId() != src.getOrdererId() ||
                    !mod.getData().equals(src.getData()) ||
                    mod.getTimeout() != src.getTimeout() );
        } catch (Exception e) {
            // this case there is some null causing a problem
            // drop the exception
        }

        return valid;
    }

    /**
     *
     * @param src
     * @param mod
     * @return
     */
    public boolean validate(User src, User mod) {
        return false;
    }

}
