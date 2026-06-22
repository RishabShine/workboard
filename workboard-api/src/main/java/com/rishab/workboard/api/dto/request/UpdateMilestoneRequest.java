package com.rishab.workboard.api.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateMilestoneRequest {

    private Long projectId;

    private Long id;

    private String name;

}
