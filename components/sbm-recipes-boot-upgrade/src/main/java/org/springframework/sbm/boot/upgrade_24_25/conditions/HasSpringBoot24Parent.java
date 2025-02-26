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
package org.springframework.sbm.boot.upgrade_24_25.conditions;

import org.springframework.sbm.engine.context.ProjectContext;
import org.springframework.sbm.engine.recipe.Condition;

public class HasSpringBoot24Parent implements Condition {
    @Override
    public String getDescription() {
        return "Check if updating Spring Boot version from 2.4.x to 2.5.x is applicable.";
    }

    @Override
    public boolean evaluate(ProjectContext context) {
        return context.getBuildFile().hasParent() &&
                context.getBuildFile().getParentPomDeclaration().getArtifactId().equals("spring-boot-starter-parent") &&
                context.getBuildFile().getParentPomDeclaration().getVersion().startsWith("2.4.");
    }
}
