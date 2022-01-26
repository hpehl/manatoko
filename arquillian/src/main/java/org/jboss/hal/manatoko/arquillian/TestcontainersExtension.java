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
package org.jboss.hal.manatoko.arquillian;

import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.drone.spi.Configurator;
import org.jboss.arquillian.drone.spi.Destructor;
import org.jboss.arquillian.drone.spi.Instantiator;
import org.jboss.arquillian.graphene.spi.enricher.SearchContextTestEnricher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestcontainersExtension implements LoadableExtension {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestcontainersExtension.class);

    @Override
    public void register(final ExtensionBuilder builder) {
        LOGGER.info("Register {}", this.getClass().getSimpleName());
        builder.service(Configurator.class, TestcontainersWebDriverFactory.class);
        builder.service(Instantiator.class, TestcontainersWebDriverFactory.class);
        builder.service(Destructor.class, TestcontainersWebDriverFactory.class);
        builder.service(SearchContextTestEnricher.class, ConsoleEnricher.class);
    }
}
