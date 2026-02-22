package com.rishab.workboard.api.repository.custom.impl;

import com.rishab.workboard.api.domain.Member;
import com.rishab.workboard.api.domain.Milestone;
import com.rishab.workboard.api.repository.custom.MilestoneRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MilestoneRepositoryCustomImpl implements MilestoneRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Milestone> getMilestonesByProjectId(Long projectId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Milestone> cq = cb.createQuery(Milestone.class);

        Root<Milestone> milestone = cq.from(Milestone.class);

        cq.select(milestone)
                .where(
                        cb.equal(milestone.get("project").get("id"), projectId)
                );

        return entityManager.createQuery(cq).getResultList();
    }

}
