package com.rishab.workboard.api.service.impl;

import com.rishab.workboard.api.domain.Milestone;
import com.rishab.workboard.api.domain.Project;
import com.rishab.workboard.api.dto.response.common.MilestoneDto;
import com.rishab.workboard.api.mapper.Mapper;
import com.rishab.workboard.api.repository.MemberRepository;
import com.rishab.workboard.api.repository.MilestoneRepository;
import com.rishab.workboard.api.repository.ProjectRepository;
import com.rishab.workboard.api.service.MilestoneService;
import com.rishab.workboard.api.service.exceptions.ForbiddenException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MilestoneServiceImpl implements MilestoneService {

    private final MilestoneRepository milestoneRepository;
    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final Mapper<Milestone, MilestoneDto> milestoneMapper;

    public MilestoneServiceImpl(
            MilestoneRepository milestoneRepository,
            ProjectRepository projectRepository,
            MemberRepository memberRepository,
            Mapper<Milestone, MilestoneDto> milestoneMapper
    ) {
        this.milestoneRepository = milestoneRepository;
        this.projectRepository = projectRepository;
        this.memberRepository = memberRepository;
        this.milestoneMapper = milestoneMapper;
    }

    @Override
    public List<MilestoneDto> getMilestonesByProject(Long projectId, Long userId) {

        requireProjectMember(projectId, userId);

        List<Milestone> milestones = milestoneRepository.getMilestonesByProjectId(projectId);
        return milestones.stream()
                .map(milestoneMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public MilestoneDto createMilestone(Long projectId, String name, Long userId) {

        requireProjectMember(projectId, userId);

        Project projectRef = projectRepository.getReferenceById(projectId);

        Milestone milestone = new Milestone();
        milestone.setName(name);
        milestone.setProject(projectRef);

        milestone = milestoneRepository.save(milestone);
        return milestoneMapper.toDto(milestone);
    }

    @Override
    @Transactional
    public MilestoneDto updateMilestone(Long milestoneId, Long projectId, String name, Long userId) {

        requireProjectMember(projectId, userId);

        Milestone milestone = milestoneRepository.getReferenceById(milestoneId);

        milestone.setName(name);

        milestone = milestoneRepository.save(milestone);
        return milestoneMapper.toDto(milestone);
    }

    private void requireProjectMember(Long projectId, Long currentUserId) {
        if (!memberRepository.isUserInProject(projectId, currentUserId)) {
            throw new ForbiddenException("You are not a member of this project");
        }
    }

}
