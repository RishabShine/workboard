package com.rishab.workboard.api.repository.custom;

import com.rishab.workboard.api.domain.User;

import java.util.List;

public interface UserRepositoryCustom {

    /*
    will return top 20 results that match the given search on either username or email
     */
    List<User> searchByUsernameOrEmail(String query);

}
