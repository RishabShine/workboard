package com.rishab.workboard.api.mapper;

import com.rishab.workboard.api.domain.Tag;
import com.rishab.workboard.api.dto.response.common.TagDto;
import com.rishab.workboard.api.mapper.impl.TagMapperImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class TagMapperTest {

    @Autowired
    private TagMapperImpl tagMapper;

    @Test
    void toDto_mapsTagFields() {
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("BUG");
        tag.setColor("red");

        TagDto dto = tagMapper.toDto(tag);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("BUG");
        assertThat(dto.getColor()).isEqualTo("red");
    }
}
