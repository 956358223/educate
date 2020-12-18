package com.sora.configure.grant.domain;

import lombok.Data;

@Data
public class AuthEntity {

    private String username;

    private String phone;

    private String password;

    private String uuid;

    private String captcha;

}
