package com.rishab.workboard.api.mapper.impl;

import com.rishab.workboard.api.domain.Comment;
import com.rishab.workboard.api.dto.response.comment.CommentDto;
import com.rishab.workboard.api.mapper.CommentMapper;
import com.rishab.workboard.api.mapper.Mapper;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class CommentMapperImpl implements CommentMapper {

    private final UserMapperImpl userMapper;

    public CommentMapperImpl(UserMapperImpl userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public CommentDto toDto(Comment comment) {

        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setBody(comment.getBody());
        commentDto.setUser(userMapper.toDto(comment.getUser()));
        commentDto.setCreatedAt(comment.getCreatedAt());
        Long replyTo = comment.getReplyTo() != null ? comment.getReplyTo().getId() : null;
        commentDto.setReplyTo(replyTo);

        return commentDto;
    }

}
