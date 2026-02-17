package com.rishab.workboard.api.dto.response.project;

import com.rishab.workboard.api.dto.response.common.MilestoneDto;
import com.rishab.workboard.api.dto.response.common.RoleDto;
import com.rishab.workboard.api.dto.response.common.TagDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProjectDetailsDto {

    private Long id;

    private String name;

    private List<MilestoneDto> milestones;

    private List<TagDto> tags;

    private RoleDto userRole;

}
