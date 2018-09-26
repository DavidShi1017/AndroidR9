package com.nmbs.listeners;

/**
 * Created by shig on 2015/12/17.
 */
public interface SettingsListener {
    void setPassword(String password);
    void setEmail(String email);
    void setStartNotifi(String time);
    void setDelayNotifi(String time);
}
