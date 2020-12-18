package com.sora.modules.grant.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Role implements Serializable {

    private Long id;

    private String name;

    private String label;

    private Integer sort;

    private Boolean fixed;

    private Date createTime;

    private Date modifyTime;

}
