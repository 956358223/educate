package com.sora.configure.grant.context;

import com.sora.configure.grant.domain.Principals;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.stream.Collectors;

public class PrincipalsHolder {

    public static Principals getPrincipals() {
        return (Principals) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public static Long getId() {
        if (getPrincipals() != null) return getPrincipals().getId();
        else return null;
    }

    public static String getUsername() {
        if (getPrincipals() != null) return getPrincipals().getUsername();
        else return null;
    }

    public static String getRoles() {
        if (getPrincipals() != null)
            return getPrincipals().getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
        else return null;
    }

}
