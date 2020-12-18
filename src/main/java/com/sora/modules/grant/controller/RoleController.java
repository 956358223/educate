package com.sora.modules.grant.controller;

import com.sora.common.auto.AssertParam;
import com.sora.common.auto.LogType;
import com.sora.common.auto.Logback;
import com.sora.common.base.CrudController;
import com.sora.common.base.CrudService;
import com.sora.common.http.RespBody;
import com.sora.modules.grant.entity.Role;
import com.sora.modules.grant.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/role")
@Logback(module = "角色管理")
public class RoleController extends CrudController<Role,Long> {

    @Autowired
    private RoleService roleService;

    @Override
    public CrudService getCrudService() {
        return roleService;
    }

    @GetMapping("/list")
    @Logback(value = "角色列表", type = LogType.SELECT)
    public RespBody list() {
        return roleService.list();
    }

    @GetMapping("/auth/{id}")
    @Logback(value = "角色资源", type = LogType.SELECT)
    public RespBody auth(@PathVariable Long id) {
        return roleService.auth(id);
    }

    @PostMapping("/auth")
    @AssertParam(value = "id,ids", target = Map.class)
    @Logback(value = "配置资源", type = LogType.INSERT)
    public RespBody auth(@RequestBody Map<String, Object> map) {
        return roleService.auth(map);
    }

}
