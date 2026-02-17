package com.rishab.workboard.api.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/*
the createdBy (userId) will be passed by the url
 */
@Getter
@Setter
public class CreateProjectRequest {

    private String name;

    private List<CreateMilestoneRequest> milestones;

    private List<CreateTagRequest> tags;

}
