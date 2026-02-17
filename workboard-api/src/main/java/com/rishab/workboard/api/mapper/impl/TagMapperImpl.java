package com.rishab.workboard.api.mapper.impl;

import com.rishab.workboard.api.domain.Tag;
import com.rishab.workboard.api.dto.response.common.TagDto;
import com.rishab.workboard.api.mapper.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class TagMapperImpl implements Mapper<Tag, TagDto> {

    private final ModelMapper modelMapper;

    public TagMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public TagDto toDto(Tag tag) {
        return modelMapper.map(tag, TagDto.class);
    }

}
