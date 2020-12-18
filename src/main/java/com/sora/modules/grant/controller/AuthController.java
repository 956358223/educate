package com.sora.modules.grant.controller;

import com.sora.common.auto.LogType;
import com.sora.common.auto.Logback;
import com.sora.common.base.CrudController;
import com.sora.common.base.CrudService;
import com.sora.common.http.RespBody;
import com.sora.configure.grant.context.PrincipalsHolder;
import com.sora.modules.grant.entity.Auth;
import com.sora.modules.grant.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Logback(module = "资源管理")
public class AuthController extends CrudController<Auth, Long> {

    @Autowired
    private AuthService authService;

    @Override
    public CrudService getCrudService() {
        return authService;
    }

    @GetMapping("/cascade")
    @Logback(value = "级联资源", type = LogType.SELECT)
    public RespBody cascade() {
        return authService.cascade();
    }

    @GetMapping("/menu")
    @Logback(value = "用户菜单", type = LogType.SELECT)
    public RespBody menu() {
        return authService.menu(PrincipalsHolder.getPrincipals());
    }

    @DeleteMapping("/refresh")
    @Logback(value = "刷新缓存", type = LogType.UPDATE)
    public RespBody refresh() {
        return authService.refresh();
    }


}
