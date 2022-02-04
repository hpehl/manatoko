/*
 *  Copyright 2022 Red Hat
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.jboss.hal.testsuite.arquillian;

import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.drone.spi.Configurator;
import org.jboss.arquillian.drone.spi.Destructor;
import org.jboss.arquillian.drone.spi.Instantiator;
import org.jboss.arquillian.graphene.spi.enricher.SearchContextTestEnricher;
import org.jboss.hal.testsuite.environment.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManatokoExtension implements LoadableExtension {

    private static final Logger logger = LoggerFactory.getLogger(ManatokoExtension.class);

    @Override
    public void register(final ExtensionBuilder builder) {
        if (Environment.instance().local()) {
            logger.info("Use Arquillian Graphene 2 extension");
            // No need to register anything. Graphene extension found in the classpath
            // will take over, since the Testcontainers extension isn't registered!
        } else if (Environment.instance().remote()) {
            logger.info("Register Testcontainers extension");
            builder.service(Configurator.class, TestcontainersWebDriverFactory.class);
            builder.service(Instantiator.class, TestcontainersWebDriverFactory.class);
            builder.service(Destructor.class, TestcontainersWebDriverFactory.class);
        }
        logger.info("Register Manatoko enricher");
        builder.service(SearchContextTestEnricher.class, ManatokoEnricher.class);
    }
}
