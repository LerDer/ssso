package com.sbt.domain;

import java.io.Serializable;
import lombok.Data;

/**
 * 权限
 *
 * @author lww
 */
@Data
public class Permission implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 权限id
     */
    private Long perId;

    /**
     * 权限名称
     * 例: 用户管理-新增
     */
    private String perName;

    /**
     * 权限key
     * 例: user-add
     */
    private String perKey;

    /**
     * 请求url
     * 例: user/add
     */
    private String rquestUrl;

    /**
     * 排序,数值越小,越靠前
     */
    private Integer order;
}
