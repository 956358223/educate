package com.sora.modules.grant.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sora.common.http.RespBody;
import com.sora.common.http.RespState;
import com.sora.common.spec.SpecBody;
import com.sora.configure.grant.domain.Principals;
import com.sora.configure.grant.token.JwtHelper;
import com.sora.modules.grant.entity.Auth;
import com.sora.modules.grant.entity.Role;
import com.sora.modules.grant.entity.RoleAuth;
import com.sora.modules.grant.mapper.AuthMapper;
import com.sora.modules.grant.mapper.RoleAuthMapper;
import com.sora.modules.grant.mapper.RoleMapper;
import com.sora.modules.grant.service.AuthService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    @Value("${security.source.key}")
    private String sourceKey;

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private AuthMapper authMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RoleAuthMapper roleAuthMapper;

    @Override
    public RespBody append(Auth auth) {
        if (!authMapper.selectList(Wrappers.<Auth>lambdaQuery().eq(Auth::getPath, auth.getPath())).isEmpty()) {
            return RespBody.body(RespState.ACCESS_DENIED);
        }
        auth.setCreateTime(new Date());
        if (auth.getPid() == null) auth.setPid(0L);
        return authMapper.insert(auth) > 0 ? RespBody.ok() : RespBody.body(RespState.INSERT_FAILURE);
    }

    @Override
    public RespBody modify(Auth auth) {
        Auth entity = authMapper.selectById(auth.getId());
        if (entity == null) return RespBody.body(RespState.SELECT_FAILURE);
        auth.setModifyTime(new Date());
        return authMapper.updateById(auth) > 0 ? refresh() : RespBody.no();
    }

    @Override
    public RespBody delete(List<Long> longs) {
        int i = roleAuthMapper.delete(Wrappers.<RoleAuth>lambdaQuery().in(RoleAuth::getAuthId, longs));
        int j = authMapper.deleteBatchIds(longs);
        return i > 0 && j > 0 ? RespBody.ok() : RespBody.no();
    }

    @Override
    public RespBody details(Long id) {
        return RespBody.ok(authMapper.selectById(id));
    }

    @Override
    public RespBody search(SpecBody<Auth> body) {
        PageHelper.startPage(body.getStart(), body.getSize());
        return RespBody.ok(new PageInfo<>(authMapper.findAll(body.getBody())));
    }

    @Override
    public RespBody cascade() {
        List<Auth> listAuth = authMapper.selectList(Wrappers.<Auth>lambdaQuery().eq(Auth::getPid, 0L));
        return RespBody.ok(listAuth);
    }

    @Override
    public RespBody delete(Long id) {
        if (!authMapper.selectList(Wrappers.<Auth>lambdaQuery().eq(Auth::getPid, id)).isEmpty())
            return RespBody.body(RespState.RELATE_EXISTS);
        int i = roleAuthMapper.deleteById(id);
        i += authMapper.deleteById(id);
        return i > 0 ? refresh() : RespBody.no();
    }

    @Override
    public RespBody menu(Principals principals) {
        return RespBody.ok(getAuthList(getRedisKey(principals)));
    }

    @Override
    public RespBody refresh() {
        String value = (String) redisTemplate.opsForValue().get(sourceKey);
        if (!StringUtils.isEmpty(value)) redisTemplate.delete(sourceKey);
        return list().size() > 0 ? RespBody.ok() : RespBody.no();
    }

    @Override
    public List<Auth> list() {
        List<Auth> list = getAuthList(sourceKey);
        if (list.size() > 0) return list;
        Gson gson = new GsonBuilder().serializeNulls().create();
        list = authMapper.selectList(Wrappers.<Auth>lambdaQuery().eq(Auth::getEnabled, true));
        for (Auth x : list) {
            List<Role> roles = roleMapper.findAllByAuthId(x.getId());
            x.setRoles(roles);
        }
        redisTemplate.opsForValue().set(sourceKey, list.size() > 0 ? gson.toJson(list) : gson.toJson(Collections.emptyList()));
        return list;
    }

    @Override
    public void menu(Principals principals, Long expire) {
        String key = getRedisKey(principals);
        String value = Optional.ofNullable((String) redisTemplate.opsForValue().get(key)).orElse("");
        if (!StringUtils.isEmpty(value)) {
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
            return;
        }
        Gson gson = new GsonBuilder().serializeNulls().create();
        List<Auth> list = getAuthList(principals);
        if (list.size() == 0) {
            redisTemplate.opsForValue().set(key, gson.toJson(Collections.emptyList()), expire, TimeUnit.SECONDS);
            return;
        }
        List<Auth> listAuth = list.stream().filter(x -> x.getPid().equals(0L)).map(x -> convert(x, list)).collect(Collectors.toList());
        redisTemplate.opsForValue().set(key, gson.toJson(listAuth), expire, TimeUnit.SECONDS);
    }

    private String getRedisKey(Principals principals) {
        Objects.requireNonNull(principals);
        return Optional.ofNullable(jwtHelper.getMenusKey(principals.getUsername(), principals.getSole())).orElse("");
    }

    private Auth convert(Auth auth, List<Auth> list) {
        Auth node = new Auth();
        BeanUtils.copyProperties(auth, node);
        List<Auth> children = list.stream().filter(x -> x.getPid().equals(auth.getId())).map(x -> convert(x, list)).collect(Collectors.toList());
        node.setChildren(children);
        return node;
    }

    private List<Auth> getAuthList(Principals principals) {
        if (principals.getAuthorities().isEmpty()) return Collections.emptyList();
        List<Role> roles = roleMapper.selectList(Wrappers.<Role>lambdaQuery().in(Role::getName,
                principals.getAuthorities().stream().map(GrantedAuthority::getAuthority).distinct().collect(Collectors.toList())));
        if (roles.size() == 0) return Collections.emptyList();
        return authMapper.findAllByRoleIdIn(roles.stream().map(Role::getId).collect(Collectors.toList()));
    }

    private List<Auth> getAuthList(String key) {
        Gson gson = new GsonBuilder().serializeNulls().create();
        if (!StringUtils.isEmpty(key)) {
            String value = Optional.ofNullable((String) redisTemplate.opsForValue().get(key)).orElse("");
            if (!StringUtils.isEmpty(value)) {
                Type type = new TypeToken<List<Auth>>() {{
                }}.getType();
                List<Auth> listAuth = gson.fromJson(value, type);
                if (listAuth != null) return listAuth;
            }
            return Collections.emptyList();
        } else return Collections.emptyList();
    }

}
