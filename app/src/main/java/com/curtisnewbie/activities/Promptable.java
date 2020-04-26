package com.curtisnewbie.activities;

/**
 * ------------------------------------
 * <p>
 * Author: Yongjie Zhuang
 * <p>
 * ------------------------------------
 * <p>
 * Interface that defines an {@code Activity} to have a {@code prompt} method to
 * show a msg (e.g., using Toast).
 * </p>
 */
public interface Promptable {
    /**
     * Display a msg in this corresponding context (e.g., using Toast). This method
     * should always create a UI thread to display a msg (if necessary), such that
     * the caller won't need to worry about attaching a UI Thread.
     *
     * @param msg message
     */
    void prompt(String msg);
}
