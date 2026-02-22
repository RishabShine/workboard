package com.rishab.workboard.api.repository.custom.impl;

import com.rishab.workboard.api.domain.Role;
import com.rishab.workboard.api.repository.custom.RoleRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RoleRepositoryCustomImpl implements RoleRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Role> findByProjectIdAndName(Long projectId, String roleName) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Role> cq = cb.createQuery(Role.class);

        Root<Role> role = cq.from(Role.class);

        cq.select(role)
                .where(
                        cb.and(
                                cb.equal(role.get("project").get("id"), projectId),
                                cb.equal(role.get("name"), roleName)
                        )
                );

        List<Role> roles = entityManager.createQuery(cq).getResultList();

        return roles.stream().findFirst();
    }

}
