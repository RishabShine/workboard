package com.rishab.workboard.api.repository.custom.impl;

import com.rishab.workboard.api.domain.User;
import com.rishab.workboard.api.repository.custom.UserRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    // TODO: improve logic to fuzzy matching
    @Override
    public List<User> searchByUsernameOrEmail(String query) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);

        Root<User> user = cq.from(User.class);

        /*
        formatting for wildcards
        username and email are stored as lowercase, so the query must also be lowercase
         */
        String pattern = "%" + query.toLowerCase() + "%";

        cq.select(user)
                .where(
                        cb.or(
                                cb.like(user.get("username"), pattern),
                                cb.like(user.get("email"), pattern)
                        )
                );

        return  entityManager.createQuery(cq).setMaxResults(20).getResultList();

    }

}
