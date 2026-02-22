package com.rishab.workboard.api.repository.custom.impl;

import com.rishab.workboard.api.domain.Member;
import com.rishab.workboard.api.domain.Role;
import com.rishab.workboard.api.repository.custom.RoleRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class RoleRepositoryCustomImpl implements RoleRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    public Role findByProjectIdAndName(Long projectId, String roleName) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Role> cq = cb.createQuery(Role.class);

        Root<Role> role = cq.from(Role.class);

        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.equal(role.get("project").get("id"), projectId));
        predicates.add(cb.equal(role.get("name"), roleName));

        cq.where(predicates.toArray(new Predicate[0]));

        TypedQuery<Role> query = entityManager.createQuery(cq);
        List<Role> roles = query.getResultList();

        return roles.isEmpty() ? null : roles.get(0);
    }

}
