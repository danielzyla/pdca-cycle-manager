package io.github.danielzyla.pdcaApp.service;

import io.github.danielzyla.pdcaApp.dto.ProjectReadDto;
import io.github.danielzyla.pdcaApp.dto.ProjectWriteApiDto;
import io.github.danielzyla.pdcaApp.dto.ProjectWriteDto;
import io.github.danielzyla.pdcaApp.model.Cycle;
import io.github.danielzyla.pdcaApp.model.Department;
import io.github.danielzyla.pdcaApp.model.Product;
import io.github.danielzyla.pdcaApp.model.Project;
import io.github.danielzyla.pdcaApp.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepositoryMock;

    @Mock
    private DepartmentService departmentServiceMock;

    @Mock
    private ProductService productServiceMock;

    @InjectMocks
    private ProjectService projectService;

    @Test
    void createNewProject_ShouldReturnProjectReadDtoWithPredefinedVariables() {
        //given
        LocalDateTime dateTimeNow = LocalDateTime.now();
        Project project = new Project();
        project.setComplete(false);
        project.setStartTime(dateTimeNow);
        Cycle cycle = new Cycle();
        cycle.setCycleName("cykl_1");
        cycle.setStartTime(dateTimeNow);
        cycle.getPlanPhase().setName("cykl_1 / project-1");
        cycle.getPlanPhase().setStartTime(dateTimeNow);
        project.getCycles().add(cycle);

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("username");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        given(projectRepositoryMock.save(argThat(project1 -> !project1.isComplete()
                && project1.getStartTime() != null
                && project1.getUsername().equals("username")
                && new ArrayList<>(project1.getCycles()).get(0).getCycleName().equals("cykl_1")
                && new ArrayList<>(project1.getCycles()).get(0).getStartTime() != null
                && new ArrayList<>(project1.getCycles()).get(0).getPlanPhase().getName().equals("cykl_1 / project-1")
                && new ArrayList<>(project1.getCycles()).get(0).getPlanPhase().getStartTime() != null
        ))).willReturn(project);
        ProjectWriteDto projectWriteDto = new ProjectWriteDto();
        projectWriteDto.setProjectName("project-1");

        //when
        ProjectReadDto newProject = projectService.createNewProject(projectWriteDto);

        //then
        assertThat(newProject, instanceOf(ProjectReadDto.class));
        assertThat(newProject.isComplete(), is(false));
        assertThat(newProject.getStartTime(), notNullValue(LocalDateTime.class));
        assertThat(newProject.getCycles().get(0).getCycleName(), is("cykl_1"));
        assertThat(newProject.getCycles().get(0).getStartTime(), notNullValue(LocalDateTime.class));
        assertThat(newProject.getCycles().get(0).getPlanPhase().getName(), is("cykl_1 / project-1"));
        assertThat(newProject.getCycles().get(0).getPlanPhase().getStartTime(), notNullValue(LocalDateTime.class));
    }

    @Test
    void getAll_ShouldReturnAllProjectAsReadDtos() throws NoSuchFieldException, IllegalAccessException {
        //given
        List<Project> projectList = getProjectListStub();
        given(projectRepositoryMock.findAll()).willReturn(projectList);

        //when + then
        assertEquals(projectService.getAll().size(), 3);
        assertThat(projectService.getAll().get(0), instanceOf(ProjectReadDto.class));
        assertNotNull(projectService.getAll().get(0).getStartTime());
        assertThat(projectService.getAll().get(0).getProjectName(), is("project-1"));
        assertThat(projectService.getAll().get(0).getProjectCode(), is("1/2021"));
        assertThat(projectService.getAll().get(0).getCycles(), not(instanceOf(Cycle.class)));
        assertThat(projectService.getAll().get(0).getProducts(), contains(instanceOf(Product.class)));
        assertThat(projectService.getAll().get(0).getDepartments(), contains(instanceOf(Department.class)));
    }

    private List<Project> getProjectListStub() throws NoSuchFieldException, IllegalAccessException {
        Project project1 = new Project();
        project1.setStartTime(LocalDateTime.now());
        project1.setProjectName("project-1");
        project1.setProjectCode("1/2021");
        project1.setProducts(Set.of(new Product()));
        project1.setDepartments(Set.of(new Department()));
        project1.setCycles(Set.of(new Cycle()));
        project1.setComplete(true);
        project1.setEndTime(LocalDateTime.now());

        Project project2 = new Project();
        project2.setComplete(false);
        project2.setEndTime(null);

        Project project3 = new Project();
        project3.setProjectName(null);
        project3.setProjectCode(null);

        Field id = Project.class.getDeclaredField("id");
        id.setAccessible(true);
        id.setLong(project1, 1);
        id.setLong(project2, 2);
        id.setLong(project3, 3);
        return Arrays.asList(project1, project2, project3);
    }

    @Test
    void removeProject_ShouldThrowExceptionWhenNonExistingIdIsUsed() {
        //given
        given(projectRepositoryMock.findById(4L)).willReturn(Optional.empty());

        //when + then
        assertThrows(ResourceNotFoundException.class, () -> projectService.removeProject(4L));
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3})
    void removeProject_ShouldCallRepositoryDeleteByIdMethodWhenExistingIdIsUsed(long id) throws NoSuchFieldException, IllegalAccessException {
        //given
        List<Project> projectList = getProjectListStub();
        Project toRemove = projectList.stream()
                .filter(project -> project.getId() == id)
                .collect(Collectors.toList())
                .get(0);
        given(projectRepositoryMock.findById(id)).willReturn(Optional.ofNullable(toRemove));

        //when
        projectService.removeProject(id);

        //then
        then(projectRepositoryMock).should().deleteById(id);
    }

    @Test
    void getById_ShouldThrowExceptionWhenNonExistingIdIsUsed() {
        //given
        given(projectRepositoryMock.findById(5L)).willReturn(Optional.empty());

        //when + then
        assertThrows(ResourceNotFoundException.class, () -> projectService.getById(5L));
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3})
    void getById_ShouldReturnProjectReadDtoWhenExistingIdIsUsed(long id) throws NoSuchFieldException, IllegalAccessException {
        //given
        List<Project> projectList = getProjectListStub();
        Project found = projectList.stream()
                .filter(project -> project.getId() == id)
                .collect(Collectors.toList())
                .get(0);
        given(projectRepositoryMock.findById(id)).willReturn(Optional.ofNullable(found));

        //when
        ProjectReadDto readDto = projectService.getById(id);

        //then
        assertThat(readDto, instanceOf(ProjectReadDto.class));
        assertNotNull(readDto);
        assertEquals(readDto.getId(), id);
    }

    @Test
    void updateProject_ShouldThrowExceptionWhenNonExistingIdIsUsed() {
        //given
        ProjectWriteDto dto = new ProjectWriteDto();
        dto.setId(6);
        given(projectRepositoryMock.findById(6L)).willReturn(Optional.empty());

        //when + then
        assertThrows(ResourceNotFoundException.class, () -> projectService.updateProject(dto));
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3})
    void updateProject_ShouldCallRepositorySaveMethodWhenExistingIdIsUsed(long id) throws NoSuchFieldException, IllegalAccessException {
        //given
        ProjectWriteDto dto = new ProjectWriteDto();
        dto.setId(id);
        dto.setProjectName("project-1-update");
        dto.setProjectCode("1/2021-update");
        Department department = new Department();
        department.setDeptName("update");
        dto.setDepartments(List.of(department));
        Product product = new Product();
        product.setProductName("update");
        dto.setProducts(List.of(product));

        List<Project> projectList = getProjectListStub();
        Project toUpdate = projectList.stream()
                .filter(project -> project.getId() == id)
                .collect(Collectors.toList())
                .get(0);
        given(projectRepositoryMock.findById(id)).willReturn(Optional.ofNullable(toUpdate));

        //when
        projectService.updateProject(dto);

        //then
        then(projectRepositoryMock).should().save(toUpdate);
        assertEquals(toUpdate.getProjectName(), "project-1-update");
        assertEquals(toUpdate.getProjectCode(), "1/2021-update");
        assertEquals(new ArrayList<>(toUpdate.getDepartments()).get(0).getDeptName(), "update");
        assertEquals(new ArrayList<>(toUpdate.getProducts()).get(0).getProductName(), "update");
    }

    @Test
    void toProjectWriteDto_ShouldReturnProjectWriteDto() throws NoSuchFieldException, IllegalAccessException {
        //given
        ProjectWriteApiDto apiDto = new ProjectWriteApiDto();
        Field id = ProjectWriteApiDto.class.getDeclaredField("id");
        id.setAccessible(true);
        id.set(apiDto, 1L);
        Field projectName = ProjectWriteApiDto.class.getDeclaredField("projectName");
        projectName.setAccessible(true);
        projectName.set(apiDto, "project-1-update");
        Field projectCode = ProjectWriteApiDto.class.getDeclaredField("projectCode");
        projectCode.setAccessible(true);
        projectCode.set(apiDto, "1/2021-update");
        Department department1 = new Department();
        Department department2 = new Department();
        Product product1 = new Product();
        Product product2 = new Product();

        given(departmentServiceMock.getDepartmentList(apiDto.getDepartmentIds())).willReturn(Arrays.asList(department1, department2));
        given(productServiceMock.getProductList(apiDto.getProductIds())).willReturn(Arrays.asList(product1, product2));

        //when
        ProjectWriteDto resultDto = projectService.toProjectWriteDto(apiDto);

        //then
        assertThat(resultDto, instanceOf(ProjectWriteDto.class));
        assertEquals(resultDto.getProjectName(), "project-1-update");
        assertEquals(resultDto.getProjectCode(), "1/2021-update");
        assertEquals(resultDto.getId(), 1L);
        then(departmentServiceMock).should().getDepartmentList(apiDto.getDepartmentIds());
        then(productServiceMock).should().getProductList(apiDto.getProductIds());
    }
}