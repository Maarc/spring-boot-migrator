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
package org.springframework.sbm.shell;

import org.springframework.sbm.engine.commands.ApplicableRecipeListCommand;
import org.springframework.sbm.engine.commands.ScanCommand;
import org.springframework.sbm.engine.context.ProjectContext;
import org.springframework.sbm.engine.context.ProjectContextHolder;
import org.springframework.sbm.engine.recipe.Recipe;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScanShellCommandTest {
    @Mock
    ScanCommand scanCommand;

    @Mock
    ApplicableRecipeListCommand applicableRecipeListCommand;

    @Mock
    ApplicableRecipeListRenderer applicableRecipeListRenderer;
    
    @Mock
    ProjectContextHolder contextHolder;

    @InjectMocks
    ScanShellCommand sut;

    @Test
    void testScan() {
        String projectRoot = "/test/projectRoot";
        Path rootPath = Path.of(projectRoot).toAbsolutePath().normalize();
        List<Recipe> recipes = List.of();
        AttributedString attributedString = new AttributedStringBuilder().toAttributedString();

        // TODO: test printing of header

        ProjectContext projectContext = mock(ProjectContext.class);
        when(scanCommand.execute(projectRoot)).thenReturn(projectContext);
        when(applicableRecipeListCommand.execute(projectContext)).thenReturn(recipes);
        when(applicableRecipeListRenderer.render(recipes)).thenReturn(attributedString);

        AttributedString result = sut.scan(projectRoot);

        assertThat(result).isSameAs(attributedString);
        verify(contextHolder).setProjectContext(projectContext);
    }

}