package com.sora.common.http;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @param <AnyType>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespBody<AnyType> implements Serializable {

    private Integer status;

    private String message;

    private AnyType data;

    public RespBody(RespState rs) {
        this(rs.getStatus(), rs.getMessage());
    }

    public RespBody(RespState rs, AnyType data) {
        this(rs.getStatus(), rs.getMessage(), data);
    }

    public RespBody(Integer status, String message) {
        this(status, message, null);
    }

    public static RespBody ok() {
        return new RespBody(RespState.SUCCESS);
    }

    public static <AnyType> RespBody ok(AnyType data) {
        return new RespBody(RespState.SUCCESS, data);
    }

    public static RespBody no() {
        return new RespBody(RespState.FAILURE);
    }

    public static <AnyType> RespBody no(RespState rs, AnyType data) {
        return new RespBody(rs, data);
    }

    public static <T> RespBody body(RespState rs) {
        return new RespBody(rs);
    }

    public static <T> RespBody body(RespState rs, T data) {
        return new RespBody(rs, data);
    }

}
