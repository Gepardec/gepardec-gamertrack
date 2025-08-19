package com.gepardec.rest.config.listeners;

import com.gepardec.core.services.AuthService;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

@ApplicationScoped
public class StartupListener {

    @Inject
    AuthService authService;

    void onStartup(@Observes StartupEvent event) {
        authService.createDefaultUser();
    }
}
