package io.github.danielzyla.pdcaApp.controller.rest;

import io.github.danielzyla.pdcaApp.dto.ProjectWriteDto;
import io.github.danielzyla.pdcaApp.security.CustomAuthenticationProvider;
import io.github.danielzyla.pdcaApp.service.ProjectService;
import io.github.danielzyla.pdcaApp.service.RoleService;
import io.github.danielzyla.pdcaApp.service.SignUpService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class ProjectApiControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private CustomAuthenticationProvider authenticationProvider;

    @Autowired
    private RoleService roleService;

    @Autowired
    private SignUpService signUpService;

    void authenticate() {
        UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken("admin", "%2021pdca");
        Authentication auth = authenticationProvider.authenticate(authReq);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void getProjectById_ReturnsGivenProject() throws Exception {
        //given
        authenticate();

        ProjectWriteDto project = new ProjectWriteDto();
        project.setProjectName("project-1");
        project.setProjectCode("1/2021");
        long id = projectService.createNewProject(project).getId();

        //when + then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/projects/" + id))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.projectName").value("project-1"))
                .andExpect(jsonPath("$.projectCode").value("1/2021"))
                .andExpect(jsonPath("$.startTime").isNotEmpty())
                .andDo(print())
                .andReturn();
    }

    @Test
    void getProjectById_ShouldReturn404ResponseWhenNonExistingIdIsUsed() throws Exception {
        //when + then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/projects/2"))
                .andExpect(status().is(404))
                .andDo(print());
    }
}
