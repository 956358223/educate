package com.sora.modules.grant.controller;

import com.sora.common.auto.AssertParam;
import com.sora.common.auto.LogType;
import com.sora.common.auto.Logback;
import com.sora.common.base.CrudController;
import com.sora.common.base.CrudService;
import com.sora.common.http.RespBody;
import com.sora.configure.grant.context.PrincipalsHolder;
import com.sora.modules.grant.entity.User;
import com.sora.modules.grant.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Logback(module = "用户管理")
public class UserController extends CrudController<User,Long> {

    @Autowired
    private UserService userService;

    @Override
    public CrudService getCrudService() {
        return userService;
    }

    @GetMapping("/list")
    @Logback(value = "用户列表", type = LogType.SELECT)
    public RespBody list() {
        return userService.list(PrincipalsHolder.getPrincipals());
    }

    @PutMapping("/change")
    @Logback(value = "修改密码", type = LogType.UPDATE)
    @AssertParam(value = "username,original,password", target = Map.class)
    public RespBody change(@RequestBody Map<String, String> map) {
        return userService.change(map);
    }

    @GetMapping("/role/{id}")
    @Logback(value = "用户角色", type = LogType.SELECT)
    public RespBody role(@PathVariable Long id) {
        return userService.role(id);
    }

    @PostMapping("/role")
    @Logback(value = "配置角色", type = LogType.INSERT)
    @AssertParam(value = "id,ids", target = Map.class)
    public RespBody role(@RequestBody Map<String, Object> entity) {
        return userService.role(entity);
    }

    @DeleteMapping("/role/{userId}/{roleId}")
    @Logback(value = "删除角色", type = LogType.DELETE)
    public RespBody role(@PathVariable Long userId, @PathVariable Long roleId) {
        return userService.role(userId, roleId);
    }

    @GetMapping("/reset/{id}")
    @Logback(value = "重置密码", type = LogType.UPDATE)
    public RespBody reset(@PathVariable Long id) {
        return userService.reset(id);
    }

    @GetMapping("/code")
    public void code(HttpServletResponse response) throws Exception {
        userService.code(response, PrincipalsHolder.getPrincipals());
    }

    @GetMapping("/principal")
    @Logback(value = "获取用户", type = LogType.SELECT)
    public RespBody principal() {
        return RespBody.ok(PrincipalsHolder.getPrincipals());
    }

}
