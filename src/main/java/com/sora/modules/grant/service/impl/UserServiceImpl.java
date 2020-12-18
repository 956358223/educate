package com.sora.modules.grant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.sora.common.http.RespBody;
import com.sora.common.http.RespState;
import com.sora.common.spec.SpecBody;
import com.sora.common.utils.CodeTools;
import com.sora.common.utils.CryptTools;
import com.sora.configure.grant.context.PrincipalsHolder;
import com.sora.configure.grant.domain.Principals;
import com.sora.modules.grant.entity.User;
import com.sora.modules.grant.entity.UserRole;
import com.sora.modules.grant.mapper.RoleMapper;
import com.sora.modules.grant.mapper.UserMapper;
import com.sora.modules.grant.mapper.UserRoleMapper;
import com.sora.modules.grant.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Value("${jwt.prefix}")
    private String prefix;

    @Value("${jwt.expire.first}")
    private Long firstExpire;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    private UserRoleMapper userRoleMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public RespBody append(User user) {
        if (user == null || StringUtils.isEmpty(user.getUsername()) || StringUtils.isEmpty(user.getPhone()))
            return RespBody.body(RespState.ILLEGAL);
        User entity = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, user.getUsername()));
        user.setSole(CryptTools.getUuid());
        if (entity != null) return RespBody.body(RespState.EQUALS_EXISTS);
        user.setPassword(bCryptPasswordEncoder.encode(CryptTools.crypt(user.getPhone())));
        return userMapper.insert(user) > 0 ? RespBody.ok() : RespBody.no();
    }

    @Override
    public RespBody modify(User user) {
        if (user.getId() == null) return RespBody.body(RespState.ILLEGAL);
        User entity = userMapper.selectById(user.getId());
        if (entity == null) return RespBody.body(RespState.SELECT_FAILURE);
        if (StringUtils.isEmpty(user.getPassword()))
            user.setPassword(bCryptPasswordEncoder.encode(CryptTools.crypt(user.getPassword())));
        user.setModifyTime(new Date());
        return userMapper.updateById(user) > 0 ? RespBody.ok() : RespBody.no();
    }

    @Override
    public RespBody delete(List<Long> longs) {
        return null;
    }

    @Override
    public RespBody details(Long aLong) {
        User user = userMapper.selectById(aLong);
        if (user == null) return RespBody.body(RespState.SELECT_FAILURE);
        user.setChildren(null);
        return RespBody.ok(user);
    }

    @Override
    public RespBody search(SpecBody<User> body) {
        PageHelper.startPage(body.getStart(), body.getSize());
        return RespBody.ok(new PageInfo<>(userMapper.findAll(body.getBody())));
    }

    @Override
    public RespBody list(Principals principals) {
        return RespBody.ok(userMapper.selectBatchIds(children(principals)));
    }

    @Override
    public RespBody role(Long userId) {
        return RespBody.ok(roleMapper.findAllByUserId(userId));
    }

    @Override
    public RespBody role(Map<String, Object> map) {
        Long id = Long.parseLong(map.get("id").toString());
        if (userMapper.selectById(id) == null) return RespBody.ok(RespState.USER_NOTFOUND);
        List<Long> longs = ((List<Object>) map.get("ids")).stream().map(x -> Long.parseLong(x.toString()))
                .filter(x -> userRoleMapper.selectOne(new QueryWrapper<>(UserRole.builder().userId(id).roleId(x).build())) == null)
                .filter(x -> roleMapper.selectById(x) != null).collect(Collectors.toList());
        if (longs.size() == 0) return RespBody.ok();
        Long result = longs.stream().filter(x -> x != null).filter(x -> userRoleMapper.insert(UserRole.builder().userId(id).roleId(x).build()) > 0).count();
        return result.intValue() == longs.size() ? RespBody.ok() : RespBody.no();
    }

    @Override
    public RespBody role(Long userId, Long roleId) {
        User user = userMapper.selectById(userId);
        redisTemplate.keys("*" + user.getSole() + "*").stream().filter(x -> !StringUtils.isEmpty(x)).forEach(x -> redisTemplate.delete(x));
        return userRoleMapper.delete(new QueryWrapper<>(UserRole.builder().userId(userId).roleId(roleId).build())) > 0 ? RespBody.ok() : RespBody.no();
    }

    @Override
    public RespBody change(Map<String, String> map) {
        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, map.get("username"))).toBuilder().build();
        if (user == null) return RespBody.body(RespState.USER_NOTFOUND);
        if (bCryptPasswordEncoder.matches(map.get("original"), user.getPassword())) {
            user = user.toBuilder().enabled(true).password(bCryptPasswordEncoder.encode(map.get("password"))).build();
            if (userMapper.updateById(user) > 0) {
                SecurityContextHolder.clearContext();
                redisTemplate.keys("*" + user.getSole() + "*").stream().filter(x -> !StringUtils.isEmpty(x)).forEach(x -> redisTemplate.delete(x));
                return RespBody.ok();
            } else return RespBody.no();
        } else return RespBody.body(RespState.BAD_CREDENTIAL);
    }

    @Override
    public RespBody reset(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) return RespBody.body(RespState.USER_NOTFOUND);
        user.setEnabled(true);
        user.setPassword(bCryptPasswordEncoder.encode(CryptTools.crypt(user.getPhone())));
        return userMapper.updateById(user) > 0 ? RespBody.ok() : RespBody.no();
    }

    @Override
    public List<Long> children(Principals principals) {
        Objects.requireNonNull(principals);
        String key = getChildKey(PrincipalsHolder.getUsername(), principals.getSole());
        String value = (String) redisTemplate.opsForValue().get(key);
        Gson gson = new Gson();
        if (!StringUtils.isEmpty(value)) {
            Type type = new TypeToken<Set<Long>>() {{
            }}.getType();
            Set<Long> longs = gson.fromJson(value, type);
            return new ArrayList<>(longs);
        }
        Long id = PrincipalsHolder.getId();
        List<User> listUser = userMapper.findAllByPid(id);
        Set<Long> set = new TreeSet<>();
        set = getChildren(listUser, id, set);
        set.add(id);
        redisTemplate.opsForValue().set(key, gson.toJson(set), firstExpire, TimeUnit.SECONDS);
        return new ArrayList<>(set);
    }

    @Override
    public void code(HttpServletResponse response, Principals principals) throws Exception {
        CodeTools.encode("address", "src/main/resources/static/favicon.png", response.getOutputStream(), true);
    }

    private String getChildKey(String username, String sole) {
        return prefix + "[" + username + "]:" + sole + ":child";
    }

    private List<Long> convert(User user, List<User> users) {
        return users.parallelStream().filter(x -> x.getPid().equals(user.getId())).map(x -> convert(x, users))
                .peek(y -> y.add(user.getId())).flatMap(Collection::parallelStream).distinct().collect(Collectors.toList());
    }

    private Set<Long> getChildren(List<User> list, Long pid, Set<Long> collection) {
        if (list == null || list.size() == 0) return Collections.emptySet();
        for (Iterator<User> it = list.iterator(); it.hasNext(); ) {
            User user = it.next();
            collection.add(user.getId());
            if (user.getPid().equals(pid))
                if (!user.getChildren().isEmpty()) getChildren(user.getChildren(), user.getId(), collection);
        }
        return collection;
    }

}
