package com.sora.modules.grant.service;

import com.sora.common.base.CrudService;
import com.sora.common.http.RespBody;
import com.sora.modules.grant.entity.Role;

import java.util.Map;

public interface RoleService extends CrudService<Role, Long> {

    RespBody list();

    RespBody auth(Long id);

    RespBody auth(Map<String, Object> map);

}