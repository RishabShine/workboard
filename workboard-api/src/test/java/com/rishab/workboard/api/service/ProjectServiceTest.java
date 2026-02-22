package com.rishab.workboard.api.service;

import com.rishab.workboard.api.domain.*;
import com.rishab.workboard.api.domain.id.MemberId;
import com.rishab.workboard.api.dto.request.CreateProjectRequest;
import com.rishab.workboard.api.dto.request.UpdateProjectRequest;
import com.rishab.workboard.api.dto.request.CreateMilestoneRequest;
import com.rishab.workboard.api.dto.request.CreateTagRequest;
import com.rishab.workboard.api.dto.response.project.ProjectDetailDto;
import com.rishab.workboard.api.dto.response.project.ProjectListDto;
import com.rishab.workboard.api.dto.response.project.ProjectOverviewDto;
import com.rishab.workboard.api.repository.*;
import com.rishab.workboard.api.service.exceptions.ForbiddenException;
import com.rishab.workboard.api.service.exceptions.NotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProjectServiceTest {

    @Autowired private ProjectService projectService;

    @Autowired private UserRepository userRepository;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired private MilestoneRepository milestoneRepository;
    @Autowired private TagRepository tagRepository;

    @Test
    @Transactional
    void getProjectDetail_whenUserIsMember_returnsProjectDetailDto() {
        // Arrange
        User user = createAndSaveUser("proj_detail_user");
        Project project = createAndSaveProject("DETAIL PROJECT", user);

        Role role = new Role();
        role.setName("OWNER");
        role.setProject(project);
        role = roleRepository.save(role);

        Member member = new Member();
        member.setId(new MemberId(user.getId(), project.getId()));
        member.setUser(user);
        member.setProject(project);
        member.setRole(role);
        memberRepository.save(member);

        Milestone m1 = new Milestone();
        m1.setName("Backlog");
        m1.setProject(project);
        milestoneRepository.save(m1);

        Tag t1 = new Tag();
        t1.setName("BUG");
        t1.setColor("red");
        t1.setProject(project);
        tagRepository.save(t1);

        // Act
        ProjectDetailDto dto = projectService.getProjectDetail(project.getId(), user.getId());

        // Assert
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(project.getId());
        assertThat(dto.getName()).isEqualTo("DETAIL PROJECT");
        assertThat(dto.getUserRole()).isNotNull();
        assertThat(dto.getUserRole().getName()).isEqualTo("OWNER");
        assertThat(dto.getMilestones()).extracting("name").contains("Backlog");
        assertThat(dto.getTags()).extracting("name").contains("BUG");
    }

    @Test
    @Transactional
    void getProjectDetail_whenUserNotMember_throwsForbidden() {
        // Arrange
        User projectOwner = createAndSaveUser("owner_user");
        Project project = createAndSaveProject("PRIVATE PROJECT", projectOwner);

        User outsider = createAndSaveUser("outsider_user");

        // Act + Assert
        assertThatThrownBy(() -> projectService.getProjectDetail(project.getId(), outsider.getId()))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("not a member");
    }

    @Test
    @Transactional
    void listProjectsForUser_returnsOnlyProjectsUserIsMemberOf() {
        // Arrange
        User user = createAndSaveUser("list_projects_user");

        Project p1 = createAndSaveProject("P1", user);
        Project p2 = createAndSaveProject("P2", user);

        Role r1 = createAndSaveRole("MEMBER", p1);
        Role r2 = createAndSaveRole("MEMBER", p2);

        memberRepository.save(createMember(user, p1, r1));
        memberRepository.save(createMember(user, p2, r2));

        // extra project that user is NOT a member of
        User other = createAndSaveUser("other_user");
        Project p3 = createAndSaveProject("P3", other);
        Role r3 = createAndSaveRole("MEMBER", p3);
        memberRepository.save(createMember(other, p3, r3));

        // Act
        ProjectListDto dto = projectService.listProjectsForUser(user.getId());

        // Assert
        assertThat(dto).isNotNull();
        assertThat(dto.getProjects()).extracting("name")
                .contains("P1", "P2")
                .doesNotContain("P3");
    }

    @Test
    @Transactional
    void createProject_savesProjectMilestonesAndTags_andReturnsOverviewDto() {
        // Arrange
        User user = createAndSaveUser("create_project_user");

        CreateMilestoneRequest m1 = new CreateMilestoneRequest();
        m1.setName("Backlog");
        CreateMilestoneRequest m2 = new CreateMilestoneRequest();
        m2.setName("Sprint 1");

        CreateTagRequest t1 = new CreateTagRequest();
        t1.setName("BUG");
        t1.setColor("red");
        CreateTagRequest t2 = new CreateTagRequest();
        t2.setName("FEATURE");
        t2.setColor("blue");

        CreateProjectRequest req = new CreateProjectRequest();
        req.setName("NEW PROJECT");
        req.setMilestones(List.of(m1, m2));
        req.setTags(List.of(t1, t2));

        // Act
        ProjectOverviewDto overview = projectService.createProject(req, user.getId());

        // Assert - overview
        assertThat(overview).isNotNull();
        assertThat(overview.getId()).isNotNull();
        assertThat(overview.getName()).isEqualTo("NEW PROJECT");

        // Assert - persisted project
        Project saved = projectRepository.findById(overview.getId())
                .orElseThrow(() -> new AssertionError("Expected saved project"));

        assertThat(saved.getCreatedBy().getId()).isEqualTo(user.getId());

        // Assert - milestones/tags linked to project
        List<Milestone> milestones = milestoneRepository.getMilestonesByProjectId(saved.getId());
        assertThat(milestones).extracting(Milestone::getName)
                .containsExactlyInAnyOrder("Backlog", "Sprint 1");

        List<Tag> tags = tagRepository.FindTagsByProjectId(saved.getId());
        assertThat(tags).extracting(Tag::getName)
                .containsExactlyInAnyOrder("BUG", "FEATURE");
    }

    @Test
    @Transactional
    void createProject_whenUserMissing_throwsNotFound() {
        CreateProjectRequest req = new CreateProjectRequest();
        req.setName("SHOULD FAIL");

        assertThatThrownBy(() -> projectService.createProject(req, 999999999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    @Transactional
    void updateProject_updatesName_andReturnsUpdatedOverview() {
        // Arrange
        User user = createAndSaveUser("update_project_user");
        Project project = createAndSaveProject("OLD NAME", user);

        UpdateProjectRequest req = new UpdateProjectRequest();
        req.setName("NEW NAME");

        // Act
        ProjectOverviewDto dto = projectService.updateProject(project.getId(), req, user.getId());

        // Assert
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(project.getId());
        assertThat(dto.getName()).isEqualTo("NEW NAME");

        Project updated = projectRepository.findById(project.getId())
                .orElseThrow(() -> new AssertionError("Expected updated project"));

        assertThat(updated.getName()).isEqualTo("NEW NAME");
    }

    @Test
    @Transactional
    void updateProject_whenProjectMissing_throwsNotFound() {
        UpdateProjectRequest req = new UpdateProjectRequest();
        req.setName("X");

        assertThatThrownBy(() -> projectService.updateProject(999999999L, req, 1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Project not found");
    }

    // ---------- helpers ----------

    private User createAndSaveUser(String prefix) {
        long ts = System.currentTimeMillis();
        User u = new User();
        u.setUsername(prefix + "_" + ts);
        u.setEmail((prefix + "_" + ts + "@example.com").toUpperCase());
        u.setPassword("hash");
        return userRepository.save(u);
    }

    private Project createAndSaveProject(String name, User createdBy) {
        Project p = new Project();
        p.setName(name);
        p.setCreatedBy(createdBy);
        return projectRepository.save(p);
    }

    private Role createAndSaveRole(String name, Project project) {
        Role role = new Role();
        role.setName(name);
        role.setProject(project);
        return roleRepository.save(role);
    }

    private Member createMember(User user, Project project, Role role) {
        Member m = new Member();
        m.setId(new MemberId(user.getId(), project.getId()));
        m.setUser(user);
        m.setProject(project);
        m.setRole(role);
        return m;
    }
}
