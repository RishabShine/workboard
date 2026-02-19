package com.rishab.workboard.api.mapper;

import com.rishab.workboard.api.domain.Project;
import com.rishab.workboard.api.dto.response.project.ProjectOverviewDto;
import com.rishab.workboard.api.mapper.impl.ProjectOverviewMapperImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class ProjectOverviewMapperTest {

    @Autowired
    private ProjectOverviewMapperImpl projectOverviewMapper;

    @Test
    void toDto_mapsProjectOverviewFields() {
        Project p = new Project();
        p.setId(100L);
        p.setName("Workboard");
        p.setCreatedAt(OffsetDateTime.now());

        ProjectOverviewDto dto = projectOverviewMapper.toDto(p);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(100L);
        assertThat(dto.getName()).isEqualTo("Workboard");
        assertThat(dto.getCreatedAt()).isEqualTo(p.getCreatedAt());
    }

}
