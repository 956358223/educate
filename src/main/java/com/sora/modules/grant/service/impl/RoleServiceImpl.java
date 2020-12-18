package com.sora.modules.grant.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sora.common.http.RespBody;
import com.sora.common.http.RespState;
import com.sora.common.spec.SpecBody;
import com.sora.modules.grant.entity.Role;
import com.sora.modules.grant.entity.RoleAuth;
import com.sora.modules.grant.entity.UserRole;
import com.sora.modules.grant.mapper.AuthMapper;
import com.sora.modules.grant.mapper.RoleAuthMapper;
import com.sora.modules.grant.mapper.RoleMapper;
import com.sora.modules.grant.mapper.UserRoleMapper;
import com.sora.modules.grant.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private AuthMapper authMapper;

    @Autowired
    private RoleAuthMapper roleAuthMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public RespBody append(Role role) {
        if (!roleMapper.selectList(Wrappers.<Role>lambdaQuery().eq(Role::getName, role.getName())).isEmpty()) {
            return RespBody.body(RespState.RELATE_EXISTS);
        }
        return roleMapper.insert(role.toBuilder().name("ROLE_" + role.getName()).fixed(false).createTime(new Date()).build()) > 0 ? RespBody.ok() : RespBody.body(RespState.INSERT_FAILURE);
    }

    @Override
    public RespBody modify(Role role) {
        if (role.getId() == null) return RespBody.body(RespState.ILLEGAL);
        Role entity = roleMapper.selectById(role.getId());
        if (entity == null) RespBody.body(RespState.DATA_NOTFOUND);
        role.setModifyTime(new Date());
        return roleMapper.updateById(role) > 0 ? RespBody.ok() : RespBody.body(RespState.INSERT_FAILURE);
    }

    @Override
    public RespBody delete(List<Long> longs) {
        if (longs == null || longs.size() == 0) return RespBody.body(RespState.ILLEGAL);
        List<Long> ids = longs.stream().filter(x -> !roleMapper.selectById(x).getFixed()).collect(Collectors.toList());
        int i = roleAuthMapper.delete(Wrappers.<RoleAuth>lambdaQuery().in(RoleAuth::getRoleId, ids));
        int j = userRoleMapper.delete(Wrappers.<UserRole>lambdaQuery().eq(UserRole::getRoleId, ids));
        int k = roleMapper.deleteBatchIds(ids);
        return i > 0 && j > 0 && k > 0 ? RespBody.ok() : RespBody.no();
    }

    @Override
    public RespBody details(Long aLong) {
        Role role = roleMapper.selectById(aLong);
        return role != null ? RespBody.ok(role) : RespBody.no();
    }

    @Override
    public RespBody search(SpecBody<Role> body) {
        PageHelper.startPage(body.getStart(), body.getSize());
        return RespBody.ok(new PageInfo<>(roleMapper.findAll(body.getBody())));
    }

    @Override
    public RespBody list() {
        return RespBody.ok(roleMapper.selectList(null));
    }


    @Override
    public RespBody auth(Long id) {
        return RespBody.ok(roleAuthMapper.selectList(Wrappers.<RoleAuth>lambdaQuery().eq(RoleAuth::getAuthId, id)).stream().map(RoleAuth::getId).collect(Collectors.toList()));
    }

    @Override
    public RespBody auth(Map<String, Object> map) {
        Long id = Long.parseLong(map.get("id").toString());
        if (roleMapper.selectById(id) == null) return RespBody.body(RespState.DATA_NOTFOUND);
        roleAuthMapper.delete(Wrappers.<RoleAuth>lambdaQuery().eq(RoleAuth::getRoleId, id));
        List<Long> longs = ((List<Object>) map.get("ids")).stream().map(x -> Long.parseLong(x.toString())).filter(x -> authMapper.selectById(x) != null).collect(Collectors.toList());
        if (longs.size() == 0) return RespBody.ok();
        Long result = longs.stream().filter(x -> x != null).filter(x -> roleAuthMapper.insert(RoleAuth.builder().roleId(id).authId(x).build()) > 0).count();
        return longs.size() == result.intValue() ? RespBody.ok() : RespBody.no();
    }

}
