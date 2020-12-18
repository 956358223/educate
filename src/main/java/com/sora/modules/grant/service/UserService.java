package com.sora.modules.grant.service;

import com.sora.common.base.CrudService;
import com.sora.common.http.RespBody;
import com.sora.configure.grant.domain.Principals;
import com.sora.modules.grant.entity.User;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface UserService extends CrudService<User, Long> {

    RespBody list(Principals principals);

    RespBody role(Long userId);

    RespBody role(Map<String, Object> map);

    RespBody role(Long userId, Long roleId);

    RespBody change(Map<String, String> map);

    RespBody reset(Long userId);

    List<Long> children(Principals principals);

    void code(HttpServletResponse response, Principals principals) throws Exception;

}
