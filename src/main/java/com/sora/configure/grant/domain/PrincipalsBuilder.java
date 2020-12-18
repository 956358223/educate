package com.sora.configure.grant.domain;

import com.sora.modules.grant.entity.Role;
import com.sora.modules.grant.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.stream.Collectors;

public class PrincipalsBuilder {

    public static Principals create(User user, List<Role> roles) {
        return new Principals(user.getId(), user.getSole(), user.getPhone(), user.getUsername(), user.getPassword(), user.getEnabled(), user.getProfile(), toGrantedAuthorities(roles));
    }

    private static List<GrantedAuthority> toGrantedAuthorities(List<Role> authorities) {
        return authorities.stream()
                .map(x -> new SimpleGrantedAuthority(x.getName())).distinct()
                .collect(Collectors.toList());
    }
}
