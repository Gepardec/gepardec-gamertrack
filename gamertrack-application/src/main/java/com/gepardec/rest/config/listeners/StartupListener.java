package com.gepardec.rest.config.listeners;

import com.gepardec.core.services.AuthService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class StartupListener implements ServletContextListener {

    @Inject
    private AuthService authService;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        authService.createDefaultUserIfNotExists();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Do something on shutdown
    }
}
