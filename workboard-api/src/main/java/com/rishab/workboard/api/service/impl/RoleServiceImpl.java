package com.rishab.workboard.api.service.impl;

import com.rishab.workboard.api.domain.Project;
import com.rishab.workboard.api.domain.Role;
import com.rishab.workboard.api.domain.User;
import com.rishab.workboard.api.dto.request.CreateRoleRequest;
import com.rishab.workboard.api.dto.request.UpdateRoleRequest;
import com.rishab.workboard.api.dto.response.common.RoleDto;
import com.rishab.workboard.api.mapper.Mapper;
import com.rishab.workboard.api.repository.MemberRepository;
import com.rishab.workboard.api.repository.ProjectRepository;
import com.rishab.workboard.api.repository.RoleRepository;
import com.rishab.workboard.api.repository.UserRepository;
import com.rishab.workboard.api.service.RoleService;
import com.rishab.workboard.api.service.exceptions.ForbiddenException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private Mapper<Role, RoleDto> roleMapper;

    RoleServiceImpl(
            RoleRepository roleRepository,
            MemberRepository memberRepository,
            UserRepository userRepository,
            ProjectRepository projectRepository,
            Mapper<Role, RoleDto> roleMapper
    ) {
        this.roleRepository = roleRepository;
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.roleMapper = roleMapper;
    }

    @Override
    @Transactional
    public RoleDto createRole(CreateRoleRequest req, Long userId, Long projectId) {
        requireProjectMember(projectId, userId);

        Role role = new Role();
        role.setCreatedBy(userRepository.getReferenceById(userId));
        role.setName(req.getName());
        role.setProject(projectRepository.getReferenceById(projectId));

        Role savedRole = roleRepository.save(role);
        return roleMapper.toDto(savedRole);
    }

    @Override
    @Transactional
    public List<RoleDto> createRoles(List<CreateRoleRequest> req, Long userId, Long projectId) {
        requireProjectMember(projectId, userId);

        User user = userRepository.getReferenceById(userId);
        Project project = projectRepository.getReferenceById(projectId);

        List<RoleDto> savedRoles = new ArrayList<>();

        for (CreateRoleRequest roleRequest : req) {
            Role role = new Role();
            role.setName(roleRequest.getName());
            role.setCreatedBy(user);
            role.setProject(project);

            role = roleRepository.save(role);
            savedRoles.add(roleMapper.toDto(role));
        }

        return savedRoles;
    }

    @Override
    @Transactional
    public RoleDto updateRole(UpdateRoleRequest req, Long userId, Long projectId) {
        requireProjectMember(projectId, userId);

        Role role = new Role();
        role.setName(req.getName());

        Role savedRole = roleRepository.save(role);
        return roleMapper.toDto(savedRole);
    }

    @Override
    public List<RoleDto> getRolesByProject(Long userId, Long projectId) {
        requireProjectMember(projectId, userId);

        List<Role> roles =  roleRepository.findByProjectId(projectId);
        return roles.stream()
                .map(roleMapper::toDto)
                .toList();
    }

    private void requireProjectMember(Long projectId, Long currentUserId) {
        if (!memberRepository.isUserInProject(projectId, currentUserId)) {
            throw new ForbiddenException("You are not a member of this project");
        }
    }

}
