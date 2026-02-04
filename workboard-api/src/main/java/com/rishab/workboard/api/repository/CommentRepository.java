package com.rishab.workboard.api.repository;

import com.rishab.workboard.api.domain.Comment;
import com.rishab.workboard.api.repository.custom.CommentRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {

    // include findCommentByTicket
    // include findRecentComments
}
