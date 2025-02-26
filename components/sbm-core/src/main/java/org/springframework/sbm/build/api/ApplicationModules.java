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
package org.springframework.sbm.build.api;

import org.springframework.sbm.build.impl.OpenRewriteMavenBuildFile;
import org.jetbrains.annotations.NotNull;
import org.openrewrite.maven.tree.Modules;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ApplicationModules {
    private final List<ApplicationModule> modules;

    public ApplicationModules(List<ApplicationModule> modules) {
        this.modules = modules;
    }

    public Stream<ApplicationModule> stream() {
        return modules.stream();
    }

    public ApplicationModule getRootModule() {
        return modules.stream()
                .sorted((m2, m1) -> m1.getBuildFile().getAbsolutePath().toString().compareTo(m2.getBuildFile().getAbsolutePath().toString()))
                .findFirst()
                .orElse(modules.get(0));
    }

    public List<ApplicationModule> list() {
        return stream().collect(Collectors.toUnmodifiableList());
    }

    public ApplicationModule getModule(Path name) {
        return modules.stream()
                .filter(m -> m.getModulePath().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Could not find module with name '" + name + "'"));
    }

    public ApplicationModule getModule(String name) {
        return getModule(Path.of(name));
    }

    public List<ApplicationModule> getModules(ApplicationModule module) {
        Optional<Modules> modulesMarker = ((OpenRewriteMavenBuildFile) module.getBuildFile()).getPom().getMarkers().findFirst(Modules.class);
        if (modulesMarker.isPresent()) {
            return getModulesForMarkers(modulesMarker.get());
        } else {
            return new ArrayList<>();
        }
    }

    @NotNull
    private List<ApplicationModule> getModulesForMarkers(Modules modulesMarker) {
        List<String> collect = modulesMarker.getModules().stream()
                .map(m -> m.getGroupId() + ":" + m.getArtifactId())
                .collect(Collectors.toList());

        List<ApplicationModule> modules = this.modules.stream()
                .filter(module -> {
                    String groupAndArtifactId = module.getBuildFile().getGroupId() + ":" + module.getBuildFile().getArtifactId();
                    return collect.contains(groupAndArtifactId);
                })
                .collect(Collectors.toList());
        return modules;
    }

    public List<ApplicationModule> getTopmostApplicationModules() {
        List<ApplicationModule> topmostModules = new ArrayList<>();
        modules.forEach(module -> {
            // is jar
            if ("jar".equals(module.getBuildFile().getPackaging())) {
                // no other pom depends on this pom in its dependency section
                if (noOtherPomDependsOn(module.getBuildFile())) {
                    // has no parent or parent has packaging pom
                    ParentDeclaration parentPomDeclaration = module.getBuildFile().getParentPomDeclaration();
                    if (parentPomDeclaration == null) {
                        topmostModules.add(module);
                    } else if (isDeclaredInProject(parentPomDeclaration) && isPackagingOfPom(parentPomDeclaration)) {
                        topmostModules.add(module);
                    } else if (!isDeclaredInProject(parentPomDeclaration)) {
                        topmostModules.add(module);
                    }
                }
            }
        });
        return topmostModules;
    }

    private boolean isPackagingOfPom(ParentDeclaration parentPomDeclaration) {
        Optional<ApplicationModule> applicationModule = this.modules.stream()
                .filter(module -> module.getBuildFile().getCoordinates().equals(parentPomDeclaration.getCoordinates()))
                .findFirst();
        if (applicationModule.isPresent()) {
            BuildFile buildFile = applicationModule.get().getBuildFile();
            return "pom".equals(buildFile.getPackaging());
        }
        return true;
    }

    private boolean isDeclaredInProject(ParentDeclaration parentPomDeclaration) {
        return this.modules.stream()
                .anyMatch(module -> module.getBuildFile().getCoordinates().equals(parentPomDeclaration.getCoordinates()));
    }

    private boolean noOtherPomDependsOn(BuildFile buildFile) {
        return !this.modules.stream()
                .anyMatch(module -> module.getBuildFile().getDeclaredDependencies().stream().anyMatch(d -> d.getCoordinates().equals(buildFile.getCoordinates())));
    }

    public boolean isSingleModuleApplication() {
        return modules.size() == 1;
    }
}
