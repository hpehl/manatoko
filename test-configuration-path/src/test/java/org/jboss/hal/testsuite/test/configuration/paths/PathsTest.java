/*
 *  Copyright 2022 Red Hat
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.jboss.hal.testsuite.test.configuration.paths;

import java.io.IOException;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fixtures.PathsFixtures;
import org.jboss.hal.testsuite.page.configuration.PathsPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.testsuite.container.WildFlyConfiguration.STANDALONE;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26_1;

@Manatoko
@Testcontainers
class PathsTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(_26_1, STANDALONE);

    @BeforeAll
    static void setupModel() throws Exception {
        Operations operations = new Operations(wildFly.managementClient());
        createPathWithRandomPathValue(operations, PathsFixtures.PATH_EDIT);
        createPathWithRandomPathValue(operations, PathsFixtures.PATH_DELETE);
        createPathWithRandomPathValue(operations, PathsFixtures.RELATIVE_TO_PATH);
    }

    private static void createPathWithRandomPathValue(Operations operations, String pathName) throws IOException {
        operations.add(PathsFixtures.pathAddress(pathName), Values.of(PathsFixtures.PATH, Random.name()));
    }

    @Inject CrudOperations crud;
    @Page PathsPage page;

    @BeforeEach
    void setUp() {
        page.navigate();
    }

    @Test
    void create() throws Exception {
        String path = Random.name();
        crud.create(PathsFixtures.pathAddress(PathsFixtures.PATH_CREATE), page.getPathsTable(), form -> {
            form.text("name", PathsFixtures.PATH_CREATE);
            form.text(PathsFixtures.PATH, path);
        }, resourceVerifier -> {
            resourceVerifier.verifyExists();
            resourceVerifier.verifyAttribute(PathsFixtures.PATH, path);
        });
    }

    @Test
    void editPath() throws Exception {
        String path = Random.name();
        page.getPathsTable().select(PathsFixtures.PATH_EDIT);
        crud.update(PathsFixtures.pathAddress(PathsFixtures.PATH_EDIT), page.getPathsForm(), PathsFixtures.PATH, path);
    }

    @Test
    void editRelativeTo() throws Exception {
        page.getPathsTable().select(PathsFixtures.PATH_EDIT);
        crud.update(PathsFixtures.pathAddress(PathsFixtures.PATH_EDIT), page.getPathsForm(), "relative-to",
                PathsFixtures.RELATIVE_TO_PATH);
    }

    @Test
    void delete() throws Exception {
        crud.delete(PathsFixtures.pathAddress(PathsFixtures.PATH_DELETE), page.getPathsTable(),
                PathsFixtures.PATH_DELETE);
    }

}
