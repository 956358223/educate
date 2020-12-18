package com.sora.common.spec;

import lombok.Data;

import java.io.Serializable;

@Data
public class SpecBody<AnyType> implements Serializable {

    private Integer start;

    private Integer size;

    private AnyType body;

}
