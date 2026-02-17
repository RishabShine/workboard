package com.rishab.workboard.api.mapper;

public interface Mapper<A, B> {

    B toDto(A entity);

}