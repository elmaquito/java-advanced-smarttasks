package fr.limayrac.demo.project.infrastructure.web;

import fr.limayrac.demo.project.application.port.in.ProjectUseCase;
import fr.limayrac.demo.project.domain.model.Project;
import fr.limayrac.demo.project.infrastructure.web.dto.ProjectCreateRequest;
import fr.limayrac.demo.project.infrastructure.web.dto.ProjectResponse;
import fr.limayrac.demo.project.infrastructure.web.mapper.ProjectDtoMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProjectController.class)
@Import(ProjectDtoMapper.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectUseCase projectUseCase;

    // We can rely on @Import(ProjectDtoMapper.class) or SpyBean if needed, but import is fine for mapper

    @Test
    @WithMockUser(username = "user")
    void findAll_ShouldReturnProjects() throws Exception {
        Project project = Project.builder()
                .id(1L)
                .name("Test Project")
                .tenantId("default")
                .createdOn(LocalDateTime.now())
                .build();

        when(projectUseCase.getAll(anyString())).thenReturn(Collections.singletonList(project));

        mockMvc.perform(get("/api/projects")
                        .header("X-Tenant-ID", "default"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Project"));
    }

    @Test
    @WithMockUser(username = "user")
    void create_ShouldReturnCreatedProject() throws Exception {
        Project createdProject = Project.builder()
                .id(1L)
                .name("New Project")
                .tenantId("default")
                .createdOn(LocalDateTime.now())
                .build();

        when(projectUseCase.create(anyString(), anyString())).thenReturn(createdProject);

        String jsonRequest = "{\"name\":\"New Project\"}";

        mockMvc.perform(post("/api/projects")
                        .header("X-Tenant-ID", "default")
                        .contentType("application/json")
                        .content(jsonRequest)
                        .with(csrf())) // CSRF handling for POST
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Project"));
    }

    @Test
    void findAll_WithoutUser_ShouldBeUnauthorized() throws Exception {
        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isUnauthorized());
    }
}
