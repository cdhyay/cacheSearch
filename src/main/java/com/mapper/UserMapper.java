package com.mapper;

import com.model.User;
import org.springframework.stereotype.Component;


import java.util.List;

@Component
public interface UserMapper {
    int deleteByPrimaryKey(Long id);

    int insert(User record);

    User selectByPrimaryKey(Long id);

    List<User> selectAll();

    int updateByPrimaryKey(User record);

    User selectByName(String name);

    List<User> selectByNameOrAge(User user);
}