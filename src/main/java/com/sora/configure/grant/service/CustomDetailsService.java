package com.sora.configure.grant.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sora.common.utils.CryptTools;
import com.sora.configure.grant.domain.PrincipalsBuilder;
import com.sora.modules.grant.entity.Role;
import com.sora.modules.grant.entity.User;
import com.sora.modules.grant.mapper.RoleMapper;
import com.sora.modules.grant.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomDetailsService implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = null;
        LambdaQueryWrapper<User> wrapper = Wrappers.<User>lambdaQuery();
        if (CryptTools.isPhone(username)) user = userMapper.selectOne(wrapper.eq(User::getPhone, username));
        else user = userMapper.selectOne(wrapper.eq(User::getUsername, username));
        if (user == null) throw new UsernameNotFoundException("user not found");
        List<Role> roles = roleMapper.findAllByUserId(user.getId());
        return PrincipalsBuilder.create(user, roles);
    }
}
