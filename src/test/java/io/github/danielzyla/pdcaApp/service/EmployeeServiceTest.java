package io.github.danielzyla.pdcaApp.service;

import io.github.danielzyla.pdcaApp.dto.EmployeeReadDto;
import io.github.danielzyla.pdcaApp.dto.EmployeeWriteApiDto;
import io.github.danielzyla.pdcaApp.dto.EmployeeWriteDto;
import io.github.danielzyla.pdcaApp.model.Department;
import io.github.danielzyla.pdcaApp.model.Employee;
import io.github.danielzyla.pdcaApp.model.PlanPhase;
import io.github.danielzyla.pdcaApp.model.Task;
import io.github.danielzyla.pdcaApp.repository.EmployeeRepository;
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
import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {
    @Mock
    private EmployeeRepository employeeRepositoryMock;

    @Mock
    private DepartmentService departmentServiceMock;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void create_ShouldReturnSavedEmployeeAsEmployeeReadDto() throws NoSuchFieldException, IllegalAccessException {
        //given
        EmployeeWriteDto writeDto = new EmployeeWriteDto();
        writeDto.setName("employee-name");
        writeDto.setSurname("employee-surname");
        writeDto.setEmail("employee@email.com");
        Employee employee = new Employee(writeDto.getName(), writeDto.getSurname(), writeDto.getEmail());
        Field id = Employee.class.getDeclaredField("id");
        id.setAccessible(true);
        id.set(employee, 1L);

        given(employeeRepositoryMock.save(
                argThat(employee1 -> employee1.getName().equals(employee.getName())
                        && employee1.getSurname().equals(employee.getSurname())
                        && employee1.getEmail().equals(employee.getEmail())
                )
        )).willReturn(employee);

        //when
        EmployeeReadDto employeeReadDto = employeeService.create(writeDto);

        //then
        assertAll(
                () -> assertThat(employeeReadDto.getName(), equalTo("employee-name")),
                () -> assertThat(employeeReadDto.getSurname(), equalTo("employee-surname")),
                () -> assertThat(employeeReadDto.getEmail(), equalTo("employee@email.com")),
                () -> assertThat(employeeReadDto.getId(), is(1L)),
                () -> assertThat(employeeReadDto.getPlanPhases(), is(empty())),
                () -> assertThat(employeeReadDto.getTasks(), is(empty())),
                () -> assertNull(employeeReadDto.getDepartment())
        );
    }

    @Test
    void getAll() throws NoSuchFieldException, IllegalAccessException {
        //given
        List<Employee> employeeListStub = getEmployeeListStub();
        given(employeeRepositoryMock.findAll()).willReturn(employeeListStub);

        //when
        List<EmployeeReadDto> employeeReadDtoList = employeeService.getAll();

        //then
        assertAll(
                () -> assertThat(employeeReadDtoList.size(), is(105)),
                () -> assertThat(employeeReadDtoList.get(0).getName(), is("employee-name-1")),
                () -> assertThat(employeeReadDtoList.get(0).getSurname(), is("employee-surname-1")),
                () -> assertThat(employeeReadDtoList.get(0).getEmail(), is("employee1@email.com")),
                () -> assertThat(employeeReadDtoList.get(0).getPlanPhases(), is(emptyCollectionOf(PlanPhase.class))),
                () -> assertThat(employeeReadDtoList.get(0).getTasks(), is(emptyCollectionOf(Task.class))),
                () -> assertNull(employeeReadDtoList.get(0).getDepartment())
        );
    }

    private List<Employee> getEmployeeListStub() throws NoSuchFieldException, IllegalAccessException {
        Field id = Employee.class.getDeclaredField("id");
        id.setAccessible(true);
        List<Employee> employeeListStub = new ArrayList<>();
        for (int i = 1; i <= 105; i++) {
            Employee employee = new Employee("employee-name-" + i, "employee-surname-" + i, "employee" + i + "@email.com");
            id.set(employee, i);
            employeeListStub.add(employee);
        }
        return employeeListStub;
    }

    @Test
    void getAllPaged_ShouldReturnAllEmployeesAsReadDtoPagedList() throws NoSuchFieldException, IllegalAccessException {
        //given
        List<Employee> employeeListStub = getEmployeeListStub();
        given(employeeRepositoryMock.findAll()).willReturn(employeeListStub);

        //when
        Pageable pageable1 = PageRequest.of(0, 10);
        Page<EmployeeReadDto> page1 = employeeService.getAllPaged(pageable1);
        Pageable pageable2 = PageRequest.of(10, 10);
        Page<EmployeeReadDto> page11 = employeeService.getAllPaged(pageable2);

        //then
        assertAll(
                () -> assertThat(page1.getTotalPages(), is(11)),
                () -> assertThat(page1.getTotalElements(), is(105L)),
                () -> assertThat(page1.getContent().get(0), is(instanceOf(EmployeeReadDto.class))),
                () -> assertThat(page1.getContent().get(0).getName(), equalTo("employee-name-1")),
                () -> assertThat(page1.getContent().get(0).getSurname(), equalTo("employee-surname-1")),
                () -> assertThat(page1.getContent().get(0).getEmail(), equalTo("employee1@email.com")),
                () -> assertThat(page1.getContent().get(9).getName(), equalTo("employee-name-10")),
                () -> assertThat(page1.getContent().size(), is(10)),
                () -> assertThat(page11.getContent().get(0).getName(), equalTo("employee-name-101")),
                () -> assertThat(page11.getContent().get(4).getName(), equalTo("employee-name-105")),
                () -> assertThat(page11.getContent().get(4).getId(), is(105L)),
                () -> assertThat(page11.getContent().size(), is(5)),
                () -> assertThat(page11.getNumberOfElements(), is(5)),
                () -> assertFalse(page11.getTotalElements() > 105)
        );
    }

    @Test
    void searchByName_ShouldReturnAllEmployeesMeetingSearchCriteriaAsReadDtoPagedList() throws NoSuchFieldException, IllegalAccessException {
        //given
        List<Employee> employeeListStub = getEmployeeListStub();
        List<Employee> employeesByName = employeeListStub.stream()
                .filter(employee -> employee.getName().contains("name-1"))
                .collect(Collectors.toList());
        List<Employee> employeesBySurname = employeeListStub.stream()
                .filter(employee -> employee.getSurname().contains("name-1"))
                .collect(Collectors.toList());
        for (Employee e : employeesBySurname) {
            if (!employeesByName.contains(e)) {
                employeesByName.add(e);
            }
        }
        given(
                employeeRepositoryMock.findByNameContainsOrSurnameContains("name-1", "name-1")
        ).willReturn(employeesByName);

        //when
        Pageable pageable1 = PageRequest.of(0, 5);
        Page<EmployeeReadDto> page1 = employeeService.searchByName("name-1","name-1", pageable1);
        Pageable pageable2 = PageRequest.of(3, 5);
        Page<EmployeeReadDto> page4 = employeeService.searchByName("name-1","name-1", pageable2);

        //then
        assertAll(
                () -> assertThat(page1.getTotalElements(), is(17L)),
                () -> assertThat(page1.getTotalPages(), is(4)),
                () -> assertThat(page1.getContent().get(4).getName(), equalTo("employee-name-13")),
                () -> assertThat(page4.getContent().size(), is(2)),
                () -> assertThat(page4.getNumberOfElements(), is(2)),
                () -> assertFalse(page4.getTotalElements() > 17L)
        );
    }

    @Test
    void getById_ShouldThrowExceptionWhenNonExistingIdIsUsed() {
        //given
        long id = 1;
        given(employeeRepositoryMock.findById(id)).willReturn(Optional.empty());

        //when + then
        assertThrows(ResourceNotFoundException.class, () -> employeeService.getById(id));
    }

    @Test
    void getById_ShouldReturnEmployeeReadDtoWhenExistingIdIsUsed() throws NoSuchFieldException, IllegalAccessException {
        //given
        long id = 1;
        given(employeeRepositoryMock.findById(id)).willReturn(Optional.of(getEmployeeListStub().get(0)));

        //when
        EmployeeReadDto readDto = employeeService.getById(id);

        //then
        assertAll(
                () -> assertThat(readDto, allOf(isA(EmployeeReadDto.class), is(notNullValue()))),
                () -> assertEquals(readDto.getName(), "employee-name-1"),
                () -> assertEquals(readDto.getSurname(), "employee-surname-1"),
                () -> assertEquals(readDto.getEmail(), "employee1@email.com"),
                () -> assertThat(readDto.getPlanPhases(), is(emptyCollectionOf(PlanPhase.class))),
                () -> assertThat(readDto.getTasks(), is(emptyCollectionOf(Task.class))),
                () -> assertNull(readDto.getDepartment())
        );
    }

    @Test
    void getEmployeeById_ShouldThrowExceptionWhenNonExistingIdIsUsed() {
        //given
        long id = 1;
        given(employeeRepositoryMock.findById(id)).willReturn(Optional.empty());

        //when + then
        assertThrows(ResourceNotFoundException.class, () -> employeeService.getEmployeeById(id));
    }

    @Test
    void getEmployeeById_ShouldReturnEmployeeWhenExistingIdIsUsed() throws NoSuchFieldException, IllegalAccessException {
        //given
        long id = 1;
        given(employeeRepositoryMock.findById(id)).willReturn(Optional.of(getEmployeeListStub().get(0)));

        //when
        Employee employeeById = employeeService.getEmployeeById(id);

        //then
        assertAll(
                () -> assertThat(employeeById, allOf(isA(Employee.class), is(notNullValue()))),
                () -> assertEquals(employeeById.getName(), "employee-name-1"),
                () -> assertEquals(employeeById.getSurname(), "employee-surname-1"),
                () -> assertEquals(employeeById.getEmail(), "employee1@email.com"),
                () -> assertThat(employeeById.getPlanPhases(), is(emptyCollectionOf(PlanPhase.class))),
                () -> assertThat(employeeById.getTasks(), is(emptyCollectionOf(Task.class))),
                () -> assertNull(employeeById.getDepartment())
        );
    }

    @Test
    void update_ShouldThrowExceptionWhenNonExistingIdIsUsed() {
        //given
        EmployeeWriteDto dto = new EmployeeWriteDto();
        dto.setId(1L);
        given(employeeRepositoryMock.findById(1L)).willReturn(Optional.empty());

        //when + then
        assertThrows(ResourceNotFoundException.class, () -> employeeService.update(dto));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 10, 20, 50, 100, 105})
    void update_ShouldCallRepositorySaveMethodWhenExistingIdIsUsed(long id) throws NoSuchFieldException, IllegalAccessException {
        //given
        EmployeeWriteDto dto = new EmployeeWriteDto();
        dto.setId(id);
        dto.setName("employee-name-update-test");
        dto.setSurname("employee-surname-update-test");
        dto.setEmail("employeeUpdateTest@email.com");

        List<Employee> employeeListStub = getEmployeeListStub();
        Employee toUpdate = employeeListStub.stream()
                .filter(employee -> employee.getId() == id)
                .collect(Collectors.toList())
                .get(0);
        given(employeeRepositoryMock.findById(id)).willReturn(Optional.ofNullable(toUpdate));

        //when
        employeeService.update(dto);

        //then
        then(employeeRepositoryMock).should().save(toUpdate);
        assertEquals(toUpdate.getName(), "employee-name-update-test");
        assertEquals(toUpdate.getSurname(), "employee-surname-update-test");
        assertEquals(toUpdate.getEmail(), "employeeUpdateTest@email.com");
    }

    @Test
    void reducedByCurrent_ShouldReturnProperlyReducedEmployeeListAsReadDto() throws NoSuchFieldException, IllegalAccessException {
        //given
        List<Employee> employeeListStub = getEmployeeListStub();
        Employee employeeToReduce1 = employeeListStub.get(1);
        Employee employeeToReduce2 = employeeListStub.get(50);
        Employee employeeToReduce3 = employeeListStub.get(104);
        Set<Employee> toReduce = new HashSet<>(List.of(employeeToReduce1, employeeToReduce2, employeeToReduce3));

        given(employeeRepositoryMock.findAll()).willReturn(employeeListStub);

        //when
        List<EmployeeReadDto> reducedByCurrent = employeeService.reducedByCurrent(toReduce);

        //then
        assertAll(
                () -> assertEquals(reducedByCurrent.size(), 102),
                () -> assertTrue(reducedByCurrent.stream()
                        .noneMatch(employeeReadDto -> Objects.equals(employeeReadDto.getName(), employeeToReduce1.getName()))),
                () -> assertTrue(reducedByCurrent.stream()
                        .noneMatch(employeeReadDto -> Objects.equals(employeeReadDto.getSurname(), employeeToReduce2.getSurname()))),
                () -> assertTrue(reducedByCurrent.stream()
                        .noneMatch(employeeReadDto -> Objects.equals(employeeReadDto.getEmail(), employeeToReduce3.getEmail()))),
                () -> assertTrue(reducedByCurrent.stream()
                        .allMatch(employeeReadDto -> employeeReadDto.getClass() == EmployeeReadDto.class))
        );
    }

    @Test
    void removeEmployee_ShouldReturnExceptionWhenNonExistingIdIsUsed() {
        //given
        long id = 1;
        given(employeeRepositoryMock.findById(id)).willReturn(Optional.empty());

        //when + then
        assertThrows(ResourceNotFoundException.class, () -> employeeService.removeEmployee(id));
    }

    @Test
    void removeEmployee_ShouldCallDeleteMethodOnRepositoryWhenExistingIdIsUsed() {
        //given
        long id = 1;
        given(employeeRepositoryMock.findById(id)).willReturn(Optional.of(new Employee()));

        //when
        employeeService.removeEmployee(id);

        //then
        then(employeeRepositoryMock).should().deleteById(id);
    }

    @Test
    void toEmployeeWriteDto_ShouldReturnEmployeeWriteDto() throws NoSuchFieldException, IllegalAccessException {
        //given
        EmployeeWriteApiDto apiDto = new EmployeeWriteApiDto();
        Field id = EmployeeWriteApiDto.class.getDeclaredField("id");
        id.setAccessible(true);
        id.set(apiDto, 1L);
        Field name = EmployeeWriteApiDto.class.getDeclaredField("name");
        name.setAccessible(true);
        name.set(apiDto, "employee-name-update");
        Field surname = EmployeeWriteApiDto.class.getDeclaredField("surname");
        surname.setAccessible(true);
        surname.set(apiDto, "employee-surname-update");
        Field email = EmployeeWriteApiDto.class.getDeclaredField("email");
        email.setAccessible(true);
        email.set(apiDto, "employeeUpdate@email.com");
        Field departmentId = EmployeeWriteApiDto.class.getDeclaredField("departmentId");
        departmentId.setAccessible(true);
        departmentId.set(apiDto, 1);

        Department department = new Department();
        Field deptId = Department.class.getDeclaredField("id");
        deptId.setAccessible(true);
        deptId.set(department, apiDto.getDepartmentId());

        given(departmentServiceMock.getDepartmentById(1)).willReturn(department);

        //when
        EmployeeWriteDto resultDto = employeeService.toEmployeeWriteDto(apiDto);

        //then
        assertThat(resultDto, instanceOf(EmployeeWriteDto.class));
        assertEquals(resultDto.getName(), "employee-name-update");
        assertEquals(resultDto.getSurname(), "employee-surname-update");
        assertEquals(resultDto.getEmail(), "employeeUpdate@email.com");
        assertEquals(resultDto.getId(), 1L);
        then(departmentServiceMock).should().getDepartmentById(1);
    }
}