package com.rishab.workboard.api.mapper;

import com.rishab.workboard.api.domain.Milestone;
import com.rishab.workboard.api.domain.Project;
import com.rishab.workboard.api.domain.Role;
import com.rishab.workboard.api.domain.Tag;
import com.rishab.workboard.api.dto.response.project.ProjectDetailDto;
import com.rishab.workboard.api.mapper.impl.ProjectDetailMapperImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class ProjectDetailMapperTest {

    @Autowired
    private ProjectDetailMapperImpl projectDetailMapper;

    @Test
    void toDto_mapsProjectDetailWithMilestonesTagsAndUserRole() {
        Project project = new Project();
        project.setId(99L);
        project.setName("Demo Project");

        Milestone m1 = new Milestone();
        m1.setId(1L);
        m1.setName("Backlog");

        Milestone m2 = new Milestone();
        m2.setId(2L);
        m2.setName("Sprint 1");

        Tag t1 = new Tag();
        t1.setId(10L);
        t1.setName("BUG");
        t1.setColor("red");

        Tag t2 = new Tag();
        t2.setId(11L);
        t2.setName("FEATURE");
        t2.setColor("blue");

        Role role = new Role();
        role.setId(5L);
        role.setName("OWNER");

        ProjectDetailDto dto = projectDetailMapper.toDto(project, List.of(m1, m2), List.of(t1, t2), role);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(99L);
        assertThat(dto.getName()).isEqualTo("Demo Project");
        assertThat(dto.getUserRole()).isNotNull();
        assertThat(dto.getUserRole().getId()).isEqualTo(5L);
        assertThat(dto.getUserRole().getName()).isEqualTo("OWNER");

        assertThat(dto.getMilestones()).extracting("id").containsExactly(1L, 2L);
        assertThat(dto.getMilestones()).extracting("name").containsExactly("Backlog", "Sprint 1");

        assertThat(dto.getTags()).extracting("id").containsExactly(10L, 11L);
        assertThat(dto.getTags()).extracting("name").containsExactly("BUG", "FEATURE");
    }

}
