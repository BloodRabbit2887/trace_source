package qichen.code.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 表单提交选项表
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-01-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_submit_table_options")
public class SubmitTableOptions implements Serializable {

private static final long serialVersionUID=1L;

    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 提交内容
     */
    @TableField("answer")
    private String answer;

    /**
     * 提交选项ID
     */
    @TableField("optionId")
    private Integer tableOptionId;

    /**
     * 表单类型
     */
    @TableField("tableType")
    private Integer tableType;

    /**
     * 工单ID
     */
    @TableField("orderId")
    private Integer orderId;

    /**
     * 状态 0正常 1锁定
     */
    @TableField("`Status`")
    private Integer Status;

    /**
     * 排序
     */
    @TableField("`orders`")
    private Integer orders;

    /**
     * 0未删除 1已删除
     */
    @TableField("Deltf")
    @TableLogic
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
     * 备注
     */
    @TableField("`remark`")
    private String remark;

    @TableField("`submitAnswerId`")
    private Integer submitAnswerId;


}
