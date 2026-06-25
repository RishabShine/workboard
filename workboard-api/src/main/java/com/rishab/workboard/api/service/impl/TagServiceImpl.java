package com.rishab.workboard.api.service.impl;

import com.rishab.workboard.api.domain.Tag;
import com.rishab.workboard.api.dto.request.CreateTagRequest;
import com.rishab.workboard.api.dto.response.common.TagDto;
import com.rishab.workboard.api.mapper.Mapper;
import com.rishab.workboard.api.repository.*;
import com.rishab.workboard.api.service.TagService;
import com.rishab.workboard.api.service.exceptions.ForbiddenException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private Mapper<Tag, TagDto> tagMapper;

    TagServiceImpl(
            TagRepository tagRepository,
            MemberRepository memberRepository,
            UserRepository userRepository,
            ProjectRepository projectRepository,
            Mapper<Tag, TagDto> tagMapper
    ) {
        this.tagRepository = tagRepository;
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.tagMapper = tagMapper;
    }

    @Override
    @Transactional
    public TagDto createTag(CreateTagRequest req, Long userId, Long projectId) {
        requireProjectMember(projectId, userId);

        Tag tag = new Tag();
        tag.setProject(projectRepository.getReferenceById(projectId));
        tag.setName(req.getName());
        tag.setColor(req.getColor());

        Tag savedTag = tagRepository.save(tag);
        return tagMapper.toDto(savedTag);
    }

    @Override
    public List<TagDto> getTagsByProject(Long userId, Long projectId) {
        requireProjectMember(projectId, userId);

        List<Tag> tags = tagRepository.FindTagsByProjectId(projectId);
        return tags.stream()
                .map(tagMapper::toDto)
                .toList();
    }

    private void requireProjectMember(Long projectId, Long currentUserId) {
        if (!memberRepository.isUserInProject(projectId, currentUserId)) {
            throw new ForbiddenException("You are not a member of this project");
        }
    }

}
