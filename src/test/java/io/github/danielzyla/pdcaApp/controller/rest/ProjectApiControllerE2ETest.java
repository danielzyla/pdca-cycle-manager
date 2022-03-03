package io.github.danielzyla.pdcaApp.controller.rest;

import io.github.danielzyla.pdcaApp.dto.ProjectReadDto;
import io.github.danielzyla.pdcaApp.dto.ProjectWriteApiDto;
import io.github.danielzyla.pdcaApp.dto.ProjectWriteDto;
import io.github.danielzyla.pdcaApp.dto.UserWriteDto;
import io.github.danielzyla.pdcaApp.model.Department;
import io.github.danielzyla.pdcaApp.model.Product;
import io.github.danielzyla.pdcaApp.repository.DepartmentRepository;
import io.github.danielzyla.pdcaApp.repository.ProductRepository;
import io.github.danielzyla.pdcaApp.security.CustomAuthenticationProvider;
import io.github.danielzyla.pdcaApp.service.ProjectService;
import io.github.danielzyla.pdcaApp.service.RoleService;
import io.github.danielzyla.pdcaApp.service.SignUpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ProjectApiControllerE2ETest {
    @Value("${app.protocol.host}")
    private String PROTOCOL_HOST_URL;

    @LocalServerPort
    private int port;

    private static final String URL_LOGIN_PATH = "/api/login";
    private static final String API_PROJECTS_URL_PATH = "/api/projects";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private CustomAuthenticationProvider authenticationProvider;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SignUpService signUpService;

    private String token;

    @BeforeEach
    void authenticate() {
        UserWriteDto userWriteDto = new UserWriteDto();
        userWriteDto.setUsername("admin");
        userWriteDto.setPassword(System.getenv("SECRET"));
        ResponseEntity<String> tokenResponse = restTemplate.postForEntity(
                PROTOCOL_HOST_URL + ":" + port + URL_LOGIN_PATH,
                userWriteDto,
                String.class
        );
        UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(userWriteDto.getUsername(), userWriteDto.getPassword());
        Authentication auth = authenticationProvider.authenticate(authReq);
        SecurityContextHolder.getContext().setAuthentication(auth);
        this.token = tokenResponse.getBody();
    }

    @Test
    void newProject_ShouldReturnSavedProjectAsReadDto() {
        //given
        ProjectWriteApiDto apiDto = new ProjectWriteApiDto();
        apiDto.setProjectName("project-name");
        apiDto.setProjectCode("project-code");
        int deptId1 = departmentRepository.save(new Department("dept1")).getId();
        int deptId2 = departmentRepository.save(new Department("dept2")).getId();
        long productId1 = productRepository.save(new Product("p1", "code1", "serial1")).getId();
        long productId2 = productRepository.save(new Product("p2", "code2", "serial2")).getId();
        apiDto.getDepartmentIds().addAll(Set.of(deptId1, deptId2));
        apiDto.getProductIds().addAll(Set.of(productId1, productId2));

        //when
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<ProjectWriteApiDto> request = new HttpEntity<>(apiDto, headers);
        ResponseEntity<ProjectReadDto> projectReadDtoResponseEntity = restTemplate.exchange(
                PROTOCOL_HOST_URL+ ":" + port + API_PROJECTS_URL_PATH,
                HttpMethod.POST,
                request,
                ProjectReadDto.class
        );
        ProjectReadDto readDtoResponse = projectReadDtoResponseEntity.getBody();

        //then
        assertAll(
                () -> assertThat(readDtoResponse, is(instanceOf(ProjectReadDto.class))),
                () -> assertThat(readDtoResponse, is(notNullValue())),
                () -> assertEquals(readDtoResponse.getProjectName(), "project-name"),
                () -> assertEquals(readDtoResponse.getProjectCode(), "project-code"),
                () -> assertEquals(readDtoResponse.getDepartments().size(), 2),
                () -> assertThat(readDtoResponse.getDepartments().get(0).getDeptName(), anyOf(equalTo("dept1"), equalTo("dept2"))),
                () -> assertThat(readDtoResponse.getDepartments().get(1).getDeptName(), anyOf(equalTo("dept1"), equalTo("dept2"))),
                () -> assertThat(readDtoResponse.getProducts().get(0).getProductName(), anyOf(equalTo("p1"), equalTo("p2"))),
                () -> assertThat(readDtoResponse.getProducts().get(1).getProductName(), anyOf(equalTo("p1"), equalTo("p2"))),
                () -> assertThat(readDtoResponse.getProducts().get(0).getProductCode(), anyOf(equalTo("code1"), equalTo("code2"))),
                () -> assertThat(readDtoResponse.getProducts().get(1).getProductCode(), anyOf(equalTo("code1"), equalTo("code2"))),
                () -> assertThat(readDtoResponse.getProducts().get(0).getSerialNo(), anyOf(equalTo("serial1"), equalTo("serial2"))),
                () -> assertThat(readDtoResponse.getProducts().get(1).getSerialNo(), anyOf(equalTo("serial1"), equalTo("serial2")))
        );
    }

    @Test
    void readAll_ShouldReturnAllProjectsAsReadDtoList() {
        //given
        ProjectWriteDto project1 = new ProjectWriteDto();
        project1.setProjectName("project-1");
        project1.setProjectCode("1/2021");
        ProjectWriteDto project2 = new ProjectWriteDto();
        project2.setProjectName("project-2");
        project2.setProjectCode("2/2021");
        projectService.createNewProject(project1);
        projectService.createNewProject(project2);

        //when
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(this.token);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<ProjectReadDto[]> response = restTemplate.exchange(
                PROTOCOL_HOST_URL+ ":" + port + API_PROJECTS_URL_PATH,
                HttpMethod.GET,
                request,
                ProjectReadDto[].class
        );
        List<ProjectReadDto> projectReadDtos = Arrays.asList(Objects.requireNonNull(response.getBody()));

        //then
        assertEquals(projectReadDtos.size(), 2);
        assertThat(projectReadDtos.get(0).getProjectName(), startsWith("project"));
        assertThat(projectReadDtos.get(0).getProjectCode(), containsString("2021"));
    }
}