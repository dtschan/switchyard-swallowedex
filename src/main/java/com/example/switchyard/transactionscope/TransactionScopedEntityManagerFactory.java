/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.switchyard.transactionscope;

import java.util.Map;

import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.metamodel.Metamodel;

import org.jboss.logging.Logger;

/**
 * EntityManagerFactory which can be used to "create" transaction scoped entity managers. Used by
 * {@link EntityManagerFactoryProducer} to provide Camel JPA with transaction scoped entity managers.
 * 
 * @author Daniel Tschan <tschan@puzzle.ch>
 */
public class TransactionScopedEntityManagerFactory implements EntityManagerFactory {

    private final EntityManager entityManager;

    private static final Logger logger = Logger.getLogger(TransactionScopedEntityManagerFactory.class);

    public TransactionScopedEntityManagerFactory(EntityManager entityManager) {
        if (!entityManager.getClass().getSimpleName().startsWith("TransactionScoped")) {
            logger.warn("Given entity manager of " + entityManager.getClass()
                    + " doesn't seem to be transaction scoped. This is usually not what you want!");
        }

        this.entityManager = entityManager;
    }

    @Override
    public EntityManager createEntityManager() {
        return entityManager;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public EntityManager createEntityManager(Map map) {
        throw new RuntimeException("Not implemented!");
    }

    @Override
    public CriteriaBuilder getCriteriaBuilder() {
        return getEntityManagerFactory().getCriteriaBuilder();
    }

    @Override
    public Metamodel getMetamodel() {
        return getEntityManagerFactory().getMetamodel();
    }

    @Override
    public boolean isOpen() {
        return getEntityManagerFactory().isOpen();
    }

    @Override
    public void close() {
        getEntityManagerFactory().close();
    }

    @Override
    public Map<String, Object> getProperties() {
        return getEntityManagerFactory().getProperties();
    }

    @Override
    public Cache getCache() {
        return getEntityManagerFactory().getCache();
    }

    @Override
    public PersistenceUnitUtil getPersistenceUnitUtil() {
        return getEntityManagerFactory().getPersistenceUnitUtil();
    }

    private EntityManagerFactory getEntityManagerFactory() {
        return entityManager.getEntityManagerFactory();
    }
}
