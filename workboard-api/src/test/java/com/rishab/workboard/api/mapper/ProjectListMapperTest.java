package com.rishab.workboard.api.mapper;

import com.rishab.workboard.api.domain.Project;
import com.rishab.workboard.api.dto.response.project.ProjectListDto;
import com.rishab.workboard.api.mapper.impl.ProjectListMapperImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class ProjectListMapperTest {

    @Autowired
    private ProjectListMapperImpl projectListMapper;

    @Test
    void toDto_mapsListOfProjectsToProjectListDto() {
        Project p1 = new Project();
        p1.setId(1L);
        p1.setName("P1");
        p1.setCreatedAt(OffsetDateTime.now());

        Project p2 = new Project();
        p2.setId(2L);
        p2.setName("P2");
        p2.setCreatedAt(OffsetDateTime.now().minusDays(1));

        ProjectListDto dto = projectListMapper.toDto(List.of(p1, p2));

        assertThat(dto).isNotNull();
        assertThat(dto.getProjects()).hasSize(2);
        assertThat(dto.getProjects()).extracting("id").containsExactly(1L, 2L);
        assertThat(dto.getProjects()).extracting("name").containsExactly("P1", "P2");
    }

}
