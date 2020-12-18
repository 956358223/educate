package com.sora.modules.grant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sora.modules.grant.entity.Role;

import java.util.List;

public interface RoleMapper extends BaseMapper<Role> {

    List<Role> findAll(Role role);

    List<Role> findAllByUserId(Long userId);

    List<Role> findAllByAuthId(Long authId);

}
