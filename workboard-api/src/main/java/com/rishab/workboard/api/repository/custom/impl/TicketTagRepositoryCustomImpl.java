package com.rishab.workboard.api.repository.custom.impl;

import com.rishab.workboard.api.domain.TicketTag;
import com.rishab.workboard.api.repository.custom.TicketTagRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class TicketTagRepositoryCustomImpl implements TicketTagRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void deleteAllByTicketId(Long ticketId) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaDelete<TicketTag> delete = cb.createCriteriaDelete(TicketTag.class);
        Root<TicketTag> root = delete.from(TicketTag.class);

        delete.where(cb.equal(root.get("ticket").get("id"), ticketId));

        entityManager.createQuery(delete).executeUpdate();
    }
}
