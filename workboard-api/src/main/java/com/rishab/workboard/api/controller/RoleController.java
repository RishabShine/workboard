package com.rishab.workboard.api.controller;

import com.rishab.workboard.api.dto.request.CreateRoleRequest;
import com.rishab.workboard.api.dto.request.UpdateRoleRequest;
import com.rishab.workboard.api.dto.response.common.RoleDto;
import com.rishab.workboard.api.security.auth.AuthUser;
import com.rishab.workboard.api.service.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects/{projectId}/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(
            RoleService roleService
    ) {
        this.roleService = roleService;
    }

    @GetMapping
    public ResponseEntity<List<RoleDto>> getRoles(
            @PathVariable Long projectId,
            @AuthenticationPrincipal AuthUser user
    ) {
        List<RoleDto> result = roleService.getRolesByProject(user.userId(), projectId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/createRole")
    public ResponseEntity<RoleDto> createRole(
            @PathVariable Long projectId,
            @RequestBody CreateRoleRequest request,
            @AuthenticationPrincipal AuthUser user
    ) {
        RoleDto result = roleService.createRole(request, user.userId(), projectId);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PostMapping("/createRoles")
    public ResponseEntity<List<RoleDto>> createRoles(
            @PathVariable Long projectId,
            @RequestBody List<CreateRoleRequest> request,
            @AuthenticationPrincipal AuthUser user
    ) {
        List<RoleDto> result = roleService.createRoles(request, user.userId(), projectId);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PatchMapping("/update")
    public ResponseEntity<RoleDto> updateRole(
            @PathVariable Long projectId,
            @RequestBody UpdateRoleRequest request,
            @AuthenticationPrincipal AuthUser user
    ) {
        RoleDto result = roleService.updateRole(
                request,
                user.userId(),
                projectId
        );

        return ResponseEntity.ok(result);
    }

}
