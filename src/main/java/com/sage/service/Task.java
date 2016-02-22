package com.sage.service;

/**
 * @Author Nick Hale NJohnHale@gmail.com
 *
 * Defines how a Task should behave
 */
public interface Task {

    /**
     * The main operation that a task performs
     * @param taskNum Identifying task number
     * @param data A byte array that holds all data to be
     *             worked upon when the task is running
     * @return A byte array containing status or result data
     */
    public byte[] runTask(long taskNum, byte[] data);

}
