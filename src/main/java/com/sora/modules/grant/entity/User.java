package com.sora.modules.grant.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.sora.common.type.basic.Gender;
import com.sora.modules.major.entity.College;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

    private Long id;

    private String sole;

    private String name;

    private Gender gender;

    private String identify;

    private String phone;

    private String email;

    private String qq;

    private String wechat;

    private String username;

    private String password;

    private Boolean enabled;

    private Double scale;

    private Long pid;

    private Integer count;

    private String profile;

    private Date lastTime;

    private Date createTime;

    private Date modifyTime;

    @TableField(exist = false)
    private List<Role> roles;

    @TableField(exist = false)
    private List<User> children;

    @TableField(exist = false)
    private List<College> colleges;

}
