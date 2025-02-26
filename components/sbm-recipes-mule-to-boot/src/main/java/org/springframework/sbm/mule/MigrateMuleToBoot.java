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
package org.springframework.sbm.mule;

import org.springframework.sbm.build.api.Dependency;
import org.springframework.sbm.build.migration.actions.AddDependencies;
import org.springframework.sbm.build.migration.actions.RemoveDependenciesMatchingRegex;
import org.springframework.sbm.build.migration.actions.RemovePluginsMatchingRegex;
import org.springframework.sbm.build.migration.conditions.NoDependencyExistMatchingRegex;
import org.springframework.sbm.engine.recipe.Recipe;
import org.springframework.sbm.java.migration.actions.AddTypeAnnotationToTypeAnnotatedWith;
import org.springframework.sbm.java.migration.conditions.HasNoTypeAnnotation;
import org.springframework.sbm.java.migration.conditions.HasTypeAnnotation;
import org.springframework.sbm.mule.actions.JavaDSLAction2;
import org.springframework.sbm.mule.conditions.MuleConfigFileExist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class MigrateMuleToBoot {

    @Autowired
    private freemarker.template.Configuration configuration;

    @Autowired
    private JavaDSLAction2 javaDSLAction2;


    @Bean
    public Recipe muleRecipe() {
        return Recipe.builder()
                .name("migrate-mule-to-boot")
                .description("Migrate Mulesoft 3.9 to Spring Boot")
                .order(60)
                .description("Migrate Mulesoft to Spring Boot")
                .condition(new MuleConfigFileExist())
                .actions(List.of(
                        /*
                        * Add dependencies for spring integration
                        */
                        AddDependencies.builder()
                                .condition(NoDependencyExistMatchingRegex.builder()
                                            .dependencies(List.of(
                                                    "org.springframework.boot:spring-boot-starter-web:2.5.5",
                                                    "org.springframework.boot:spring-boot-starter-integration:2.5.5",
                                                    "org.springframework.integration:spring-integration-http",
                                                    "org.springframework.integration:spring-integration-amqp:2.5.5",
                                                    "org.springframework.integration:spring-integration-stream:2.5.5"
                                            )
                                        )
                                        .build())
                                        .dependencies(List.of(
                                                Dependency.builder()
                                                        .groupId("org.springframework.boot")
                                                        .artifactId("spring-boot-starter-web")
                                                        .version("2.5.5")
                                                        .build(),
                                                Dependency.builder()
                                                        .groupId("org.springframework.boot")
                                                        .artifactId("spring-boot-starter-integration")
                                                        .version("2.5.5")
                                                        .build(),
                                                Dependency.builder()
                                                        .groupId("org.springframework.integration")
                                                        .artifactId("spring-integration-amqp")
                                                        .version("5.4.4")
                                                        .build(),
                                                Dependency.builder()
                                                        .groupId("org.springframework.integration")
                                                        .artifactId("spring-integration-stream")
                                                        .version("5.4.4")
                                                        .build(),
                                                Dependency.builder()
                                                        .groupId("org.springframework.integration")
                                                        .artifactId("spring-integration-http")
                                                        .version("5.4.4")
                                                        .build()
                                        ))
                                        .build(),

                        /*
                        * Annotate Spring Boot Application class with @EnableIntegration
                        */
                        AddTypeAnnotationToTypeAnnotatedWith.builder()
                                .annotatedWith("org.springframework.boot.autoconfigure.SpringBootApplication")
                                .annotation("org.springframework.integration.config.EnableIntegration")
                                .condition(
                                        HasNoTypeAnnotation.builder()
                                                .hasTypeAnnotation(
                                                   HasTypeAnnotation.builder()
                                                    .annotation("org.springframework.integration.config.EnableIntegration")
                                                    .build()
                                                ).build()
                                ).build(),


                        /*
                         * Add java class with Spring Integration DSL statements
                         */
                        javaDSLAction2,

//                        /*
//                        * Migrate Mulesoft XML to Spring Integration XML
//                        */
//                        MigrateMulesoftFile.builder().configuration(configuration).build(),

                        /*
                        * Remove Mule dependencies
                        */
                        RemoveDependenciesMatchingRegex.builder()
                                .dependenciesRegex(List.of("org\\.mule\\..*", "com\\.mulesoft\\..*"))
                                .build(),

                        /*
                        * Remove Mule plugins
                        */
                        RemovePluginsMatchingRegex.builder()
                                .pluginsRegex(List.of("org\\.mule\\..*", "com\\.mulesoft\\..*"))
                                .build()

                        /*
                        * TODO: How to find out if all elements were successfully migrated and Mule XML can be deleted?
                        */
                ))

                .build();
    }
}