package com.rishab.workboard.api.mapper.impl;

import com.rishab.workboard.api.domain.Role;
import com.rishab.workboard.api.dto.response.common.RoleDto;
import com.rishab.workboard.api.mapper.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class RoleMapperImpl implements Mapper<Role, RoleDto> {

    private final ModelMapper modelMapper;

    public RoleMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public RoleDto toDto(Role role) {
        return modelMapper.map(role, RoleDto.class);
    }

}
