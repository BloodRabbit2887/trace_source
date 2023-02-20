package qichen.code.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户工单分配任务表
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-02-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_user_table_project")
public class UserTableProject implements Serializable {

private static final long serialVersionUID=1L;

    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 发布人ID
     */
    @TableField("submitId")
    private Integer submitId;

    /**
     * 用户ID
     */
    @TableField("userId")
    private Integer userId;

    /**
     * 部门ID
     */
    @TableField("deptId")
    private Integer deptId;

    /**
     * 模号
     */
    @TableField("`number`")
    private String number;

    /**
     * 工单类型
     */
    @TableField("tableType")
    private Integer tableType;

    /**
     * 0未完成 1已完成 2已取消
     */
    @TableField("`Status`")
    private Integer Status;

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

    /**
     * 发布人备注
     */
    @TableField("`remark`")
    private String remark;


    @TableField("skipTypes")
    private String skipTypes;

}
