package com.sora.modules.grant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sora.modules.grant.entity.User;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {

    List<User> findAll(User user);

    List<User> findAllByPid(Long pid);

}
