package com.sbt.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.Data;

/**
 * 角色
 *
 * @author lww
 */
@Data
public class SsoRole implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色id
     */
    private Long roleId;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色状态
     */
    private Integer status;

    /**
     * 有效期开始时间
     */
    private Date startTime;

    /**
     * 有效期截止时间
     */
    private Date endTime;

    /**
     * 权限
     */
    private List<Permission> permissions;
}
