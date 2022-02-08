package org.jboss.hal.testsuite.test.configuration.web.services.endpoint.configuration;

import org.apache.commons.lang3.RandomStringUtils;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fixtures.WebServicesFixtures;
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
class PostHandlerChainTest {

    private static final String END_POINT_CONFIGURATION_EDIT =
            "end-point-configuration-to-be-edited-" + RandomStringUtils.randomAlphanumeric(7);

    private static final WebServicesFixtures.HandlerChain POST_HANDLER_CHAIN_CREATE =
            new WebServicesFixtures.HandlerChain.Builder(END_POINT_CONFIGURATION_EDIT)
                    .handlerChainName("post-handler-chain-to-be-created-" + RandomStringUtils.randomAlphanumeric(7))
                    .endpointConfiguration()
                    .postHandlerChain()
                    .build();

    private static final WebServicesFixtures.HandlerChain POST_HANDLER_CHAIN_EDIT =
            new WebServicesFixtures.HandlerChain.Builder(END_POINT_CONFIGURATION_EDIT)
                    .handlerChainName("post-handler-chain-to-be-edited-" + RandomStringUtils.randomAlphanumeric(7))
                    .endpointConfiguration()
                    .postHandlerChain()
                    .build();

    private static final WebServicesFixtures.HandlerChain POST_HANDLER_CHAIN_DELETE =
            new WebServicesFixtures.HandlerChain.Builder(END_POINT_CONFIGURATION_EDIT)
                    .handlerChainName("post-handler-chain-to-be-removed-" + RandomStringUtils.randomAlphanumeric(7))
                    .endpointConfiguration()
                    .postHandlerChain()
                    .build();

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, STANDALONE);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);
        operations.add(WebServicesFixtures.endpointConfigurationAddress(END_POINT_CONFIGURATION_EDIT));
        operations.add(POST_HANDLER_CHAIN_EDIT.handlerChainAddress());
        operations.add(POST_HANDLER_CHAIN_DELETE.handlerChainAddress());
    }

    @Inject Console console;
    @Inject CrudOperations crudOperations;
    @Page WebServicesPage page;

    @BeforeEach
    void prepare() {
        page.navigate();
        console.verticalNavigation().selectPrimary(Ids.WEBSERVICES_ENDPOINT_CONFIG_ITEM);
        page.getEndpointConfigurationTable().action(END_POINT_CONFIGURATION_EDIT, "Post");
    }

    @Test
    void create() throws Exception {
        crudOperations.create(POST_HANDLER_CHAIN_CREATE.handlerChainAddress(),
                page.getEndpointConfigurationHandlerChainTable(), POST_HANDLER_CHAIN_CREATE.getHandlerChainName());
    }

    @Test
    void remove() throws Exception {
        crudOperations.delete(POST_HANDLER_CHAIN_DELETE.handlerChainAddress(),
                page.getEndpointConfigurationHandlerChainTable(), POST_HANDLER_CHAIN_DELETE.getHandlerChainName());
    }

    @Test
    void editProtocolBindings() throws Exception {
        page.getEndpointConfigurationHandlerChainTable().select(POST_HANDLER_CHAIN_EDIT.getHandlerChainName());
        crudOperations.update(POST_HANDLER_CHAIN_EDIT.handlerChainAddress(),
                page.getEndpointConfigurationHandlerChainForm(), "protocol-bindings");
    }

}
