package com.rishab.workboard.api.service;

import com.rishab.workboard.api.dto.request.CreateCommentRequest;
import com.rishab.workboard.api.dto.response.comment.CommentDto;

import java.util.List;

public interface CommentService {

    List<CommentDto> listComments(Long ticketId, Long currentUserId);

    CommentDto addComment(Long ticketId, CreateCommentRequest req, Long currentUserId);

}
