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
package org.springframework.sbm.recipes;

import org.springframework.sbm.IntegrationTestBaseClass;
import org.springframework.sbm.engine.git.Commit;
import org.springframework.sbm.engine.git.GitSupport;
import org.springframework.sbm.project.resource.ApplicationProperties;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class MigrateJsf2xToSpringBootRecipeIntegrationTest extends IntegrationTestBaseClass {

    @Override
    protected String getTestSubDir() {
        return "jsf-2.x-migration";
    }

    @BeforeAll
    public static void beforeAll() {
        System.setProperty("sbm.gitSupportEnabledInTest", "true");
    }

    @Test
    @Tag("integration")
    void testRecipe() throws IOException {
        enableGitSupport();
        intializeTestProject();
        // create repo
        GitSupport gitSupport = initGitRepo();
        List<String> modifiedResources = Files.list(getTestDir()).map(f -> f.toAbsolutePath().toString()).collect(Collectors.toList());
        List<String> deletedResources = List.of();
        Commit initialCommit = gitSupport.addAllAndCommit(getTestDir().toFile(), "initial commit", modifiedResources, deletedResources);

        scanProject();
        assertApplicableRecipesContain(
                "initialize-spring-boot-migration",
                "migrate-jsf-2.x-to-spring-boot"
        );
        applyRecipe(
                "initialize-spring-boot-migration",
                "migrate-jsf-2.x-to-spring-boot"
        );
        // get latest commit
        Commit latestCommit = gitSupport.getLatestCommit(getTestDir().toFile()).get();
        assertThat(initialCommit.getHash()).isNotEqualTo(latestCommit.getHash());
        assertThat(latestCommit.getMessage()).contains("migrate-jsf-2.x-to-spring-boot");
        // FIXME: add more assertions
    }

    @NotNull
    private GitSupport initGitRepo() {
        ApplicationProperties applicationProperties = new ApplicationProperties();
        applicationProperties.setGitSupportEnabled(true);
        GitSupport gitSupport = new GitSupport(applicationProperties);
        gitSupport.initGit(getTestDir().toFile());
        return gitSupport;
    }
}
