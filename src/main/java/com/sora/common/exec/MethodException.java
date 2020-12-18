package com.sora.common.exec;


import org.springframework.security.core.AuthenticationException;

public class MethodException extends AuthenticationException {
    public MethodException(String msg) {
        super(msg);
    }
}
