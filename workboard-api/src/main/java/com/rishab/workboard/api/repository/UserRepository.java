package com.rishab.workboard.api.repository;

import com.rishab.workboard.api.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
