package com.rishab.workboard.api.repository.custom.impl;

import com.rishab.workboard.api.domain.ProjectInvite;
import com.rishab.workboard.api.repository.custom.ProjectInviteRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProjectInviteRepositoryCustomImpl implements ProjectInviteRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ProjectInvite> getInvites(Long currentUserId) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProjectInvite> cq = cb.createQuery(ProjectInvite.class);

        Root<ProjectInvite> invite = cq.from(ProjectInvite.class);

        cq.select(invite)
                .where(
                        cb.equal(
                                invite.get("recipient").get("id"),
                                currentUserId
                        )
                );

        return entityManager
                .createQuery(cq)
                .getResultList();

    }

}
