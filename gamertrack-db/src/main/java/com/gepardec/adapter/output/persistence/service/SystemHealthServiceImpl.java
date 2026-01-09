package com.gepardec.adapter.output.persistence.service;

import com.gepardec.core.services.SystemHealthService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic readiness check that verifies database connectivity.
 */
@ApplicationScoped
public class SystemHealthServiceImpl implements SystemHealthService {

    private static final Logger log = LoggerFactory.getLogger(SystemHealthServiceImpl.class);

    @Inject
    EntityManager entityManager;

    @Override
    public boolean isReady() {
        try {
            // A minimal query to validate DB connectivity/transaction capability
            entityManager.createNativeQuery("SELECT 1").getSingleResult();
            return true;
        } catch (PersistenceException | IllegalStateException ex) {
            log.warn("Health check failed: database not ready", ex);
            return false;
        } catch (Exception ex) {
            log.warn("Health check failed with unexpected error", ex);
            return false;
        }
    }
}
