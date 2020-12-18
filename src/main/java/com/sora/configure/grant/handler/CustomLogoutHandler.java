package com.sora.configure.grant.handler;

import com.sora.common.http.RespBody;
import com.sora.common.http.RespState;
import com.sora.common.http.Response;
import com.sora.configure.grant.token.JwtHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomLogoutHandler implements LogoutSuccessHandler {

    @Autowired
    public JwtHelper jwtHelper;

    @Value("${jwt.header}")
    public String header;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (StringUtils.isEmpty(request.getHeader(header))) {
            Response.stream(RespBody.body(RespState.TOKEN_UNKNOWN));
            return;
        }
        String username = null;
        try {
            username = jwtHelper.getSubject(request.getHeader(header));
        } catch (Exception e) {
            Response.stream(RespBody.body(RespState.TOKEN_EXPIRED));
            return;
        }
        redisTemplate.keys("*" + username + "*").stream().filter(x -> !StringUtils.isEmpty(x)).peek(x -> redisTemplate.delete(x));
    }
}
