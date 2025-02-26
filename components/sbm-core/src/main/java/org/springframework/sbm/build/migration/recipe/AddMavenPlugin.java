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
package org.springframework.sbm.build.migration.recipe;

import org.springframework.sbm.build.api.Plugin;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.maven.MavenVisitor;
import org.openrewrite.maven.tree.Maven;
import org.openrewrite.xml.AddToTagVisitor;
import org.openrewrite.xml.ChangeTagValueVisitor;
import org.openrewrite.xml.XPathMatcher;
import org.openrewrite.xml.tree.Xml;

import java.util.Optional;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = true)
public class AddMavenPlugin extends Recipe {

    private static final XPathMatcher BUILD_MATCHER = new XPathMatcher("/project/build");

    private final Plugin plugin;

    @Override
    protected TreeVisitor<?, ExecutionContext> getVisitor() {
        return new AddPluginVisitor();
    }

    private class AddPluginVisitor extends MavenVisitor {

        @Override
        public Maven visitMaven(Maven maven, ExecutionContext ctx) {
            Xml.Tag root = maven.getRoot();
            if (!root.getChild("build").isPresent()) {
                doAfterVisit(new AddToTagVisitor<>(root,
                        Xml.Tag.build("<build>\n" +
                                "<plugins>\n" +
                                createPluginTagString() +
                                "</plugins>\n" +
                                "</build>"),
                        new MavenTagInsertionComparator(root.getChildren())));
            }

            return super.visitMaven(maven, ctx);
        }


        public Xml visitTag(Xml.Tag tag, ExecutionContext ctx) {
            Xml.Tag t = (Xml.Tag) super.visitTag(tag, ctx);
            if (BUILD_MATCHER.matches(this.getCursor())) {
                Optional<Xml.Tag> maybePlugins = t.getChild("plugins");
                if (!maybePlugins.isPresent()) {
                    this.doAfterVisit(new AddToTagVisitor(t, Xml.Tag.build("<plugins/>")));
                } else {
                    Xml.Tag plugins = maybePlugins.get();
                    Optional<Xml.Tag> maybePlugin = plugins.getChildren().stream().filter((pluginx) -> {
                        return pluginx.getName().equals("plugin") && plugin.getGroupId().equals(pluginx.getChildValue("groupId").orElse(null)) &&
                                plugin.getArtifactId().equals(pluginx.getChildValue("artifactId").orElse(null));
                    }).findAny();
                    if (maybePlugin.isPresent()) {
                        Xml.Tag plugin = maybePlugin.get();
                        if (AddMavenPlugin.this.plugin.getVersion() != null && !AddMavenPlugin.this.plugin.getVersion().equals(plugin.getChildValue("version").orElse(null))) {
                            this.doAfterVisit(new ChangeTagValueVisitor(plugin.getChild("version").get(), AddMavenPlugin.this.plugin.getVersion()));
                        }
                    } else {
                        Xml.Tag pluginTag = Xml.Tag.build(createPluginTagString());
                        this.doAfterVisit(new AddToTagVisitor(plugins, pluginTag));
                    }
                }
            }

            return t;
        }
    }

    private String createPluginTagString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<plugin>\n");
        sb.append("<groupId>");
        sb.append(plugin.getGroupId());
        sb.append("</groupId>\n");
        sb.append("<artifactId>");
        sb.append(plugin.getArtifactId());
        sb.append("</artifactId>\n");
        sb.append(renderVersion());
        sb.append(renderExecutions());
        sb.append(plugin.getConfiguration() != null ? plugin.getConfiguration().trim() + "\n" : "");
        sb.append(plugin.getDependencies() != null ? plugin.getDependencies().trim() + "\n" : "");
        sb.append("</plugin>\n");
        return sb.toString();
    }

    private String renderGoal(String goal) {
        return "<goal>" + goal + "</goal>";
    }

    private String renderVersion() {
        return plugin.getVersion() != null ? "<version>" + plugin.getVersion() + "</version>\n" : "";
    }

    private String renderExecutions() {
        if (plugin.getExecutions() == null || plugin.getExecutions().isEmpty()) return "";
        String executions = AddMavenPlugin.this.plugin.getExecutions().stream()
                .map(this::renderExecution)
                .collect(Collectors.joining("\n"));
        return "<executions>\n" + executions + "\n</executions>\n";
    }

    private String renderExecution(Plugin.Execution execution) {
        return "<execution>\n" +
                renderId(execution) +
                renderGoals(execution) +
                renderPhase(execution) +
                renderConfiguration(execution) +
                "</execution>";
    }

    private String renderConfiguration(Plugin.Execution execution) {
        return execution.getConfiguration() == null ? "" : execution.getConfiguration().trim();
    }

    private String renderId(Plugin.Execution execution) {
        return execution.getId() != null && !execution.getId().isBlank() ? "<id>" + execution.getId() + "</id>\n" : "";
    }

    private String renderGoals(Plugin.Execution execution) {
        if (execution.getGoals() == null || execution.getGoals().isEmpty()) return "";
        String goals = execution.getGoals()
                .stream()
                .map(this::renderGoal)
                .collect(Collectors.joining("\n"));
        return "<goals>\n" + goals + "\n</goals>\n";
    }

    private String renderPhase(Plugin.Execution execution) {
        return execution.getPhase() == null ? "" : "<phase>" + execution.getPhase() + "</phase>";
    }

    @Override
    public String getDisplayName() {
        return "Add Maven Plugin";
    }

}
