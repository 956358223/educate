package com.sora.common.auto;

import com.alibaba.druid.util.StringUtils;
import com.sora.common.http.RespBody;
import com.sora.common.http.RespState;
import com.sora.common.http.Response;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author LangWu
 * @apiNote 入参切面校验
 */
@Slf4j
@Aspect
@Component
public class AssertParamHandler {

    private MethodSignature signature = null;

    private Set<String> collection = new HashSet<>();

    @Pointcut("@annotation(com.sora.common.auto.AssertParam)")
    public void execute() {
        log.info("Start parameter scanning...");
    }

    @Around("execute()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Object[] args = point.getArgs();
        signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        AssertParam param = method.getAnnotation(AssertParam.class);
        for (Object object : args) {
            Predicate<String> predicate = x -> !x.equals("") && !StringUtils.isEmpty(x);
            if (object.getClass() == param.target()) {
                collection = Arrays.stream(param.value().split(",")).filter(predicate).map(x -> getProperty(object, x)).collect(Collectors.toSet());
            } else if (object instanceof Map) {
                HashMap<String, Object> map = (HashMap<String, Object>) object;
                if (map.isEmpty()) collection = Arrays.stream(args).map(Object::toString).collect(Collectors.toSet());
                else {
                    collection = Arrays.stream(param.value().split(",")).filter(predicate).map(x -> map.get(x) == null || map.get(x).toString().equals("") ? x : null).collect(Collectors.toSet());
                }
            }
        }
        collection.removeIf(Objects::isNull);
        if (collection.size() > 0) return null;
        return point.proceed();
    }

    @AfterReturning(value = "@annotation(com.sora.common.auto.AssertParam)", returning = "object")
    public void afterReturning(JoinPoint point, Object object) throws IOException {
        if (collection.size() > 0) {
            Map<String, Object> map = new HashMap<String,Object>() {{
                put("require", collection.stream().filter(x -> !x.isEmpty()).collect(Collectors.joining(",")));
            }};
            collection.clear();
            Response.stream(RespBody.body(RespState.ILLEGAL, map));
            return;
        }
    }

    private String getProperty(Object object, String str) {
        try {
            Field field = object.getClass().getDeclaredField(str);
            ReflectionUtils.makeAccessible(field);
            Object value = Optional.ofNullable(field.get(object)).orElse(null);
            if (value instanceof List) {
                if (((List) value).size() == 0) return str;
            } else if (value == null || value.toString().equals("")) return str;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

}
