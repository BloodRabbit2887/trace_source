package qichen.code.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 质量管理部工单表
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-12-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_quality_order")
public class QualityOrder implements Serializable {

private static final long serialVersionUID=1L;

    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 标题
     */
    private String title;

    /**
     * 工单ID
     */
    @TableField("orderID")
    private Integer orderID;

    /**
     * 模号
     */
    @TableField("`number`")
    private String number;

    /**
     * 状态 0未开始 1进行中 2已完成 3已结束
     */
    @TableField("Status")
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

    /**
     * 创建人ID
     */
    @TableField("submitId")
    private Integer submitId;

    @TableField("`draft`")
    private Integer draft;

    @TableField("verifyId")
    private Integer verifyId;

    @TableField("verifyStatus")
    private Integer verifyStatus;

    @TableField("verifyRemark")
    private String verifyRemark;

    @TableField("verifyTime")
    private LocalDateTime verifyTime;


}
