package com.rishab.workboard.api.repository.custom;

import com.rishab.workboard.api.domain.Comment;

import java.util.List;

public interface CommentRepositoryCustom {

    int getNumComments(Long ticketId);

    List<Comment> findCommentsByTicketId(Long ticketId);

}
