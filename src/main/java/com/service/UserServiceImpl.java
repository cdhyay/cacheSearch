package com.service;

import com.mapper.UserMapper;
import com.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> selectAll() {
        return userMapper.selectAll();
    }

    @Override
    public User selectById(long id) {
        return userMapper.selectByPrimaryKey(id);
    }

    @Override
    public void insert(User user) {
        userMapper.insert(user);
    }

    @Override
    public void deleteById(long id) {
        userMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void updateById(User user) {
        userMapper.updateByPrimaryKey(user);
    }



    @Override
    public List<User> selectByNameOrAge(User user) {
        return userMapper.selectByNameOrAge(user);
    }

    @Override
    public Integer isExist(String name) {
        Integer status = 0;
        User user = userMapper.selectByName(name);
        if(user == null) { //用户名不存在
            status = 1;
        }
        return status;
    }
}
