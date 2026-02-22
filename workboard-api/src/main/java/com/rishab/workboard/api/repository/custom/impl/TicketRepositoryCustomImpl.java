package com.rishab.workboard.api.repository.custom.impl;

import com.rishab.workboard.api.domain.Ticket;
import com.rishab.workboard.api.repository.custom.TicketRepositoryCustom;
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
public class TicketRepositoryCustomImpl implements TicketRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Ticket> findTicketsByProject(Long projectId){

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Ticket> cq = cb.createQuery(Ticket.class);

        Root<Ticket> ticket = cq.from(Ticket.class);
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.equal(ticket.get("project").get("id"), projectId));

//        if (status != null) {
//            predicates.add(cb.equal(ticket.get("status"), status));
//        }

        // Tag filtering would require a join to ticket_tags

        cq.where(predicates.toArray(new Predicate[0]));
        cq.orderBy(cb.desc(ticket.get("createdAt")));

        return entityManager.createQuery(cq).getResultList();
    }
}
