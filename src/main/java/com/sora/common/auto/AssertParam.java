package com.sora.common.auto;

import java.lang.annotation.*;

/**
 * @author LangWu
 * @apiNote 断言入参
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AssertParam {

    String value();

    Class<?> target();

}