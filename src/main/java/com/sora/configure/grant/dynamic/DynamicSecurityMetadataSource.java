package com.sora.configure.grant.dynamic;

import com.sora.modules.grant.entity.Auth;
import com.sora.modules.grant.entity.Role;
import com.sora.modules.grant.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class DynamicSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    @Autowired
    private AuthService authService;

    private AntPathMatcher matcher = new AntPathMatcher();

    @Override
    public Collection<ConfigAttribute> getAttributes(Object o) throws IllegalArgumentException {
        String url = ((FilterInvocation) o).getRequestUrl();
        for (Auth auth : authService.list()) {
            if (auth.getUrl() != null && matcher.match(auth.getUrl(), url) && auth.getRoles().size() > 0) {
                return SecurityConfig.createList(auth.getRoles().stream().map(Role::getName).collect(Collectors.joining(",")));
            }
        }
        return Collections.emptyList();
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return FilterInvocation.class.isAssignableFrom(aClass);
    }
}
