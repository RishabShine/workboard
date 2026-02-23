package com.rishab.workboard.api.mapper;

import com.rishab.workboard.api.domain.Comment;
import com.rishab.workboard.api.dto.response.comment.CommentDto;

public interface CommentMapper {

    CommentDto toDto(Comment comment);

}
