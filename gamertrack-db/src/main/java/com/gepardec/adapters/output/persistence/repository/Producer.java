package com.gepardec.adapters.output.persistence.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class Producer {

    @Produces
    @PersistenceContext
    private EntityManager entityManager;
}
