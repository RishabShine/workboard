package com.rishab.workboard.api.repository;

import com.rishab.workboard.api.domain.Role;
import com.rishab.workboard.api.repository.custom.RoleRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>, RoleRepositoryCustom {

    List<Role> findByProjectId(Long projectId);

}
