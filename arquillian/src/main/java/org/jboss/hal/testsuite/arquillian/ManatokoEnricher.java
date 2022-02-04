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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.enricher.AbstractSearchContextEnricher;
import org.jboss.arquillian.graphene.enricher.ReflectionHelper;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.openqa.selenium.SearchContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Injects instances of the common classes into test classes, pages or page fragments.
 * <p>
 * The following classes can be injected by this enricher:
 * <ul>
 * <li>{@link Console}</li>
 * <li>{@link CrudOperations}</li>
 * </ul>
 */
public class ManatokoEnricher extends AbstractSearchContextEnricher {

    private static final Logger logger = LoggerFactory.getLogger(ManatokoEnricher.class);

    @Override
    public void enrich(SearchContext searchContext, Object target) {
        List<Field> fields = ReflectionHelper.getFieldsWithAnnotation(target.getClass(), Inject.class);
        for (Field field : fields) {
            if (field.getType().isAssignableFrom(Console.class)) {
                Console console = new Console();
                logger.debug("Enrich {}.{} with console {}",
                        target.getClass().getSimpleName(), field.getName(), console);
                enrichRecursively(searchContext, console);
                setValue(field, target, console);
            }
            if (field.getType().isAssignableFrom(CrudOperations.class)) {
                CrudOperations crud = new CrudOperations();
                logger.debug("Enrich {}.{} with CRUD operations {}",
                        target.getClass().getSimpleName(), field.getName(), crud);
                enrichRecursively(searchContext, crud);
                setValue(field, target, crud);
            }
        }
    }

    @Override
    public Object[] resolve(SearchContext searchContext, Method method, Object[] resolvedParams) {
        return resolvedParams;
    }

    @Override
    public int getPrecedence() {
        return 1;
    }
}
