package com.rishab.workboard.api.repository.custom;

import com.rishab.workboard.api.domain.Role;

import java.util.Optional;

public interface RoleRepositoryCustom {

    Optional<Role> findByProjectIdAndName(Long id, String name);

}
