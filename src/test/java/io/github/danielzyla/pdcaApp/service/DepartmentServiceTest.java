package io.github.danielzyla.pdcaApp.service;

import io.github.danielzyla.pdcaApp.dto.DepartmentReadDto;
import io.github.danielzyla.pdcaApp.dto.DepartmentWriteDto;
import io.github.danielzyla.pdcaApp.model.Department;
import io.github.danielzyla.pdcaApp.model.Employee;
import io.github.danielzyla.pdcaApp.model.Project;
import io.github.danielzyla.pdcaApp.repository.DepartmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @Mock
    DepartmentRepository departmentRepositoryMock;

    @InjectMocks
    DepartmentService departmentService;

    private List<Department> getDepartmentListStub() throws NoSuchFieldException, IllegalAccessException {
        Field id = Department.class.getDeclaredField("id");
        id.setAccessible(true);
        List<Department> departmentListStub = new ArrayList<>();
        for (int i = 1; i <= 105; i++) {
            Department department = new Department("department-" + i);
            id.set(department, i);
            departmentListStub.add(department);
        }
        return departmentListStub;
    }

    @Test
    void create_ShouldReturnSavedDepartmentAsDepartmentReadDto() throws NoSuchFieldException, IllegalAccessException {
        //given
        DepartmentWriteDto writeDto = new DepartmentWriteDto();
        writeDto.setDeptName("department-create-test");
        Department department = new Department(writeDto.getDeptName());
        Field id = Department.class.getDeclaredField("id");
        id.setAccessible(true);
        id.set(department, 1);

        given(departmentRepositoryMock.save(
                argThat(department1 -> department1.getDeptName().equals(department.getDeptName()))
        )).willReturn(department);

        //when
        DepartmentReadDto departmentReadDto = departmentService.create(writeDto);

        //then
        assertThat(departmentReadDto.getDeptName(), equalTo("department-create-test"));
        assertThat(departmentReadDto.getEmployees().size(), is(0));
        assertThat(departmentReadDto.getId(), is(1));
    }

    @Test
    void getAll_ShouldReturnAllProductsAsReadDtoList() throws NoSuchFieldException, IllegalAccessException {
        //given
        List<Department> departmentListStub = getDepartmentListStub();
        given(departmentRepositoryMock.findAll()).willReturn(departmentListStub);

        //when
        List<DepartmentReadDto> departmentReadDtoList = departmentService.getAll();

        //then
        assertAll(
                () -> assertThat(departmentReadDtoList.size(), is(105)),
                () -> assertThat(departmentReadDtoList.get(0).getDeptName(), is("department-1")),
                () -> assertThat(departmentReadDtoList.get(0).getEmployees(), is(emptyCollectionOf(Employee.class)))
        );
    }

    @Test
    void getAllPaged_ShouldReturnAllDepartmentsAsReadDtoPagedList() throws NoSuchFieldException, IllegalAccessException {
        //given
        List<Department> departmentList = getDepartmentListStub();
        given(departmentRepositoryMock.findAll()).willReturn(departmentList);

        //when
        Pageable pageable1 = PageRequest.of(0, 10);
        Page<DepartmentReadDto> page1 = departmentService.getAllPaged(pageable1);
        Pageable pageable2 = PageRequest.of(10, 10);
        Page<DepartmentReadDto> page11 = departmentService.getAllPaged(pageable2);

        //then
        assertAll(
                () -> assertThat(page1.getTotalPages(), is(11)),
                () -> assertThat(page1.getTotalElements(), is(105L)),
                () -> assertThat(page1.getContent().get(0), is(instanceOf(DepartmentReadDto.class))),
                () -> assertThat(page1.getContent().get(0).getDeptName(), equalTo("department-1")),
                () -> assertThat(page1.getContent().get(9).getDeptName(), equalTo("department-10")),
                () -> assertThat(page1.getContent().size(), is(10)),
                () -> assertThat(page11.getContent().get(0).getDeptName(), equalTo("department-101")),
                () -> assertThat(page11.getContent().get(4).getDeptName(), equalTo("department-105")),
                () -> assertThat(page11.getContent().get(4).getId(), is(105)),
                () -> assertThat(page11.getContent().size(), is(5)),
                () -> assertThat(page11.getNumberOfElements(), is(5)),
                () -> assertFalse(page11.getTotalElements() > 105)
        );
    }

    @Test
    void searchByName_ShouldReturnAllDepartmentsMeetingSearchCriteriaAsReadDtoPagedList() throws NoSuchFieldException, IllegalAccessException {
        //given
        List<Department> departmentList = getDepartmentListStub();
        given(departmentRepositoryMock.findByDeptNameContains("1")).willReturn(
                departmentList.stream()
                        .filter(department -> department.getDeptName().contains("1"))
                        .collect(Collectors.toList())
        );

        //when
        Pageable pageable1 = PageRequest.of(0, 10);
        Page<DepartmentReadDto> page1 = departmentService.searchByName("1", pageable1);
        Pageable pageable2 = PageRequest.of(2, 10);
        Page<DepartmentReadDto> page3 = departmentService.searchByName("1", pageable2);

        //then
        assertAll(
                () -> assertThat(page1.getTotalElements(), is(25L)),
                () -> assertThat(page1.getTotalPages(), is(3)),
                () -> assertThat(page1.getContent().get(4).getDeptName(), equalTo("department-13")),
                () -> assertThat(page3.getContent().size(), is(5)),
                () -> assertThat(page3.getNumberOfElements(), is(5)),
                () -> assertFalse(page3.getTotalElements() > 25L)
        );
    }

    @Test
    void getById_ShouldThrowExceptionWhenNonExistingIdIsUsed() {
        //given
        int id = 1;
        given(departmentRepositoryMock.findById(id)).willReturn(Optional.empty());

        //when + then
        assertThrows(ResourceNotFoundException.class, () -> departmentService.getById(id));
    }

    @Test
    void getById_ShouldReturnDepartmentReadDtoWhenExistingIdIsUsed() throws NoSuchFieldException, IllegalAccessException {
        //given
        int id = 1;
        given(departmentRepositoryMock.findById(id)).willReturn(Optional.of(getDepartmentListStub().get(0)));

        //when
        DepartmentReadDto readDto = departmentService.getById(id);

        //then
        assertAll(
                () -> assertThat(readDto, allOf(isA(DepartmentReadDto.class), is(notNullValue()))),
                () -> assertEquals(readDto.getDeptName(), "department-1"),
                () -> assertThat(readDto.getEmployees(), is(emptyCollectionOf(Employee.class))),
                () -> assertThat(readDto.getProjects(), is(emptyCollectionOf(Project.class)))
        );
    }

    @Test
    void getDepartmentById_ShouldThrowExceptionWhenNonExistingIdIsUsed() {
        //given
        given(departmentRepositoryMock.findById(1)).willReturn(Optional.empty());

        //when + then
        assertThrows(ResourceNotFoundException.class, () -> departmentService.getDepartmentById(1));
    }

    @Test
    void getById_ShouldReturnDepartmentWhenExistingIdIsUsed() throws NoSuchFieldException, IllegalAccessException {
        //given
        int id = 1;
        given(departmentRepositoryMock.findById(id)).willReturn(Optional.of(getDepartmentListStub().get(0)));

        //when
        Department departmentById = departmentService.getDepartmentById(id);

        //then
        assertAll(
                () -> assertThat(departmentById, allOf(isA(Department.class), is(notNullValue()))),
                () -> assertEquals(departmentById.getDeptName(), "department-1"),
                () -> assertThat(departmentById.getEmployees(), is(emptyCollectionOf(Employee.class))),
                () -> assertThat(departmentById.getProjects(), is(emptyCollectionOf(Project.class)))
        );
    }

    @Test
    void update_ShouldThrowExceptionWhenNonExistingIdIsUsed() {
        //given
        DepartmentWriteDto dto = new DepartmentWriteDto();
        dto.setId(1);
        given(departmentRepositoryMock.findById(1)).willReturn(Optional.empty());

        //when + then
        assertThrows(ResourceNotFoundException.class, () -> departmentService.update(dto));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 10, 20, 50, 100, 105})
    void update_ShouldCallRepositorySaveMethodWhenExistingIdIsUsed(int id) throws NoSuchFieldException, IllegalAccessException {
        //given
        DepartmentWriteDto dto = new DepartmentWriteDto();
        dto.setId(id);
        dto.setDeptName("department-name-update-test");

        List<Department> departmentListStub = getDepartmentListStub();
        Department toUpdate = departmentListStub.stream()
                .filter(department -> department.getId() == id)
                .collect(Collectors.toList())
                .get(0);
        given(departmentRepositoryMock.findById(id)).willReturn(Optional.ofNullable(toUpdate));

        //when
        departmentService.update(dto);

        //then
        then(departmentRepositoryMock).should().save(toUpdate);
        assertEquals(toUpdate.getDeptName(), "department-name-update-test");
    }

    @Test
    void getDepartmentList_ShouldThrowExceptionWhenNonExistingIdIsUsed() {
        //given
        List<Integer> departmentIds = List.of(1, 2, 3);
        given(departmentRepositoryMock.findById(1)).willReturn(Optional.of(new Department()));
        given(departmentRepositoryMock.findById(2)).willReturn(Optional.empty());
        given(departmentRepositoryMock.findById(2)).willReturn(Optional.of(new Department()));

        //when + then
        assertThrows(ResourceNotFoundException.class, () -> departmentService.getDepartmentList(departmentIds));
    }

    @Test
    void getDepartmentList_ShouldReturnDepartmentListWhenExistingIdsAreUsed() throws NoSuchFieldException, IllegalAccessException {
        //given
        List<Department> departmentListStub = getDepartmentListStub();
        List<Integer> departmentIds = List.of(1, 50, 105);
        for (int id : departmentIds) {
            given(departmentRepositoryMock.findById(id)).willReturn(Optional.ofNullable(departmentListStub.stream()
                    .filter(department -> department.getId() == id)
                    .collect(Collectors.toList())
                    .get(0)));
        }

        //when
        List<Department> departmentList = departmentService.getDepartmentList(departmentIds);

        //then
        assertThat(departmentList, not(empty()));
        assertEquals(departmentList, List.of(departmentListStub.get(0), departmentListStub.get(49), departmentListStub.get(104)));
        assertEquals(departmentList.size(), 3);
        assertTrue(departmentList.stream().allMatch(department -> department.getDeptName().startsWith("department-")));
    }


    @Test
    void removeDepartment_ShouldReturnExceptionWhenNonExistingIdIsUsed() {
        //given
        int id = 1;
        given(departmentRepositoryMock.findById(id)).willReturn(Optional.empty());

        //when + then
        assertThrows(ResourceNotFoundException.class, () -> departmentService.removeDepartment(id));
    }

    @Test
    void removeDepartment_ShouldCallDeleteMethodOnRepositoryWhenExistingIdIsUsed() {
        //given
        int id = 1;
        given(departmentRepositoryMock.findById(id)).willReturn(Optional.of(new Department()));

        //when
        departmentService.removeDepartment(id);

        //then
        then(departmentRepositoryMock).should().deleteById(id);
    }
}