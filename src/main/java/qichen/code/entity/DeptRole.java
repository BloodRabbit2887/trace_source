package qichen.code.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 部门角色表
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-02-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_dept_role")
public class DeptRole implements Serializable {

private static final long serialVersionUID=1L;

    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 名称
     */
    @TableField("`name`")
    private String name;

    /**
     * 等级
     */
    @TableField("`level`")
    private Integer level;

    /**
     * 填写部门内所有工单权限 0无 1有
     */
    @TableField("createAllOrderPermission")
    private Integer createAllOrderPermission;

    /**
     * 分配工单至下级用户权限 0无 1有
     */
    @TableField("distributionPermission")
    private Integer distributionPermission;

    /**
     * 工单审核权限 0无 1有
     */
    @TableField("verifyPermission")
    private Integer verifyPermission;

    /**
     * 工单推送至下一环节权限 0无 1有
     */
    @TableField("linkChangePermission")
    private Integer linkChangePermission;

    /**
     * 工单编辑权限 0无 1有
     */
    @TableField("updatePermission")
    private Integer updatePermission;

    /**
     * 0未删除 1已删除
     */
    @TableLogic
    @TableField("Deltf")
    private Integer Deltf;

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


    @TableField("deptId")
    private Integer deptId;


}
