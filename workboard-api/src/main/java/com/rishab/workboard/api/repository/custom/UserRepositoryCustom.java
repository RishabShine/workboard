package com.rishab.workboard.api.repository.custom;

import com.rishab.workboard.api.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryCustom {

    /*
    will return top 20 results that match the given search on either username or email
     */
    List<User> searchByUsernameOrEmail(String query);

    Optional<User> findByUsernameOrEmail(String username);

}
