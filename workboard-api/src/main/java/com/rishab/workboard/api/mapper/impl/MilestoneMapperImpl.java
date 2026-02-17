package com.rishab.workboard.api.mapper.impl;

import com.rishab.workboard.api.domain.Milestone;
import com.rishab.workboard.api.dto.response.common.MilestoneDto;
import com.rishab.workboard.api.mapper.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class MilestoneMapperImpl implements Mapper<Milestone, MilestoneDto> {

    private final ModelMapper modelMapper;

    public MilestoneMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public MilestoneDto toDto(Milestone milestone) {
        return modelMapper.map(milestone, MilestoneDto.class);
    }

}
