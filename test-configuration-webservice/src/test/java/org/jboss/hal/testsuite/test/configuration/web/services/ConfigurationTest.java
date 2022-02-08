package org.jboss.hal.testsuite.test.configuration.web.services;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fixtures.WebServicesFixtures;
import org.jboss.hal.testsuite.page.configuration.WebServicesPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.operations.Operations;

import static org.jboss.hal.testsuite.container.WildFlyConfiguration.STANDALONE;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;

@Manatoko
@Testcontainers
class ConfigurationTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, STANDALONE);
    private static Operations operations;

    @BeforeAll
    static void setupModel() {
        operations = new Operations(wildFly.managementClient());
    }

    @Inject Console console;
    @Inject CrudOperations crudOperations;
    @Page WebServicesPage page;

    @BeforeEach
    void prepare() {
        page.navigate();
        console.verticalNavigation().selectPrimary(Ids.WEBSERVICES_ITEM);
    }

    @Test
    void toggleModifyWSDLAddress() throws Exception {
        boolean modifyWSDLAddress =
                operations.readAttribute(WebServicesFixtures.WEB_SERVICES_ADDRESS, "modify-wsdl-address")
                        .booleanValue();
        crudOperations.update(WebServicesFixtures.WEB_SERVICES_ADDRESS, page.getWebServicesConfigurationForm(),
                "modify-wsdl-address", !modifyWSDLAddress);
    }

    // TODO: recent wildfly uses an expression for this value and the flip operation should switch to normal mode
    // before flipping
    @Test
    @Disabled
    void toggleStatisticsEnabled() throws Exception {
        boolean statisticsEnabled =
                operations.readAttribute(WebServicesFixtures.WEB_SERVICES_ADDRESS, "statistics-enabled").booleanValue();
        crudOperations.update(WebServicesFixtures.WEB_SERVICES_ADDRESS, page.getWebServicesConfigurationForm(),
                "statistics-enabled", !statisticsEnabled);
    }

    @Test
    void editWSDLHost() throws Exception {
        crudOperations.update(WebServicesFixtures.WEB_SERVICES_ADDRESS, page.getWebServicesConfigurationForm(),
                "wsdl-host");
    }

    @Test
    void editWSDLPathRewriteRule() throws Exception {
        String wsdlPathRewriteRule = String.format("s/%s/%s/g", Random.name(), Random.name());
        crudOperations.update(WebServicesFixtures.WEB_SERVICES_ADDRESS, page.getWebServicesConfigurationForm(),
                "wsdl-path-rewrite-rule", wsdlPathRewriteRule);
    }

    @Test
    void editWSDLPort() throws Exception {
        crudOperations.update(WebServicesFixtures.WEB_SERVICES_ADDRESS, page.getWebServicesConfigurationForm(),
                "wsdl-port", Random.number(0, 65536));
    }

    @Test
    void editWSDLSecurePort() throws Exception {
        crudOperations.update(WebServicesFixtures.WEB_SERVICES_ADDRESS, page.getWebServicesConfigurationForm(),
                "wsdl-secure-port", Random.number(0, 65536));
    }

    @Test
    void editWSDLURIScheme() throws Exception {
        crudOperations.update(WebServicesFixtures.WEB_SERVICES_ADDRESS, page.getWebServicesConfigurationForm(),
                formFragment -> formFragment.select("wsdl-uri-scheme", "http"),
                resourceVerifier -> resourceVerifier.verifyAttribute("wsdl-uri-scheme", "http"));
    }
}
