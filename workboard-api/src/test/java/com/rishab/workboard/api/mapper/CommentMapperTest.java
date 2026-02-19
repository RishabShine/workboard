package com.rishab.workboard.api.mapper;

import com.rishab.workboard.api.domain.Comment;
import com.rishab.workboard.api.dto.response.comment.CommentDto;
import com.rishab.workboard.api.mapper.impl.CommentMapperImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class CommentMapperTest {

    @Autowired
    private CommentMapperImpl commentMapper;

    @Test
    void toDto_mapsBasicCommentFields() {
        Comment c = new Comment();
        c.setId(77L);
        c.setBody("hello");
        c.setCreatedAt(OffsetDateTime.now());

        CommentDto dto = commentMapper.toDto(c);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(77L);
        assertThat(dto.getBody()).isEqualTo("hello");
        assertThat(dto.getCreatedAt()).isEqualTo(c.getCreatedAt());
    }

}
