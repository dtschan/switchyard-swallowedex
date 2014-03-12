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

import java.io.File;
import java.util.zip.ZipFile;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.component.bean.Reference;

/**
 * Arquillian test showing that JPA and Camel JPA use different transaction scopes in same transaction.
 * 
 * @author Daniel Tschan <tschan@puzzle.ch>
 */
@RunWith(Arquillian.class)
public class TransactionScopeTest {
    private static final String JAR_FILE = "target/switchyard-swallowedex-0.0.1-SNAPSHOT.jar";

    @PersistenceContext
    EntityManager entityManager;

    @Inject
    @Reference("StoreReference")
    StoreService storeService;

    // mappedName needed because of ARQ-538?
    @Resource(mappedName = "java:jboss/UserTransaction")
    UserTransaction utx;

    @Deployment
    public static Archive<?> createTestArchive() {
        File artifact = new File(JAR_FILE);
        try {
            // TransactionScopeIntegrationTest added for subclass TransactionScopeWorkaroundTest
            JavaArchive archive =
                    ShrinkWrap.create(ZipImporter.class, artifact.getName()).importFrom(new ZipFile(artifact))
                            .as(JavaArchive.class).addClass(TransactionScopeTest.class);

            return archive;
        } catch (Exception e) {
            throw new RuntimeException(JAR_FILE + " not found. Do \"mvn package\" before the test", e);
        }
    }

    @Test
    public void testTransactionScope() throws Exception {
        try {
            utx.begin();

            entityManager.setFlushMode(FlushModeType.COMMIT);
            entityManager.joinTransaction();

            TestEntity entity1 = new TestEntity("John", -1);

            storeService.store(entity1);

            entity1 = TestEntity.lastPersistedEntity;

            TestEntity entity2 = entityManager.find(TestEntity.class, entity1.getId());

            Assert.assertNotNull(entity2);
            Assert.assertSame(entity2, entity1);
        } finally {
            utx.rollback();
        }
    }
}
