package com.sora.modules.grant.service;

import com.sora.common.base.CrudService;
import com.sora.common.http.RespBody;
import com.sora.configure.grant.domain.Principals;
import com.sora.modules.grant.entity.Auth;

import java.util.List;

public interface AuthService extends CrudService<Auth, Long> {

    RespBody cascade();

    RespBody delete(Long id);

    RespBody menu(Principals principals);

    RespBody refresh();

    List<Auth> list();

    void menu(Principals principals, Long expire);

}
