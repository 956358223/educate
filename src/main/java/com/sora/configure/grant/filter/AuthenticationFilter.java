package com.sora.configure.grant.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sora.common.exec.CaptchaException;
import com.sora.common.exec.MethodException;
import com.sora.common.http.RespBody;
import com.sora.common.http.RespState;
import com.sora.common.http.Response;
import com.sora.configure.grant.domain.AuthEntity;
import com.sora.configure.grant.service.CustomProcessService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

public class AuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final RedisTemplate redisTemplate;

    private final CustomProcessService customProcessService;

    public AuthenticationFilter(String defaultFilterProcessesUrl, AuthenticationManager authenticationManager, CustomProcessService customProcessService, RedisTemplate redisTemplate) {
        super(defaultFilterProcessesUrl);
        setAuthenticationManager(authenticationManager);
        this.redisTemplate = redisTemplate;
        this.customProcessService = customProcessService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        if (!request.getMethod().equals("POST")) throw new MethodException("Bad Method!");
        AuthEntity entity = null;
        try {
            entity = new ObjectMapper().readValue(request.getInputStream(), AuthEntity.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Objects.requireNonNull(entity);
        return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(entity.getUsername(), entity.getPassword()));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        this.customProcessService.attestation(authResult);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        RespBody body = null;
        if (failed instanceof UsernameNotFoundException) body = RespBody.body(RespState.USER_NOTFOUND);
        else if (failed instanceof BadCredentialsException) body = RespBody.body(RespState.BAD_CREDENTIAL);
        else if (failed instanceof LockedException) body = RespBody.body(RespState.ACCOUNT_LOCKED);
        else if (failed instanceof CredentialsExpiredException) body = RespBody.body(RespState.BAD_CREDENTIAL);
        else if (failed instanceof AccountExpiredException) body = RespBody.body(RespState.ACCOUNT_EXPIRE);
        else if (failed instanceof DisabledException) body = RespBody.body(RespState.ACCOUNT_EXIST);
        else if (failed instanceof LockedException) body = RespBody.body(RespState.ACCOUNT_LOCKED);
        else if (failed instanceof CaptchaException) body = RespBody.body(RespState.CAPTCHA_EMPTIES);
        Response.stream(body);
    }

}
