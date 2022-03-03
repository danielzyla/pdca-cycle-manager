package io.github.danielzyla.pdcaApp.repository;

import io.github.danielzyla.pdcaApp.model.Project;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
class ProjectRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    void save_ShouldPersistGivenProjectIntoDatabase() {
        //given
        Project project = new Project();
        project.setProjectName("project-1");
        project.setProjectCode("1/2021");

        //when
        Project saved = projectRepository.save(project);

        //then
        assertEquals(saved, testEntityManager.find(Project.class, saved.getId()));
        assertThat(saved).hasFieldOrPropertyWithValue("projectName", "project-1");
        assertThat(saved).hasFieldOrPropertyWithValue("projectCode", "1/2021");
    }

    @Test
    void findAll_ShouldFindNoProjectsIfRepositoryIsEmpty() {
        //given
        /*Default @DataJpaTest configuration of schema: spring.jpa.hibernate.ddl-auto=create-drop.*/
        //when
        List<Project> projects = projectRepository.findAll();

        //then
        assertThat(projects).isEmpty();
    }

    @Test
    void findAll_ShouldReturnListOfAllProjectsFromRepository() {
        //given
        Project project1 = new Project();
        project1.setProjectName("project-1");
        project1.setProjectCode("1/2021");
        Project project2 = new Project();
        project2.setProjectName("project-2");
        project2.setProjectCode("2/2021");
        Project persisted1 = testEntityManager.persist(project1);
        Project persisted2 = testEntityManager.persist(project2);
        testEntityManager.flush();

        //when
        List<Project> projects = projectRepository.findAll();

        //then
        assertThat(projects).hasSize(2).contains(persisted1, persisted2).isInstanceOf(List.class);
        assertThat(projects).allMatch(project -> project.getProjectName().startsWith("project"));
        assertThat(projects).allMatch(project -> project.getProjectCode().contains("/2021"));
    }

    @Test
    void deleteById() {
        //given
        Project project1 = new Project();
        project1.setProjectName("project-1");
        project1.setProjectCode("1/2021");
        Project project2 = new Project();
        project2.setProjectName("project-2");
        project2.setProjectCode("2/2021");
        Project persisted1 = testEntityManager.persist(project1);
        Project persisted2 = testEntityManager.persist(project2);
        testEntityManager.flush();

        //when
        projectRepository.deleteById(persisted1.getId());

        //then
        assertThat(testEntityManager.find(Project.class, persisted1.getId())).isNull();
        assertThat(testEntityManager.find(Project.class, persisted2.getId())).isEqualTo(persisted2);
        assertThat(projectRepository.findAll()).hasSize(1).contains(persisted2);
    }

    @Test
    void deleteById_ShouldThrowExceptionWhenInvalidIsUsed() {
        //when
        long id = 1;
        //then
        assertThrows(RuntimeException.class, () -> projectRepository.deleteById(id));
    }

    @Test
    void findById_ShouldReturnNotEmptyOptionalOfProjectForExistingId() {
        //given
        Project project = new Project();
        project.setProjectName("project-1");
        project.setProjectCode("1/2021");
        Project persisted = testEntityManager.persist(project);
        testEntityManager.flush();

        //when
        Optional<Project> found = projectRepository.findById(persisted.getId());

        //then
        assertEquals(found.get().getProjectName(), "project-1");
        assertEquals(found.get().getProjectCode(), "1/2021");
        assertEquals(found.get(), persisted);
        assertEquals(found, Optional.of(persisted));
        assertTrue(found.isPresent());
    }

    @Test
    void findById_ShouldReturnEmptyOptionalForNonExistingId() {
        //given
        long id = 1;

        //when
        Optional<Project> found = projectRepository.findById(id);

        //then
        assertEquals(found, Optional.empty());
    }
}