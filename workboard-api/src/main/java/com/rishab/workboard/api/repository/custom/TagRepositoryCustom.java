package com.rishab.workboard.api.repository.custom;

import com.rishab.workboard.api.domain.Tag;

import java.util.List;

public interface TagRepositoryCustom {

    public List<Tag> FindTagsByProjectId(Long projectId);

    public List<Tag> findTagsByTicketId(Long ticketId);

}