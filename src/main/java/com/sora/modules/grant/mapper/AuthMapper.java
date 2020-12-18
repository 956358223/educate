package com.sora.modules.grant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sora.modules.grant.entity.Auth;

import java.util.List;

public interface AuthMapper extends BaseMapper<Auth> {

    List<Auth> findAll(Auth auth);

    List<Auth> findAllByPid(Long pid);

    List<Auth> findAllByRoleIdIn(List<Long> roleIds);

}
