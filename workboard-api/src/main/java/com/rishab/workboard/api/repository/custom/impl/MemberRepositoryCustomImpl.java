package com.rishab.workboard.api.repository.custom.impl;

import com.rishab.workboard.api.domain.Member;
import com.rishab.workboard.api.domain.Project;
import com.rishab.workboard.api.domain.Role;
import com.rishab.workboard.api.repository.custom.MemberRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Role getMemberById(Long projectId, Long userId) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Role> cq = cb.createQuery(Role.class);

        Root<Member> member = cq.from(Member.class);

        cq.select(member.get("role"))
                .where(
                        cb.and(
                                cb.equal(member.get("project").get("id"), projectId),
                                cb.equal(member.get("user").get("id"), userId)
                        )
                );

        TypedQuery<Role> query = entityManager.createQuery(cq);
        List<Role> roles = query.getResultList();

        return roles.isEmpty() ? null : roles.get(0);
    }

    @Override
    public boolean isUserInProject(Long projectId, Long userId) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);

        Root<Member> member = cq.from(Member.class);

        cq.select(cb.count(member))
                .where(
                        cb.and(
                                cb.equal(member.get("project").get("id"), projectId),
                                cb.equal(member.get("user").get("id"), userId)
                        )
                );

        Long count = entityManager.createQuery(cq).getSingleResult();
        return count != null && count > 0;
    }

    @Override
    public List<Project> findAllProjectsByUserId(Long userId) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Project> cq = cb.createQuery(Project.class);

        Root<Member> member = cq.from(Member.class);

        // Select the project associated with the membership
        cq.select(member.get("project"))
                .where(cb.equal(member.get("user").get("id"), userId));

        return entityManager.createQuery(cq).getResultList();
    }

    @Override
    public List<Member> findAllMembersByProject(Long projectId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Member> cq = cb.createQuery(Member.class);

        Root<Member> member = cq.from(Member.class);

        cq.select(member)
                .where(
                        cb.equal(member.get("project").get("id"), projectId)
                );

        return entityManager.createQuery(cq).getResultList();
    }

}
