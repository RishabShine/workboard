package com.rishab.workboard.api.repository.custom;

import com.rishab.workboard.api.domain.Role;

public interface RoleRepositoryCustom {

    Role findByProjectIdAndName(Long id, String name);

}
