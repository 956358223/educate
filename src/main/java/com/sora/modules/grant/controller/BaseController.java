package com.sora.modules.grant.controller;

import com.sora.common.http.RespBody;
import com.sora.modules.grant.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
public class BaseController {

    @Autowired
    private BaseService baseService;

    @GetMapping("/message")
    public RespBody message(@RequestParam Integer type, @RequestParam String phone) {
        return baseService.message(type, phone);
    }

    @GetMapping("/captcha")
    public RespBody captcha(HttpServletRequest request, @RequestParam(required = false) String uuid) throws IOException {
        return baseService.captcha(request, uuid);
    }

}
