package com.sora.modules.major.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserCollege implements Serializable {

    private Long id;

    private Long userId;

    private Long collegeId;

}
