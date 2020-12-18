package com.sora.modules.major.entity;

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
public class College implements Serializable {

    private Long id;

    private String name;

    private Integer type;

    private Long pid;

    private String level;

    private Boolean enabled;

    private Date createTime;

    private Date modifyTime;

    @TableField(exist = false)
    private List<College> children;
}
