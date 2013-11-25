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

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

/**
 * CDI producer for EntityManagerFactory used by Camel JPA. Producer method must be @Named annotated so that SwitchYard
 * will make in available through the Camel registry where Camel JPA will pick it up.
 * 
 * @author Daniel Tschan <tschan@puzzle.ch>.
 */
@Dependent
public class EntityManagerFactoryProducer {

    public static boolean workaround;

    @PersistenceUnit
    EntityManagerFactory entityManagerFactory;

    @PersistenceContext
    EntityManager entityManager;

    @Produces
    @Named
    EntityManagerFactory getEntityManagerFactory() {
        if (workaround) {
            return new TransactionScopedEntityManagerFactory(entityManager);
        } else {
            return entityManagerFactory;
        }
    }
}
