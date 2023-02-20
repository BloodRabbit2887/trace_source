package qichen.code.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-12-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_user")
public class User implements Serializable {

private static final long serialVersionUID=1L;

    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 姓名
     */
    @TableField("`name`")
    private String name;

    /**
     * 性别 1男 2女
     */
    @TableField("`sex`")
    private Integer sex;

    /**
     * 账号
     */
    @TableField("`account`")
    private String account;

    /**
     * 密码
     */
    @TableField("`pass`")
    private String pass;

    /**
     * 盐值
     */
    @TableField("saltValue")
    private String saltValue;

    /**
     * 部门ID
     */
    @TableField("deptId")
    private Integer deptId;

    /**
     * 状态 0正常 1锁定
     */
    @TableField("`Status`")
    private Integer Status;

    /**
     * 0未删除 1已删除
     */
    @TableField("Deltf")
    @TableLogic
    private Integer Deltf;

    /**
     * 排序
     */
    @TableField("`orders`")
    private Integer orders;

    /**
     * 创建时间
     */
    @TableField("createTime")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("updateTime")
    private LocalDateTime updateTime;

    /**
     * 头像
     */
    @TableField("`avatar`")
    private String avatar;

    /**
     * 0普通员工 1部门主管
     */
    @TableField("`type`")
    private Integer type;

    /**
     * 工单审核权限 0无 1有
     */
    @TableField("verifyPermission")
    private Integer verifyPermission;


    @TableField("AssembleTableType")
    private Integer AssembleTableType;

    @TableField("deptRoleId")
    private Integer deptRoleId;

}
