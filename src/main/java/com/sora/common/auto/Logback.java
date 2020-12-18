package com.sora.common.auto;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Logback {

    String module() default "";

    String value() default "";

    LogType type() default LogType.OTHERS;

}
