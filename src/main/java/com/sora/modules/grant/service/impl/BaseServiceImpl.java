package com.sora.modules.grant.service.impl;

import com.google.code.kaptcha.Producer;
import com.sora.common.http.RespBody;
import com.sora.modules.grant.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class BaseServiceImpl implements BaseService {

    @Autowired
    public Producer producer;

    @Autowired
    private RedisTemplate redisTemplate;

    public static final String CAPTCHA_PREFIX = "captcha:";

    public static final String MESSAGE_PREFIX = "message:";

    @Override
    public RespBody message(Integer type, String number) {
        return null;
    }

    @Override
    public RespBody captcha(HttpServletRequest request, String uuid) throws IOException {
        Set<String> set = redisTemplate.keys(String.format("*%s*", request.getRemoteAddr()));
        if (!StringUtils.isEmpty(uuid)) redisTemplate.delete(CAPTCHA_PREFIX + uuid);
        byte[] bytes = null;
        String text = producer.createText();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BufferedImage image = producer.createImage(text);
        ImageIO.write(image, "jpg", outputStream);
        bytes = outputStream.toByteArray();
        Map<String, String> map = new LinkedHashMap<>();
        String uniques = UUID.randomUUID().toString();
        map.put("uuid", uniques);
        map.put("captcha", Base64.getEncoder().encodeToString(bytes));
        redisTemplate.opsForValue().set(CAPTCHA_PREFIX + uniques, text, 300, TimeUnit.SECONDS);
        return RespBody.ok(map);
    }

}
