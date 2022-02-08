package org.jboss.hal.testsuite.test.configuration.deploymentscanner;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.creaper.ResourceVerifier;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.TableFragment;
import org.jboss.hal.testsuite.page.configuration.DeploymentScannerPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.PATH;
import static org.jboss.hal.dmr.ModelDescriptionConstants.RELATIVE_TO;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.STANDALONE;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;
import static org.jboss.hal.testsuite.fixtures.DeploymentScannerFixtures.DS_CREATE;
import static org.jboss.hal.testsuite.fixtures.DeploymentScannerFixtures.DS_DELETE;
import static org.jboss.hal.testsuite.fixtures.DeploymentScannerFixtures.DS_READ;
import static org.jboss.hal.testsuite.fixtures.DeploymentScannerFixtures.DS_UPDATE;
import static org.jboss.hal.testsuite.fixtures.DeploymentScannerFixtures.DS_UPDATE_INVALID;
import static org.jboss.hal.testsuite.fixtures.DeploymentScannerFixtures.DS_UPDATE_RESET;
import static org.jboss.hal.testsuite.fixtures.DeploymentScannerFixtures.deploymentScannerAddress;
import static org.jboss.hal.testsuite.fixtures.DeploymentScannerFixtures.path;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Manatoko
@Testcontainers
class DeploymentScannerTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, STANDALONE);
    private static OnlineManagementClient client;

    @BeforeAll
    static void setupModel() throws Exception {
        client = wildFly.managementClient();
        Operations operations = new Operations(client);
        operations.add(deploymentScannerAddress(DS_READ), Values.of(PATH, path(DS_READ)));
        operations.add(deploymentScannerAddress(DS_UPDATE), Values.of(PATH, path(DS_UPDATE)));
        operations.add(deploymentScannerAddress(DS_UPDATE_INVALID), Values.of(PATH, path(DS_UPDATE_INVALID)));
        operations.add(deploymentScannerAddress(DS_UPDATE_RESET), Values.of(PATH, path(DS_UPDATE_RESET)));
        operations.add(deploymentScannerAddress(DS_DELETE), Values.of(PATH, path(DS_DELETE)));
    }

    @Page DeploymentScannerPage page;
    @Inject CrudOperations crud;
    @Inject Console console;
    TableFragment table;
    FormFragment form;

    @BeforeEach
    void prepare() {
        page.navigate();
        form = page.getForm();
        table = page.getTable();
        table.bind(form);
    }

    @Test
    void create() throws Exception {
        crud.create(deploymentScannerAddress(DS_CREATE), table, form -> {
            form.text(NAME, DS_CREATE);
            form.text(PATH, path(DS_CREATE));
        });
    }

    @Test
    void read() {
        table.select(DS_READ);
        assertEquals(path(DS_READ), form.value(PATH));
    }

    @Test
    void update() throws Exception {
        table.select(DS_UPDATE);
        crud.update(deploymentScannerAddress(DS_UPDATE), form, PATH, Random.name() + "/" + Random.name());
    }

    @Test
    void updateInvalidRelativeTo() throws Exception {
        table.select(DS_UPDATE_INVALID);
        form.edit();
        form.text(RELATIVE_TO, "invalid");
        form.save();

        console.verifyError();
        new ResourceVerifier(deploymentScannerAddress(DS_UPDATE), client)
                .verifyAttributeIsUndefined(RELATIVE_TO);
    }

    @Test
    void reset() throws Exception {
        table.select(DS_UPDATE_RESET);
        crud.reset(deploymentScannerAddress(DS_UPDATE), form);
    }

    @Test
    void delete() throws Exception {
        crud.delete(deploymentScannerAddress(DS_DELETE), table, DS_DELETE);
    }
}
