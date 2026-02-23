package com.rishab.workboard.api.repository;

import com.rishab.workboard.api.domain.User;
import com.rishab.workboard.api.repository.custom.UserRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
}
