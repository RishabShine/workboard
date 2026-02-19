package com.rishab.workboard.api.mapper;

import com.rishab.workboard.api.domain.Milestone;
import com.rishab.workboard.api.dto.response.common.MilestoneDto;
import com.rishab.workboard.api.mapper.impl.MilestoneMapperImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class MilestoneMapperTest {

    @Autowired
    private MilestoneMapperImpl milestoneMapper;

    @Test
    void toDto_mapsMilestoneFields() {
        Milestone m = new Milestone();
        m.setId(7L);
        m.setName("Sprint 1");

        MilestoneDto dto = milestoneMapper.toDto(m);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(7L);
        assertThat(dto.getName()).isEqualTo("Sprint 1");
    }

}