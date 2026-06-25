package com.rishab.workboard.api.service;

import com.rishab.workboard.api.dto.request.CreateTagRequest;
import com.rishab.workboard.api.dto.response.common.TagDto;

import java.util.List;

public interface TagService {

    TagDto createTag(CreateTagRequest req, Long userId, Long projectId);

    List<TagDto> getTagsByProject(Long userId, Long projectId);

}
