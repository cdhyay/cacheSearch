package com.service;

import com.model.User;

import java.util.List;

public interface UserService {
    List<User> selectAll();
    User selectById(long id);
    void insert(User user);
    void deleteById(long id);
    void updateById(User user);

    List<User> selectByNameOrAge(User user);
    Integer isExist(String name) ;
}
