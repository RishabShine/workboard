package com.rishab.workboard.api.dto.response.project;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class ProjectOverviewDto {

    private Long id;

    private String name;

    private OffsetDateTime createdAt;

}