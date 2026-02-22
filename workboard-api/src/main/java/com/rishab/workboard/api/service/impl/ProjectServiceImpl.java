package com.rishab.workboard.api.service.impl;

import com.rishab.workboard.api.domain.*;
import com.rishab.workboard.api.domain.id.MemberId;
import com.rishab.workboard.api.dto.request.CreateProjectRequest;
import com.rishab.workboard.api.dto.request.UpdateProjectRequest;
import com.rishab.workboard.api.dto.response.project.ProjectDetailDto;
import com.rishab.workboard.api.dto.response.project.ProjectListDto;
import com.rishab.workboard.api.dto.response.project.ProjectOverviewDto;
import com.rishab.workboard.api.mapper.Mapper;
import com.rishab.workboard.api.mapper.ProjectDetailMapper;
import com.rishab.workboard.api.mapper.ProjectListMapper;
import com.rishab.workboard.api.mapper.impl.ProjectDetailMapperImpl;
import com.rishab.workboard.api.mapper.impl.ProjectListMapperImpl;
import com.rishab.workboard.api.mapper.impl.ProjectOverviewMapperImpl;
import com.rishab.workboard.api.repository.*;
import com.rishab.workboard.api.service.ProjectService;
import com.rishab.workboard.api.service.exceptions.ForbiddenException;
import com.rishab.workboard.api.service.exceptions.NotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProjectServiceImpl implements ProjectService {

    // repositories
    private final ProjectRepository projectRepository;
    private final TagRepository tagRepository;
    private final MemberRepository memberRepository;
    private final MilestoneRepository milestoneRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    // mappers
    private ProjectDetailMapper projectDetailMapper;
    private ProjectListMapper projectListMapper;
    private Mapper<Project, ProjectOverviewDto> projectOverviewMapper;

    public ProjectServiceImpl(ProjectRepository projectRepository,
                              ProjectDetailMapperImpl projectDetailMapper,
                              ProjectListMapperImpl projectListMapper,
                              ProjectOverviewMapperImpl projectOverviewMapper,
                              TagRepository tagRepository,
                              MemberRepository memberRepository,
                              MilestoneRepository milestoneRepository,
                              UserRepository userRepository,
                              RoleRepository roleRepository) {
        this.projectRepository = projectRepository;
        this.projectDetailMapper = projectDetailMapper;
        this.projectListMapper = projectListMapper;
        this.projectOverviewMapper = projectOverviewMapper;
        this.tagRepository = tagRepository;
        this.memberRepository = memberRepository;
        this.milestoneRepository = milestoneRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public ProjectDetailDto getProjectDetail(Long projectId, Long currentUserId) {

        // confirming user is in project and retrieving role
        Role userRole = requireProjectMember(projectId, currentUserId);
        if (userRole == null) {
            throw new NotFoundException("Role not found");
        }

        // retrieving project
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found"));

        // getting additional project detail
        List<Milestone> projectMilestones = milestoneRepository.getMilestonesByProjectId(project.getId());
        List<Tag> projectTags = tagRepository.FindTagsByProjectId(projectId);

        return projectDetailMapper.toDto(project, projectMilestones, projectTags, userRole);
    }

    public ProjectListDto listProjectsForUser(Long currentUserId) {
        List<Project> userProjects = memberRepository.findAllProjectsByUserId(currentUserId);
        return projectListMapper.toDto(userProjects);
    }

    @Transactional
    public ProjectOverviewDto createProject(CreateProjectRequest req, Long currentUserId) {

        Project project = new Project();

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        project.setCreatedBy(currentUser);
        project.setName(req.getName());

        // saving project
        Project savedProject = projectRepository.save(project);

        // saving milestones
        if (req.getMilestones() != null && !req.getMilestones().isEmpty()) {
            req.getMilestones().forEach(mReq -> {
                Milestone milestone = new Milestone();
                milestone.setName(mReq.getName());
                milestone.setProject(savedProject);
                milestoneRepository.save(milestone);
            });
        }

        // saving tags
        if (req.getTags() != null && !req.getTags().isEmpty()) {
            req.getTags().forEach(tReq -> {
                Tag tag = new Tag();
                tag.setName(tReq.getName());
                tag.setColor(tReq.getColor());
                tag.setProject(savedProject);
                tagRepository.save(tag);
            });
        }

        // create OWNER role for this project (simple MVP)
        Role ownerRole = new Role();
        ownerRole.setName("OWNER");
        ownerRole.setProject(savedProject);
        ownerRole.setCreatedBy(currentUser);
        ownerRole = roleRepository.save(ownerRole);

        // add creator as member with OWNER role
        Member creatorMembership = new Member();
        creatorMembership.setId(new MemberId(currentUser.getId(), savedProject.getId()));
        creatorMembership.setUser(currentUser);
        creatorMembership.setProject(savedProject);
        creatorMembership.setRole(ownerRole);
        memberRepository.save(creatorMembership);

        return projectOverviewMapper.toDto(savedProject);
    }

    @Transactional
    public ProjectOverviewDto updateProject(Long projectId, UpdateProjectRequest req, Long currentUserId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found"));

        project.setName(req.getName());

        Project updatedProject = projectRepository.save(project);

        return projectOverviewMapper.toDto(updatedProject);
    }

    private Role requireProjectMember(Long projectId, Long userId) {
        Member member = memberRepository.findById(new MemberId(userId, projectId))
                .orElseThrow(() -> new ForbiddenException("You are not a member of this project"));
        return member.getRole();
    }

}
