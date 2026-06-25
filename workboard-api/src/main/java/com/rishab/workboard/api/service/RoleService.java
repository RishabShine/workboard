package com.rishab.workboard.api.service;

import com.rishab.workboard.api.dto.request.CreateRoleRequest;
import com.rishab.workboard.api.dto.request.UpdateRoleRequest;
import com.rishab.workboard.api.dto.response.common.RoleDto;

import java.util.List;

public interface RoleService {

    RoleDto createRole(CreateRoleRequest req, Long userId, Long projectId);

    List<RoleDto> createRoles(List<CreateRoleRequest> req, Long userId, Long projectId);

    RoleDto updateRole(UpdateRoleRequest req, Long userId, Long projectId);

    List<RoleDto> getRolesByProject(Long userId, Long projectId);

}
