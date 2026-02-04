package com.rishab.workboard.api.repository.custom.impl;

import com.rishab.workboard.api.domain.Tag;
import com.rishab.workboard.api.domain.TicketTag;
import com.rishab.workboard.api.repository.custom.TagRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class TagRepositoryCustomImpl implements TagRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Tag> FindTagsByProjectId(Long projectId) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tag> cq = cb.createQuery(Tag.class);

        Root<Tag> tag = cq.from(Tag.class);
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.equal(tag.get("project").get("id"), projectId));

        cq.where(predicates.toArray(new Predicate[0]));
        cq.orderBy(cb.desc(tag.get("createdAt")));

        return entityManager.createQuery(cq).getResultList();
    }

    @Override
    public List<Tag> findTagsByTicketId(Long ticketId) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // finding all tag IDs that correspond to the ticket
        CriteriaQuery<TicketTag> cq = cb.createQuery(TicketTag.class);
        Root<TicketTag> ticketTag = cq.from(TicketTag.class);
        cq.where(cb.equal(ticketTag.get("ticket").get("id"), ticketId));

        List<TicketTag> ticketTags = entityManager
                .createQuery(cq)
                .getResultList();

        return ticketTags
                .stream()
                .map(TicketTag::getTag)
                .toList();
    }

}
