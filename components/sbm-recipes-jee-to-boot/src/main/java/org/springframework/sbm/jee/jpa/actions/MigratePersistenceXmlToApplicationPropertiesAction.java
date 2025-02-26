/*
 * Copyright 2021 - 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.sbm.jee.jpa.actions;

import org.springframework.sbm.boot.properties.actions.AddSpringBootApplicationPropertiesAction;
import org.springframework.sbm.boot.properties.api.SpringBootApplicationProperties;
import org.springframework.sbm.boot.properties.search.SpringBootApplicationPropertiesResourceListFilter;
import org.springframework.sbm.build.api.ApplicationModule;
import org.springframework.sbm.engine.context.ProjectContext;
import org.springframework.sbm.engine.recipe.AbstractAction;
import org.springframework.sbm.jee.jpa.api.Persistence;
import org.springframework.sbm.jee.jpa.api.PersistenceXml;
import org.springframework.sbm.jee.jpa.filter.PersistenceXmlResourceFilter;

import java.util.List;
import java.util.Optional;

public class MigratePersistenceXmlToApplicationPropertiesAction extends AbstractAction {

    @Override
    public void apply(ProjectContext context) {
        ApplicationModule applicationModule = context.getApplicationModules().stream()
                .filter(m -> m.search(new PersistenceXmlResourceFilter()).isPresent())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No file 'META-INF/persistence.xml' could be found."));

        PersistenceXml persistenceXml = applicationModule.search(new PersistenceXmlResourceFilter()).get();
        List<SpringBootApplicationProperties> applicationProperties = applicationModule.search(new SpringBootApplicationPropertiesResourceListFilter());
        if (applicationProperties.isEmpty()) {
            new AddSpringBootApplicationPropertiesAction().apply(applicationModule);
            applicationProperties = context.search(new SpringBootApplicationPropertiesResourceListFilter());
        }
        mapPersistenceXmlToApplicationProperties(applicationProperties.get(0), persistenceXml);
        applicationProperties.get(0).markChanged();
    }

    void mapPersistenceXmlToApplicationProperties(SpringBootApplicationProperties applicationProperties, PersistenceXml persistenceXml) {
        List<Persistence.PersistenceUnit> persistenceUnits = persistenceXml.getPersistence().getPersistenceUnit();
        for (int index = 0; index < persistenceUnits.size(); index++) {
            Persistence.PersistenceUnit persistenceUnit = persistenceUnits.get(index);
            if(persistenceUnit.getProperties() != null && persistenceUnit.getProperties().getProperty() != null) {
                persistenceUnit.getProperties().getProperty().stream()
                        .forEach(p -> mapJpaPropertyToProperties(p, applicationProperties));
            }
        }
    }

    void mapJpaPropertyToProperties(Persistence.PersistenceUnit.Properties.Property p, SpringBootApplicationProperties applicationProperties) {
        JpaHibernatePropertiesToSpringBootPropertiesMapper propertiesMapper = new JpaHibernatePropertiesToSpringBootPropertiesMapper();
        Optional<SpringBootJpaProperty> optKv = propertiesMapper.map(p);
        if(optKv.isPresent()) {
            SpringBootJpaProperty kv = optKv.get();
            applicationProperties.setProperty(kv.getComment(), kv.getPropertyName(), kv.getPropertyValue());
        }
    }

    @Override
    public boolean isApplicable(ProjectContext context) {
        return context.search(new PersistenceXmlResourceFilter()).isPresent();
    }
}
