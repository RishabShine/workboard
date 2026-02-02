package com.rishab.workboard.api.repository;

import com.rishab.workboard.api.domain.Tag;
import com.rishab.workboard.api.repository.custom.TagRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long>, TagRepositoryCustom {

    // include getTagByProjectId
    // include getTagByTicket
}
