package com.curtisnewbie.activities;

/**
 * An object that has a {@code prompt} method to show a msg (e.g., using Toast)
 */
public interface Promptable {
    /**
     * Display a msg in this corresponding context (e.g., using Toast).
     * This method should be called in a UI thread.
     *
     * @param msg message
     */
    void prompt(String msg);
}
