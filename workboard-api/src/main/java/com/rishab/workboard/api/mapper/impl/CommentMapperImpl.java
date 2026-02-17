package com.rishab.workboard.api.mapper.impl;

import com.rishab.workboard.api.domain.Comment;
import com.rishab.workboard.api.dto.response.comment.CommentDto;
import com.rishab.workboard.api.mapper.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class CommentMapperImpl implements Mapper<Comment, CommentDto> {

    private final ModelMapper modelMapper;

    public CommentMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public CommentDto toDto(Comment comment) {
        return modelMapper.map(comment, CommentDto.class);
    }

}
