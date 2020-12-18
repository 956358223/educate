package com.sora.configure.grant.service;

import com.sora.common.http.RespBody;
import com.sora.common.http.Response;
import com.sora.configure.grant.domain.Principals;
import com.sora.configure.grant.token.JwtEntity;
import com.sora.configure.grant.token.JwtHelper;
import com.sora.modules.grant.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CustomProcessService {

    @Value("${jwt.expire.first}")
    private Long firstExpire;

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private AuthService authService;

    @Autowired
    private RedisTemplate redisTemplate;

    public void attestation(Authentication auth) throws IOException {
        Principals principals = (Principals) auth.getPrincipal();
        Objects.requireNonNull(principals);
        String key = jwtHelper.getTokenKey(principals.getUsername(), principals.getSole());
        if (StringUtils.isEmpty(key)) return;
        String value = (String) redisTemplate.opsForValue().get(key);
        if (StringUtils.isEmpty(value)) {
            Set<String> set = redisTemplate.keys("*" + principals.getSole() + "*");
            set.stream().filter(x -> !StringUtils.isEmpty(x)).forEach(x -> redisTemplate.delete(x));
        }
        Response.stream(RespBody.ok(getJwtDetails(principals)));
    }

    private JwtEntity getJwtDetails(Principals principals) {
        Map<String, Object> map = new HashMap<String, Object>() {{
            put("content", principals.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(",")));
            put("sole", principals.getSole());
            put("userId", principals.getId());
        }};
        authService.menu(principals, firstExpire);
        JwtEntity jwtStore = jwtHelper.create(map, principals.getUsername(), principals.getSole());
        jwtStore.setAuthorities(principals.getAuthorities().stream().filter(x -> x != null).map(GrantedAuthority::getAuthority).collect(Collectors.toSet()));
        return jwtStore;
    }

}
