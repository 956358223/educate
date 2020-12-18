package com.sora.configure.grant.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class JwtEntity {

    private String username;

    private String token;

    private String type;

    private Long expire;

    private boolean refresh;

    private Set<String> authorities;
}
