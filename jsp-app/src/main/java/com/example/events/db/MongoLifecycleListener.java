package com.example.events.db;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class MongoLifecycleListener implements ServletContextListener {
    @Override public void contextInitialized(ServletContextEvent e) { MongoUtil.getDatabase(); }
    @Override public void contextDestroyed(ServletContextEvent e)  { MongoUtil.close(); }
}
