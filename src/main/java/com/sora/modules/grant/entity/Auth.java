package com.sora.modules.grant.entity;

import com.baomidou.mybatisplus.annotation.TableField;
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
public class Auth implements Serializable {

    private Long id;

    private String name;

    private String component;

    private String url;

    private String path;

    private String icon;

    private Boolean auth;

    private Boolean enabled;

    private Boolean sided;

    private Integer sort;

    private Integer type;

    private Long pid;

    private Date createTime;

    private Date modifyTime;

    @TableField(exist = false)
    private List<Role> roles;

    @TableField(exist = false)
    private List<Auth> children;

}
