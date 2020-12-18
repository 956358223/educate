package com.sora.modules.grant.service;

import com.sora.common.http.RespBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface BaseService {

    RespBody message(Integer type, String number);

    RespBody captcha(HttpServletRequest request, String sole) throws IOException;

}
