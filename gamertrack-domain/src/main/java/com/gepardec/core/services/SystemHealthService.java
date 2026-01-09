package com.gepardec.core.services;

/**
 * Reports application readiness (critical dependencies available).
 */
public interface SystemHealthService {
    /**
     * @return true if all critical dependencies are reachable/ready; false otherwise
     */
    boolean isReady();
}
