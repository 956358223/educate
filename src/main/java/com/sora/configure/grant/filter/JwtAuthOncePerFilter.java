package com.sora.configure.grant.filter;

import com.sora.common.http.RespBody;
import com.sora.common.http.RespState;
import com.sora.common.http.Response;
import com.sora.configure.grant.service.CustomDetailsService;
import com.sora.configure.grant.token.JwtHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class JwtAuthOncePerFilter extends OncePerRequestFilter {

    @Value("${jwt.header}")
    private String header;

    @Value("${security.ignores}")
    private String ignores;

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private CustomDetailsService customDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        if (!ignores.contains(uri)) {
            final String token = request.getHeader(this.header);
            String username = null;
            if (!StringUtils.isEmpty(token)) {
                try {
                    username = jwtHelper.getSubject(token);
                } catch (Exception e) {
                    Response.stream(RespBody.body(RespState.TOKEN_INVALID));
                    return;
                }
                String key = jwtHelper.getTokenKey(username, jwtHelper.getSole(token));
                if (StringUtils.isEmpty(key)) {
                    Response.stream(RespBody.body(RespState.TOKEN_EXPIRED));
                    return;
                }
                String value = (String) redisTemplate.opsForValue().get(key);
                if (StringUtils.isEmpty(value)) {
                    Response.stream(RespBody.body(RespState.TOKEN_UNKNOWN));
                    return;
                }
                if (!token.equals(value)) {
                    Response.stream(RespBody.body(RespState.TOKEN_INVALID));
                    return;
                }
            } else {
                Response.stream(RespBody.body(RespState.TOKEN_UNKNOWN));
                return;
            }
            if (!StringUtils.isEmpty(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails details = null;
                try {
                    details = customDetailsService.loadUserByUsername(username);
                } catch (UsernameNotFoundException e) {
                    Response.stream(RespBody.body(RespState.USER_NOTFOUND));
                    return;
                }
                if (jwtHelper.verify(token, details)) {
                    UsernamePasswordAuthenticationToken upToken = new UsernamePasswordAuthenticationToken(details, details.getPassword(), details.getAuthorities());
                    upToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(upToken);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
