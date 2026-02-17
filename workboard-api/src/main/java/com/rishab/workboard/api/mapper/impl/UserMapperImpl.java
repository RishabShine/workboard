package com.rishab.workboard.api.mapper.impl;

import com.rishab.workboard.api.domain.User;
import com.rishab.workboard.api.dto.response.common.UserSummaryDto;
import com.rishab.workboard.api.mapper.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl implements Mapper<User, UserSummaryDto> {

    private final ModelMapper modelMapper;

    public UserMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public UserSummaryDto toDto(User user) {
        return modelMapper.map(user, UserSummaryDto.class);
    }

}
