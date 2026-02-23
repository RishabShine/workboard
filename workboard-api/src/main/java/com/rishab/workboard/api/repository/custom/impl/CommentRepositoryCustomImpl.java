package com.rishab.workboard.api.repository.custom.impl;

import com.rishab.workboard.api.domain.Comment;
import com.rishab.workboard.api.repository.custom.CommentRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;

public class CommentRepositoryCustomImpl implements CommentRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public int getNumComments(Long ticketId) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Comment> cq = cb.createQuery(Comment.class);
        Root<Comment> comment = cq.from(Comment.class);

        cq.where(cb.equal(comment.get("ticket").get("id"), ticketId));

        return entityManager.createQuery(cq).getResultList().size();
    }

    @Override
    public List<Comment> findCommentsByTicketId(Long ticketId) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Comment> cq = cb.createQuery(Comment.class);
        Root<Comment> comment = cq.from(Comment.class);

        cq.where(cb.equal(comment.get("ticket").get("id"), ticketId));

        return entityManager.createQuery(cq).getResultList();
    }

}
