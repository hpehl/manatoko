package org.jboss.hal.testsuite.test.configuration.web.services.client.configuration;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.creaper.ResourceVerifier;
import org.jboss.hal.testsuite.fixtures.WebServicesFixtures;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.page.configuration.WebServicesPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;

import static org.jboss.hal.testsuite.container.WildFlyConfiguration.STANDALONE;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;

@Manatoko
@Testcontainers
class ClientConfigurationTest {

    private static final String CLIENT_CONFIGURATION_CREATE =
            "client-configuration-to-be-created-" + RandomStringUtils.randomAlphanumeric(7);

    private static final String CLIENT_CONFIGURATION_EDIT =
            "client-configuration-to-be-edited-" + RandomStringUtils.randomAlphanumeric(7);

    private static final String CLIENT_CONFIGURATION_REMOVE =
            "client-configuration-to-be-removed-" + RandomStringUtils.randomAlphanumeric(7);

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, STANDALONE);
    private static OnlineManagementClient client;

    @BeforeAll
    static void setupModel() throws Exception {
        client = wildFly.managementClient();
        Operations operations = new Operations(client);
        operations.add(WebServicesFixtures.clientConfigurationAddress(CLIENT_CONFIGURATION_EDIT));
        operations.add(WebServicesFixtures.clientConfigurationAddress(CLIENT_CONFIGURATION_REMOVE));
    }

    @Inject Console console;
    @Inject CrudOperations crudOperations;
    @Page WebServicesPage page;

    @BeforeEach
    void prepare() {
        page.navigate();
        console.verticalNavigation().selectPrimary(Ids.WEBSERVICES_CLIENT_CONFIG_ITEM);
    }

    @Test
    void create() throws Exception {
        crudOperations.create(WebServicesFixtures.clientConfigurationAddress(CLIENT_CONFIGURATION_CREATE),
                page.getClientConfigurationTable(), CLIENT_CONFIGURATION_CREATE);
    }

    @Test
    void editProperty() throws Exception {
        Map<String, String> properties = new HashMap<>();
        properties.put(Random.name(), Random.name());
        properties.put(Random.name(), Random.name());
        page.getClientConfigurationTable().select(CLIENT_CONFIGURATION_EDIT);
        FormFragment clientConfigurationForm = page.getClientConfigurationForm();
        clientConfigurationForm.edit();
        clientConfigurationForm.properties("property").add(properties);
        clientConfigurationForm.save();
        console.verifySuccess();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            new ResourceVerifier(
                    WebServicesFixtures.clientConfigurationAddress(CLIENT_CONFIGURATION_EDIT)
                            .and("property", entry.getKey()),
                    client)
                    .verifyExists()
                    .verifyAttribute("value", entry.getValue());
        }
    }

    @Test
    void remove() throws Exception {
        crudOperations.delete(WebServicesFixtures.clientConfigurationAddress(CLIENT_CONFIGURATION_REMOVE),
                page.getClientConfigurationTable(), CLIENT_CONFIGURATION_REMOVE);
    }
}
